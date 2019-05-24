package gui;

import utils.Loader;

public class GuiQuad {
	private int vaoID;
	private int vertexCount;
	
	public GuiQuad() {
		float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1};
		vaoID = Loader.loadToVAO(positions);
		vertexCount = positions.length / 2;
	}
	
	public int getVaoID() {
		return this.vaoID;
	}
	
	public int getVertexCount() {
		return this.vertexCount;
	}
}
