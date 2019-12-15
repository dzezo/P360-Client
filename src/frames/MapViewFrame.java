package frames;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import loaders.IconLoader;
import panorama.PanGraph;
import panorama.PanNode;
import utils.ChooserUtils;

@SuppressWarnings("serial")
public class MapViewFrame extends MapFrame {
	private static volatile MapViewFrame instance = null;
	
	public static synchronized MapViewFrame getInstance() {
		if(instance == null) {
			instance = new MapViewFrame();
		}
		
		return instance;
	}
	
	// ToolBar
	final private JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
	final private JButton b_center = new JButton(new ImageIcon(Class.class.getResource("/toolbar/center.png")));
	final private JToggleButton b_textMode = new JToggleButton(new ImageIcon(Class.class.getResource("/toolbar/text.png")));
	
	private MapViewFrame() {
		super("P360");
		// instantiate map panel
		mapPanel = new MapViewPanel();
		
		// create frame
		createToolBar();
		createFrame();
	}
	
	private void createToolBar() {
		// JButton ActionListeners
		b_center.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { centerMap(); }
		});
		b_textMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { setTextMode(); }
		});
		
		// The JToolBar uses a BoxLayout to layout it’s components.
		toolbar.add(b_center);
		toolbar.addSeparator();
		toolbar.add(b_textMode);
		
		// Disables toolbar from moving
		toolbar.setFloatable(false);
		getContentPane().add(toolbar, BorderLayout.WEST);
	}
	
	private void createFrame() {
		setSize(mapWidth, mapHeight);
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
		toFront();
		repaint();
		
		// set origin of a map
		setOrigin();
		
		// Unpause IconLoader
		IconLoader.getInstance().postponeLoading(false);
	}
	
	public void hideFrame() {
		// stop frame repaint
        stopFrameRepaint();
        
        // deselect nodes
        mapPanel.deselectNodes();
        
    	// hide frame
        setVisible(false);
        MainFrame.getInstance().setVisible(true);
        
        // get back to fullscreen mode
        if(DisplayManager.returnToFullScreenRequested())
        	DisplayManager.requestFullScreen();
        
        // Pause IconLoader
        IconLoader.getInstance().postponeLoading(true);
	}
	
	public boolean load() {
		// get path
		String loadPath = ChooserUtils.openMapDialog();
		if(loadPath == null) return false;
		
		// load
		boolean success = PanGraph.loadMap(loadPath);
		if(success) {
			// setting the origin of a map
			setOrigin();
			
			// display map source as title
			setTitle(loadPath);
		}
		
		return success;
	}
	
	/* Toolbar Actions */
	
	private void centerMap() {
		PanNode activePanorama = Scene.getActivePanorama();
		int cX = (int) activePanorama.getMapNode().getCenterX();
		int cY = (int) activePanorama.getMapNode().getCenterY();
		int width = mapPanel.getWidth() / 2;
		int height = mapPanel.getHeight() / 2;
		mapPanel.setOrigin(cX - width, cY - height);
	}
	
	private void setTextMode() {
		PanGraph.setTextMode(b_textMode.isSelected());
	}
	
}
