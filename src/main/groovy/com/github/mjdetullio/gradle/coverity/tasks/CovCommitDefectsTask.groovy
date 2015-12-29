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
 * Runs the <code>cov-commit-defects</code> command, which reads defects and
 * source data stored in an intermediate directory and writes them to a Coverity
 * Connect database in a stream that you specify. The data are written as a unit
 * to the database; this unit is a snapshot.
 *
 * @author Matthew DeTullio
 */
class CovCommitDefectsTask extends DefaultTask {
    /**
     * Specifies any additonal arguments to be passed to the external
     * <code>cov-commit-defects</code> execution.
     */
    List<String> additionalArgs

    /**
     * Creates a new {@link CovCommitDefectsTask}, setting its description and
     * placing it under the 'Coverity' group.
     */
    CovCommitDefectsTask() {
        group = 'Coverity'
        description = 'Runs the cov-commit-defects command, which reads ' +
                'defects and source data stored in an intermediate ' +
                'directory and writes them to a Coverity Connect database ' +
                'in a stream that you specify. The data are written as a ' +
                'unit to the database; this unit is a snapshot.'
    }

    /**
     * Task action that executes <code>cov-commit-defects</code>.
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    @TaskAction
    void commit() {
        def ext = project.extensions.getByType(CoverityRootExtension)

        project.exec {
            executable Utils.getExePath(ext.coverityHome, 'cov-commit-defects')
            args '--dir', project.file(ext.intermediateDir).absolutePath
            args '--stream', ext.stream

            if (ext.scm) {
                args '--scm', ext.scm
            }

            if (ext.host) {
                args '--host', ext.host
            }

            if (ext.dataport) {
                args '--dataport', ext.dataport
            } else if (ext.port) {
                args '--port', ext.port
            } else if (ext.httpsport) {
                args '--https-port', ext.httpsport
            }

            if (ext.user) {
                args '--user', ext.user
            }

            if (ext.pass) {
                args '--password', ext.pass
            }

            if (additionalArgs) {
                args additionalArgs
            }
        }
    }
}
