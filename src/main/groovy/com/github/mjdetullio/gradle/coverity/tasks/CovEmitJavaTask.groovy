package com.github.mjdetullio.gradle.coverity.tasks

import com.github.mjdetullio.gradle.coverity.internal.EmitConfig
import com.github.mjdetullio.gradle.coverity.internal.EmitConfigSet
import com.github.mjdetullio.gradle.coverity.util.Utils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Runs cov-emit-java (once per source set for Java projects, once per variant
 * for Android projects), which parses Java source code and bytecode, and saves
 * javac outputs. It stores these outputs to an emit repository for subsequent
 * static analysis and outputs it into a directory (emit repository) that can
 * later be analyzed with <code>cov-analyze-java</code>.
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
                executable Utils.getExePath((String) project.coverity.coverityHome, 'cov-emit-java')
                args '--dir', project.file((String) project.coverity.intermediateDir).absolutePath
                args '--findsource', emitConfig.sourceDirs.join(File.pathSeparator)
                args '--compiler-outputs', emitConfig.compilerOutputDirs.join(File.pathSeparator)
                args '--classpath', emitConfig.classpath.asPath
                if (additionalArgs) {
                    args additionalArgs
                }
            }
        }
    }
}
