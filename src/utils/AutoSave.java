package utils;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import main.Main;
import panorama.PanGraph;

public class AutoSave implements Runnable {
	private static final String DEFAULT_FILE_PATH = Main.WORKING_DIR.getPath() + "\\AutoSave.pmap";
	
	private static ScheduledThreadPoolExecutor autoSave = new ScheduledThreadPoolExecutor(1);
	private static ScheduledFuture<?> autoSaveTasks;
	
	private static String savingPath = new String();
	
	public void run() {
		save();
	}
	
	public static void setSavingPath(String path) {
		savingPath = new String(path);
	}
	
	public static void resetSavingPath() {
		savingPath = new String();
	}
	
	public static void save() {
		// nothing to save
		if(PanGraph.isEmpty()) return;
		
		// get save path
		String savePath = (!savingPath.isEmpty()) ? savingPath : DEFAULT_FILE_PATH;
		
		// save map
		PanGraph.saveMap(savePath);
	}
	
	public static void startSaving() {
		if(autoSaveTasks == null)
			autoSaveTasks = autoSave.scheduleAtFixedRate(new AutoSave(), 1, 1, TimeUnit.MINUTES);
	}
	
	public static void stopSaving() {
		if(autoSaveTasks != null) {
			autoSaveTasks.cancel(false);
			autoSaveTasks = null;
		}
	}
}
