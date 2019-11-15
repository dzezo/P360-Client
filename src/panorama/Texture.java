package panorama;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import utils.BuffUtils;
import utils.ImageData;
import utils.ImageLoader;

public class Texture {
	protected int textureID;
	protected int width, height;
	
	public Texture() {
		ImageData img = ImageLoader.getImageData();
		
		width = img.getWidth();
		height = img.getHeight();
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_BGRA, GL_UNSIGNED_BYTE, 
				BuffUtils.storeInIntBuffer(img.getPixels()));

		glBindTexture(GL_TEXTURE_2D, 0);
		
		ImageLoader.clearImageData();
	}

	public int getTextureID() {
		return textureID;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
