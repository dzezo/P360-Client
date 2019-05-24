package utils;

public class ImageData {
	private int[] pixels;
	private int width;
	private int height;
	
	public ImageData(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void clearImageData() {
		pixels = null;
	}
 }
