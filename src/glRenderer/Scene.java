package glRenderer;

import gui.GuiNavButtons;
import panorama.PanNode;
import panorama.Panorama;
import utils.ConfigData;

public class Scene {
	private static PanNode activePanorama;
	private static PanNode queuedPanorama;
	private static Camera camera;
	private static boolean ready = false;
	
	public static void loadNewActivePanorama(PanNode newImage) {
		activePanorama = newImage;
		activePanorama.loadPanorama();
		
		Renderer.requestNewProjection();		
		GuiNavButtons.setAvailableNavButtons(activePanorama);
		
		queuedPanorama = null;
		ready = true;
	}
	
	public static void unloadActivePanorama() {
		if(activePanorama != null) 
			activePanorama.unloadPanorama();
	}
	
	/**
	 * Getters and Setters
	 */	
	public static boolean isReady() {
		return ready;
	}
	
	public static void setReady(boolean b) {
		ready = b;
	}
	
	public static Panorama getPanorama() {
		return activePanorama.getPanorama();
	}
	
	public static Camera getCamera() {
		return camera;
	}
	
	public static void setCamera(Camera cam) {
		camera = cam;
	}
	
	public static PanNode getActivePanorama() {
		return activePanorama;
	}
	
	public static void queuePanorama(PanNode panorama) {
		// Queue image for loading
		queuedPanorama = panorama;
		
		// Loading started
		// Stop displaying active panorama
		ready = false;
	}
	
	public static void dequeuePanorama() {
		// Loading canceled
		if(activePanorama != null) {
			// Display active panorama if exists
			queuePanorama(activePanorama);
		}
		else {
			// Wait for another input
			queuedPanorama = null;
			ready = false;
		}
		
	}
	
	public static PanNode getQueuedPanorama() {
		return queuedPanorama;
	}
	
	public static boolean changeRequested() {
		return queuedPanorama != null;
	}
	
	// Interfejs za navigaciju dugmicima
	
	/**
	 * Skace na odabranu susednu panoramu.
	 * Strana na koju korisnik zapravo zeli da skoci se odredjuje na osnovu pozicije kamere i selektovane strane.
	 * @param selectedSide - strana na koju korisnik zeli da skoci (0-3)
	 * <br><b>0</b> - gore
	 * <br><b>1</b> - desno
	 * <br><b>2</b> - dole
	 * <br><b>3</b> - levo
	 */
	public static void goSide(int selectedSide) {
		int actualSide;
		
		if(ConfigData.getFixGUIFlag()) {
			actualSide = selectedSide;
		}
		else {
			int numOfSides = 4;
			float camAngle = camera.getYaw();
			float refAngle = 315.0f;
			
			actualSide = numOfSides;
			while(!(camAngle>refAngle) && refAngle>0) {
				actualSide--;
				refAngle -= 90;
			}
			actualSide = (selectedSide + actualSide) % numOfSides;
		}
		
		switch(actualSide) {
		case 0:
			goTop();
			break;
		case 1:
			goRight();
			break;
		case 2:
			goBot();
			break;
		case 3:
			goLeft();
			break;
		default:
			break;
		}
		
	}
	
	public static void goLeft() {
		if(activePanorama.getLeft() !=null) {
			queuePanorama(activePanorama.getLeft());
		}
	}
	
	public static void goRight() {
		if(activePanorama.getRight() !=null) {
			queuePanorama(activePanorama.getRight());
		}
	}
	
	public static void goTop() {
		if(activePanorama.getTop() !=null) {
			queuePanorama(activePanorama.getTop());
		}
	}
	
	public static void goBot() {
		if(activePanorama.getBot() !=null) {
			queuePanorama(activePanorama.getBot());
		}
	}
	
}