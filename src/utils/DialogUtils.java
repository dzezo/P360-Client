package utils;

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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DialogUtils {
	public static int YES = JOptionPane.YES_OPTION;
	public static int NO = JOptionPane.NO_OPTION;
	
	public static void showMessage(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int showConfirmDialog(String msg, String title) {
		return JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION);
	}
	
	/**
	 * Returns true if user wants to replace path, and vice versa
	 */
	public static boolean replacePathDialog(String path) {
		int dialogRes = showConfirmDialog("Could not find: " + path + "\nDo you want to change the path?", "Path Not Found");
		if(dialogRes == YES) {
			return true;
		}
		else {
			DialogUtils.showMessage("Could not find: " + path + "\nLoading is aborted.", "Loading Aborted");
			return false;
		}
	}
	
	/**
	 * Otvara dijalog za unos kljuca za enkripciju/dekripciju MAPE
	 * @param imagePath - putanja do slike
	 * @return uneti kljuc
	 */
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
	 * @return
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
