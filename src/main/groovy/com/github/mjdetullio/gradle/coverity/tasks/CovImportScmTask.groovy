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
