package frames;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import glRenderer.AudioManager;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import input.ControllerScanner;
import input.InputManager;
import panorama.PanGraph;
import panorama.PanNode;
import touring.TourManager;
import utils.ChooserUtils;
import utils.DialogUtils;

@SuppressWarnings("serial")
public class MainFrame extends Frame {
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private Canvas displayCanvas = new Canvas();
	private boolean running = false;
	
	/* MenuBar */
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenu viewMenu = new JMenu("View");
	private JMenu soundMenu = new JMenu("Sound");
	private JMenu gamePadMenu = new JMenu("Gamepad");
	
	/* fileMenu items */
	private JMenuItem file_openImage = new JMenuItem("Open Image");
	private JMenuItem file_openMap = new JMenuItem("Open Map");
	/* viewMenu items */
	private JCheckBoxMenuItem view_autoPan = new JCheckBoxMenuItem("Auto Pan");
	private JCheckBoxMenuItem view_skipVisited = new JCheckBoxMenuItem("Skip Visited Panoramas");
	private JMenuItem view_showMap = new JMenuItem("Show Map");
	private JMenuItem view_fullScreen = new JMenuItem("Full Screen");
	/* soundMenu items */
	private JMenuItem sound_playPause = new JMenuItem("Play");
	private JMenuItem sound_stop = new JMenuItem("Stop");
	/* gamePadMenu items */
	private JMenuItem gamePad_scan = new JMenuItem("Scan");
	private ControllerScanner controllerScanner = new ControllerScanner(gamePadMenu);
	/* map gui */
	private static MapViewFrame mapView = new MapViewFrame("View Map");
	
	
	public MainFrame(String title) {
		super(title);
		// create gui
		createMenuBar();
		createFrame();
		
		// init audio manager
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
		executor.scheduleAtFixedRate(new AudioManager(this), 0, 50, TimeUnit.MILLISECONDS);
		
		// start ControllerScanner service
		controllerScanner.start();
		
		// create openGL display
		DisplayManager.createDisplay(displayCanvas);
		
		// ready
		running = true;
	}
	
	private void createFrame() {
		displayCanvas.setPreferredSize(new Dimension(DisplayManager.getWidth(), DisplayManager.getHeight()));
		displayCanvas.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				requestFocusInWindow();
			}
		});
		
		mainPanel.add(displayCanvas, BorderLayout.CENTER);
		
		this.add(mainPanel);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(null);
		
		// Frame Listener
		this.addWindowListener(new WindowAdapter() 
		{
            public void windowClosing(WindowEvent we){
            	// Stop ControllerScanner service
            	controllerScanner.doStop();
            	// Break main loop
            	running = false;
            	// Disposing mapView frame
            	mapView.cleanUp();
            	// Disposing self
                cleanUp();
            }
            public void windowActivated(WindowEvent we) {
            	// windowActivated is invoked when the Window is set to be the active Window.
            	// Such case is after exiting map creation mode.
            	displayCanvas.requestFocusInWindow();
            }
        });
	}
	
	private void createMenuBar() {
		// FILE
		fileMenu.add(file_openImage);
		fileMenu.add(file_openMap);
		
		// VIEW
		viewMenu.add(view_autoPan);
		viewMenu.add(view_skipVisited);
		viewMenu.addSeparator();
		viewMenu.add(view_showMap);
		viewMenu.addSeparator();
		viewMenu.add(view_fullScreen);
		
		view_skipVisited.setSelected(TourManager.getSkipVisited());
		view_autoPan.setSelected(true);
		
		// SOUND
		soundMenu.add(sound_playPause);
		soundMenu.add(sound_stop);
		
		enableSoundControl(false);
		
		// GAMEPAD
		gamePadMenu.add(gamePad_scan);
		gamePadMenu.addSeparator();
		
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		menuBar.add(soundMenu);
		menuBar.add(gamePadMenu);
		
		setJMenuBar(menuBar);
		
		/* Item listeners */
		file_openImage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { openImage(); }
		});	
		file_openMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) { loadMap(); }
		});
		view_showMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { showMap(); }
		});	
		view_fullScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { fullscreen(); }
		});
		view_autoPan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { autoPan(); }
		});
		view_skipVisited.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { skipVisited(); }
		});
		sound_playPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { soundPlayPause(); }
		});
		sound_stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { soundStop(); }
		});
		gamePad_scan.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent arg0) { scanForController(); }
		});
	}

	public boolean isRunning() {
		return running;
	}
	
	public MapViewFrame getMapViewFrame() {
		return mapView;
	}

	/* Menubar Actions */
	
	private void openImage(){
		// Map loaded, prompt overwrite
		if(!PanGraph.isEmpty()) {
			int dialogRes = DialogUtils.showConfirmDialog("This action will discard existing map, \nDo you want to continue?", "New Image");
			if(dialogRes != DialogUtils.YES) return;
		}
		
		String imagePath = ChooserUtils.openImageDialog();
		if(imagePath == null) return;
		
		// Set new map flag
		PanGraph.removeMap();
		
		// Add to new map
		int spawnX, spawnY;
		spawnX = mapView.getMapPanel().getOriginX();
		spawnY = mapView.getMapPanel().getOriginY();
		PanGraph.addNode(imagePath, spawnX, spawnY);
		PanGraph.setName(PanGraph.DEFAULT_NAME);
		
		// Queue image for loading
		Scene.queuePanorama(PanGraph.getHome());
		TourManager.prepare(PanGraph.getHome());
	}
	
	private void loadMap() {
		boolean success = mapView.load();
		
		if(success) {
			Scene.queuePanorama(PanGraph.getHome());
			TourManager.prepare(PanGraph.getHome());
		}
	}
	
	private void showMap() {
		if(PanGraph.isEmpty()) return;
		
		mapView.showFrame();
	}
	
	private void fullscreen() {
		InputManager.requestFullscreen();
	}
	
	private void autoPan() {
		boolean set = Scene.getCamera().setAutoPan();
		view_autoPan.setSelected(set);
	}
	
	private void skipVisited() {
		// isSelected() returns state AFTER click
		boolean newState = view_skipVisited.isSelected();
		
		System.out.println(newState);
		
		TourManager.setSkipVisited(newState);
		view_skipVisited.setSelected(newState);
	}
	
	private void soundPlayPause() {
		PanNode pan = Scene.getActivePanorama();
		
		// is sound is playing -> pause it
		if(pan.isAudioPlaying()) {
			pan.pauseAudio();
		}
		// is sound paused -> play it
		else {
			pan.playAudio();
		}
	}
	
	private void soundStop() {
		Scene.getActivePanorama().stopAudio();
	}
	
	private void scanForController() {
		controllerScanner.requestScan();
	}

	/* Audio control */
	
	public void enableSoundControl(boolean status) {
		sound_playPause.setEnabled(status);
		sound_stop.setEnabled(status);
	}
	
	public void setPlayPauseText(boolean audioPlaying) {
		if(audioPlaying)
			sound_playPause.setText("Pause");
		else
			sound_playPause.setText("Play");
	}
	
	/* Map GUI */
	
	public static MapViewFrame getMap() {
		return mapView;
	}
	
	public static boolean isMapVisible() {
		return mapView.isVisible();
	}
}