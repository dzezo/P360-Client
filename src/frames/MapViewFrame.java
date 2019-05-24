package frames;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import glRenderer.DisplayManager;
import input.InputManager;
import panorama.PanGraph;

@SuppressWarnings("serial")
public class MapViewFrame extends MapFrame {

	public MapViewFrame(String title) {
		super(title);
		// instantiate map panel
		mapPanel = new MapViewPanel();
		
		// create frame
		createFrame();
	}
	
	private void createFrame() {
		setSize(mapWidth, mapHeight);
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		
		// add map panel to frame
		mapPanel.setParent(this);
		add(mapPanel, BorderLayout.CENTER);
		
		setVisible(false);
		
		// Listeners	
		addWindowListener(new WindowAdapter() 
		{
			public void windowActivated(WindowEvent we) {
				// repaint frame every 20milis
				startFrameRepaint();
			}
			
            public void windowClosing(WindowEvent we){
            	// hide frame
            	hideFrame();
            }
        });
	}
	
	public void showFrame() {
		// show frame
		setVisible(true);
		setTitle(PanGraph.getName());
		
		// set origin of a map
		setOrigin();
	}
	
	public void hideFrame() {
		// stop frame repaint
        stopFrameRepaint();
        
        // deselect nodes
        mapPanel.deselectNodes();
        
    	// hide frame
        setVisible(false);
        
        // set fullscreen if necessary
        if(DisplayManager.returnToFullscreen()) {
        	InputManager.requestFullscreen();
        }
	}
}
