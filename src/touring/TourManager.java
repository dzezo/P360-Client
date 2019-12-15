package touring;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import glRenderer.AudioManager;
import glRenderer.Scene;
import panorama.PanGraph;
import panorama.PanNode;
import utils.ConfigData;

public class TourManager implements Runnable{
	private static ScheduledThreadPoolExecutor tour = new ScheduledThreadPoolExecutor(1);
	private static ScheduledFuture<?> tourTasks;
	
	private static PanNode[] path;
	private static boolean hasPath;
	private static int pathLocation;
	
	private static long timeOfChange;
	private static long visitedPanTime = 3000;
	
	private static boolean touring = false;
	
	/**
	 * Postavlja stanje TourManager-a
	 * Ukoliko postoji putanja na mapi pokrece se tura, u suprotnom se zaustavlja
	 * @param startNode je polazni cvor putanje
	 */
	public static void prepare(PanNode startNode) {
		if(PanGraph.hasTour()) {
			TourManager.init(PanGraph.getTour(), startNode);
		}
		else {
			TourManager.stopTourManager();
		}
	}
	
	/**
	 * Funkcija se poziva ukoliko ucitana/izmenjena mapa poseduje putanju.
	 * Ukoliko se startni cvor nalazi na putanji, putanja je validna, otvara se nova nit koja izvrsava
	 * run metodu ove klase.
	 * @param p je putanja
	 * @param start je polazni cvor na putanji
	 */
	public static void init(PanNode[] p, PanNode start) {
		path = new PanNode[p.length];
		
		pathLocation = -1;
		for(int i = 0; i < p.length; i++) {
			// panorama na putanji odgovara startnom cvoru i lokacija startnog cvora nije nadjena
			if(start.equals(p[i]) && pathLocation == -1) {
				// postavi indeks odakle pocinje putanja
				pathLocation = i;
			}
			
			// dodaj panorame na putanju
			path[i] = p[i];
			path[i].visited = false;
		}
		
		// ukoliko postoji pocetak putanje, pokreni turu
		if(pathLocation != -1){
			hasPath = true;
			
			// resetuj audio menadzer
			AudioManager.resetAudioPlayed();
			
			// resetuj kameru pre pocetka ture
			Scene.getCamera().resetTripMeter();
			timeOfChange = System.currentTimeMillis();
			
			// pokreni turua
			tourTasks = tour.scheduleAtFixedRate(new TourManager(), 0, 50, TimeUnit.MILLISECONDS);
		}
		else {
			hasPath = false;
		}
	}
	
	/**
	 * Funkcija koja ispituje da li su zadovoljeni uslovi za prelazak na sledecu panoramu
	 */
	public void run() {
		// ukoliko je automatsko paniranje iskljuceno 
		// ili se nije postavila nova panorama na scenu
		if(!Scene.isReady() || !Scene.getCamera().isAutoPanning()) {
			// sacekaj
			touring = false;
			return;
		}
		
		touring = true;
		
		PanNode activePano = Scene.getActivePanorama();
		
		// da li je tokom ture doslo do rucne promene panorame
		if(!path[pathLocation].equals(activePano)) {
			// nadji gde se na putanji nalazi nova panorama
			for(int i = 0; i < path.length; i++) {
				if(activePano.equals(path[i])) {
					pathLocation = i;
					break;
				}
			}
			
			// resetuj audio menadzer
			AudioManager.resetAudioPlayed();
			
			// resetuj kameru
			Scene.getCamera().resetTripMeter();
			timeOfChange = System.currentTimeMillis();
			
			return;
		}
		
		// da li je panorama vec posecena, ukoliko jeste ispitaj uslov za prelaz na sledecu
		if(ConfigData.getSkipFlag() && activePano.visited) {
			// na sledecu panoramu se prelazi ukoliko je isteklo dozvoljeno vreme za vec posecenu panoramu
			long currentTime = System.currentTimeMillis();
			if((currentTime - timeOfChange) > visitedPanTime) {
				goNextPano();
			}
			
			return;
		}
		
		// ukoliko je kamera napravila pun krug i naracija je zavrsena
		if(Scene.getCamera().cycleComplete() && !activePano.isAudioPlaying()) {
			// oznaci da je panorama posecena i predji na sledecu
			activePano.visited = true;
			
			goNextPano();
		}
	}
	
	/**
	 * @return je true ukoliko mapa sadrzi validnu putanju, a putanja je validna ukoliko se na njoj nalazi
	 * startni (home) cvor
	 */
	public static boolean hasPath() {
		return hasPath;
	}
	
	/**
	 * Prelazi na sledeci cvor na putanji
	 */
	public static void goNextPano() {
		pathLocation = (pathLocation + 1)%path.length;
		PanNode nextPano = path[pathLocation];
		
		// preskoci poslednju panoramu ukoliko je ista kao i prva
		if(pathLocation == path.length - 1 
				&& path[0].equals(path[pathLocation])) 
		{
			pathLocation ^= pathLocation;
			nextPano = path[pathLocation];
		}
		
		// resetuj visited flag ukoliko se ciklus zavrsio
		if(pathLocation == 0) {
			int i = 0;
			while(i != path.length) {
				path[i++].visited = false;
			}
		}
		
		// predji na sledeci cvor
		Scene.queuePanorama(nextPano);
		
		// resetuj audio menadzer
		AudioManager.resetAudioPlayed();
		
		// resetuj kameru
		Scene.getCamera().resetTripMeter();
		timeOfChange = System.currentTimeMillis();
	}
	
	/**
	 * @return true ukoliko menadzer obilazi putanju
	 */
	public static boolean isTouring() {
		return touring;
	}
	
	public static void stopTourManager() {
		path = null;
		hasPath = false;
		if(tourTasks != null)
			tourTasks.cancel(false);
	}
	
}
