package panorama;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
		
		textureID = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, 
				BuffUtils.storeInIntBuffer(img.getPixels()));

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
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
