package utils;

import javax.swing.SwingWorker;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import panorama.PanGraph;
import touring.TourManager;

public class AutoLoad {
	
	public static void load() {
		// Defining background thread for map loading
		SwingWorker<Void, Void> load = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				// Load previously used map
				if(loadMapFile()) {
					// Set scene if loading is a success
					Scene.queuePanorama(PanGraph.getHome());
					TourManager.prepare(PanGraph.getHome());
					
					// Set fullscreen if path exists
					if(TourManager.hasPath() && ConfigData.getPanFlag())
						DisplayManager.requestFullScreen();
				}
				
				// loading complete
				return null;
			}
		};
		
		// Load map
		load.execute();
	}
	
	private static boolean loadMapFile() {
		if(ConfigData.loadConfigFile())
			return PanGraph.loadMap(ConfigData.getLastUsedMap());
		else
			return false;
	}
	
}
