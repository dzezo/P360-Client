package utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import gui.GuiSprites;

public class ImageLoader {
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	
	private static ImageData img;
	private static boolean isLoading = false;
	private static boolean isLoaded = false;
	private static boolean isCanceled = false;
	
	public static void loadImage(String path) {
		// Set loader
		isLoading = true;
		isLoaded = false;
		
		// Show loading sprite
		GuiSprites.showLoadingSprite(true);
		
		// Execute loading
		executor.execute(new Runnable() {
			public void run() {
				BufferedImage image = null;
				int width, height;
				int[] pixels;
				
				try {
					image = loadBufferedImage(path);
					width = image.getWidth();
					height = image.getHeight();
					pixels = new int[width*height];
					image.getRGB(0, 0, width, height, pixels, 0, width);
					
					img = new ImageData(pixels, width, height);
					
					// loading completed
					isLoaded = true;
				} catch (OutOfMemoryError | Exception e) {
					e.printStackTrace();
					
					// release memory
					if(image != null) {
						image.flush();
						image = null;
						pixels = null;
					}
					
					// set cancel flag
					isCanceled = true;
				}
			}		
		});
	}
	
	private static BufferedImage loadBufferedImage(String path) throws Exception {
		// load image
		Image image;
		// image is .pimg
		if(ImageCipher.isEncrypted(path)) {
			image = new ImageIcon(ImageCipher.imageDecrypt(path)).getImage();
		}
		// image is not encrypted
		else {
			image = new ImageIcon(path).getImage();
		}
		
		// create bufferedImage
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bufferedImage.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
		
        // release memory
        image.flush();
        image = null;
        
        // return buffered image
		return bufferedImage;
	}
	
	public static ImageData getImageData() {
		return img;
	}
	
	public static void clearImageData() {
		img.clearImageData();
	}
	
	public static boolean isLoading() {
		return isLoading;
	}
	
	public static boolean isLoaded() {
		return isLoaded;
	}
	
	public static boolean isCanceled() {
		// Show cancel sprite if loading is canceled
		if(isCanceled)
			GuiSprites.showCancelSprite(true);
		
		// Return cancel flag
		return isCanceled;
	}
	
	public static void resetLoader() {
		// Reset loader
		img = null;
		isLoading = false;
		isLoaded = false;
		isCanceled = false;
		
		// Hide loading sprite
		GuiSprites.showLoadingSprite(false);
	}
}
