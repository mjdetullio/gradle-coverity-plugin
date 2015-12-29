package com.github.mjdetullio.gradle.coverity.tasks

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
     * Specifies any additional arguments to be passed to the external
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
        project.exec {
            executable Utils.getExePath((String) project.coverity.coverityHome, 'cov-commit-defects')
            args '--dir', project.file((String) project.coverity.intermediateDir).absolutePath
            args '--stream', project.coverity.stream

            if (project.coverity.scm) {
                args '--scm', project.coverity.scm
            }

            if (project.coverity.host) {
                args '--host', project.coverity.host
            }

            if (project.coverity.dataport) {
                args '--dataport', project.coverity.dataport
            } else if (project.coverity.port) {
                args '--port', project.coverity.port
            } else if (project.coverity.httpsport) {
                args '--https-port', project.coverity.httpsport
            }

            if (project.coverity.user) {
                args '--user', project.coverity.user
            }

            if (project.coverity.pass) {
                args '--password', project.coverity.pass
            }

            if (additionalArgs) {
                args additionalArgs
            }
        }
    }
}
