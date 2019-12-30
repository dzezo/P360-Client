package gui;

import org.lwjgl.util.vector.Vector2f;

public class GuiSprites {
	public static GuiSprite loading;	
	public static GuiSprite cancel;
	
	private static long cancelTime;
	private static long cancelTimeOut = 3000; 
	
	public static void init() {
		loading = new GuiSprite("/sprites/loading.png", new Vector2f(0,0), new Vector2f(0.075f, 0.075f)) {};
		cancel = new GuiSprite("/sprites/cancel.png", new Vector2f(0,0), new Vector2f(0.075f, 0.075f)) {};
	}
	
	public static void update() {
		// Auto hide cancel sprite on time out
		if(!cancel.isHidden()) {
			long currentTime = System.currentTimeMillis();
			if(currentTime > cancelTime + cancelTimeOut)
				showCancelSprite(false);
		}
	}
	
	/**
	 * Shows or hides loading sprite
	 * @param show - shows sprite if set to true, and vice versa
	 */
	public static void showLoadingSprite(boolean show) {
		// Show loading sprite if loading sprite is hidden and show is requested
		if(show && loading.isHidden()) {
			if(!cancel.isHidden()) {
				cancel.setPositon(0.125f, 0.0f);
				cancel.setScale(0.05f, 0.05f);
			}
			loading.show();
		}
		// Hide loading sprite if loading sprite is showing and hide is requested
		else if(!show && !loading.isHidden()) {
			loading.hide();
			if(!cancel.isHidden()) {
				cancel.setPositon(0, 0);
				cancel.setScale(0.075f, 0.075f);
			}
		}
	}
	
	/**
	 * Shows or hides cancel sprite
	 * @param show - shows sprite if set to true, and vice versa
	 */
	public static void showCancelSprite(boolean show) {
		if(show) {	
			// Hide loading sprite before showing cancel sprite
			if(!loading.isHidden())
				loading.hide();
			
			// Set cancel sprite show time
			cancelTime = System.currentTimeMillis();
			
			// Show cancel sprite
			cancel.show();
		}
		else {
			cancel.hide();
			cancel.setPositon(0, 0);
			cancel.setScale(0.075f, 0.075f);
		}
	}
}
