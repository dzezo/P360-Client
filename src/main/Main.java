package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.UIManager;

import frames.MainFrame;
import glRenderer.AudioManager;
import glRenderer.Camera;
import glRenderer.DisplayManager;
import glRenderer.Renderer;
import glRenderer.Scene;
import gui.GuiNavButtons;
import gui.GuiRenderer;
import gui.GuiSprites;
import input.InputManager;
import shaders.GuiShader;
import shaders.StaticShader;
import utils.AutoLoad;
import utils.ChooserUtils;
import utils.ImageLoader;
import utils.Loader;

public class Main {
	public static final File WORKING_DIR = new File("C:\\p360system");
	
	public static void main(String[] args) throws Exception {
		// set system look and feel
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		// create working dir
		try {
			Files.createDirectory(Paths.get(WORKING_DIR.getPath()));
		} catch (IOException e) {
			System.out.println("Radni direktorijum postoji.");
		}
		
		// set working dir
		ChooserUtils.setWorkingDir(WORKING_DIR);
		
		// init
		MainFrame mainFrame = new MainFrame("P360-Client");
		
		Camera camera = new Camera();
		StaticShader shader = new StaticShader();
		GuiShader guiShader = new GuiShader();
		
		Scene.setCamera(camera);
		GuiNavButtons.init();
		GuiSprites.init();
		
		// load previously used map
		AutoLoad.load();
		
		while(mainFrame.isRunning()) {
			// check for changes
			if(Scene.getActivePanorama() != Scene.getQueuedPanorama()) {
				if(Scene.getQueuedPanorama().isLoaded() || ImageLoader.isLoaded()) {
					Scene.loadNewImage(Scene.getQueuedPanorama());
	
					ImageLoader.resetLoader();
				}
				else if(!ImageLoader.isLoading()){
					AudioManager.stopAudio();
					ImageLoader.loadImage(Scene.getQueuedPanorama().getPanoramaPath());
				}
				else if(ImageLoader.isCanceled()) {
					Scene.dequeuePanorama();
					
					ImageLoader.resetLoader();
				}
			}
			
			// Prepare renderer
			Renderer.prepare();
			
			if(Scene.isReady()) {
				if(!ImageLoader.isLoading()) {
					InputManager.readInput();		
				}
				
				// move camera
				Scene.getCamera().rotateCamera();
				Scene.getCamera().autoPan();
				
				// update gui buttons
				GuiNavButtons.update();
				
				// render scene
				Renderer.render(shader);
			}
			
			// render gui graphics
			GuiSprites.update();
			GuiRenderer.render(guiShader);
			
			// update display
			DisplayManager.updateDisplay();
			
		}
		
		// stop audio
		AudioManager.stopAudio();
		
		// Releasing resources
		shader.cleanUp();
		guiShader.cleanUp();
		Loader.cleanUp();
		DisplayManager.closeDisplay();
		
		// Everything is released exit
		System.exit(0);
	}
}
