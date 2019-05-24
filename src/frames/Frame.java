package frames;

import javax.swing.*;

@SuppressWarnings("serial")
public abstract class Frame extends JFrame{
	
	public Frame(String title) {
		super(title);
	}
	
	public void cleanUp() {
		this.setVisible(false);
        this.dispose();
        System.out.println(this.getTitle() + " is disposed...");
	}
	
}
