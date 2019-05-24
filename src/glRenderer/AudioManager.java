package glRenderer;

import frames.MainFrame;
import panorama.PanNode;
import touring.TourManager;

public class AudioManager implements Runnable {
	private MainFrame mainFrame;
	
	private static boolean controlsEnabled = false;
	private static boolean playText = true;
	
	private static PanNode myActivePano;
	
	// flag to play audio only once
	private static boolean audioPlayed = false;
	
	public AudioManager(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public void run() {
		PanNode activePano = Scene.getActivePanorama();
		if(activePano == null) return;
		
		// Stop audio when switching scenes
		if(myActivePano == null)
			myActivePano = activePano;
		else if(!activePano.equals(myActivePano)) {
			if(myActivePano.isAudioPlaying())
				myActivePano.stopAudio();
			myActivePano = activePano;
		}
		
		// Enable/Disable audio controls
		if(activePano.hasAudio() && !controlsEnabled) {
			mainFrame.enableSoundControl(true);
			controlsEnabled = true;
		}
		else if(!activePano.hasAudio() && controlsEnabled) {
			mainFrame.enableSoundControl(false);
			controlsEnabled = false;
		}
		
		// Set play/pause text
		if(activePano.isAudioPlaying() && playText || !activePano.isAudioPlaying() && !playText) {
			mainFrame.setPlayPauseText(activePano.isAudioPlaying());
			playText = !playText;
		}
		
		// Automatically play audio while touring
		if(Scene.isReady()
				&& TourManager.isTouring() 
				&& !activePano.visited 
				&& activePano.hasAudio() 
				&& !audioPlayed) 
		{
			activePano.playAudio();
			audioPlayed = true;
		}
	}
	
	public static void resetAudioPlayed() {
		audioPlayed = false;
	}
	
	public static void stopAudio() {
		if(myActivePano != null && myActivePano.isAudioPlaying())
			myActivePano.stopAudio();
	}

}
