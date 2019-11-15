package input;

import javax.swing.JCheckBoxMenuItem;

@SuppressWarnings("serial")
public class ControllerItem extends JCheckBoxMenuItem {
	private Controller controller;
	
	public ControllerItem(Controller controller) {
		this.controller = controller;
		this.setText(controller.getName());
	}
	
	public Controller getController() {
		return this.controller;
	}
}
