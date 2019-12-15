package glRenderer;

import frames.MainFrame;
import panorama.PanNode;
import touring.TourManager;
import utils.ConfigData;

public class AudioManager implements Runnable {
	private MainFrame mainFrame;
	
	private static boolean controlsEnabled = false;
	private static boolean playText = true;
	
	private static PanNode myActivePano;
	
	private static boolean audioPlayed = false; // flag to play audio only once
	private static boolean audioPaused;
	
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
				&& !(activePano.visited && ConfigData.getPanFlag())
				&& activePano.hasAudio()
				&& !audioPlayed) 
		{
			if(!myActivePano.isAudioPlaying()) activePano.playAudio();
			audioPlayed = true;
			audioPaused = false;
		}
		
		// Automatically pause audio once video player is requested
		if(MainFrame.getInstance().getVideoPlayer().getFrame().isVisible()
				&& myActivePano.isAudioPlaying()
				&& !audioPaused) 
		{
			myActivePano.pauseAudio();
			audioPaused = true;
		}
		else if(!MainFrame.getInstance().getVideoPlayer().getFrame().isVisible()
				&& audioPaused) 
		{
			myActivePano.playAudio();
			audioPaused = false;
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
