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
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.vector.Vector2f;

public class DisplayManager {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int FPS_CAP = 60;
	
	private static boolean resized = false;
	
	private static boolean cursorVisible = true;
	private static Cursor nativeCursor;
	private static Cursor emptyCursor;
	
	private static boolean toFullscreen = false;
	
	public static void createDisplay(Canvas canvas) {
		ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true).withProfileCore(true);
		
		try{
			Display.setParent(canvas);
			Display.setTitle("P360");
			Display.setVSyncEnabled(true);
			Display.create(new PixelFormat(), attribs);
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
	
	public static void setFullscreen() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int) screenSize.getWidth();
		int height = (int) screenSize.getHeight();
		try {
			Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// Recalculate projection matrix
		Renderer.setNewProjection();
		// Where to render on display
		glViewport(0, 0, width, height);
		// Inform about resizing
		resized = true;
	}
	
	public static boolean isFullscreen() {
		return Display.isFullscreen();
	}
	
	public static void setWindowed() {
		try {
			Display.setFullscreen(false);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		// Recalculate projection matrix
		Renderer.setNewProjection();
		// Where to render on display
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
		// Inform about resizing
		resized = true;
	}
	
	/**
	 * Sets request to return to fullscreen mode when possible
	 */
	public static void setReturnToFullscreen() {
		toFullscreen = true;
	}
	
	/**
	 * Checks for a fullscreen request
	 * @return true if request was set
	 */
	public static boolean returnToFullscreen() {
		if(toFullscreen) {
			toFullscreen = false;
			return true;
		}
		
		return false;
	}
	
	public static boolean wasResized() {
		return Display.wasResized() || resized;
	}
	
	public static void confirmResize() {
		if(resized) resized = false;
	}
	
	public static Vector2f getNormalizedMouseCoords() {
		float normalizedX = -1.0f + 2.0f * (float)Mouse.getX() / (float)Display.getWidth();
		float normalizedY = 1.0f - 2.0f * (float)Mouse.getY() / (float)Display.getHeight();
		return new Vector2f(normalizedX, normalizedY);
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
}
