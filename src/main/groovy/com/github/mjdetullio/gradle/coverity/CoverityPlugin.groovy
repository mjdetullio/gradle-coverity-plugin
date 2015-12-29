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
package com.github.mjdetullio.gradle.coverity

import com.github.mjdetullio.gradle.coverity.model.CoverityExtension
import com.github.mjdetullio.gradle.coverity.model.CoverityRootExtension
import com.github.mjdetullio.gradle.coverity.tasks.CovAnalyzeJavaTask
import com.github.mjdetullio.gradle.coverity.tasks.CovCommitDefectsTask
import com.github.mjdetullio.gradle.coverity.tasks.CovEmitJavaTask
import com.github.mjdetullio.gradle.coverity.tasks.CovImportScmTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * A plugin for integrating with <a href="http://www.coverity.com">Coverity</a>,
 * a static analysis platform.  Adds tasks to the project that will emit,
 * analyze, and commit data using Coverity analysis tools that are installed on
 * the user's machine.
 * <p/>
 * Supports analysis of Java code for Java and/or Android projects.
 *
 * @author Matthew DeTullio
 */
class CoverityPlugin implements Plugin<Project> {
    /**
     * Creates the root extension, child extensions, the covEmitJava task, the
     * covAnalyzeJava task, and the covCommitDefects task.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    void apply(Project project) {
        project.extensions.create(CoverityExtension.EXTENSION_NAME,
                CoverityRootExtension, project)

        project.task('covEmitJava', type: CovEmitJavaTask)
        project.task('covImportScm', type: CovImportScmTask,
                dependsOn: project.tasks.covEmitJava)
        project.task('covAnalyzeJava', type: CovAnalyzeJavaTask,
                dependsOn: project.tasks.covEmitJava)
        project.task('covCommitDefects', type: CovCommitDefectsTask,
                dependsOn: project.tasks.covAnalyzeJava)

        configureChildProjects(project)

        project.afterEvaluate {
            if (project.extensions.getByType(CoverityRootExtension).scm) {
                project.tasks.covAnalyzeJava.dependsOn(
                        project.tasks.covImportScm)
            }
        }
    }

    /**
     * Recursively configures the given project's child projects with the
     * {@link CoverityExtension}.  This is run during {@link #apply}.
     *
     * @param project project to recurse and configure
     */
    void configureChildProjects(Project project) {
        for (Project childProject : project.childProjects.values()) {
            childProject.extensions.create(CoverityExtension.EXTENSION_NAME,
                    CoverityExtension, childProject)

            configureChildProjects(childProject)
        }
    }
}
