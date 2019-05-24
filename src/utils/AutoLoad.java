package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.SwingWorker;

import glRenderer.Scene;
import input.InputManager;
import main.Main;
import panorama.PanGraph;
import touring.TourManager;

@SuppressWarnings("serial")
public class AutoLoad implements Serializable {
	private static final String AUTO_LOAD_CONFIG_PATH = Main.WORKING_DIR.getPath() + "\\auto_load.cfg";
	
	private static String lastUsedMap;
	
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
					if(TourManager.hasPath())
						InputManager.requestFullscreen();
				}
				
				// loading complete
				return null;
			}
		};
		
		// Load map
		load.execute();
	}
	
	private static boolean loadMapFile() {
		// get file path of the last map
		try {
			FileInputStream fin = new FileInputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectInputStream ois = new ObjectInputStream(fin);
			lastUsedMap = (String) ois.readObject();
			ois.close();
		} catch (Exception e) {
			System.out.println("Config file not found!");
			return false;
		}
		
		// load last used map
		return PanGraph.loadMap(lastUsedMap);
	}
	
	public static void setLastUsedMap(String filePath) {
		// update path of the last map
		lastUsedMap = new String(filePath);
		
		// update config file
		try {
			FileOutputStream fs = new FileOutputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			oos.writeObject(lastUsedMap);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
}
