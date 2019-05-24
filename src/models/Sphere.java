package models;

import utils.Loader;

/*
 * Sphere
 * Iscrtavanje: TRIANGLE_STRIP
 * coordCount: broj jedinstvenih tacaka
 * vertexCount: broj tacaka
 * Ovo postoji zato sto se u svakoj iteraciji prave dve tacke gornja i donja, 
 * sto znaci da je gornja tacka u sledecoj iteraciji vec generisana.
 * Resenje:
 * 		newVertexIndex: pracenje indeksa novih tacaka
 * 		oldVertexIndex: pracenje indeksa tacka iz prethodnog reda
 */

public class Sphere extends Body {
	
	private static final float angleStep = 2.0f; // Must be divisible with 360
	private static final int coordCount = (int) ((360/angleStep + 1) * (180/angleStep + 1));
	
	public Sphere(int width) {
		vertexCount = 2*coordCount;
		radius = (float) (width/(2*Math.PI));
		
		posCoords = new float[coordCount*3];
		texCoords = new float[coordCount*2];
		indices = new int[vertexCount];
		
		int vertexCounter = 0;
		// indices array
		int newVertexIndex = 0;
		int oldVertexIndex = 1;
		
		float phi, theta;
		float x, y, z;
		float s,t;
		float tStep = angleStep/180;
		float sStep = angleStep/360;
		
		for (phi = 0, t = 0; phi < 180; phi += angleStep, t += tStep) {
			for (theta = 360, s = 0; theta >= 0; theta -= angleStep, s += sStep) {
				if(phi == 0) {
					z = (float) (Math.sin(phi / 180 * Math.PI)*Math.cos(theta / 180 * Math.PI));
					x = (float) (Math.sin(phi / 180 * Math.PI)*Math.sin(theta / 180 * Math.PI));
					y = (float) Math.cos(phi / 180 * Math.PI);
					
					posCoords[3*newVertexIndex] = x*radius;
					posCoords[3*newVertexIndex + 1] = y*radius;
					posCoords[3*newVertexIndex + 2] = z*radius;
					texCoords[2*newVertexIndex] = s;
					texCoords[2*newVertexIndex + 1] = t;
					indices[vertexCounter++] = newVertexIndex++;
				}
				else if (phi == angleStep && theta > 0) {
					indices[vertexCounter++] = oldVertexIndex;
					oldVertexIndex += 2;
				}
				else
					indices[vertexCounter++] = oldVertexIndex++;
				
				z = (float) (Math.sin((phi+angleStep) / 180 * Math.PI)*Math.cos(theta / 180 * Math.PI));
				x = (float) (Math.sin((phi+angleStep) / 180 * Math.PI)*Math.sin(theta / 180 * Math.PI));
				y = (float) Math.cos((phi+angleStep) / 180 * Math.PI);
				
				posCoords[3*newVertexIndex] = x*radius;
				posCoords[3*newVertexIndex + 1] = y*radius;
				posCoords[3*newVertexIndex + 2] = z*radius;
				texCoords[2*newVertexIndex] = s;
				texCoords[2*newVertexIndex + 1] = t + tStep;		
				indices[vertexCounter++] = newVertexIndex++;
			}
		}
		
		vaoID = Loader.loadToVAO(posCoords, texCoords, indices);
	}
}
