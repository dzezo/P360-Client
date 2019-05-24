package panorama;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import glRenderer.Scene;

@SuppressWarnings("serial")
public class PanNode implements Serializable {
	// id cvora
	private int ID = Integer.MAX_VALUE;
	
	// susedi cvora
	private PanNode top;
	private PanNode right;
	private PanNode bot;
	private PanNode left;
	private PanNode next;
	
	// graficka reprezentacija cvora na mapi
	private PanMap mapNode;
	
	// slika i putanja do slike
	private transient Panorama panorama;
	private String panoramaPath;
	
	// pozicije cvora na putanji
	public List<Integer> tourNum = new ArrayList<Integer>();
	public transient boolean visited = false;
	
	// audio zapis
	private PanAudio audio;
	
	/**
	 * Konstruktor
	 * 
	 * @param panoramaPath - file system putanja
	 * @param x, y - koordinate na mapi
	 */
	public PanNode(String panoramaPath, int x, int y) {
		this.panoramaPath = panoramaPath;		
		mapNode = new PanMap(this, x, y);
	}
	
	/**
	 * Provera da li je panorama ucitana
	 * @return true ukoliko jeste
	 */
	public boolean isLoaded() {
		if(panorama == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Ucitava panoramu ukoliko ona nije ucitana
	 */
	public void loadPanorama() {
		if(!isLoaded())
			panorama = new Panorama();
	}
	
	/**
	 * Proverava da li je cvor, pocetni cvor mape
	 */
	public boolean isHome() {
		if (PanGraph.getHome().equals(this)) 
			return true;
		else
			return false;
	}
	
	/**
	 * Proverava da li se ovaj cvor trenutno prikazuje
	 */
	public boolean isActive() {
		PanNode activePano = Scene.getActivePanorama();
		if(activePano != null && activePano.equals(this))
			return true;
		else
			return false;
	}
	
	/**
	 * Proverava da li su cvorovi konektovani
	 * @param node - drugi cvor
	 * @return true - kada postoji konekcija izmedju cvorova
	 */
	public boolean isConnectedTo(PanNode node) {
		if(node == null) return false;
		
		if(node.equals(top) 
				|| node.equals(bot)
				|| node.equals(left)
				|| node.equals(right)) 
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	
	/* audio kontrola */
	
	public void playAudio() {
		this.audio.play();
	}
	
	public void pauseAudio() {
		this.audio.pause();
	}
	
	public void stopAudio() {
		this.audio.stop();
	}
	
	public void setAudioPath(String newAudioPath) {
		audio.setAudioPath(newAudioPath);
	}
	
	public String getAudioPath() {
		if(audio != null)
			return audio.getAudioPath();
		return null;
	}
	
	public boolean hasAudio() {
		if(audio == null)
			return false;
		return true;
	}
	
	public boolean isAudioPlaying() {
		if(audio != null)
			return audio.isPlaying();
		return false;
	}
	
	/* geteri i seteri */
	
	public int getID() {
		return this.ID;
	}
	
	public void setID(int i) {
		this.ID = i;
	}

	public PanMap getMapNode() {
		return mapNode;
	}
	
	/* panoramska slika */
	
	public Panorama getPanorama() {
		return panorama;
	}
	
	public void setPanorama(Panorama panorama) {
		this.panorama = panorama;
	}
	
	public String getPanoramaPath() {
		return panoramaPath;
	}
	
	public void setPanoramaPath(String panoramaPath) {
		this.panoramaPath = panoramaPath;
		mapNode.panName = mapNode.setNameFromPath(panoramaPath);
	}
	
	/* susedi cvora */
	
	public PanNode getNext() {
		return next;
	}
	
	public void setNext(PanNode next) {
		this.next = next;
	}
	
	public PanNode getLeft() {
		return left;
	}
	
	public void setLeft(PanNode left) {
		this.left = left;
	}
	
	public PanNode getRight() {
		return right;
	}
	
	public void setRight(PanNode right) {
		this.right = right;
	}
	
	public PanNode getTop() {
		return top;
	}
	
	public void setTop(PanNode top) {
		this.top = top;
	}
	
	public PanNode getBot() {
		return bot;
	}
	
	public void setBot(PanNode bot) {
		this.bot = bot;
	}

	/* audio */
	
	public PanAudio getAudio() {
		return this.audio;
	}
	
	public void setAudio(String audioPath) {
		if(audioPath != null)
			this.audio = new PanAudio(audioPath);
		else
			this.audio = null;
	}
	
	public void addAudio(String audioPath) {
		// set audio
		setAudio(audioPath);
		
		// set graphic representation
		mapNode.audioName = mapNode.setNameFromPath(audioPath);
	}
	
	public void removeAudio() {
		// stop audio
		if(isAudioPlaying())
			stopAudio();
		
		// remove audio	
		setAudio(null);
		
		// remove graphic representation
		mapNode.audioName = null;
	}
	
}