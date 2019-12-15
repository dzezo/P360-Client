package models;

import static org.lwjgl.opengl.GL11.*;
import utils.Loader;

public class Cylinder extends Body {
	private static final int primitiveType = GL_TRIANGLE_STRIP;
	private static final int sectorCount = 180;
	private static final int indicesCount = 2*(sectorCount + 1);
	
	public Cylinder(int width, int height, int sliceX, int slicesX, int textureID) {
		radius = (float) (width / (2 * Math.PI));
		
		posCoords = new float[indicesCount*3];
		texCoords = new float[indicesCount*2];
		indices = new int[indicesCount];
		
		double sliceXSize = 2.0 * Math.PI / slicesX;
		double sliceXOffset = sliceXSize * sliceX;
		
		double sectorStep = sliceXSize / sectorCount;
		double sectorAngle;
		
		float x, y, z;
		float s, t;
		
		int vertexIndex = 0;
		for(int i = 0; i <= sectorCount; i++) {
			sectorAngle = i * sectorStep + sliceXOffset;
			
			x = (float) (radius * Math.cos(sectorAngle));
			z = (float) (radius * Math.sin(sectorAngle));
			y = (float) height/2;
			posCoords[3*vertexIndex] = x;
			posCoords[3*vertexIndex + 1] = y;
			posCoords[3*vertexIndex + 2] = z;
			
			s = (float) i / sectorCount;
			t = 0;
			texCoords[2*vertexIndex] = s;
			texCoords[2*vertexIndex + 1] = t;
			
			indices[vertexIndex] = vertexIndex++;
			
			y = (float) -height/2;
			posCoords[3*vertexIndex] = x;
			posCoords[3*vertexIndex + 1] = y;
			posCoords[3*vertexIndex + 2] = z;
			
			t = 1;
			texCoords[2*vertexIndex] = s;
			texCoords[2*vertexIndex + 1] = t;
			
			indices[vertexIndex] = vertexIndex++;
		}
		
		vaoID = Loader.loadToVAO(posCoords, texCoords, indices, vbosID);
		texID = textureID;
	}

	public int getPrimitiveType() {
		return primitiveType;
	}

	public int getType() {
		return TYPE_CYLINDRICAL;
	}
}
