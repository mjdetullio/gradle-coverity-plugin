package com.github.mjdetullio.gradle.coverity.internal

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.variant.BaseVariantData
import com.android.build.gradle.internal.variant.TestVariantData
import com.android.builder.core.BuilderConstants
import com.github.mjdetullio.gradle.coverity.model.CoverityExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention

/**
 * A set of {@link EmitConfig}s for a given project that is populated when
 * constructing this type.
 *
 * @author Matthew DeTullio
 */
class EmitConfigSet {
    /**
     * The base project for this set.
     */
    Project baseProject

    /**
     * Projects that have already been populated for this config set.
     */
    Set<Project> populatedProjects

    /**
     * Emit configs for base project and all its child projects (recursively),
     * which are created during population.
     */
    Set<EmitConfig> emitConfigs

    /**
     * Initializes and populates a new set of {@link EmitConfig}s for the given
     * {@link Project}.
     *
     * @param baseProject project to get a set of emit configs for
     */
    EmitConfigSet(Project baseProject) {
        this.baseProject = baseProject
        populatedProjects = []
        emitConfigs = []

        populate(baseProject)
    }

    /**
     * Recursively populates {@link EmitConfig}s for the given project, skipping
     * those specified in their extension.  When skipped, child projects are
     * still evaluated unless it is also specified that they should not be
     * included.
     *
     * @param project (sub)project to populate emit configs for
     */
    void populate(Project project) {
        if (populatedProjects.contains(project)) {
            return
        }

        if (!project.extensions.getByType(CoverityExtension).skip) {
            if (checkIsJavaProject(project)) {
                populateForJava(project)
            } else if (checkIsAndroidProject(project)) {
                populateForAndroid(project)
            }
        }

        /*
         * Mark as populated even if skipped so we don't attempt to iterate
         * child projects again.  Mark before iterating child projects so we
         * don't end up with an infinite recursive loop.
         */
        populatedProjects += project

        // Iterate child projects even if skipped
        if (baseProject.extensions.getByType(CoverityExtension).includeChildProjects) {
            for (Project childProject : project.childProjects.values()) {
                populate(childProject)
            }
        }
    }

    /**
     * Populates the {@link EmitConfig}s for a Java project (no child projects).
     * One config is created per {@link org.gradle.api.tasks.SourceSet} that has
     * a Java compilation task that is part of the <code>assemble</code> task.
     *
     * @param project project to populate emit configs for
     */
    void populateForJava(Project project) {
        project.convention.getPlugin(JavaPluginConvention).sourceSets.each { sourceSet ->
            Task compileJavaTask = project.tasks.getByName(sourceSet.compileJavaTaskName)

            boolean inAssemble = taskDependenciesContainsTask(project.tasks.assemble, compileJavaTask, true)

            if (inAssemble) {
                emitConfigs += new EmitConfig().with {
                    it.sourceDirs += sourceSet.allSource.srcDirs
                    it.compilerOutputDirs += sourceSet.output.files
                    it.classpath += sourceSet.compileClasspath
                    it.classpath += sourceSet.runtimeClasspath
                    return it
                }
            }
        }
    }

    /**
     * Populates the {@link EmitConfig}s for an Android project (no child
     * projects).  One config is created per {@link BaseVariantData} that is of
     * the <code>debug</code> build type and not a test variant.
     * <p/>
     * This logic should be skipped entirely if not an Android project in order
     * to avoid {@link ClassNotFoundException}s and
     * {@link NoClassDefFoundError}s.
     *
     * @param project project to populate emit configs for
     */
    void populateForAndroid(Project project) {
        BasePlugin plugin

        if (project.plugins.hasPlugin(AppPlugin)) {
            plugin = project.plugins.getPlugin(AppPlugin)
        } else if (project.plugins.hasPlugin(LibraryPlugin)) {
            plugin = project.plugins.getPlugin(LibraryPlugin)
        } else {
            throw new GradleException(
                    'Attempted to populate for Android when project does not apply Android plugin')
        }

        // Variant = Product Flavor + Build Type
        // This loop should hit all possible versions of the app
        for (BaseVariantData variantData : plugin.variantManager.variantDataList) {
            // Only use the debug build type (Coverity requires debug build)
            // Exclude test variants
            if (!variantData.variantConfiguration.buildType.name.equals(
                    BuilderConstants.DEBUG)
                    || variantData instanceof TestVariantData) {
                continue
            }

            EmitConfig emitConfig = new EmitConfig()

            variantData.variantConfiguration.sortedSourceProviders.each { sourceProvider ->
                emitConfig.sourceDirs += sourceProvider.javaDirectories
            }

            emitConfig.sourceDirs +=
                    variantData.generateRClassTask.sourceOutputDir
            emitConfig.sourceDirs +=
                    variantData.generateBuildConfigTask.sourceOutputDir
            emitConfig.sourceDirs +=
                    variantData.aidlCompileTask.sourceOutputDir

            if (!variantData.variantConfiguration.renderscriptNdkModeEnabled) {
                emitConfig.sourceDirs +=
                        variantData.renderscriptCompileTask.sourceOutputDir
            }

            if (variantData.hasProperty('javaCompileTask')) {
                // Android plugin before 1.3.x
                emitConfig.compilerOutputDirs +=
                        variantData.javaCompileTask.outputs.files.files

                emitConfig.classpath +=
                        variantData.javaCompileTask.classpath
            } else if (variantData.hasProperty('javacTask')) {
                // Android plugin 1.3.x
                emitConfig.compilerOutputDirs +=
                        variantData.javacTask.outputs.files.files

                emitConfig.classpath +=
                        variantData.javacTask.classpath
            } else {
                // Also in 1.3.x, added in case javacTask is removed in a newer version
                emitConfig.compilerOutputDirs +=
                        variantData.javaCompilerTask.outputs.files.files

                emitConfig.classpath +=
                        variantData.javaCompilerTask.classpath
            }

            emitConfig.classpath +=
                    project.files(plugin.androidBuilder.bootClasspath)

            emitConfigs += emitConfig
        }
    }

    /**
     * Checks if the given project applies the {@link JavaPlugin}.
     *
     * @param project project to check
     * @return true if Java project, otherwise false
     */
    static boolean checkIsJavaProject(Project project) {
        return project.plugins.hasPlugin(JavaPlugin)
    }

    /**
     * Checks if the given project applies the {@link AppPlugin} or
     * {@link LibraryPlugin}.  Will handle {@link ClassNotFoundException}s and
     * {@link NoClassDefFoundError}s in the event that the Android plugin is not
     * part of the build script.
     *
     * @param project project to check
     * @return true if Android project, otherwise false
     */
    static boolean checkIsAndroidProject(Project project) {
        try {
            return (project.plugins.hasPlugin(AppPlugin)
                    || project.plugins.hasPlugin(LibraryPlugin))
        } catch (ClassNotFoundException e) {
            project.logger.debug(
                    "Assuming ${project.name} is not an Android project.", e)
        } catch (NoClassDefFoundError e) {
            project.logger.debug(
                    "Assuming ${project.name} is not an Android project.", e)
        }
        false
    }

    /**
     * Checks if a given parent task's dependencies includes the given child,
     * optionally recursive.
     *
     * @param parent parent task
     * @param child child task
     * @param recursive defaults to false
     * @return true if child is a dependency of the parent, otherwise false
     */
    static boolean taskDependenciesContainsTask(Task parent, Task child,
                boolean recursive = false) {
        Set<Task> parentDependencies =
                parent.taskDependencies.getDependencies(parent)

        if (parentDependencies.contains(child)) {
            return true
        }

        if (!recursive) {
            return false
        }

        for (Task dependency : parentDependencies) {
            if (taskDependenciesContainsTask(dependency, child, true)) {
                return true
            }
        }

        return false
    }
}
