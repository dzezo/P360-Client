package input;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import frames.MapViewFrame;
import frames.MapViewPanel;
import glRenderer.DisplayManager;
import glRenderer.Scene;
import gui.GuiNavButtons;
import utils.ConfigData;

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
	
	// Controllers
	private static ControllerItem activeCI;
	private static ControllerItem nextCI;
	private static boolean changeController = false;
	
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
	private static long lastInteractTime;
	
	public static void readInput() {
		// Standard
		readKeyboard();
		readMouse();
		
		// Controller
		if(changeController) {
			if(nextCI != null) {
				activeCI.setSelected(false);
				activeCI = nextCI;
				
				controller = activeCI.getController();
			}
			else {
				controller = null;
			}
			
			changeController = false;
		}
		
		if(controller != null) {
			if(!MapViewFrame.getInstance().isVisible())
				readControler();
			else 
				readControlerOnMap();
		}
		
	}
	
	public static void setController(ControllerItem ci) {
		activeCI = ci;
		controller = ci.getController();
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
	
	public static Controller getController() {
		return controller;
	}
	
	public static void changeController(ControllerItem ci) {
		nextCI = ci;
		changeController = true;
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
					if(!DisplayManager.isFullscreen()) DisplayManager.requestFullScreen();
					else DisplayManager.requestWindowed();
					break;
				case K_LPAN:
					if(!GuiNavButtons.areHidden()) Scene.goSide(3);
					else GuiNavButtons.showAll();
					break;
				case K_RPAN:
					if(!GuiNavButtons.areHidden()) Scene.goSide(1);
					else GuiNavButtons.showAll();	
					break;
				case K_TPAN:
					if(!GuiNavButtons.areHidden()) Scene.goSide(0);
					else GuiNavButtons.showAll();
					break;
				case K_BPAN:
					if (!GuiNavButtons.areHidden()) Scene.goSide(2);
					else GuiNavButtons.showAll();
					break;
				case K_PAN:
					ConfigData.setPanFlag();
					lastInteractTime ^= lastInteractTime;
					break;
				case K_MAP:
					GuiNavButtons.navMap.performAction();
					break;
				case K_LEFT:
				case K_RIGHT:
				case K_UP:
				case K_DOWN:
					break;
				default:
					if(DisplayManager.isFullscreen()) DisplayManager.requestWindowed();
					break;
				}
			}
		}				
	}

	private static void readMouse() {
		while(Mouse.next()) {
			if(Mouse.getEventButtonState()) {
				// Left click
				if(Mouse.getEventButton() == 0) {
					// double click condition
					if (click && clickTime + doubleClickLatency > System.currentTimeMillis()){
						if(DisplayManager.isFullscreen())
							DisplayManager.requestWindowed();
						else
							DisplayManager.requestFullScreen();
						
						// reset for next detection
						click = false;
					}
					else {
						click = true;
						clickTime = System.currentTimeMillis();
					}
					
					// detect if click happened on gui
					if(!GuiNavButtons.areHidden())
						GuiNavButtons.click();
				}
				else if (click && clickTime + doubleClickLatency < System.currentTimeMillis()) {
					click = false;
				}
			} else {
				if(Mouse.isButtonDown(0) && !GuiNavButtons.isMouseOver()) {
					DisplayManager.showMouseCursor();
					float pitchDelta = Mouse.getDY() * mouseSensitivity;
					float yawDelta = Mouse.getDX() * mouseSensitivity;
					
					Scene.getCamera().setRotationVelocity(yawDelta, pitchDelta);
					
					// Last time user interacted
					lastInteractTime = System.currentTimeMillis();
				}
			}
		}
		
		// detect movement (if not draging)
		if(DisplayManager.isMouseInWindow()) {
			if(isMouseMoving()){
				DisplayManager.showMouseCursor();
			}
			else if(isMouseIdling() && GuiNavButtons.areHidden()){
				DisplayManager.hideMouseCursor();
			}
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
						Scene.goSide(3);
				}
				else if(controller.isButtonPressed(GP_RPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goSide(1);
				}
				else if(controller.isButtonPressed(GP_TPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goSide(0);
				}
				else if(controller.isButtonPressed(GP_BPAN)) {
					if(GuiNavButtons.areHidden())
						GuiNavButtons.showAll();
					else
						Scene.goSide(2);
				}
				else if(controller.isButtonPressed(GP_FSCREEN)) {
					if(DisplayManager.isFullscreen())
						DisplayManager.requestWindowed();
					else
						DisplayManager.requestFullScreen();
				}
				else if(controller.isButtonPressed(GP_PAN)) {
					ConfigData.setPanFlag();
					lastInteractTime ^= lastInteractTime;
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
					MapViewPanel map = (MapViewPanel) MapViewFrame.getInstance().getMapPanel();
					map.selectLeft();
				}
				else if(controller.isButtonPressed(GP_RPAN)) {
					MapViewPanel map = (MapViewPanel) MapViewFrame.getInstance().getMapPanel();
					map.selectRight();
				}
				else if(controller.isButtonPressed(GP_TPAN)) {
					MapViewPanel map = (MapViewPanel) MapViewFrame.getInstance().getMapPanel();
					map.selectTop();
				}
				else if(controller.isButtonPressed(GP_BPAN)) {
					MapViewPanel map = (MapViewPanel) MapViewFrame.getInstance().getMapPanel();
					map.selectBot();
				}
				else if(controller.isButtonPressed(GP_MAP)) {
					MapViewFrame.getInstance().hideFrame();
				}
				else if(controller.isButtonPressed(GP_MAP_CONFIRM)) {
					MapViewPanel map = (MapViewPanel) MapViewFrame.getInstance().getMapPanel();
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
	
	public static long getLastInteractTime() {
		return lastInteractTime;
	}
	
	public static void setLastInteractTime(long time) {
		lastInteractTime = time;
	}

}
