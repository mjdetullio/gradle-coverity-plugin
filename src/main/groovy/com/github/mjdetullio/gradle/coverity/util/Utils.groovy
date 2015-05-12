package com.github.mjdetullio.gradle.coverity.util

/**
 * Utility class containing static methods.
 *
 * @author Matthew DeTullio
 */
class Utils {
    /**
     * Returns the path to an executable given a home directory and file name.
     *
     * @param homeDir the executable's home directory
     * @param executableFileName the executable's file name
     * @return "${homeDir}/bin/${executableFileName}" if homeDir is not null or
     *         empty, otherwise <code>executableFileName</code>
     */
    static String getExePath(String homeDir, String executableFileName) {
        if (homeDir) {
            return "${homeDir}/bin/${executableFileName}"
        }

        return executableFileName
    }
}
