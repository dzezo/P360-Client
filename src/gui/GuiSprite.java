package gui;

import org.lwjgl.util.vector.Vector2f;

import loaders.VertexLoader;

public abstract class GuiSprite implements ISprite {
	private GuiTexture guiTexture;
	private boolean isHidden = true;
	
	public GuiSprite(String texturePath, Vector2f position, Vector2f scale) {
		guiTexture = new GuiTexture(VertexLoader.loadTexture(texturePath), position, scale);
	}
	
	public void show() {
		if(isHidden) {
			GuiRenderer.getGuiList().add(guiTexture);
			isHidden = false;
		}
	}
	
	public void setPositon(float x, float y) {
		guiTexture.setPosition(x, y);
	}
	
	public void setScale(float sx, float sy) {
		guiTexture.setScale(sx, sy);
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
