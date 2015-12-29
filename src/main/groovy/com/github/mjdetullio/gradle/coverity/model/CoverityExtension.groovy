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
package com.github.mjdetullio.gradle.coverity.model

import org.gradle.api.Project

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

    /**
     * Specifies files that should be excluded from analysis by deleting them
     * from the emit DB using <code>cov-manage-emit</code>.
     * <p/>
     * Default value is <code>[project.buildDir]</code>.
     */
    Set<File> excludes

    /**
     * Instantiates a new instance of this extension.
     *
     * @param project project this extension is applied to, which is used to
     *                default the fields.
     */
    CoverityExtension(Project project) {
        excludes = [project.buildDir]
    }
}
