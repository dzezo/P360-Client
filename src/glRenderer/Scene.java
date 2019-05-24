package glRenderer;

import gui.GuiNavButtons;
import panorama.PanNode;
import panorama.Panorama;

public class Scene {
	private static PanNode activePanorama;
	private static PanNode queuedPanorama;
	private static Camera camera;
	private static boolean ready;
	
	public static void loadNewImage(PanNode newImage) {
		// reset ready flag
		ready = false;
		
		// prepare renderer
		initScene(newImage);
		
		// set available nav buttons
		GuiNavButtons.setAvailableNavButtons(activePanorama);
		
		// set ready flag
		ready = true;
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
		
		// If loaded, there is no need to stop the scene
		if(queuedPanorama.isLoaded()) return;
		
		// Loading started
		// Stop displaying active panorama
		ready = false;
	}
	
	public static void dequeuePanorama() {
		queuedPanorama = activePanorama;
		
		// Loading canceled
		// Start displaying active panorama
		ready = true;
	}
	
	public static PanNode getQueuedPanorama() {
		return queuedPanorama;
	}
	
	/**
	 * Interfejs za navigaciju dugmicima
	 */
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
	
	/**
	 * Ucitava aktivnu panoramu na scenu
	 */
	public static void initScene(PanNode newImage) {
		// set image to load
		activePanorama = newImage;
		
		activePanorama.loadPanorama();
		Renderer.setNewProjection();
	}
	
}