package gui;

import java.util.List;

public interface ISprite {
	/**
	 * Shows sprite
	 * That's done by ADDING button to list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void show(List<GuiTexture> guiTextureList);
	
	/**
	 * Hides sprite
	 * That's done by REMOVING button from list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void hide(List<GuiTexture> guiTextureList);
	
}
