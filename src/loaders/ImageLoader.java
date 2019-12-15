package loaders;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import gui.GuiSprites;
import sun.awt.image.codec.JPEGImageDecoderImpl;
import utils.ImageCipher;

public class ImageLoader extends Loader {
	private static volatile ImageLoader instance = null;
	
	// Putanja do slike koju treba ucitati
	private String imagePath;
	
	// Ucitana slika
	private BufferedImage image;
	
	// Flag-ovi
	private boolean isLoading = false;
	private boolean isLoaded = false;
	private boolean isCanceled = false;
	
	private ImageLoader() {
		this.setName("Image loader");
		this.start();
	}
	
	public static synchronized ImageLoader getInstance() {
		if(instance == null)
			instance = new ImageLoader();
		return instance;
	}
	
	/**
	 * Funkcija koja sluzi za zakazivanje ucitavanja slike.
	 * @param path - Putanja do slike koju treba ucitati
	 */
	public void loadImage(String path) {
		synchronized(LOAD_LOCK) {
			// Postavljanje parametara
			imagePath = new String(path);
			isLoading = true;
			isLoaded = false;
			// Prikazivanje znaka za ucitavanje
			GuiSprites.showLoadingSprite(true);
			// Podnosenje zahteva za ucitavanje
			LOAD_LOCK.notify();
		}
	}
	
	/**
	 * Funkcija koja vrsi ucitavanje slike
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
			
			// Ukoliko postoji zadata putanja, ucitava sliku sa nje
			if(imagePath != null) {
				try {
					image = loadBufferedImage(imagePath);
					// Ucitavanje uspesno
					imagePath = null;
					isLoaded = true;
				} catch (OutOfMemoryError | Exception e) {
					e.printStackTrace();
					if(image != null) 
						clearImage();
					// Ucitavanje neuspesno
					imagePath = null;
					isCanceled = true;
				}
			}
			
			// Ucitavanje je zavrseno oslobadja se semafor
			SYNC_LOCK.release();
		}
	}
	
	/**
	 * Pomocna funkcija koja ucitava sliku sa zadate putanje i pretvara je u BufferedImage objekat.
	 * @param path - Putanja do slike
	 * @return - Slika sa putanje
	 */
	private BufferedImage loadBufferedImage(String path) throws Exception {
		if(ImageCipher.isEncrypted(path))
			return new JPEGImageDecoderImpl(new ByteArrayInputStream(ImageCipher.imageDecrypt(path))).decodeAsBufferedImage();
		return new JPEGImageDecoderImpl(new FileInputStream(path)).decodeAsBufferedImage();
	}
	
	/**
	 * Vraca handle na ucitanu sliku.
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Funkcija deli sliku na sektore i vraca piksele iz zeljenog sektora.
	 * @param x		- redni broj sektora na X-osi <br><b>[0, secX)</b>
	 * @param y		- redni broj sektora na Y-osi <br><b>[0, secY)</b>
	 * @param secX 	- Na koliko se sektora deli po X-osi
	 * @param secY 	- Na koliko se sektora deli po Y-osi
	 * @return Vraca niz piksela po RGB color modelu
	 */
	public int[] getImageData(int x, int y, int secX, int secY) {
		int w = image.getWidth() / secX;	// isto je i za scanline
		int h = image.getHeight() / secY;
		
		return image.getRGB(x * w, y * h, w, h, null, 0, w);
	}
	
	/**
	 * Uklanja ucitanu sliku iz memorije
	 */
	public void clearImage() {
		image.flush();
		image = null;
		System.gc();
	}
	
	/**
	 * Resetuje sve flag-ove, i uklanja znak za ucitavanje
	 */
	public void resetLoader() {
		isLoading = false;
		isLoaded = false;
		isCanceled = false;
		// Skrivanje zanaka za ucitavanje
		GuiSprites.showLoadingSprite(false);
	}
	
	public boolean isLoading() {
		return isLoading;
	}
	
	public boolean isLoaded() {
		return isLoaded;
	}
	
	public boolean isCanceled() {
		// Show cancel sprite if loading is canceled
		if(isCanceled)
			GuiSprites.showCancelSprite(true);
		
		// Return cancel flag
		return isCanceled;
	}

}