package glRenderer;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import models.Body;
import static models.Body.*;

import shaders.StaticShader;

public class Renderer {
	private static final float HFOV_CAP = 80f;
	private static final float VFOV_CAP = 70f;
	private static final float NEAR_PLANE = 0.1f;
	private static final float FAR_PLANE = 500000f;
	
	private static boolean projectionRequestFlag = false;
	
	public static void requestNewProjection() {
		projectionRequestFlag = true;
	}
	
	private static boolean newProjectionRequested() {
		if(projectionRequestFlag) {
			projectionRequestFlag = false;
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void prepare() {
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glClearColor(0, 0, 0, 1);
	}
	
	public static void render(StaticShader shader) {
		shader.start();
		shader.loadViewMatrix(createViewMatrix());
		if (DisplayManager.wasResized() || newProjectionRequested()) {
			DisplayManager.confirmResize();
			shader.loadProjectionMatrix(createProjectionMatrix());
		}
		shader.loadTransformationMatrix(createTransformationMatrix());
		
		glActiveTexture(GL_TEXTURE0);
		for(Body part : Scene.getPanorama().getParts()) {
			glBindVertexArray(part.getVaoID());
			glEnableVertexAttribArray(0);
			glEnableVertexAttribArray(1);
			
			glBindTexture(GL_TEXTURE_2D, part.getTexID());
			glDrawElements(part.getPrimitiveType(), part.getIndicesCount(), GL_UNSIGNED_INT, 0);
			
			glDisableVertexAttribArray(0);
			glDisableVertexAttribArray(1);
			glBindVertexArray(0);
		}
				
		shader.stop();
	}

	private static Matrix4f createProjectionMatrix() {	
		float height = (float) Scene.getPanorama().getHeight();
		float radius = (float) Scene.getPanorama().getRadius();
		double angle = height/(2*radius);
		
		// Calculating aspect
		float aspectRatio;
		if(DisplayManager.isFullscreen()) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			aspectRatio = (float) screenSize.getWidth() / (float) screenSize.getHeight();
		}
		else {
			aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		}
		
		float vFOV_MAXIMUM = (float) (2*Math.toDegrees(Math.atan(angle)));
		float hFOV = (float) Math.toDegrees(2f*Math.atan(Math.tan(Math.toRadians(vFOV_MAXIMUM)/2) * aspectRatio));
		if(hFOV > HFOV_CAP) 
			hFOV = HFOV_CAP;
		float vFOV = (float) Math.toDegrees(2f*Math.atan(Math.tan(Math.toRadians(hFOV)/2) / aspectRatio));
		if(vFOV > VFOV_CAP) 
			vFOV = VFOV_CAP;
		
		// Camera
		int panType = Scene.getPanorama().getType();
		Scene.getCamera().setPitch(0);
		if(panType == TYPE_CYLINDRICAL)
			Scene.getCamera().setPitchLimit((vFOV_MAXIMUM - vFOV)/2);
		else
			Scene.getCamera().setPitchLimit(90);
		
		float x_scale = (float)(1f/Math.tan(Math.toRadians(hFOV)/2));
		float y_scale = (float)(1f/Math.tan(Math.toRadians(vFOV)/2));
		float frustrum_length = FAR_PLANE - NEAR_PLANE;
		
		Matrix4f projectionMatrix = new Matrix4f();
		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustrum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2*NEAR_PLANE*FAR_PLANE) / frustrum_length);
		projectionMatrix.m33 = 0;
		return projectionMatrix;
	}

	private static Matrix4f createTransformationMatrix() {
		Matrix4f transformationMatrix = new Matrix4f();
		transformationMatrix.setIdentity();
		Matrix4f.translate(Scene.getPanorama().getTranslation(), transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(Scene.getPanorama().getRotation().x), new Vector3f(1,0,0), transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(Scene.getPanorama().getRotation().y), new Vector3f(0,1,0), transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(Scene.getPanorama().getRotation().z), new Vector3f(0,0,1), transformationMatrix, transformationMatrix);
		Matrix4f.scale(Scene.getPanorama().getScale(), transformationMatrix, transformationMatrix);
		return transformationMatrix;
	}
	
	private static Matrix4f createViewMatrix() {
		Matrix4f viewMatrix = new Matrix4f();
		viewMatrix.setIdentity();
		Matrix4f.rotate((float) Math.toRadians(Scene.getCamera().getPitch()), new Vector3f(1,0,0), viewMatrix, viewMatrix);
		Matrix4f.rotate((float) Math.toRadians(Scene.getCamera().getYaw()), new Vector3f(0,1,0), viewMatrix, viewMatrix);
		Vector3f cameraPos = Scene.getCamera().getPosition();
		Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
		Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
		return viewMatrix;
	}
}
