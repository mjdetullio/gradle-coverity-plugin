package com.github.mjdetullio.gradle.coverity.model

import org.gradle.api.Project

/**
 * The base extension that contains settings to be applied to all child projects
 * and will be inherited by {@link CoverityRootExtension}.
 *
 * @author Matthew DeTullio
 */
class CoverityExtension {
    /**
     * The name that should be given when creating this extension.
     */
    static String EXTENSION_NAME = 'coverity'

    /**
     * Specifies if the current project's child projects should be included.
     * If true, child projects will be included even if the current project is
     * skipped.
     * <p/>
     * Default value is true.
     */
    boolean includeChildProjects = true

    /**
     * Specifies if the current project should be skipped.  Does not also skip
     * child projects (this is defined by {@link #includeChildProjects}).
     * <p/>
     * Default value is false.
     */
    boolean skip = false

    /**
     * Specifies files that should be excluded from analysis by deleting them
     * from the emit DB using <code>cov-manage-emit</code>.
     * <p/>
     * Default value is <code>[project.buildDir]</code>.
     */
    Set<File> excludes

    /**
     * Instantiates a new instance of this extension.
     *
     * @param project project this extension is applied to, which is used to
     *                default the fields.
     */
    CoverityExtension(Project project) {
        excludes = [project.buildDir]
    }
}
