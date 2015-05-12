package com.github.mjdetullio.gradle.coverity.model

/**
 * The base extension that contains settings to be applied to all child projects
 * and will be inherited by {@link CoverityRootExtension}.
 *
 * @author Matthew DeTullio
 */
class CoverityExtension {
    /**
     * The name that should be given when creating this extension.
     */
    static String EXTENSION_NAME = 'coverity'

    /**
     * Specifies if the current project's child projects should be included.
     * If true, child projects will be included even if the current project is
     * skipped.
     * <p/>
     * Default value is true.
     */
    boolean includeChildProjects = true

    /**
     * Specifies if the current project should be skipped.  Does not also skip
     * child projects (this is defined by {@link #includeChildProjects}).
     * <p/>
     * Default value is false.
     */
    boolean skip = false
}
