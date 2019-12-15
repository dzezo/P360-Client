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
import utils.ConfigData;
import utils.DialogUtils;
import videoPlayer.VideoPlayer;

@SuppressWarnings("serial")
public class MainFrame extends Frame {
	private static volatile MainFrame instance = null;
	
	public static synchronized MainFrame getInstance() {
		if(instance == null) {
			instance = new MainFrame();
		}
		
		return instance;
	}
	
	private JPanel mainPanel = new JPanel(new BorderLayout());
	private Canvas displayCanvas = new Canvas();
	private boolean running = false;
	private boolean closing = false;
	
	/* Video player */
	private VideoPlayer videoPlayer = new VideoPlayer();
	
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
	public JCheckBoxMenuItem view_autoPan = new JCheckBoxMenuItem("Auto Pan");
	public JCheckBoxMenuItem view_skipVisited = new JCheckBoxMenuItem("Skip Visited Panoramas");
	public JCheckBoxMenuItem view_fixGUI = new JCheckBoxMenuItem("Fix Navigation Buttons");
	private JMenuItem view_showMap = new JMenuItem("Show Map");
	private JMenuItem view_fullScreen = new JMenuItem("Full Screen");
	/* soundMenu items */
	private JMenuItem sound_playPause = new JMenuItem("Play");
	private JMenuItem sound_stop = new JMenuItem("Stop");
	/* gamePadMenu items */
	private JMenuItem gamePad_scan = new JMenuItem("Scan");
	private ControllerScanner controllerScanner = new ControllerScanner(gamePadMenu);
	
	private MainFrame() {
		super("P360-Client");
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
            	if(closing) return;
            	
            	closing = true;
            	controllerScanner.doStop();
            	videoPlayer.cleanUp();
            	running = false;
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
		viewMenu.add(view_fixGUI);
		viewMenu.addSeparator();
		viewMenu.add(view_showMap);
		viewMenu.addSeparator();
		viewMenu.add(view_fullScreen);
		
		view_autoPan.setSelected(ConfigData.getPanFlag());
		view_skipVisited.setSelected(ConfigData.getSkipFlag());
		view_fixGUI.setSelected(ConfigData.getFixGUIFlag());
	
		
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
		view_fixGUI.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) { fixGUIButtons(); }
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
		spawnX = MapViewFrame.getInstance().getMapPanel().getOriginX();
		spawnY = MapViewFrame.getInstance().getMapPanel().getOriginY();
		PanGraph.addNode(imagePath, spawnX, spawnY);
		PanGraph.setName(PanGraph.DEFAULT_NAME);
		
		// Queue image for loading
		Scene.queuePanorama(PanGraph.getHome());
		TourManager.prepare(PanGraph.getHome());
	}
	
	private void loadMap() {
		boolean success = MapViewFrame.getInstance().load();
		
		if(success) {
			Scene.queuePanorama(PanGraph.getHome());
			TourManager.prepare(PanGraph.getHome());
		}
	}
	
	private void showMap() {
		if(PanGraph.isEmpty()) return;
		
		MapViewFrame.getInstance().showFrame();
	}
	
	private void fullscreen() {
		DisplayManager.requestFullScreen();
	}
	
	private void autoPan() {
		InputManager.setLastInteractTime(0);
		ConfigData.setPanFlag();
	}
	
	private void skipVisited() {		
		ConfigData.setSkipFlag();
	}
	
	private void fixGUIButtons() {
		ConfigData.setFixGUIFlag();
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

	/* Video player */
	
	public VideoPlayer getVideoPlayer() {
		return videoPlayer;
	}
}