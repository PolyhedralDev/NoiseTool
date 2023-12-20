/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.util;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLProfile;


public class GLUtil {

    public static void logGLProfiles() {
        GLProfile glp = GLProfile.getDefault();
        final GLCapabilitiesImmutable glcaps = new GLCapabilities(glp);
        final GLCapabilities tGLCapabilities = new GLCapabilities(glp);

        System.out.println("Default GL Profile Details: " + glp.toString());
        System.out.println("System Capabilities:" + glcaps);
    }
}
