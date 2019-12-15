package utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Loader {
	private static List<Integer> vaos = new ArrayList<Integer>();
	private static List<Integer> vbos = new ArrayList<Integer>();
	private static List<Integer> textures = new ArrayList<Integer>();
	
	public static int loadToVAO(float[] positions, float[] textureCoords, int[] indices, int[] vbos) {
		int vaoID;
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		vbos[0] = bindIndicesBuffer(indices);
		vbos[1] = storeDataInAttributeList(0, 3, positions);
		vbos[2] = storeDataInAttributeList(1, 2, textureCoords);
		
		glBindVertexArray(0);
		
		return vaoID;
	}
	
	public static int loadToVAO(float[] positions) {
		int vaoID, vboID;
		
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);
		
		vboID = storeDataInAttributeList(0, 2, positions);
		
		glBindVertexArray(0);
		
		vaos.add(vaoID);
		vbos.add(vboID);
		return vaoID;
	}
	
	/**
	 * Koristi slickUtil lib za ucitavanje tekstura. Tekstura mora da bude POT i PNG.
	 * @param texturePath putanja do teksture
	 * @return textureID
	 */
	public static int loadTexture(String texturePath) {
		Texture texture = null;
		try {
			InputStream in = Class.class.getResourceAsStream(texturePath);
			texture = TextureLoader.getTexture("PNG", in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	
	public static void cleanUp() {
		for(int vao:vaos) {
			glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos) {
			glDeleteBuffers(vbo);
		}
		for(int texture:textures) {
			glDeleteTextures(texture);
		}
	}
	
	private static int storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
		int vboID = glGenBuffers();
		
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = BuffUtils.storeInFloatBuffer(data);
		glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		return vboID;
	}

	private static int bindIndicesBuffer(int[] indices) {
		int vboID = glGenBuffers();
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = BuffUtils.storeInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		
		return vboID;
	}
}
