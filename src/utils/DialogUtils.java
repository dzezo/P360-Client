package utils;

import static utils.ConfigData.WORKING_DIR;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class DialogUtils {
	public static final int YES = JOptionPane.YES_OPTION;
	public static final int NO = JOptionPane.NO_OPTION;
	
	/* chooser */
	private static JFileChooser jfc = new JFileChooser();
	
	/* chooser filters */
	private static FileFilter ff_image = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			fileName = fileName.toLowerCase();
			return (fileName.endsWith(".jpg") || fileName.endsWith(".pimg"));
		}
		public String getDescription() {
			return "*.jpg, *.pimg";
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
	private static FileFilter ff_video = new FileFilter() {
		public boolean accept(File file) {
			if(file.isDirectory())
				return true;
			String fileName = file.getName();
			return (fileName.endsWith(".avi") || fileName.endsWith(".mp4"));
		}
		public String getDescription() {
			return "*.avi, *.mp4";
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
				showMessage("File does not exist", "Load Map");
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
	
	/* chooser interaction */
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
	        		  showConfirmDialog("The file already exists. Do you want to overwrite it?", "Confirm Replace");
	          
	          // overwrite is refused
	          if(overwriteResult == NO)
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
		jfc.setAccessory(null);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		// open
		if(result == JFileChooser.APPROVE_OPTION) {
			String panPath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// check file type
			if(!(panPath.toLowerCase().endsWith(".jpg") || panPath.toLowerCase().endsWith(".pimg"))) 
			{
				// show error msg and leave
				showMessage("File type not supported", "Load Image");
				return null;
			}
			
			// check if file exists
			File file = new File(panPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				showMessage("File does not exist", "Load Image");
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
		jfc.setAccessory(null);
		
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
				if(!(image.getPath().toLowerCase().endsWith(".jpg") || image.getPath().toLowerCase().endsWith(".pimg"))) 
				{
					// show error msg and leave
					showMessage("File type not supported", "Load Image");
					return null;
				}
				
				// check if exists
				if(!image.exists()) {
					// show error msg and leave
					showMessage("File does not exist", "Load Image");
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
				showMessage("File does not exist", "Load Audio");
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
	
	public static String openVideoDialog() {
		// prep jfc
		jfc.setAcceptAllFileFilterUsed(false);
		jfc.setMultiSelectionEnabled(false);
		jfc.setFileFilter(ff_video);
		jfc.setAccessory(null);
		
		// show jfc
		int result = jfc.showOpenDialog(null);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			String loadPath = jfc.getSelectedFile().getPath();
			
			// reset selected file
			jfc.setSelectedFile(new File(""));
			
			// check file type
			if(!(loadPath.toLowerCase().endsWith(".avi") || loadPath.toLowerCase().endsWith(".mp4"))) 
			{
				// show error msg and leave
				showMessage("File type not supported", "Load Video");
				return null;
			}
			
			// check if file exists
			File file = new File(loadPath);
			
			// file does not exist
			if(!file.exists()) {
				// show error msg and leave
				showMessage("File does not exist", "Load Video");
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
	
	public static void setWorkingDir() {
		jfc.setCurrentDirectory(WORKING_DIR);
	}

	/* gui interaction */
	public static void showMessage(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int showConfirmDialog(String msg, String title) {
		return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);
	}
	
	public static boolean replacePathDialog(String path) {
		int dialogRes = showConfirmDialog("Could not find: " + path + "\nDo you want to change the path?", "Path Not Found");
		if(dialogRes == YES) {
			return true;
		}
		else {
			showMessage("Could not find: " + path + "\nLoading is aborted.", "Loading Aborted");
			return false;
		}
	}
	
	public static String showKeyInputDialog() {
		InputDialog inputDialog = new InputDialog();
		return inputDialog.showInputDialog(
				"Please enter encryption key <8-32>: ", 
				"Enter key");
	}
	
}

/**
 * Custom Input Dialog
 * @author Nikola Dzezo
 *
 */
@SuppressWarnings("serial")
class InputDialog extends JDialog {
	String input;
	
	JLabel messageLabel = new JLabel();
	JTextField inputField = new JTextField(32);
	JButton okButton = new JButton("OK");
	JButton cancelButton = new JButton("Cancel");
	
	public InputDialog() {
		super();
		
		/* Button init */
		
		Dimension buttonSize = new Dimension(70, 25);
		okButton.setPreferredSize(buttonSize);
		cancelButton.setPreferredSize(buttonSize);
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {	
				closeInputDialog(inputField.getText());
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeInputDialog();
			}
		});
		
		/* TextField init */
		
		/**
		 * On key ENTER close dialog with OK option,
		 * else check key char
		 */
		inputField.addKeyListener(new KeyAdapter() {
			 public void keyPressed(KeyEvent key) {
				 if(key.getKeyCode() == KeyEvent.VK_ENTER) {
					 closeInputDialog(inputField.getText());
				 }
				 else {
					 checkNewCharacter(key.getKeyChar());
				 }	 
			 }
		});
		
		/* Dialog init */
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		
		JPanel contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(5, 5, 0, 5);
		gc.gridy = 0;
		contentPanel.add(messageLabel, gc);
		
		gc.insets = new Insets(2, 5, 0, 5);
		gc.gridy++;
		contentPanel.add(inputField, gc);
		
		gc.anchor = GridBagConstraints.CENTER;
		gc.insets = new Insets(0, 5, 10, 5);
		gc.gridy++;
		contentPanel.add(buttonPanel, gc);
		
		JPanel mainPanel = new JPanel();
		mainPanel.add(contentPanel);
		
		this.add(mainPanel);
		this.setResizable(false);
		this.setModal(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		this.pack();
		this.setVisible(false);
		
		this.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
            	closeInputDialog();
            }
        });
	}
	
	/**
	 * Dialog modality allows this function to stop execution after dialog visibility is set to true,
	 * execution resumes after dialog window is disposed.
	 * @param message - dialog message
	 * @param title - dialog frame title
	 * @return input
	 */
	public String showInputDialog(String message, String title) {
		messageLabel.setText(message);
		
		this.setTitle(title);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		return input;
	}
	
	/**
	 * Checks validity of every new character in dialog text field
	 * @param ch - key stroke char
	 */
	private void checkNewCharacter(char ch) {
		String specialchar="!@#$%^&*()~?>'<:{}|+_/\".,;'][=-` \\";
		
        if (inputField.getText().trim().length() <= 32)
        {
            if (specialchar.indexOf(ch)>-1)
            {
                JOptionPane.showMessageDialog(
                		null, 
                		"Special characters are not allowed!", 
                		"Invalid key", 
                		JOptionPane.WARNING_MESSAGE);
                inputField.setText(inputField.getText().substring(0, inputField.getText().length()-1));
            }
        }
        else
        {
            JOptionPane.showMessageDialog(
            		null, 
            		"Key limit is reached!\nKey size is limited to 32 characters.", 
            		"Invalid key",
            		JOptionPane.WARNING_MESSAGE);
            inputField.setText(inputField.getText().substring(0, inputField.getText().length()-1));       
        }
	}
	
	/**
	 * Closes input dialog after OK option
	 * @param input - text field content
	 */
	private void closeInputDialog(String input) {
		if(input.trim().length() < 8) {
			JOptionPane.showMessageDialog(
					null, 
					"Key is too short!\nKey size needs to be at least 8 characters long.", 
					"Invalid key", 
					JOptionPane.WARNING_MESSAGE);
			
			// don't close yet
			return;
		}
		
		// close dialog
		this.input = input;
		this.dispose();
	}
	
	/**
	 * Closes input dialog after CANCEL option
	 */
	private void closeInputDialog() {
		this.input = null;	
		this.dispose();
	}
}
