package gui;

import org.lwjgl.util.vector.Vector2f;

import utils.Loader;

public abstract class GuiSprite implements ISprite {
	private GuiTexture guiTexture;
	private boolean isHidden = true;
	
	public GuiSprite(String texturePath, Vector2f position, Vector2f scale) {
		guiTexture = new GuiTexture(Loader.loadTexture(texturePath), position, scale);
	}
	
	public void show() {
		if(isHidden) {
			GuiRenderer.getGuiList().add(guiTexture);
			isHidden = false;
		}
	}
	
	public void setPositon(Vector2f position) {
		guiTexture.setPosition(position);
	}
	
	public void setScale(Vector2f scale) {
		guiTexture.setScale(scale);
	}

	public void hide() {
		if(!isHidden) {
			GuiRenderer.getGuiList().remove(guiTexture);
			isHidden = true;
		}
	}
	
	public boolean isHidden() {
		return isHidden;
	}
}
