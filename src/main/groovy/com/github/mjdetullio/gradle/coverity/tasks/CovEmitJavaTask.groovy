/*
 * The MIT License
 *
 * Copyright (c) 2015, Matthew DeTullio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.mjdetullio.gradle.coverity.tasks

import com.github.mjdetullio.gradle.coverity.internal.EmitConfig
import com.github.mjdetullio.gradle.coverity.internal.EmitConfigSet
import com.github.mjdetullio.gradle.coverity.model.CoverityRootExtension
import com.github.mjdetullio.gradle.coverity.util.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * Runs <code>cov-emit-java</code> (once per source set for Java projects, once
 * per variant for Android projects), which parses Java source code and
 * bytecode, and saves javac outputs. It stores these outputs to an emit
 * repository for subsequent static analysis and outputs it into a directory
 * (emit repository) that can later be analyzed with
 * <code>cov-analyze-java</code>.
 * <p/>
 * After each emit, <code>cov-manage-emit</code> is run for each excluded path.
 *
 * @author Matthew DeTullio
 */
class CovEmitJavaTask extends DefaultTask {
    /**
     * Specifies any additional arguments to be passed to <i>each</i> external
     * <code>cov-emit-java</code> execution.
     */
    List<String> additionalArgs

    /**
     * Creates a new {@link CovEmitJavaTask}, setting its description and placing
     * it under the 'Coverity' group.
     */
    CovEmitJavaTask() {
        group = 'Coverity'
        description = 'Runs cov-emit-java (once per source set for Java ' +
                'projects, once per variant for Android projects), which ' +
                'parses Java source code and bytecode, and saves javac ' +
                'outputs. It stores these outputs to an emit repository for ' +
                'subsequent static analysis and outputs it into a directory ' +
                '(emit repository) that can later be analyzed with ' +
                'cov-analyze-java.'
    }

    /**
     * Task action that builds an {@link EmitConfigSet} for this task's project,
     * then executes <code>cov-emit-java</code> for each {@link EmitConfig}.
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    @TaskAction
    void emit() {
        def ext = project.extensions.getByType(CoverityRootExtension)

        for (EmitConfig emitConfig : new EmitConfigSet(project).emitConfigs) {
            // Remove source dirs that do not exist, otherwise cov-emit-java will throw an error
            for (Iterator<File> i = emitConfig.sourceDirs.iterator(); i.hasNext();) {
                File f = i.next();
                if (!f.exists()) {
                    i.remove();
                }
            }

            /*
             * Note:
             * Not removing non-existent compiler outputs.  If none in the list
             * we provide exist, cov-emit-java will fail.  We want that to
             * happen because that case probably represents a misconfiguration.
             */

            project.exec {
                executable Utils.getExePath(ext.coverityHome, 'cov-emit-java')
                args '--dir', project.file(ext.intermediateDir).absolutePath
                args '--findsource', emitConfig.sourceDirs.join(File.pathSeparator)
                args '--compiler-outputs', emitConfig.compilerOutputDirs.join(File.pathSeparator)
                args '--classpath', emitConfig.classpath.asPath
                if (additionalArgs) {
                    args additionalArgs
                }
            }
        }

        for (File exclude : gatherExcludes(project)) {
            project.exec {
                executable Utils.getExePath(ext.coverityHome, 'cov-manage-emit')
                args '--java'
                args '--dir', project.file(ext.intermediateDir).absolutePath
                args '--tu-pattern', "file('${exclude.absolutePath}')"
                args 'delete'

                /*
                 * cov-manage-emit will exit with 2 if the specified file is
                 * not in the emit DB, either because the File does not exist
                 * or because it was already removed from the emit DB.
                 *
                 * cov-manage-emit will log "No matching translation units."
                 * if this occurs.
                 */
                ignoreExitValue = true
            }
        }
    }

    /**
     * Recursively gathers the excludes for a project and its children,
     * respecting the
     * {@link com.github.mjdetullio.gradle.coverity.model.CoverityExtension#skip}
     * and
     * {@link com.github.mjdetullio.gradle.coverity.model.CoverityExtension#includeChildProjects}
     * settings for the projects.
     *
     * @param project the project
     * @return excludes for project and its children
     */
    static Set<File> gatherExcludes(Project project) {
        def ext = project.extensions.getByType(CoverityRootExtension)

        Set<File> excludes = []

        if (!ext.skip && ext.excludes) {
            excludes += ext.excludes.findAll { it.exists() }
        }

        if (ext.includeChildProjects) {
            for (Project childProject : project.childProjects.values()) {
                excludes += gatherExcludes(childProject)
            }
        }

        excludes
    }
}
