package gui;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import utils.Loader;

public abstract class GuiSprite implements ISprite {
	private GuiTexture guiTexture;
	private boolean isHidden = true;
	
	public GuiSprite(String texturePath, Vector2f position, Vector2f scale) {
		guiTexture = new GuiTexture(Loader.loadTexture(texturePath), position, scale);
	}
	
	public void show(List<GuiTexture> list) {
		if(isHidden) {
			list.add(guiTexture);
			isHidden = false;
		}
	}

	public void hide(List<GuiTexture> list) {
		if(!isHidden) {
			list.remove(guiTexture);
			isHidden = true;
		}
	}
	
	public boolean isHidden() {
		return isHidden;
	}
}
