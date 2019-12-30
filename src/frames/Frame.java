package frames;

import java.awt.Toolkit;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public abstract class Frame extends JFrame{
	
	public Frame(String title) {
		super(title);
	    this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("sprites/appIcon.png")));
	}
	
	public void cleanUp() {
		this.setVisible(false);
        this.dispose();
        System.out.println(this.getTitle() + " is disposed...");
	}
	
}
