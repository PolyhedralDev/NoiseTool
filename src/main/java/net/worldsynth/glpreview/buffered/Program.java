/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.opengl.GL2;


public class Program {

    private int programId;

    private Shader vertexShader;
    private Shader geometryShader = null;
    private Shader fragmentShader;

    public Program(GL2 gl, Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;

        // generate the id of the program
        programId = gl.glCreateProgram();

        // attach the two shader
        gl.glAttachShader(programId, vertexShader.getShaderId());
        gl.glAttachShader(programId, fragmentShader.getShaderId());

        // link them
        gl.glLinkProgram(programId);

        //Print out the log
        String log = getProgramInfoLog(gl);
        if(log.equals("")) {
            System.out.println("Compiled program \"" + programId + "\" successfully");
        } else {
            System.out.println("Problems with program \"" + programId + "\", printing log");
            System.out.println(log);
        }
    }

    public String getProgramInfoLog(GL2 gl) {
        //Get the program log length
        final int logLen = getProgramLogLength(gl);
        if(logLen <= 0)
            return "";

        //If there is content in the log, get and return it
        final int[] retLength = new int[1];
        final byte[] bytes = new byte[logLen + 1];
        gl.glGetProgramInfoLog(programId, logLen, retLength, 0, bytes, 0);
        final String logMessage = new String(bytes);

        return String.format("ProgramLog: %s", logMessage);
    }

    private int getProgramLogLength(GL2 gl) {
        final int[] params = new int[1];
        gl.glGetProgramiv(programId, GL2.GL_INFO_LOG_LENGTH, params, 0);
        return params[0];
    }

    public int getProgramId() {
        return programId;
    }

    public int glGetAttribLocation(GL2 gl, String attribute) {
        return gl.glGetAttribLocation(getProgramId(), attribute);
    }

    public Shader getVertexShader() {
        return vertexShader;
    }

    public Shader getGeometryShader() {
        return geometryShader;
    }

    public Shader getFragmentShader() {
        return fragmentShader;
    }

    public void decommissionProgram(GL2 gl) {
        gl.glDetachShader(programId, vertexShader.getShaderId());
        vertexShader.decommissionShader(gl);
        vertexShader = null;

        if(geometryShader != null) {
            gl.glDetachShader(programId, geometryShader.getShaderId());
            geometryShader.decommissionShader(gl);
            geometryShader = null;
        }

        gl.glDetachShader(programId, fragmentShader.getShaderId());
        fragmentShader.decommissionShader(gl);
        fragmentShader = null;

        gl.glDeleteProgram(programId);
        programId = 0;
    }
}
