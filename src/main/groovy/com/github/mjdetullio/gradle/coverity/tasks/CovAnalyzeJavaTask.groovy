package com.github.mjdetullio.gradle.coverity.tasks

import com.github.mjdetullio.gradle.coverity.util.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Runs the <code>cov-analyze-java</code> command, which analyzes code that was
 * previously parsed and emitted into an intermediate directory. It stores
 * analysis information in the intermediate directory and is later pushed by to
 * the Coverity Connect database by <code>cov-commit-defects</code>.
 *
 * @author Matthew DeTullio
 */
class CovAnalyzeJavaTask extends DefaultTask {
    /**
     * Specifies any additonal arguments to be passed to the external
     * <code>cov-analyze-java</code> execution.
     */
    List<String> additionalArgs

    /**
     * Creates a new {@link CovAnalyzeJavaTask}, setting its description and
     * placing it under the 'Coverity' group.
     */
    CovAnalyzeJavaTask() {
        group = 'Coverity'
        description = 'Runs the cov-analyze-java command, which analyzes ' +
                'code that was previously parsed and emitted into an ' +
                'intermediate directory. It stores analysis information in ' +
                'the intermediate directory and is later pushed by to the ' +
                'Coverity Connect database by cov-commit-defects.'
    }

    /**
     * Task action that executes <code>cov-analyze-java</code>.
     */
    @SuppressWarnings('GroovyUnusedDeclaration')
    @TaskAction
    void analyze() {
        project.exec {
            executable Utils.getExePath((String) project.coverity.coverityHome, 'cov-analyze-java')
            args '--dir', project.file((String) project.coverity.intermediateDir).absolutePath
            args '--strip-path', project.projectDir.absolutePath
            args '--jobs', 'auto'
            args '--all'
            if (additionalArgs) {
                args additionalArgs
            }
        }
    }
}
