package gui;

import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import glRenderer.DisplayManager;
import utils.Loader;

public abstract class GuiButton implements IButton {
	
	private GuiTexture guiTexture;
	private Vector2f originalScale;
	private boolean isHidden = true;
	private boolean isHovering = false;
	
	public GuiButton(String texturePath, Vector2f position, Vector2f scale) {
		guiTexture = new GuiTexture(Loader.loadTexture(texturePath), position, scale);
		originalScale = scale;
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

	public void playHoverAnimation(float scaleFactor) {
		guiTexture.setScale(new Vector2f(originalScale.x + scaleFactor, originalScale.y + scaleFactor));
	}

	public void resetScale() {
		guiTexture.setScale(originalScale);
	}
	
	public void update() {
		if(!isHidden) {
			if(mouseOver()){
				if(!isHovering) {
					isHovering = true;
					onStartHover(this);
				}
				
				whileHovering(this);
			}
			else {
				isHovering = false;
				onStopHover(this);
			}
		}
	}
	
	public boolean mouseOver() {
		Vector2f location = guiTexture.getPosition();
		Vector2f scale = guiTexture.getScale();
		Vector2f mouseCoords = DisplayManager.getNormalizedMouseCoords();
		
		return (location.y + scale.y > -mouseCoords.y 
				&& location.y - scale.y < -mouseCoords.y
				&& location.x + scale.x > mouseCoords.x
				&& location.x - scale.x < mouseCoords.x);
	}
	
	public void click() {
		if(mouseOver()) 
			onClick(this);
	}
	
	public void performAction() {
		onClick(this);
	}
	
	/**
	 * Sets gui button positon on xy plane
	 * @param pos - x,y position of gui element, can range from -1 to +1
	 */
	public void setPosition(Vector2f pos) {
		guiTexture.setPosition(pos);
	}
	
	/**
	 * Sets gui button rotation on xy plane
	 * @param rot - rotation is in degrees
	 */
	public void setRotation(Vector3f rot) {
		guiTexture.setRotation(rot);
	}
}
