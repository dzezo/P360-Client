package glRenderer;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Toolkit;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class DisplayManager {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FPS_CAP = 60;
	
	private static boolean resized = false;
	
	private static Vector2f normalizedCursorPosition = new Vector2f();
	private static boolean cursorVisible = true;
	private static Cursor nativeCursor;
	private static Cursor emptyCursor;
	
	private static boolean toFullscreen = false;
	private static boolean fullScreenRequest = false;
	private static boolean windowedRequest = false;
	
	public static void createDisplay(Canvas canvas) {
		ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true).withProfileCore(true);
		
		try{
			Display.setParent(canvas);
			Display.setTitle("P360");
			Display.setVSyncEnabled(true);
			Display.create(new PixelFormat().withSamples(4), attribs);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
		}
		catch(LWJGLException e){
			e.printStackTrace();
		}
		
		// Where to render on display
		glViewport(0, 0, WIDTH, HEIGHT);
		
		// Set cursor
		try {
			nativeCursor = Mouse.getNativeCursor();
			emptyCursor = new Cursor(1, 1, 0, 0, 1, BufferUtils.createIntBuffer(1), null);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateDisplay(){
		if (Display.wasResized()) {
			glViewport(0, 0, Display.getWidth(), Display.getHeight());
		}
		
		Display.update();
		Display.sync(FPS_CAP);
	}
	
	public static void closeDisplay(){
		Display.destroy();
	}
	
	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}
	
	private static void setFullscreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		try {
			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// Recalculate projection matrix
		Renderer.requestNewProjection();
		// Where to render on display
		glViewport(0, 0, width, height);
		// Inform about resizing
		resized = true;
	}
	
	public static boolean isFullscreen() {
		return Display.isFullscreen();
	}
	
	private static void setWindowed() {
		try {
			Display.setFullscreen(false);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// Recalculate projection matrix
		Renderer.requestNewProjection();
		// Where to render on display
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		// Inform about resizing
		resized = true;
	}
	
	public static boolean wasResized() {
		return Display.wasResized() || resized;
	}
	
	public static void confirmResize() {
		if(resized) resized = false;
	}
	
	public static Vector2f getNormalizedCursorPosition() {
		float normalizedX = -1.0f + 2.0f * (float)Mouse.getX() / (float)Display.getWidth();
		float normalizedY = 1.0f - 2.0f * (float)Mouse.getY() / (float)Display.getHeight();
		normalizedCursorPosition.set(normalizedX, normalizedY);
		return normalizedCursorPosition;
	}
	
	/**
	 * Hides mouse cursor if its shown
	 */
	public static void hideMouseCursor() {
		if(!cursorVisible) return;
		
		try {
			Mouse.setNativeCursor(emptyCursor);
			cursorVisible = false;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Shows mouse cursor if its hidden
	 */
	public static void showMouseCursor() {
		if(cursorVisible) return;
		
		try {
			Mouse.setNativeCursor(nativeCursor);
			cursorVisible = true;
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves whether or not the mouse cursor is within the bounds of the window.
	 */
	public static boolean isMouseInWindow() {
		return Mouse.isInsideWindow();
	}

	/* Requests */
	public static void serveRequests() {
		if(fullScreenRequest && !Display.isFullscreen()) {
			DisplayManager.setFullscreen();
			fullScreenRequest = false;
		}
		if(windowedRequest && Display.isFullscreen()) {
			DisplayManager.setWindowed();
			windowedRequest = false;
		}
	}
	
	public static void requestFullScreen() {
		fullScreenRequest = true;
	}
	
	public static void requestWindowed() {
		windowedRequest = true;
	}
	
	
	public static void requestReturnToFullScreen() {
		toFullscreen = true;
	}
	
	/**
	 * Poziv ce resetovati return to full screen flag.
	 * @return stanje return to full screen flag-a
	 */
	public static boolean returnToFullScreenRequested() {
		if(toFullscreen) {
			toFullscreen = false;
			return true;
		}
		
		return toFullscreen;
	}
	
}
