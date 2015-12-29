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
 * Runs the <code>cov-analyze</code> command with the <code>--java</code>
 * argument, which analyzes code that was previously parsed and emitted into an
 * intermediate directory. It stores analysis information in the intermediate
 * directory and is later pushed by to the Coverity Connect database by
 * <code>cov-commit-defects</code>.
 *
 * @author Matthew DeTullio
 */
class CovAnalyzeJavaTask extends DefaultTask {
    /**
     * Specifies any additional arguments to be passed to the external
     * <code>cov-analyze</code> execution.
     */
    List<String> additionalArgs

    /**
     * Creates a new {@link CovAnalyzeJavaTask}, setting its description and
     * placing it under the 'Coverity' group.
     */
    CovAnalyzeJavaTask() {
        group = 'Coverity'
        description = 'Runs the cov-analyze command with the --java ' +
                'argument, which analyzes code that was previously parsed ' +
                'and emitted into an intermediate directory. It stores ' +
                'analysis information in the intermediate directory and ' +
                'is later pushed by to the Coverity Connect database by ' +
                'cov-commit-defects.'
    }

    /**
     * Task action that executes <code>cov-analyze</code>.
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    @TaskAction
    void analyze() {
        def ext = project.extensions.getByType(CoverityRootExtension)

        project.exec {
            executable Utils.getExePath(ext.coverityHome, 'cov-analyze')
            args '--java'
            args '--dir', project.file(ext.intermediateDir).absolutePath
            args '--strip-path', project.file(ext.stripPath).absolutePath
            args '--jobs', 'auto'
            args '--all'
            if (additionalArgs) {
                args additionalArgs
            }
        }
    }
}
