package gui;

import loaders.VertexLoader;

public class GuiQuad {
	private int vaoID;
	private int vertexCount;
	
	public GuiQuad() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
		vaoID = VertexLoader.loadToVAO(positions);
		vertexCount = positions.length / 2;
	}
	
	public int getVaoID() {
		return this.vaoID;
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
}
