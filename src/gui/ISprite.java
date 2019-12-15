package gui;

public interface ISprite {
	/**
	 * Shows sprite
	 * That's done by ADDING button to list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void show();
	
	/**
	 * Hides sprite
	 * That's done by REMOVING button from list of gui elements that are being rendered
	 * @param guiTextureList
	 */
	public void hide();
	
}
