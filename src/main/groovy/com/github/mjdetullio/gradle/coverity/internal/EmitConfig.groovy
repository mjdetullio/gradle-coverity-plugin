package com.github.mjdetullio.gradle.coverity.internal

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.file.UnionFileCollection

/**
 * Configuration for a single execution of <code>cov-emit-java</code> that holds
 * the files to be specified for the <code>--findsource</code>,
 * <code>--compiler-outputs</code>, and <code>--classpath</code> arguments.
 *
 * @author Matthew DeTullio
 */
class EmitConfig {
    /**
     * Directories to be passed to the <code>--findsource</code> argument.
     */
    Set<File> sourceDirs = []

    /**
     * Directories to be passed to the <code>--compiler-outputs</code> argument.
     */
    Set<File> compilerOutputDirs = []

    /**
     * The classpath to be passed to the <code>--classpath</code> argument.
     */
    FileCollection classpath = new UnionFileCollection()
}
