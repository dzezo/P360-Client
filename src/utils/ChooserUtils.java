package utils;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ChooserUtils {
	/* chooser */
	private static JFileChooser jfc = new JFileChooser();
	//private static ImagePreview preview = new ImagePreview(jfc);
	
	/* chooser filters */
	private static FileFilter ff_image = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			fileName = fileName.toLowerCase();
			return (fileName.endsWith(".jpg") 
					|| fileName.endsWith(".tif"))
					|| fileName.endsWith(".pimg");
		}
		public String getDescription() {
			return "*.jpg, *.tif, *.pimg";
		}
	};
	private static FileFilter ff_map = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			return (fileName.endsWith(".pmap"));
		}
		public String getDescription() {
			return "*.pmap";
		}
	};
	private static FileFilter ff_audio = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			fileName = fileName.toLowerCase();
			return (fileName.endsWith(".wav"));
		}
		public String getDescription() {
			return "*.wav";
		}
	};

	public static String openMapDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(ff_map);
		jfc.setAccessory(null);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String loadPath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// attach extension if there is not any
			if(!loadPath.endsWith(".pmap"))
				loadPath = loadPath.concat(".pmap");
			
			// check if file exists
			File file = new File(loadPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				DialogUtils.showMessage("File does not exist", "Load Map");
				return null;
			}
			// file exists
			else {
				// return path to file
				return loadPath;
			}
		}
		// opening canceled
		else {
			return null;
		}
		
	}
	
	public static String saveMapDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(ff_map);
		jfc.setAccessory(null);
		
		// show jfc
		int result = jfc.showSaveDialog(null);
		
		// determining saving path
		if(result == JFileChooser.APPROVE_OPTION) {
			String savePath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// attach extension if there is not any
			if(!savePath.endsWith(".pmap"))
				savePath = savePath.concat(".pmap");
			
			// check if file exists
			File saveFile = new File(savePath);
			
			// file exists
	        if (saveFile.exists()) {
	          int overwriteResult = 
	        		  DialogUtils.showConfirmDialog("The file already exists. Do you want to overwrite it?", "Confirm Replace");
	          
	          // overwrite is refused
	          if(overwriteResult == DialogUtils.NO)
	        	  return null;
	        }
	        
	        // path is free to use or overwrite path
	        return savePath;
		}
		// saving canceled
		else {
			return null;
		}
	}
	
	public static String openImageDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(ff_image);
		//jfc.setAccessory(preview);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		// open
		if(result == JFileChooser.APPROVE_OPTION) {
			String panPath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// check file type
			if(!(panPath.toLowerCase().endsWith(".jpg") 
					|| panPath.toLowerCase().endsWith(".tif")
					|| panPath.toLowerCase().endsWith(".pimg"))) 
			{
				// show error msg and leave
				DialogUtils.showMessage("File type not supported", "Load Image");
				return null;
			}
			
			// check if file exists
			File file = new File(panPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				DialogUtils.showMessage("File does not exist", "Load Image");
				return null;
			}
			// file exists
			else {
				// return path to file
				return panPath;
			}
		}
		// opening canceled;
		else {
			return null;
		}
	}
	
	public static File[] openImagesDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(true);
		jfc.setFileFilter(ff_image);
		//jfc.setAccessory(preview);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		// open
		if(result == JFileChooser.APPROVE_OPTION) {
			File images[] = jfc.getSelectedFiles();
			
			// reset selected files
			jfc.setSelectedFile(new File(""));
			
			// check selected images
			for(File image : images) {
				// check file type
				if(!(image.getPath().toLowerCase().endsWith(".jpg") 
						|| image.getPath().toLowerCase().endsWith(".tif")
						|| image.getPath().toLowerCase().endsWith(".pimg"))) 
				{
					// show error msg and leave
					DialogUtils.showMessage("File type not supported", "Load Image");
					return null;
				}
				
				// check if exists
				if(!image.exists()) {
					// show error msg and leave
					DialogUtils.showMessage("File does not exist", "Load Image");
					return null;
				}
			}
			
			return images;
		}
		// opening canceled;
		else {
			return null;
		}
	}
	
	public static String openAudioDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(ff_audio);
		jfc.setAccessory(null);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String loadPath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// attach extension if there is not any
			if(!loadPath.endsWith(".wav"))
				loadPath = loadPath.concat(".wav");
			
			// check if file exists
			File file = new File(loadPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				DialogUtils.showMessage("File does not exist", "Load Audio");
				return null;
			}
			// file exists
			else {
				// return path to file
				return loadPath;
			}
		}
		// opening canceled
		else {
			return null;
		}
		
	}
	
	public static void setWorkingDir(File workingDir) {
		jfc.setCurrentDirectory(workingDir);
	}
}
