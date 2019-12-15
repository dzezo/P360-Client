package panorama;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import loaders.ImageLoader;
import utils.BuffUtils;
public class Texture {
	protected static final int partsCount = 2;
	
	protected int[] textureID = new int[partsCount];
	protected int width, height;
	
	public Texture() {
		width = ImageLoader.getInstance().getImage().getWidth();
		height = ImageLoader.getInstance().getImage().getHeight();
		
		int texW = width / partsCount;
		int texH = height;
		for(int i = 0; i < partsCount; i++) {
			textureID[i] = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureID[i]);
			
			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texW, texH, 0, GL_BGRA, GL_UNSIGNED_BYTE, 
					BuffUtils.storeInIntBuffer(ImageLoader.getInstance().getImageData(i, 0, partsCount, 1)));
			
			glBindTexture(GL_TEXTURE_2D, 0);
		}
		
		ImageLoader.getInstance().clearImage();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
