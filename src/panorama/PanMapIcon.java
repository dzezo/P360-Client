package panorama;

import java.awt.Graphics2D;
import java.awt.Image;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;

import utils.ImageCipher;

public class PanMapIcon {
	private ExecutorService iconLoaderThread = Executors.newFixedThreadPool(1);
	
	private PanMap parent;
	private ImageIcon icon;
	private boolean isLoaded;
	
	public PanMapIcon(PanMap parent) {
		this.parent = parent;
		this.icon = null;
		this.isLoaded = false;
		
		iconLoaderThread.execute(() -> {
			// Load icon
			try {
				loadIcon(parent.getParent().getPanoramaPath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Terminate thread
			iconLoaderThread.shutdown();
		});
	}
	
	public void drawIcon(Graphics2D g) {
		int dx1 = parent.x + 1;
		int dy1 = parent.y + 1;
		int dx2 = parent.x + PanMap.WIDTH;
		int dy2 = parent.y + PanMap.HEIGHT;
		g.drawImage(icon.getImage(), dx1, dy1, dx2, dy2, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	private void loadIcon(String iconPath) throws Exception {
		// load image
		Image image;
		// image is .pimg
		if(ImageCipher.isEncrypted(iconPath)) {
			image = new ImageIcon(ImageCipher.imageDecrypt(iconPath)).getImage();
		}
		// image is not encrypted
		else {
			image = new ImageIcon(iconPath).getImage();
		}
		
		// Create icon from image
        if (image != null) {
        	icon = new ImageIcon(image.getScaledInstance(PanMap.WIDTH, PanMap.HEIGHT, Image.SCALE_DEFAULT));
        	
        	// free memory
        	image.flush();
           	image = null;
            
            // load completed
            isLoaded = true;
        }
	}
}
