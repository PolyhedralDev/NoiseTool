/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

#version 110

uniform float lightIntensity;

varying vec4 Color;
varying vec4 Normal;
varying vec4 Light;

void main()
{
    gl_FragColor = Color * max(dot(normalize(Normal), normalize(Light)), 0.2) * lightIntensity;
}