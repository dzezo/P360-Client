package gui;

import java.util.List;

import javax.swing.SwingUtilities;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import frames.MainFrame;
import frames.MapViewFrame;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import panorama.PanGraph;
import panorama.PanNode;
import utils.ConfigData;

public class GuiNavButtons {
	public static GuiButton navTop;
	public static GuiButton navBot;
	public static GuiButton navRight;
	public static GuiButton navLeft;
	public static GuiButton navMap;
	public static GuiButton playVideo;
	
	private static boolean navTopAvail = false;
	private static boolean navBotAvail = false;
	private static boolean navRightAvail = false;
	private static boolean navLeftAvail = false;
	private static boolean navMapAvail = false;
	private static boolean playVideoAvail = false;
	
	private static boolean areHidden = false;
	
	private static long currentTime;
	private static long lastShowTime;
	private static final long hideLatency = 3000; // in milis
	
	private static final float btnLocation = 0.935f;
	private static final float btnArea = 0.875f;
	private static final float btnScaleX = 0.055f;
	private static final float btnScaleY = 0.055f;
	
	public static void init() {
		navTop = new GuiButton("/nav/top.png", new Vector2f(0,btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goTop();
			}
			
			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navBot = new GuiButton("/nav/bot.png", new Vector2f(0,-btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goBot();
			}
			
			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navLeft = new GuiButton("/nav/left.png", new Vector2f(-btnLocation,0), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goLeft();	
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navRight = new GuiButton("/nav/right.png", new Vector2f(btnLocation,0), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				Scene.goRight();
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}
			
		};
		navMap = new GuiButton("/nav/minimap.png", new Vector2f(btnLocation,-btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {
				// Minimize if dislay is in fullscreen
				if(DisplayManager.isFullscreen()) {
					DisplayManager.requestWindowed();
					DisplayManager.requestReturnToFullScreen();
				}
				
				// Show map
				// onClick is called from initial thread therefore GUI needs to be set using
				// SwingUtilities.invokeLater or SwingUtilities.invokeAndWait
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MainFrame.getInstance().setVisible(false);
						MapViewFrame.getInstance().showFrame();
					}	
				});
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}		
		};
		playVideo = new GuiButton("/nav/playVideo.png", new Vector2f(-btnLocation,-btnLocation), new Vector2f(btnScaleX, btnScaleY)) {
			public void onClick(IButton button) {	
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Scene.setReady(false);
						MainFrame.getInstance().getVideoPlayer().playVideo(Scene.getActivePanorama().getVideoPath());
					}
				});
			}

			public void onStartHover(IButton button) {
				button.playHoverAnimation(0.01f);
			}

			public void onStopHover(IButton button) {
				button.resetScale();
			}

			public void whileHovering(IButton button) {
				
			}		
		};
	}
	
	public static void hideAll() {
		if(!areHidden) {
			List<GuiTexture> guis = GuiRenderer.getGuiList();
			
			if(navTopAvail)
				navTop.hide(guis);
			
			if(navBotAvail)
				navBot.hide(guis);
			
			if(navRightAvail)
				navRight.hide(guis);
			
			if(navLeftAvail)
				navLeft.hide(guis);
			
			if(navMapAvail)
				navMap.hide(guis);
			
			if(playVideoAvail)
				playVideo.hide(guis);
			
			areHidden = true;
		}
	}
	
	public static void showAll() {
		if(areHidden) {
			lastShowTime = System.currentTimeMillis();
			
			List<GuiTexture> guis = GuiRenderer.getGuiList();
			
			if(navTopAvail)
				navTop.show(guis);
			
			if(navBotAvail)
				navBot.show(guis);
			
			if(navRightAvail)
				navRight.show(guis);
			
			if(navLeftAvail)
				navLeft.show(guis);
			
			if(navMapAvail)
				navMap.show(guis);
			
			if(playVideoAvail)
				playVideo.show(guis);
			
			areHidden = false;
		}
	}
	
	public static boolean areHidden() {
		return areHidden;
	}
	
	public static void setAvailableNavButtons(PanNode node) {
		// Get gui rendering list
		List<GuiTexture> guis = GuiRenderer.getGuiList();
		
		// Remove previous nav buttons from rendering list
		if(navTopAvail) navTop.hide(guis);
		if(navBotAvail) navBot.hide(guis);	
		if(navRightAvail) navRight.hide(guis);	
		if(navLeftAvail) navLeft.hide(guis);
		if(navMapAvail) navMap.hide(guis);
		if(playVideoAvail) playVideo.hide(guis);
		areHidden = true;
		
		// Set nav buttons that are available for currently active panorama
		navTopAvail = (node.getTop() != null) ? true : false;
		navRightAvail = (node.getRight() != null) ? true : false;
		navBotAvail = (node.getBot() != null) ? true : false;
		navLeftAvail = (node.getLeft() != null) ? true : false;
		navMapAvail = (PanGraph.isEmpty()) ? false : true;
		playVideoAvail = node.hasVideo();
		
		// Nav buttons are initially showing
		showAll();
	}
	
	public static boolean isMouseNear() {
		Vector2f mouseCoord = DisplayManager.getNormalizedMouseCoords();
		if(Math.abs(mouseCoord.x) > btnArea || Math.abs(mouseCoord.y) > btnArea)
			return true;
		else
			return false;
	}
	
	public static boolean isMouseOver() {
		return 	navTop.mouseOver() ||
				navBot.mouseOver() ||
				navRight.mouseOver() ||
				navLeft.mouseOver() ||
				navMap.mouseOver() ||
				playVideo.mouseOver();
	}
	
	public static void click() {
		navTop.click();
		navBot.click();
		navRight.click();
		navLeft.click();
		navMap.click();
		playVideo.click();
	}
	
	public static void update() {
		currentTime = System.currentTimeMillis();
		
		// Display/Hide nav. btn.
		if(isMouseNear()) {
			showAll();
		}
		else if(currentTime >= lastShowTime + hideLatency) {
			hideAll();
		}
		
		// Calculating nav. btn. position based on current camera angle
		if(!ConfigData.getFixGUIFlag()) {
			float f_yaw = Scene.getCamera().getYaw();
			double d_yaw = (double) f_yaw;
			float posX,posY;
			if(45 < d_yaw && d_yaw < 135) {
				posX = -btnLocation;
				posY = (float) (1/Math.tan(Math.toRadians(d_yaw)) * btnLocation);
			}
			else if (135 <= d_yaw && d_yaw <= 225) {
				posX = (float) (Math.tan(Math.toRadians(d_yaw)) * btnLocation);
				posY = -btnLocation;
			}
			else if(225 < d_yaw && d_yaw < 315) {
				posX = btnLocation;
				posY = (float) (-1/Math.tan(Math.toRadians(d_yaw)) * btnLocation);
			}
			else {
				posX = (float) (-Math.tan(Math.toRadians(d_yaw)) * btnLocation);
				posY = btnLocation;
			}
			
			// Setting nav. btn. positon
			navTop.setPosition(new Vector2f(posX, posY));
			navBot.setPosition(new Vector2f(-posX, -posY));
			navRight.setPosition(new Vector2f(posY, -posX));
			navLeft.setPosition(new Vector2f(-posY, posX));
			
			// Setting nav. btn. rotation to match new position
			navTop.setRotation(new Vector3f(0, 0, f_yaw));
			navBot.setRotation(new Vector3f(0, 0, f_yaw));
			navRight.setRotation(new Vector3f(0, 0, f_yaw));
			navLeft.setRotation(new Vector3f(0, 0, f_yaw));
		}
		else {
			navTop.setPosition(new Vector2f(0, btnLocation));
			navBot.setPosition(new Vector2f(0, -btnLocation));
			navRight.setPosition(new Vector2f(btnLocation, 0));
			navLeft.setPosition(new Vector2f(-btnLocation, 0));
			
			navTop.setRotation(new Vector3f(0, 0, 0));
			navBot.setRotation(new Vector3f(0, 0, 0));
			navRight.setRotation(new Vector3f(0, 0, 0));
			navLeft.setRotation(new Vector3f(0, 0, 0));
		}
		
		// Detecting mouse events on nav. btn.
		navTop.update();
		navBot.update();
		navRight.update();
		navLeft.update();
		navMap.update();
		playVideo.update();
	}
	
}
