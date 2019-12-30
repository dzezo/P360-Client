package gui;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import shaders.GuiShader;

public class GuiRenderer {
	private static final Vector3f X_AXIS = new Vector3f(1, 0, 0);
	private static final Vector3f Y_AXIS = new Vector3f(0, 1, 0);
	private static final Vector3f Z_AXIS = new Vector3f(0, 0, 1);
	
	private static final GuiQuad quad = new GuiQuad();
	private static List<GuiTexture> guis = new ArrayList<GuiTexture>();
	
	private static Matrix4f transformationMatrix = new Matrix4f();
	private static Vector3f transformationScale = new Vector3f();
	
	public static void render(GuiShader shader) {
		shader.start();
		glBindVertexArray(quad.getVaoID());
		glEnableVertexAttribArray(0);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		// RENDERING
		for(GuiTexture gui : guis) {
			createTransformationMatrix(gui.getPosition(), gui.getRotation(), gui.getScale());
			shader.loadTransformation(transformationMatrix);
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, gui.getTexture());
			glDrawArrays(GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
		glDisableVertexAttribArray(0);
		shader.stop();
	}
	
	public static List<GuiTexture> getGuiList(){
		return guis;
	}
	
	private static void createTransformationMatrix(Vector2f translation, Vector3f rotation, Vector2f scale) {
		transformationMatrix.setIdentity();
		Matrix4f.translate(translation, transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.x), X_AXIS, transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.y), Y_AXIS, transformationMatrix, transformationMatrix);
		Matrix4f.rotate((float) Math.toRadians(rotation.z), Z_AXIS, transformationMatrix, transformationMatrix);
		transformationScale.set(scale.getX(), scale.getY(), 1.0f);
		Matrix4f.scale(transformationScale, transformationMatrix, transformationMatrix);
	}
}
