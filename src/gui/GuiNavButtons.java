package gui;

import java.util.List;

import javax.swing.SwingUtilities;

import org.lwjgl.util.vector.Vector2f;

import frames.MainFrame;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import panorama.PanGraph;
import panorama.PanNode;

public class GuiNavButtons {
	public static GuiButton navTop;
	public static GuiButton navBot;
	public static GuiButton navRight;
	public static GuiButton navLeft;
	public static GuiButton navMap;
	
	private static boolean navTopAvail = false;
	private static boolean navBotAvail = false;
	private static boolean navRightAvail = false;
	private static boolean navLeftAvail = false;
	private static boolean navMapAvail = false;
	
	private static boolean areHidden = false;
	
	private static long currentTime;
	private static long lastShowTime;
	private static final long hideLatency = 3000; // in milis
	
	private static float btnLocation = 0.935f;
	private static float btnArea = 0.875f;
	private static float btnScaleX = 0.055f;
	private static float btnScaleY = 0.055f;
	
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
					DisplayManager.setWindowed();
					DisplayManager.setReturnToFullscreen();
				}
				
				// Show map
				// onClick is called from initial thread therefore GUI needs to be set using
				// SwingUtilities.invokeLater or SwingUtilities.invokeAndWait
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						MainFrame.getMap().showFrame();
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
		areHidden = true;
		
		// Set nav buttons that are available for currently active panorama
		navTopAvail = (node.getTop() != null) ? true : false;
		navRightAvail = (node.getRight() != null) ? true : false;
		navBotAvail = (node.getBot() != null) ? true : false;
		navLeftAvail = (node.getLeft() != null) ? true : false;
		navMapAvail = (PanGraph.isEmpty()) ? false : true;
		
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
		return navTop.mouseOver() ||
				navBot.mouseOver() ||
				navRight.mouseOver() ||
				navLeft.mouseOver() ||
				navMap.mouseOver();
	}
	
	public static void click() {
		navTop.click();
		navBot.click();
		navRight.click();
		navLeft.click();
		navMap.click();
	}
	
	public static void update() {
		currentTime = System.currentTimeMillis();
		
		if(isMouseNear()) {
			showAll();
		}
		else if(currentTime >= lastShowTime + hideLatency) {
			hideAll();
		}
		
		navTop.update();
		navBot.update();
		navRight.update();
		navLeft.update();
		navMap.update();
	}
}
