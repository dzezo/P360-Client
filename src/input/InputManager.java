package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import frames.MainFrame;
import frames.MapViewPanel;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import gui.GuiNavButtons;

public class InputManager {
	// Keyboard controls
	public static final int K_LEFT = Keyboard.KEY_LEFT;
	public static final int K_RIGHT = Keyboard.KEY_RIGHT;
	public static final int K_UP = Keyboard.KEY_UP;
	public static final int K_DOWN = Keyboard.KEY_DOWN;
	public static final int K_LPAN = Keyboard.KEY_A;
	public static final int K_RPAN = Keyboard.KEY_D;
	public static final int K_TPAN = Keyboard.KEY_W;
	public static final int K_BPAN = Keyboard.KEY_S;
	public static final int K_FSCREEN = Keyboard.KEY_F;
	public static final int K_PAN = Keyboard.KEY_P;
	public static final int K_MAP = Keyboard.KEY_M;
	
	// Gamepad controls
	private static Controller controller;
	private static boolean readAxis;
	public static int GP_YAXIS;
	public static int GP_XAXIS;
	public static final int GP_LPAN = 3;		// Square
	public static final int GP_RPAN = 1;		// Circle
	public static final int GP_TPAN = 0;		// Triangle
	public static final int GP_BPAN = 2;		// Cross
	public static final int GP_FSCREEN = 6;		// L1
	public static final int GP_PAN = 7; 		// R1
	public static final int GP_MAP = 4;			// L2
	public static final int GP_MAP_CONFIRM = 5;	// R2
	
	// Requests
	private static boolean fullscreenRequest = false;
	
	// Mouse movement config
	private static Vector2f prevMouseDisplayPos = new Vector2f(0, 0);
	private static Vector2f mouseDisplayPos = new Vector2f(0, 0);
	private static long lastMouseMoveTime;
	private static long mouseHideLatency = 3000; // in milis
	private	static final float mouseSensitivity = 0.1f;
	
	// Mouse double click
	private static boolean click = false;
	private static long clickTime;
	private static final long doubleClickLatency = 300; // in milis
	
	// Keyboard movement config
	private static final float yawSpeed = 0.5f;
	private static final float pitchSpeed = 0.25f;
	
	// time of last interaction in milis
	public static long lastInteractTime;
	
	public static void readInput() {
		// Standard
		readKeyboard();
		readMouse();
		
		// Controller
		if(controller != null) {
			if(!MainFrame.isMapVisible())
				readControler();
			else 
				readControlerOnMap();
		}
		
		// Other Input Requests
		fullscreenRequest();
	}
	
	public static void setController(Controller c) {
		controller = c;
		if(controller.getAxisCount() != 0) {
			for(int i=0; i<controller.getAxisCount(); i++) {
				String axisName = controller.getAxisName(i);
				if(axisName.startsWith("Y"))
					GP_YAXIS = i;
				else if(axisName.startsWith("X"))
					GP_XAXIS = i;
			}
			readAxis = true;
		}
		else
			readAxis = false;
	}
	
	private static void readKeyboard() {
		// Spammable keys
		if(Keyboard.isKeyDown(K_LEFT)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(-yawSpeed);
		}
		if(Keyboard.isKeyDown(K_RIGHT)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(yawSpeed);
		}
		if(Keyboard.isKeyDown(K_UP)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(-pitchSpeed);
		}
		if(Keyboard.isKeyDown(K_DOWN)) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(pitchSpeed);
		}
		
		// Non-spammable keys
		if(Keyboard.next()) {
			if(Keyboard.getEventKeyState()) {
				int key = Keyboard.getEventKey();
				switch(key) {
				case K_FSCREEN:
					if(!DisplayManager.isFullscreen()) DisplayManager.setFullscreen();
					else DisplayManager.setWindowed();
					break;
				case K_LPAN:
					if(!GuiNavButtons.areHidden()) Scene.goLeft();
					else GuiNavButtons.showAll();
					break;
				case K_RPAN:
					if(!GuiNavButtons.areHidden()) Scene.goRight();
					else GuiNavButtons.showAll();	
					break;
				case K_TPAN:
					if(!GuiNavButtons.areHidden()) Scene.goTop();
					else GuiNavButtons.showAll();
					break;
				case K_BPAN:
					if (!GuiNavButtons.areHidden()) Scene.goBot();
					else GuiNavButtons.showAll();
					break;
				case K_PAN:
					Scene.getCamera().setAutoPan();
					break;
				case K_MAP:
					GuiNavButtons.navMap.performAction();
					break;
				default:
					if(DisplayManager.isFullscreen()) DisplayManager.setWindowed();
					break;
				}
			}
		}				
	}

	private static void readMouse() {
		// detect draging
		if(Mouse.isButtonDown(0) && !GuiNavButtons.isMouseOver()) {
			DisplayManager.showMouseCursor();
			
			float pitchDelta = Mouse.getDY() * mouseSensitivity;
			float yawDelta = Mouse.getDX() * mouseSensitivity;
			
			Scene.getCamera().setRotationVelocity(yawDelta, pitchDelta);
			
			// Last time user interacted
			lastInteractTime = System.currentTimeMillis();
		}
		// detect movement (if not draging)
		else if(DisplayManager.isMouseInWindow()) {
			if(isMouseMoving()){
				DisplayManager.showMouseCursor();
			}
			else if(isMouseIdling() && GuiNavButtons.areHidden()){
				DisplayManager.hideMouseCursor();
			}
		}
		
		// detect left click
		if(Mouse.next() && Mouse.getEventButtonState() && Mouse.isButtonDown(0)) {
			// double click condition
			if (click && clickTime + doubleClickLatency > System.currentTimeMillis()){
				if(DisplayManager.isFullscreen())
					DisplayManager.setWindowed();
				else
					DisplayManager.setFullscreen();
				
				// reset for next detection
				click = false;
			}
			else {
				click = true;
				clickTime = System.currentTimeMillis();
				
				// detect if click happened on gui
				if(!GuiNavButtons.areHidden())
					GuiNavButtons.click();
			}
		}
		else if (click && clickTime + doubleClickLatency < System.currentTimeMillis()) {
			click = false;
		}
	}
	
	private static void readControler() {
		// Poll
		if(!controller.poll()) {
			controller = null;
			System.out.println("device disconnected");
			return;
		}
		
		// Axis
		float valueY, valueX;
		if(readAxis) {
			valueY = controller.getAxisValue(GP_YAXIS);
			valueX = controller.getAxisValue(GP_XAXIS);
		}
		else {
			valueY = 0;
			valueX = 0;
		}
		if(valueY == 0)
			valueY = controller.getPovY();
		if(valueX == 0)
			valueX = controller.getPovX();
		
		if(valueY != 0) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().pitch(valueY * pitchSpeed);
		}
		if(valueX != 0) {
			lastInteractTime = System.currentTimeMillis();
			Scene.getCamera().yaw(valueX * yawSpeed);
		}
		
		// Buttons
		if(Controllers.next() && Controllers.getEventSource() == controller) {
			if(Controllers.isEventButton() && Controllers.getEventButtonState()) {
				if(controller.isButtonPressed(GP_LPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goLeft();
				}
				else if(controller.isButtonPressed(GP_RPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goRight();
				}
				else if(controller.isButtonPressed(GP_TPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goTop();
				}
				else if(controller.isButtonPressed(GP_BPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goBot();
				}
				else if(controller.isButtonPressed(GP_FSCREEN)) {
					if(DisplayManager.isFullscreen())
						DisplayManager.setWindowed();
					else
						DisplayManager.setFullscreen();
				}
				else if(controller.isButtonPressed(GP_PAN)) {
					Scene.getCamera().setAutoPan();
				}
				else if(controller.isButtonPressed(GP_MAP)) {
					GuiNavButtons.navMap.performAction();
				}
			}
		}
	}
	
	private static void readControlerOnMap() {
		// Poll
		if(!controller.poll()) {
			controller = null;
			System.out.println("device disconnected");
			return;
		}
		
		// Buttons
		if(Controllers.next() && Controllers.getEventSource() == controller) {
			if(Controllers.isEventButton() && Controllers.getEventButtonState()) {
				if(controller.isButtonPressed(GP_LPAN)) {
					MapViewPanel map = (MapViewPanel) MainFrame.getMap().getMapPanel();
					map.selectLeft();
				}
				else if(controller.isButtonPressed(GP_RPAN)) {
					MapViewPanel map = (MapViewPanel) MainFrame.getMap().getMapPanel();
					map.selectRight();
				}
				else if(controller.isButtonPressed(GP_TPAN)) {
					MapViewPanel map = (MapViewPanel) MainFrame.getMap().getMapPanel();
					map.selectTop();
				}
				else if(controller.isButtonPressed(GP_BPAN)) {
					MapViewPanel map = (MapViewPanel) MainFrame.getMap().getMapPanel();
					map.selectBot();
				}
				else if(controller.isButtonPressed(GP_MAP)) {
					MainFrame.getMap().hideFrame();
				}
				else if(controller.isButtonPressed(GP_MAP_CONFIRM)) {
					MapViewPanel map = (MapViewPanel) MainFrame.getMap().getMapPanel();
					map.confirmSelection();
				}
			}
		}
	}
	
	private static boolean isMouseMoving() {
		mouseDisplayPos = DisplayManager.getNormalizedMouseCoords();
		
		if(mouseDisplayPos.x != prevMouseDisplayPos.x || mouseDisplayPos.y != prevMouseDisplayPos.y) {
			prevMouseDisplayPos = mouseDisplayPos;
			lastMouseMoveTime = System.currentTimeMillis();
			return true;
		}
		else {
			prevMouseDisplayPos = mouseDisplayPos;
			return false;
		}
	}
	
	private static boolean isMouseIdling() {
		return System.currentTimeMillis() > lastMouseMoveTime + mouseHideLatency;
	}
	
	private static void fullscreenRequest() {
		if(fullscreenRequest) {
			DisplayManager.setFullscreen();
			
			fullscreenRequest = false;
		}
	}
	
	public static void requestFullscreen() {
		fullscreenRequest = true;
	}
}