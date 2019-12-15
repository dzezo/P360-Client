package videoPlayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicSliderUI;

import com.sun.jna.NativeLibrary;

import glRenderer.DisplayManager;
import glRenderer.Scene;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.fullscreen.adaptive.AdaptiveFullScreenStrategy;
import utils.DialogUtils;

public class VideoPlayer {

	private final JFrame frame;
	
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	
	private String videoPath;
	
	private JLayeredPane contentPane;
	private JPanel playerPane;
	private JPanel controlsPane;
	
	private JButton playPauseButton;
	private JButton fullScreenButton;
	private JButton closeButton;
	
	private JSlider timeSlider;
	private boolean timeSeeking;
	
	private Timer hideCursorTimer;
	private Timer fullScreenTimer;
	private Timer showTimer;
	private Timer hideTimer;
	private boolean doubleClick;
	
	private BufferedImage cursorImg;
	private Cursor blankCursor;
	
	private final ImageIcon pauseIcon 		= new ImageIcon(Class.class.getResource("/video_player/pause.png"));
	private final ImageIcon playIcon		= new ImageIcon(Class.class.getResource("/video_player/play.png"));
	private final ImageIcon fullScreenIcon 	= new ImageIcon(Class.class.getResource("/video_player/fullScreen.png"));
	private final ImageIcon closeIcon 		= new ImageIcon(Class.class.getResource("/video_player/close.png"));
	
	private final String keys [] = {
            "sun.arch.data.model",
            "com.ibm.vm.bitmode",
            "os.arch",
            };
	
    public VideoPlayer() {
    	frame = new JFrame("Media Player");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		// clean up
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				hideFrame();
			}
		});
		
		// Ukljucivanje VLC native biblioteka i VLC plugin-ova
		boolean jvm64 = true;
		for (String key : keys) {
			String property  = System.getProperty(key);
			if(property != null) {
				jvm64 = (property.indexOf("64") >= 0) ? true : false;
			}
		}
		if(jvm64) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "./vlc");
			System.setProperty("VLC_PLUGIN_PATH", "./vlc/plugins");
		}
		else {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "./vlc(x86)");
			System.setProperty("VLC_PLUGIN_PATH", "./vlc(x86)/plugins");
		}
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent(
				null,
				null,
				new AdaptiveFullScreenStrategy(frame),
				null,
				null);
		
		mediaPlayerComponent.mediaPlayer().events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			public void playing(MediaPlayer mediaPlayer) {
				mediaPlayer.submit(new Runnable() {
					public void run() {
						showTimer.start();
					}
				});
			}
			// finished event se triggeruje kada se playback zavrsi.
			public void finished(MediaPlayer mediaPlayer) {
				// Nije dozvoljen libVLC callback preko event niti, jer moze da dovede do nepredvidivog ponasanja JVM-a
				// Potrebno je napraviti asinhroni egzekutor koji nudi MediaPlayer objekat koji se prosledjuje listeneru.
				mediaPlayer.submit(new Runnable() {
					public void run() {
						hideFrame();
					}
				});
			}
			// play() zahteva pokretanje i odmah se vraca, a libVLC ce asinhrono probati da pokrene video.
			// Ovo znaci da ce success/error biti indikovan sa zakasnjenjem, i ovo je event koji to hvata.
			public void error(MediaPlayer mediaPlayer) {
				// Ukoliko event handler menja gui onda to moraju uraditi na Swing Event Dispatch Thread-u
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						DialogUtils.showMessage("Failed to play: " + videoPath, "Error");
					}	
				});
			}
			public void positionChanged(MediaPlayer mp, float f) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						if(!timeSeeking) {
							int iPos = (int)(f * 100.0);
				            timeSlider.setValue(iPos);
						}
					}	
				});
	        }
		});
		
		Component videoSurface = mediaPlayerComponent.videoSurfaceComponent();
		videoSurface.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent e) {
				if(!controlsPane.isVisible())
					controlsPane.setVisible(true);
				frame.getContentPane().setCursor(Cursor.getDefaultCursor());
				hideCursorTimer.restart();
			}
		});
		videoSurface.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(doubleClick) {
					mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
					doubleClick = false;
				}
				else {
					doubleClick = true;
				}
				fullScreenTimer.restart();
			}
		});
		
		playerPane = new JPanel();
		playerPane.setLayout(new BorderLayout());
		playerPane.add(mediaPlayerComponent, BorderLayout.CENTER);
		playerPane.setSize(new Dimension(600, 400));
		
		controlsPane = new JPanel();
		playPauseButton = new JButton(pauseIcon);
		playPauseButton.setFocusable(false);
		controlsPane.add(playPauseButton);
		timeSlider = new JSlider();
		timeSlider.setFocusable(false);
		timeSlider.setUI(new BasicSliderUI(timeSlider) {
			protected TrackListener createTrackListener(JSlider slider) {
				return new TrackListener() {
					public boolean shouldScroll(int direction) {
						return false;
					}
				};
			}
		});
		timeSeeking = false;
		controlsPane.add(timeSlider);
		fullScreenButton = new JButton(fullScreenIcon);
		fullScreenButton.setFocusable(false);
		controlsPane.add(fullScreenButton);
		closeButton = new JButton(closeIcon);
		closeButton.setFocusable(false);
		controlsPane.add(closeButton);
		controlsPane.setSize(controlsPane.getPreferredSize());
		controlsPane.setLocation(playerPane.getWidth() / 2 - controlsPane.getWidth() / 2, 
				playerPane.getHeight() - controlsPane.getHeight());
		
		playPauseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	if(mediaPlayerComponent.mediaPlayer().status().isPlaying()) {
            		mediaPlayerComponent.mediaPlayer().controls().pause();
            		playPauseButton.setIcon(playIcon);
            	}
            	else {
            		mediaPlayerComponent.mediaPlayer().controls().play();
            		playPauseButton.setIcon(pauseIcon);
            	}
            }
        });
		
		timeSlider.addMouseMotionListener(new MouseMotionAdapter() {		
			public void mouseDragged(MouseEvent arg0) {
				BasicSliderUI ui = (BasicSliderUI) timeSlider.getUI();
	            timeSlider.setValue(ui.valueForXPosition(arg0.getX()));
				mediaPlayerComponent.mediaPlayer().controls().setPosition((float) timeSlider.getValue() / 100);
		    }
		});
		
		timeSlider.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent arg0) {
				timeSeeking = true;
				BasicSliderUI ui = (BasicSliderUI) timeSlider.getUI();
	            timeSlider.setValue(ui.valueForXPosition(arg0.getX()));
	            mediaPlayerComponent.mediaPlayer().controls().setPosition((float) timeSlider.getValue() / 100);
			}
			
			public void mouseReleased(MouseEvent arg0) {
				timeSeeking = false;
			}	
		});
		
		fullScreenButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {	
            	mediaPlayerComponent.mediaPlayer().fullScreen().toggle();
            	if(mediaPlayerComponent.mediaPlayer().fullScreen().isFullScreen())
            		frame.setAlwaysOnTop(true);
            	else
            		frame.setAlwaysOnTop(false);
            }
        });
		
		closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	hideFrame();
            }
        });
		
		
		contentPane = new JLayeredPane();
		contentPane.add(playerPane, new Integer(0));
		contentPane.add(controlsPane, new Integer(1));
		contentPane.setPreferredSize(playerPane.getSize());
		contentPane.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				playerPane.setSize(contentPane.getSize());
				controlsPane.setLocation(playerPane.getWidth() / 2 - controlsPane.getWidth() / 2, 
						playerPane.getHeight() - controlsPane.getHeight());
				contentPane.revalidate();
				contentPane.repaint();				
			}	
		});
		
		frame.setContentPane(contentPane);
		frame.pack();
		
		cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		blankCursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
		
		hideCursorTimer = new Timer(3000, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(frame.isVisible() && controlsPane.isVisible()) {
					Rectangle controlsRect = controlsPane.getBounds();
					controlsRect.setLocation(controlsPane.getLocationOnScreen());
					if(!controlsRect.contains(MouseInfo.getPointerInfo().getLocation())) {	
						controlsPane.setVisible(false);
						frame.getContentPane().setCursor(blankCursor);
					}
				}
			}	
		});
		hideCursorTimer.start();
		
		fullScreenTimer = new Timer(750, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doubleClick = false;
			}	
		});
		fullScreenTimer.start();
		
		showTimer = new Timer(300, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(DisplayManager.isFullscreen()) {
					DisplayManager.requestReturnToFullScreen();
					DisplayManager.requestWindowed();
				}
				else {
					frame.setAlwaysOnTop(true);
					frame.toFront();
					frame.repaint();
					showTimer.stop();
				}
			}	
		});
		
		hideTimer = new Timer(300, new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(DisplayManager.isFullscreen()) {
					frame.setVisible(false);
					hideTimer.stop();
				}
			}	
		});
    }
    
    public void playVideo(String videoPath) {
    	this.videoPath = videoPath;
    	frame.setVisible(true);
    	
    	timeSlider.setValue(0);
    	
    	mediaPlayerComponent.mediaPlayer().fullScreen().set(true);
    	mediaPlayerComponent.mediaPlayer().media().play(videoPath);
    }
    
    public void cleanUp() {
    	mediaPlayerComponent.release();
		frame.dispose();
		System.out.println("Video player is disposed.");
    }
    
    public JFrame getFrame() {
    	return this.frame;
    }
    
    public void hideFrame() {
    	mediaPlayerComponent.mediaPlayer().controls().stop();
    	Scene.setReady(true);
    	
		//frame.setVisible(false);
		if(DisplayManager.returnToFullScreenRequested()) {
			DisplayManager.requestFullScreen();
			hideTimer.start();
		}
		else {
			frame.setVisible(false);
		}
    }
}
