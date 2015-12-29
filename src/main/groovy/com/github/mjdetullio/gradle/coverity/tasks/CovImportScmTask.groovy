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

import com.github.mjdetullio.gradle.coverity.model.CoverityRootExtension
import com.github.mjdetullio.gradle.coverity.util.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Runs the <code>cov-import-scm</code> command, which imports SCM information
 * into the emit database in the intermediate directory. It must be run after
 * <code>cov-emit-java</code> and before <code>cov-analyze</code>.
 *
 * @author Matthew DeTullio
 */
class CovImportScmTask extends DefaultTask {
    /**
     * Specifies any additional arguments to be passed to the external
     * <code>cov-import-scm</code> execution.
     */
    List<String> additionalArgs

    /**
     * Creates a new {@link CovImportScmTask}, setting its description and
     * placing it under the 'Coverity' group.
     */
    CovImportScmTask() {
        group = 'Coverity'
        description = 'Runs the cov-import-scm command, which imports SCM ' +
                'information into the emit database in the intermediate ' +
                'directory. It must be run after cov-emit-java and before ' +
                'cov-analyze.'
    }

    /**
     * Task action that executes <code>cov-import-scm</code>.
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    @TaskAction
    void analyze() {
        def ext = project.extensions.getByType(CoverityRootExtension)

        def intermediateDirAbsPath = project.file(ext.intermediateDir).absolutePath

        project.exec {
            executable Utils.getExePath(ext.coverityHome, 'cov-import-scm')
            args '--dir', intermediateDirAbsPath
            args '--log', "${intermediateDirAbsPath}/cov-import-scm.log"
            args '--scm', ext.scm
            if (additionalArgs) {
                args additionalArgs
            }
        }
    }
}
