package frames;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;

import panorama.PanGraph;
import panorama.PanMap;
import panorama.PanNode;

@SuppressWarnings("serial")
public abstract class MapPanel extends JComponent {
	protected MapFrame parent;
	
	protected final static int GRID_SIZE = 10;
	
	protected Color panelColor = new Color(128,128,128);
	
	protected Rectangle panelRect = new Rectangle();
	protected Point mouseClick = new Point(0, 0);
	protected Point origin = new Point(0, 0);
	
	protected PanNode selectedNode1;
	protected PanNode selectedNode2;
	
	private boolean panelDragAllowed;
	
	protected void setPanelDragAllowed(boolean b) {
		panelDragAllowed = b;
	}
	
	protected boolean isPanelDragAllowed() {
		return panelDragAllowed;
	}
	
	protected boolean isNodeSelected(PanMap node) {
		if(selectedNode1 != null && selectedNode1.getMapNode() == node)
			return true;
		if(selectedNode2 != null && selectedNode2.getMapNode() == node)
			return true;
		return false;
	}
	
	protected PanNode getSelectedNode() {
		PanNode selectedNode = null;
		PanNode start = PanGraph.getHead();
		while(start != null) {
			PanMap node = start.getMapNode();
			if(node.isPressed(mouseClick.x, mouseClick.y, origin.x, origin.y)) {
				selectedNode = start;
			}
			start = start.getNext();
		}
		return selectedNode;
	}
	
	protected void dragPanel(int dragX, int dragY) {
		int dx, dy;
		dx = dragX - mouseClick.x;
		dy = dragY - mouseClick.y;
		origin.x += dx;
		origin.y += dy;
		mouseClick.x = dragX;
		mouseClick.y = dragY;
	}
	
	public Point getOrigin() {
		return origin;
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
	
	public static int getGridSize() {
		return GRID_SIZE;
	}
	
	public abstract void paint(Graphics g);
	
	public abstract void setOrigin(int oX, int oY);
}
