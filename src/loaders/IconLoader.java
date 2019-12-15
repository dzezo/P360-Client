package loaders;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.ImageIcon;

import panorama.PanMap;
import panorama.PanMapIcon;
import utils.ImageCipher;

public class IconLoader extends Loader {
	private static volatile IconLoader instance = null;
	
	private Queue<PanMapIcon> loadingQueue = new LinkedList<>();
	
	private boolean postpone = false;
	
	private IconLoader() {
		this.setName("PanMapIcon loader");
		this.start();
	}
	
	public static synchronized IconLoader getInstance() {
		if(instance == null)
			instance = new IconLoader();
		return instance;
	}
	
	/**
	 * Dodaje ikonicu u red za ucitavanje i deblokira nit.
	 * @param icon - Ikonica koju treba ucitati.
	 */
	public void add(PanMapIcon icon) {
		synchronized(LOAD_LOCK) {
			loadingQueue.add(icon);
			LOAD_LOCK.notify();
		}
	}
	
	/**
	 * Funkcija u kojoj se vrsi ucitavanje ikonica
	 */
	public void run() {
		while(keepRunning()) {
			// Ceka se na zahtev za ucitavanje
			synchronized(LOAD_LOCK) {
				try {
					LOAD_LOCK.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}
			}
			
			// Zauzima se semafor za ucitavanje
			try {
				SYNC_LOCK.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
			
			// Ikonice se ne ucitavaju kada nema ikonica u redu za ucitavanje.
			// Kada je odlozeno ucitavanje (zato sto mapa nije vidljiva).
			// Kada je podnet zahtev za terminaciju niti.
			while(!loadingQueue.isEmpty() && keepLoading() && keepRunning()) {
				try {
					// Deque PanMapIcon
					PanMapIcon icon = loadingQueue.remove();
					String iconPath = icon.getParent().getParent().getPanoramaPath();
					
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
			        	icon.setIcon(new ImageIcon(image.getScaledInstance(PanMap.WIDTH, PanMap.HEIGHT, Image.SCALE_DEFAULT)));
			        	icon.setLoadedFlag();
			        	// free memory
			        	image.flush();
			           	image = null;
			           	System.gc();
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			// Sesija ucitavanja je zavrsena oslobadja se semafor
			SYNC_LOCK.release();
		}
	}
	
	/**
	 * Odlaze ucitavanje ikonica
	 * @param pause
	 * <br> true - Zavrsava ucitavanje i odlaze sledeca ucitavanja.
	 * <br> false - Deblokira nit i postavlja flag da je ucitavanje dozvoljeno.
	 */
	public void postponeLoading(boolean postpone) {
		if(postpone) {
			this.postpone = true;
		}
		else {
			synchronized(LOAD_LOCK) {
				this.postpone = false;
				LOAD_LOCK.notify();
			}
		}
	}
	
	/**
	 * @return 
	 * <br> <b>True</b> ukoliko flag za odlaganje ucitavanja nije postavljen.
	 * <br> <b>False</b> ukoliko je podnet zahtev za odlaganje ucitavanja.
	 */
	private boolean keepLoading() {
		return postpone == false;
	}
	
}