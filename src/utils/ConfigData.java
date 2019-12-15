package utils;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import frames.MainFrame;

public class ConfigData {
	
	public static final File WORKING_DIR = new File("C:\\p360system");
	public static final String AUTO_LOAD_CONFIG_PATH = WORKING_DIR.getPath() + "\\auto_load.cfg";
	public static final String AUTO_SAVE_DEFAULT_FILE_PATH = WORKING_DIR.getPath() + "\\AutoSave.pmap";
	
	private static String lastUsedMap;
	private static boolean panFlag = true;
	private static boolean skipFlag = true;
	private static boolean fixGUIFlag = true;
	
	public static void setLastUsedMap(String filePath) {
		// update path of the last map
		lastUsedMap = new String(filePath);
	}
	
	public static String getLastUsedMap() {
		return lastUsedMap;
	}
	
	public static void setPanFlag() {
		panFlag = !panFlag;
		MainFrame.getInstance().view_autoPan.setSelected(panFlag);
	}
	
	public static boolean getPanFlag() {
		return panFlag;
	}
	
	public static void setSkipFlag() {
		skipFlag = !skipFlag;
		MainFrame.getInstance().view_skipVisited.setSelected(skipFlag);;
	}
	
	public static boolean getSkipFlag() {
		return skipFlag;
	}
	
	public static void setFixGUIFlag() {
		fixGUIFlag = !fixGUIFlag;
		MainFrame.getInstance().view_fixGUI.setSelected(fixGUIFlag);
	}
	
	public static boolean getFixGUIFlag() {
		return fixGUIFlag;
	}
	
	/**
	 * Loads configuration file
	 * @return true if loading is successful
	 */
	public static boolean loadConfigFile() {
		try {
			FileInputStream fin = new FileInputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectInputStream ois = new ObjectInputStream(fin);
			
			// reading config data
			lastUsedMap = (String) ois.readObject();
			panFlag = (boolean) ois.readObject();
			skipFlag = (boolean) ois.readObject();
			fixGUIFlag = (boolean) ois.readObject();
			
			// updating menu items
			MainFrame.getInstance().view_autoPan.setSelected(panFlag);
			MainFrame.getInstance().view_skipVisited.setSelected(skipFlag);
			MainFrame.getInstance().view_fixGUI.setSelected(fixGUIFlag);
			
			ois.close();
		} catch (EOFException e) {
			e.printStackTrace();	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		if(lastUsedMap == null || lastUsedMap.isEmpty())
			return false;
		return true;
	}
	
	/**
	 * Saves configuration file
	 */
	public static void updateConfigFile() {
		try {
			FileOutputStream fs = new FileOutputStream(AUTO_LOAD_CONFIG_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			
			// writing config data
			oos.writeObject(lastUsedMap);
			oos.writeObject(panFlag);
			oos.writeObject(skipFlag);
			oos.writeObject(fixGUIFlag);
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		System.out.println("Config file updated.");
	}
	
}
