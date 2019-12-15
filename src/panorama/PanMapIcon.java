package panorama;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import loaders.IconLoader;

public class PanMapIcon implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private PanMap parent;
	
	private transient ImageIcon icon;
	private boolean isLoaded;
	
	private transient boolean loadRequested = false;
	
	public PanMapIcon(PanMap parent) {
		this.parent = parent;
		this.icon = null;
		this.isLoaded = false;
	}
	
	public void drawIcon(Graphics2D g) {
		int dx1 = parent.x + 1;
		int dy1 = parent.y + 1;
		int dx2 = parent.x + PanMap.WIDTH;
		int dy2 = parent.y + PanMap.HEIGHT;
		g.drawImage(icon.getImage(), dx1, dy1, dx2, dy2, 0, 0, icon.getIconWidth(), icon.getIconHeight(), null);
	}
	
	/**
	 * Zahteva ucitavanje ikonice. Ukoliko je zahtev podnet izlazi odmah, 
	 * u suprotnom dodaje objekat u red cekanja za ucitavanje
	 */
	public synchronized void loadIcon() {
		if(loadRequested)
			return;
		
		loadRequested = true;
		IconLoader.getInstance().add(this);
	}
	
	/**
	 * Koristi se prilikom ucitavanja mape, 
	 * gde se iz ucitanih bajtova ikonice rekonstruise ikonica ukoliko je loaded flag postavljen
	 * @param data - bajtovi ikonice
	 */
	public void init(byte[] data) {
		if(isLoaded)
			icon = new ImageIcon(data);
	}
	
	/**
	 * Dobavlja bajtove ikonice tako sto pretvara ikonicu u BufferedImage,
	 * zatim ovaj BufferedImage pretvara u niz bajtova
	 * @return bajtovi ikonice ukoliko je ucitana,
	 * ukoliko nije ucitana ili je doslo do greske onda se vraca jedan random bajt koji se kasnije ignorise.
	 */
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
	
	/**
	 * Pretvara sliku ikonice u BufferedImage objekat
	 * @return (BufferedImage) ikonice
	 */
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
	
	/**
	 * Getter za loaded flag
	 * @return Vraca true ukoliko je ikonica ucitana
	 */
	public boolean isLoaded() {
		return isLoaded;
	}
	
	/**
	 * Getter za roditeljski cvor
	 * @return Vraca handle na cvor mape za koji je ova ikonica vezana
	 */
	public PanMap getParent() {
		return parent;
	}

	/**
	 * Postavlja flag da je ikonica ucitana
	 */
	public void setLoadedFlag() {
		isLoaded = true;
	}
	
	/**
	 * Postavlja ikonicu
	 * @param icon - ikonica
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}
}
