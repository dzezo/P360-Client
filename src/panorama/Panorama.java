package panorama;

import org.lwjgl.util.vector.Vector3f;

import models.Body;
import models.Cylinder;
import models.Sphere;

public class Panorama extends Texture {
	/* BodyType */
	public static final int TYPE_CYLINDRICAL = 0;
	public static final int TYPE_SPHERICAL = 1;

	private Body body;
	private int type;
	
	/* World transform */
	private Vector3f translation = new Vector3f(0,0,0);
	private Vector3f rotation = new Vector3f(0,0,0);
	private Vector3f scale = new Vector3f(1,1,1);
	
	public Panorama() {
		super();
		
		float imageAspect = (float) width / (float) height;
		if(imageAspect == 2) {
			body = new Sphere(width);
			type = TYPE_SPHERICAL;
		}
		else {
			body = new Cylinder(width, height);
			type = TYPE_CYLINDRICAL;
		}
	}
	
	public Body getBody() {
		return body;
	}
	
	public int getType() {
		return type;
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
