/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

#version 110

uniform mat4 viewMatrix;
uniform mat4 projMatrix;
uniform vec4 lightPosition;

attribute vec4 in_position;
attribute vec4 in_color;

varying vec4 Color;

void main()
{
    Color = in_color;
    
    gl_Position = projMatrix * viewMatrix * in_position ;
}