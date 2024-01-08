/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.opengl.GL2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Shader {

    private final int shaderId;

    public Shader(GL2 gl, String resourcePath, ShaderType type) throws IOException {
        // Load the shader source from resources
        String shaderSource = loadShaderProgramFromResource(resourcePath);
        // Define the shaper type from the enum
        int shaderType = type == ShaderType.VertexShader ? GL2.GL_VERTEX_SHADER : GL2.GL_FRAGMENT_SHADER;
        // Create the shader id
        shaderId = gl.glCreateShader(shaderType);
        // Link the id and the source
        gl.glShaderSource(shaderId, 1, new String[]{ shaderSource }, null);
        // Compile the shader
        gl.glCompileShader(shaderId);

        // Print out the log
        String log = getShaderInfoLog(gl);
        if(log.equals("")) {
            System.out.println("Compiled shader \"" + resourcePath + "\" successfully");
        } else {
            System.out.println("Problems compiling shader \"" + resourcePath + "\", printing log");
            System.out.println(log);
        }

    }

    private String loadShaderProgramFromResource(String resourcePath) throws IOException {
        System.out.println("Loading shader program from resource " + resourcePath);
        InputStream stream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        // Get text from file, line per line
        StringBuilder strBuilder = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            strBuilder.append(line + "\n");
            line = reader.readLine();
        }
        // Close stream
        reader.close();

        return strBuilder.toString();
    }

    public String getShaderInfoLog(GL2 gl) {
        // Get the shader log length
        final int logLen = getShaderLogLength(gl);
        if(logLen <= 0)
            return "";

        // If there is content in the log, get and return it
        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetShaderInfoLog(shaderId, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return String.format("ShaderLog: %s", logMessage);
    }

    private int getShaderLogLength(GL2 gl) {
        final int[] params = new int[1];
        gl.glGetShaderiv(shaderId, GL2.GL_INFO_LOG_LENGTH, params, 0);
        return params[0];
    }

    public int getShaderId() {
        return shaderId;
    }

    public void decommissionShader(GL2 gl) {
        gl.glDeleteShader(shaderId);
    }

    public enum ShaderType {
        VertexShader,
        FragmentShader
    }
}
