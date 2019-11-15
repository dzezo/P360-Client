package panorama;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import utils.DialogUtils;

public class PanAudio implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private File audioFile;
	
	private transient AudioInputStream audioInput;
	private transient Clip audioClip;
	private transient long audioClipTimePos = 0;
	
	private transient boolean isPlaying = false;
	
	public PanAudio(String loc) {
		audioFile = new File(loc);
	}
	
	public void play() {
		try {
			audioInput = AudioSystem.getAudioInputStream(audioFile);
			audioClip = AudioSystem.getClip();
			audioClip.open(audioInput);
		    audioClip.setMicrosecondPosition(audioClipTimePos);
		    audioClip.start();
		    isPlaying = true;
		}  catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		}  catch (IOException e) {
			DialogUtils.showMessage("Could not find file: " + audioFile.getPath() + "\nFile is moved or deleted.", 
					"Could Not Find File");
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} 
	   
	}
	
	public void pause() {
		try {
			audioClipTimePos = audioClip.getMicrosecondPosition();
			audioClip.stop();
			isPlaying = false;
			audioClip.close();
			audioInput.close();
		} catch (IOException e) {
			DialogUtils.showMessage("Could not find file: " + audioFile.getPath() + "\nFile is moved or deleted.", 
					"Could Not Find File");
		}
	}
	
	public void stop() {
		try {
			audioClipTimePos = 0;
			audioClip.stop();
			isPlaying = false;
			audioClip.close();
			audioInput.close();
		} catch (IOException e) {
			DialogUtils.showMessage("Could not find file: " + audioFile.getPath() + "\nFile is moved or deleted.", 
					"Could Not Find File");
		}
	}
	
	private boolean audioReachedEnd() {
		return (audioClip.getMicrosecondPosition() == audioClip.getMicrosecondLength());
	}
	
	public String getAudioPath() {
		return audioFile.getPath();
	}
	
	public void setAudioPath(String audioPath) {
		audioFile = new File(audioPath);
	}
	
	public boolean isPlaying() {
		if(isPlaying && audioReachedEnd())
			this.stop();
		return this.isPlaying;
	}
	
}
