package gui;

import java.util.List;

public interface IButton {
	
	public void onClick(IButton button);
	
	public void onStartHover(IButton button);
	
	public void onStopHover(IButton button);
	
	public void whileHovering(IButton button);
	
	/**
	 * Shows GuiButton
	 * That's done by ADDING button to list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void show(List<GuiTexture> guiTextureList);
	
	/**
	 * Hides GuiButton
	 * That's done by REMOVING button from list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void hide(List<GuiTexture> guiTextureList);
	
	public void playHoverAnimation(float scaleFactor);
	
	public void resetScale();
	
	public void update();
	
	/**
	 * @return true if mouse cursor is over button
	 */
	public boolean mouseOver();
	
	/**
	 * Calls onClick if mouse was over button at the time of click
	 */
	public void click();
}
