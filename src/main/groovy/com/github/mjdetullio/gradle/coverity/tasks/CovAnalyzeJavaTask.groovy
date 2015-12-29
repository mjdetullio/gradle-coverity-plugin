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
