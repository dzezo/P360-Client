package input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;

import org.lwjgl.LWJGLException;

public class ControllerScanner extends Thread {
	
	private JMenu controllersMenu;
	
	private boolean doStop = false;
	private final Object scanRequest = new Object();
	
	public ControllerScanner(JMenu controllersMenu) {
		this.setName("Controller Scanner");
		this.controllersMenu = controllersMenu;
	}
	
	public void doStop() {
		doStop = true;
	}
	
	public void requestScan() {
		synchronized(scanRequest) {
			scanRequest.notify();
		}
	}
	
	private boolean keepRunning() {
		return doStop == false;
	}
	
	public void run() {
		while(keepRunning()) {
			try {
				synchronized(scanRequest) {
					scanRequest.wait();
				}
				
				// Rescan hardware
				Controllers.destroy();
				Controllers.create();
				
				updateControllerMenu();
			} catch (LWJGLException | InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		
		System.out.println("ControllerScanner has stoped.");
	}
	
	private void updateControllerMenu() {
		// Clear prev controllers
		for(int i=controllersMenu.getItemCount()-1; i>1; i--)
			controllersMenu.remove(i);
		
		// Add controllers to menu
		for(int i=0; i<Controllers.getControllerCount(); i++) {
			// Creating menu item
			ControllerItem ci = new ControllerItem(Controllers.getController(i));
			
			// Creating menu item action
			ci.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					setController(ci);
				}
				
			});
			
			// Adding controllers
			controllersMenu.add(ci);
		}
	}
	
	private void setController(ControllerItem ci) {
		Controller activeController = InputManager.getController();
		
		if(activeController == null) {
			InputManager.setController(ci);
		}
		else {
			if(activeController.equals(ci.getController())) {
				//nullify
				InputManager.changeController(null);
			}
			else {
				//replace
				InputManager.changeController(ci);
			}
		}
	}
}
