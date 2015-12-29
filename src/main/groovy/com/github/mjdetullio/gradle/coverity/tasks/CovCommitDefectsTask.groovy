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
