package models;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public abstract class Body {
	public static final int TYPE_CYLINDRICAL = 0;
	public static final int TYPE_SPHERICAL = 1;
	
	protected int vaoID;
	protected int[] vbosID = new int[3];
	protected int texID;
	
	protected float radius;
	protected float[] posCoords;
	protected float[] texCoords;
	protected int[] indices;
	
	public int getVaoID() {
		return vaoID;
	}
	
	public int getTexID() {
		return texID;
	}

	public int getIndicesCount() {
		return indices.length;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public void cleanUp() {
		glDeleteVertexArrays(vaoID);
		for(int vboID : vbosID) 
			glDeleteBuffers(vboID);
		glDeleteTextures(texID);
	}
	
	public abstract int getPrimitiveType();
	
	public abstract int getType();
}
