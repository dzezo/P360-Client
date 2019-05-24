package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import panorama.PanGraph;
import panorama.PanMap;
import panorama.PanNode;

@SuppressWarnings("serial")
public abstract class MapPanel extends JComponent {
	protected MapFrame parent;
	
	protected Color panelColor = new Color(128,128,128);
	
	protected int originX = 0;
	protected int originY = 0;
	protected int mouseX;
	protected int mouseY;
	
	protected Rectangle panelRect = new Rectangle();
	
	protected PanNode selectedNode1;
	protected PanNode selectedNode2;
	
	protected boolean isNodeSelected(PanMap node) {
		if(selectedNode1 != null && selectedNode1.getMapNode() == node)
			return true;
		if(selectedNode2 != null && selectedNode2.getMapNode() == node)
			return true;
		return false;
	}
	
	protected PanNode getSelectedNode(int x,int y) {
		PanNode selectedNode = null;
		PanNode start = PanGraph.getHead();
		while(start != null) {
			PanMap node = start.getMapNode();
			if(node.isPressed(x, y, originX, originY)) {
				selectedNode = start;
			}
			start = start.getNext();
		}
		return selectedNode;
	}
	
	protected void dragPanel(int dragX, int dragY) {
		int dx, dy;
		dx = dragX - mouseX;
		dy = dragY - mouseY;
		originX += dx;
		originY += dy;
		mouseX = dragX;
		mouseY = dragY;
	}
	
	public int getOriginX() {
		return -originX;
	}
	
	public int getOriginY() {
		return -originY;
	}

	public PanNode getSelectedNode1() {
		return selectedNode1;
	}
	
	public PanNode getSelectedNode2() {
		return selectedNode2;
	}
	
	public void deselectNodes() {
		if(selectedNode1 != null) {
			selectedNode1.getMapNode().selectNode(false);
			selectedNode1 = null;
		}
		
		if(selectedNode2 != null) {
			selectedNode2.getMapNode().selectNode(false);
			selectedNode2 = null;
		}
	}
	
	public void setParent(MapFrame parentFrame) {
		this.parent = parentFrame;
	}
	
	public abstract void paint(Graphics g);
	
	public abstract void setOrigin(int oX, int oY);
}
