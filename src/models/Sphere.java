package models;

import static org.lwjgl.opengl.GL11.*;

import loaders.VertexLoader;

public class Sphere extends Body {
	private static final int primitiveType = GL_TRIANGLES;
	private static final int sectorCount = 180;
	private static final int stackCount = 90;
	private static final int coordCount = (sectorCount + 1) * (stackCount + 1);
	private static final int indicesCount = 6*sectorCount*(stackCount - 1);
	
	public Sphere(int width, int sliceX, int slicesX, int textureID) {
		radius = (float) (width / (2 * Math.PI));
		
		posCoords = new float[coordCount*3];
		texCoords = new float[coordCount*2];
		indices = new int[indicesCount];
		
		double sliceXSize = 2.0 * Math.PI / slicesX;
		double sliceXOffset = sliceXSize * sliceX;
		
		double stackStep = Math.PI / stackCount;
		double sectorStep = sliceXSize / sectorCount;
		double stackAngle, sectorAngle;
		
		float x, y, z, zx;
		float s, t;
		
		int k = 0;
		
		for(int i = 0; i <= stackCount; i++) {
			stackAngle = Math.PI / 2 - i * stackStep;
			zx = (float) (radius * Math.cos(stackAngle));
			y = (float) (radius * Math.sin(stackAngle));
			
			for(int j = 0; j <= sectorCount; j++, k++) {
				sectorAngle = j * sectorStep + sliceXOffset;
				
				x = (float) (zx * Math.cos(sectorAngle));
				z = (float) (zx * Math.sin(sectorAngle));
				posCoords[3*k] = x;
				posCoords[3*k+1] = y;
				posCoords[3*k+2] = z;
				
				s = (float) j / sectorCount;
				t = (float) i / stackCount;	
				texCoords[2*k] = s;
				texCoords[2*k+1] = t;
			}
		}
		
		generateIndices();
		
		vaoID = VertexLoader.loadToVAO(posCoords, texCoords, indices, vbosID);
		texID = textureID;
	}
	
	private void generateIndices() {
		int k1, k2;
		int k = 0;
		
		for(int i = 0; i < stackCount; i++) {
			
			k1 = i * (sectorCount + 1);
			k2 = k1 + (sectorCount + 1);
			
			for(int j = 0; j < sectorCount; j++, k1++, k2++) {
				if(i != 0) {
					indices[k++] = k1;
					indices[k++] = k2;
					indices[k++] = k1 + 1;
				}
				if(i != (stackCount - 1)) {
					indices[k++] = k1 + 1;
					indices[k++] = k2;
					indices[k++] = k2 + 1;
				}
			}
		}
	}
	
	public int getPrimitiveType() {
		return primitiveType;
	}

	public int getType() {
		return TYPE_SPHERICAL;
	}
	
}
