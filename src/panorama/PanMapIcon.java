package panorama;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import utils.ImageCipher;

public class PanMapIcon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private transient ExecutorService iconLoaderThread;
	
	private PanMap parent;
	
	private transient ImageIcon icon;
	private boolean isLoaded;
	
	private transient boolean reloadRequested = false;
	
	public PanMapIcon(PanMap parent) {
		this.parent = parent;
		this.icon = null;
		this.isLoaded = false;
		
		try {
			loadIcon(parent.getParent().getPanoramaPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	
	public void reloadIcon() {
		synchronized(this) {
			if(reloadRequested)
				return;
			
			reloadRequested = true;
		}
		
		try {
			loadIcon(parent.getParent().getPanoramaPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void loadIcon(String iconPath) throws Exception {
		iconLoaderThread = Executors.newFixedThreadPool(1);
		iconLoaderThread.execute(() -> {
			try {
				// Load icon
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
				
				// Request served
				reloadRequested = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// Terminate thread
			iconLoaderThread.shutdown();
		});
	}
	
	public void init(byte[] data) {
		if(isLoaded)
			icon = new ImageIcon(data);
	}
	
	public byte[] getByteArray() {
		if(!isLoaded)
			return new byte[1];
		
		try {
			BufferedImage icon = getBufferedImageOfIcon();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(icon, "jpg", baos);
			
			baos.flush();
			byte[] data = baos.toByteArray();
			baos.close();
			
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return new byte[1];
		}
	}
	
	private BufferedImage getBufferedImageOfIcon() {
		// Get image from icon
		Image icon = this.icon.getImage();
		
		// Create a buffered image with transparency
	    BufferedImage bimage = new BufferedImage(icon.getWidth(null), icon.getHeight(null), BufferedImage.TYPE_INT_ARGB);

	    // Draw the image on to the buffered image
	    Graphics2D bGr = bimage.createGraphics();
	    bGr.drawImage(icon, 0, 0, null);
	    bGr.dispose();

	    // Return the buffered image
	    return bimage;
	}
}
