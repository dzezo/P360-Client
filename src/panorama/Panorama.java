package panorama;

import org.lwjgl.util.vector.Vector3f;

import models.Body;
import models.Cylinder;
import models.Sphere;

public class Panorama extends Texture {	
	private Vector3f translation = new Vector3f(0,0,0);
	private Vector3f rotation = new Vector3f(0,0,0);
	private Vector3f scale = new Vector3f(1,1,1);
	
	private Body[] parts = new Body[partsCount];
	
	public Panorama() {
		super();
		
		float imageAspect = (float) width / height;
		
		if(imageAspect == 2.0f)
			for(int i = 0; i < partsCount; i++)
				parts[i] = new Sphere(width, i, partsCount, textureID[i]);
		else
			for(int i = 0; i < 2; i++)
				parts[i] = new Cylinder(width, height, i, partsCount, textureID[i]);
	}
	
	public void cleanUp() {
		for(Body part : parts)
			part.cleanUp();
	}
	
	public Body[] getParts() {
		return parts;
	}
	
	public float getRadius() {
		return parts[0].getRadius();
	}
	
	public int getType() {
		return parts[0].getType();
	}
	
	public Vector3f getTranslation() {
		return translation;
	}

	public Vector3f getRotation() {
		return rotation;
	}

	public Vector3f getScale() {
		return scale;
	}
}
