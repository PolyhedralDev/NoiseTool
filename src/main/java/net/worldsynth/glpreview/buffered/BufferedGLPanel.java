/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package net.worldsynth.glpreview.buffered;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import net.worldsynth.glpreview.Commons;
import net.worldsynth.glpreview.buffered.Shader.ShaderType;
import net.worldsynth.glpreview.model.AbstractModel;

import javax.swing.SwingUtilities;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;


public class BufferedGLPanel extends GLJPanel implements GLEventListener, MouseMotionListener, MouseListener, MouseWheelListener {
    private static final long serialVersionUID = -4627683443751384368L;

    // Shader programs
    private Program surfaceShaderProgram;
    private Program lineShaderProgram;
    private Program pointShaderProgram;

    // Storage for Matrices
    private float[] projMatrix = new float[16];
    private final float[] viewMatrix = new float[16];

    private float[] lightPos = { 500.0f, 500.0f, 500.0f };
    private final float lightIntensity = 1.5f;

    private final Loader loader = new Loader();
    private ArrayList<BufferedModel> currentBufferedModels = new ArrayList<BufferedModel>();
    private ArrayList<BufferedModel> newBufferedModels = new ArrayList<BufferedModel>();

    private final int NO_MODELS = 0;
    private final int NEW_MODELS_BUFFERING = 1;
    private final int NEW_MODELS_READY = 2;
    private final int MODELS_READY = 3;

    private int modelsState = NO_MODELS;

    private final ArrayList<AbstractModel<?>> modelsToBuffer = new ArrayList<AbstractModel<?>>();


    private float lastMouseX;
    private float lastMouseY;

    private float pitch = 120.0f;
    private float yaw = -90.0f;
    private float zoom = -4.0f;
    private float yLookatHeight = 0.0f;

    protected float minYLookatHeight = -256;
    protected float maxYLookatHeight = 256;

    private static GLCapabilities getGLCapabilities() {
        GLProfile glprofile = GLProfile.getDefault();
        GLCapabilities glcapabilities = new GLCapabilities(glprofile);
        glcapabilities.setSampleBuffers(true);
        glcapabilities.setNumSamples(8);
        return glcapabilities;
    }

    public BufferedGLPanel() {
        super(getGLCapabilities());

        addGLEventListener(this);

        addMouseMotionListener(this);
        addMouseListener(this);
        addMouseWheelListener(this);
    }

    protected void startNewModel() {
        modelsToBuffer.clear();
        modelsState = NEW_MODELS_BUFFERING;
    }

    protected void endNewModel() {
        modelsState = NEW_MODELS_READY;
    }

    protected void loadModel(AbstractModel<?> model) throws IllegalArgumentException {
        if(model.getPrimitivesCount() == 0) {
            throw new IllegalArgumentException("Model has no primitives, primitives array has lenght 0.");
        }

        modelsToBuffer.add(model);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        float ratio;
        // Prevent a divide by zero, when window is too short
        // (you can't make a window of zero width).
        if(height == 0)
            height = 1;

        ratio = (1.0f * width) / height;
        this.projMatrix = Commons.buildProjectionMatrix(53.13f, ratio, 1.0f, 3000.0f, this.projMatrix);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        // Setup everything that needs to be set up in the openGL window
        GL2 gl = drawable.getGL().getGL2();

        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //		gl.glEnable(GL2.GL_CULL_FACE);
        //		gl.glCullFace(GL2.GL_BACK);
        //		gl.glFrontFace(GL2.GL_CCW);

        // Setup shader programs
        // Create and compile the vertex and fragment shaders and attach them to a programs
        // Point shader program
        try {
            System.out.println("Creating point shader program");
            Shader pointVertexShader = new Shader(gl, "shaders/PointVertex.shader", ShaderType.VertexShader);
            Shader pointFragmentShader = new Shader(gl, "shaders/PointFragment.shader", ShaderType.FragmentShader);
            pointShaderProgram = new Program(gl, pointVertexShader, pointFragmentShader);
        } catch(IOException e) {
            System.out.println("An error occured while making the point shader program");
            e.printStackTrace();
        }

        // Line shader program
        try {
            System.out.println("Creating line shader program");
            Shader lineVertexShader = new Shader(gl, "shaders/LineVertex.shader", ShaderType.VertexShader);
            Shader lineFragmentShader = new Shader(gl, "shaders/LineFragment.shader", ShaderType.FragmentShader);
            lineShaderProgram = new Program(gl, lineVertexShader, lineFragmentShader);
        } catch(IOException e) {
            System.out.println("An error occured while making the line shader program");
            e.printStackTrace();
        }

        // Surface shader program
        try {
            System.out.println("Creating surface shader program");
            Shader surfaceVertexShader = new Shader(gl, "shaders/SurfaceVertex.shader", ShaderType.VertexShader);
            Shader surfaceFragmentShader = new Shader(gl, "shaders/SurfaceFragment.shader", ShaderType.FragmentShader);
            surfaceShaderProgram = new Program(gl, surfaceVertexShader, surfaceFragmentShader);
        } catch(IOException e) {
            System.out.println("An error occured while making the surface shader program");
            e.printStackTrace();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        loader.cleanUpAll(gl);
        currentBufferedModels = null;
        newBufferedModels = null;

        //Decommission shader programs
        surfaceShaderProgram.decommissionProgram(gl);
        surfaceShaderProgram = null;

        lineShaderProgram.decommissionProgram(gl);
        lineShaderProgram = null;

        pointShaderProgram.decommissionProgram(gl);
        pointShaderProgram = null;
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if(modelsState == NO_MODELS) {
            return;
        }

        GL2 gl = drawable.getGL().getGL2();

        // Load new models to buffer
        while(modelsToBuffer.size() > 0) {
            BufferedModel newBufferedModel = loader.loadModelToVAO(gl, modelsToBuffer.get(0), pointShaderProgram, lineShaderProgram,
                surfaceShaderProgram);
            modelsToBuffer.remove(0);
            newBufferedModels.add(newBufferedModel);
        }

        if(modelsState == NEW_MODELS_READY) {
            loader.cleanUpModels(gl, currentBufferedModels);
            currentBufferedModels = newBufferedModels;
            newBufferedModels = new ArrayList<BufferedModel>();
            modelsState = MODELS_READY;
        }


        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        float dist = 100.0f;
        float cameraPosX = (float) Math.sin(Math.toRadians(pitch)) * (float) Math.cos(Math.toRadians(yaw)) * zoom * dist;
        float cameraPosY = (float) Math.cos(Math.toRadians(pitch)) * zoom * dist + yLookatHeight;
        float cameraPosZ = (float) Math.sin(Math.toRadians(pitch)) * (float) Math.sin(Math.toRadians(yaw)) * zoom * dist;
        float lookPosX = 0;
        float lookPosY = yLookatHeight;
        float lookPosZ = 0;

        Commons.setCamera(cameraPosX, cameraPosY, cameraPosZ,
            lookPosX, lookPosY, lookPosZ, this.viewMatrix);

        for(BufferedModel model : currentBufferedModels) {
            // Set correct shader for the model
            Program shaderProgram = model.getShaderProgram();
            gl.glUseProgram(shaderProgram.getProgramId());

            // Get the location of the uniform variables
            int projMatrixLoc = gl.glGetUniformLocation(shaderProgram.getProgramId(), "projMatrix");
            int viewMatrixLoc = gl.glGetUniformLocation(shaderProgram.getProgramId(), "viewMatrix");
            int lightVectorLoc = gl.glGetUniformLocation(shaderProgram.getProgramId(), "lightPosition");
            int lightIntensityLoc = gl.glGetUniformLocation(shaderProgram.getProgramId(), "lightIntensity");

            // Set the view and the projection matrix
            gl.glUniformMatrix4fv(projMatrixLoc, 1, false, this.projMatrix, 0);
            gl.glUniformMatrix4fv(viewMatrixLoc, 1, false, this.viewMatrix, 0);
            gl.glUniform4f(lightVectorLoc, lightPos[0], lightPos[1], lightPos[2], 1.0f);
            gl.glUniform1f(lightIntensityLoc, lightIntensity);

            // Check out error
            int error = gl.glGetError();
            if(error != 0) {
                System.out.println("ERROR on render uniforms : " + error);
            }

            model.drawModel(gl);
        }

        // Check out error
        int error = gl.glGetError();
        if(error != 0) {
            System.out.println("ERROR on render : " + error);
        }
    }

    //-------------------------------------- Interaction events --------------------------------------//

    @Override
    public void mouseMoved(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float x = e.getX();
        float y = e.getY();

        float w = getWidth();
        float h = getHeight();

        if(SwingUtilities.isLeftMouseButton(e)) {
            yaw += (x - lastMouseX) / w * 180;
            pitch += (y - lastMouseY) / h * 45;

            if(pitch <= 0.0f) {
                pitch = 0.1f;
            } else if(pitch >= 179.9f) {
                pitch = 179.9f;
            }
            display();
        } else if(SwingUtilities.isMiddleMouseButton(e)) {
            yLookatHeight += (y - lastMouseY) * -zoom * 0.2f;
            yLookatHeight = Math.min(Math.max(yLookatHeight, minYLookatHeight), maxYLookatHeight);
            display();
        } else if(SwingUtilities.isRightMouseButton(e)) {
            float lightDist = 5000;
            float mouseDistFromCenter = (float) (Math.sqrt(Math.pow(w / 2 - x, 2) + Math.pow(h / 2 - y, 2)) / Math.sqrt(
                Math.pow(w / 2, 2) + Math.pow(h / 2, 2)));
            float mouseXDistDfromCenter = (x - w / 2) / (w / 2);
            float mouseYDistDfromCenter = (y - h / 2) / (h / 2);

            float lightX = (float) (lightDist * Math.asin(mouseXDistDfromCenter));
            float lightY = (float) (lightDist * Math.acos(mouseDistFromCenter));
            float lightZ = (float) (lightDist * Math.asin(mouseYDistDfromCenter));

            // Rotate light positon acording to yaw
            double yawRad = Math.toRadians(yaw + 90);
            double rotatedLightX = lightX * Math.cos(yawRad) - lightZ * Math.sin(yawRad);
            double rotatedLightZ = lightX * Math.sin(yawRad) + lightZ * Math.cos(yawRad);

            lightPos = new float[]{ (float) rotatedLightX, lightY, (float) rotatedLightZ };
            display();
        }

        lastMouseX = x;
        lastMouseY = y;
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(SwingUtilities.isMiddleMouseButton(e)) {
            yLookatHeight = 0;
            display();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float maxZoom = -0.2f;
        float minZoom = -20.0f;

        zoom -= (float) e.getWheelRotation() / 5;
        if(zoom < minZoom) zoom = minZoom;
        else if(zoom > maxZoom) zoom = maxZoom;
        display();
    }
}
