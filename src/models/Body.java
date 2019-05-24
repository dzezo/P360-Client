package models;

public class Body {
	protected int vaoID;
	protected int vertexCount;
	protected float radius;
	
	protected float posCoords[];
	protected float texCoords[];
	protected int indices[];
	
	public int getVaoID() {
		return vaoID;
	}

	public int getVertexCount() {
		return vertexCount;
	}
	
	public float getRadius() {
		return radius;
	}
}
