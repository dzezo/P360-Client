package frames;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import glRenderer.Scene;
import panorama.PanGraph;
import panorama.PanMap;
import panorama.PanNode;

@SuppressWarnings("serial")
public class MapViewPanel extends MapPanel{
	
	public MapViewPanel() {
		// Listeners
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent press) {
				// getting mouse click location
				mouseX = press.getX();
				mouseY = press.getY();
				
				// check if node is clicked, it will be null if none
				PanNode node = getSelectedNode(mouseX, mouseY);
				
				// if node is clicked and first click is not taken
				if(node != null && selectedNode1 == null)
					selectedNode1 = node;
				// if node is clicked but first click is taken
				else if(node != null)
					selectedNode2 = node;
				// panel click
				else if(node == null)
					deselectNodes();
				
				// do we have first and second click
				if(selectedNode1 != null && selectedNode2 != null) {
					// first and second node click were on the same node
					if(selectedNode2 == selectedNode1)	
						setNextActivePanorama();
					// if they are not the same treat second click as first 
					else{
						selectedNode1 = selectedNode2;
						selectedNode2 = null;
					}
				}
			}
		});
		
		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent drag) {
				if(selectedNode1 == null)
					dragPanel(drag.getX(), drag.getY());
			}			
		});
	}

	public void paint(Graphics g) {
		Graphics2D graphicSettings = (Graphics2D)g;
		graphicSettings.setColor(panelColor);
		graphicSettings.fillRect(0, 0, getWidth(), getHeight());
		
		graphicSettings.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Translates the origin of the Graphics2D context to the point (x, y) in the current coordinate system.
		graphicSettings.translate(originX, originY);
		
		// Calculating drawing panel dimensions
		panelRect.setBounds(-originX, -originY, this.getWidth(), this.getHeight());
		
		// Drawing starts from the head (root), and id zero is assigned to it.
		int id = 0;
		PanNode start = PanGraph.getHead();
		while(start != null) {
			start.setID(id++);
			PanMap node = start.getMapNode();
			node.drawNodeOnMinimap(graphicSettings, panelRect, isNodeSelected(node));
			start = start.getNext();
		}
	}
	
	public void setOrigin(int oX, int oY) {
		originX = -oX;
		originY = -oY;
	}
	
	private void setNextActivePanorama() {
		// Queue image selected panorama
		Scene.queuePanorama(selectedNode1);
		
		// Hide map frame
		this.parent.hideFrame();
	}
	
	/* Controler Selection */
	
	public void selectTop() {
		if(PanGraph.isEmpty()) return;
		
		if(selectedNode1 == null) {
			setSelectedNode1(Scene.getActivePanorama());
			return;
		}
		
		PanNode topNode;
		if(selectedNode1.getTop() == null) {	
			PanMap selectedNode = selectedNode1.getMapNode();
			
			PanMap closestNode = null;
			double closestDistance = Double.MAX_VALUE;
			PanNode start = PanGraph.getHead();
			while(start != null) {
				PanMap node = start.getMapNode();
				
				// if node is above selected
				if(selectedNode.y > node.y) {
					double distance = getDistance(selectedNode, node);
					if(distance < closestDistance) {
						closestDistance = distance;
						closestNode = node;
					}
				}
				
				start = start.getNext();
			}
			
			// result is in closestNode
			if(closestNode == null) return;
			
			topNode = closestNode.getParent();
		}
		else {
			topNode = selectedNode1.getTop();
		}
		
		setSelectedNode1(topNode);
	}
	
	public void selectRight() {
		if(PanGraph.isEmpty()) return;
		
		if(selectedNode1 == null) {
			setSelectedNode1(Scene.getActivePanorama());
			return;
		}
		
		PanNode rightNode;
		if(selectedNode1.getRight() == null) {	
			PanMap selectedNode = selectedNode1.getMapNode();
			
			PanMap closestNode = null;
			double closestDistance = Double.MAX_VALUE;
			PanNode start = PanGraph.getHead();
			while(start != null) {
				PanMap node = start.getMapNode();
				
				// if node is right of selected
				if(selectedNode.x < node.x) {
					double distance = getDistance(selectedNode, node);
					if(distance < closestDistance) {
						closestDistance = distance;
						closestNode = node;
					}
				}
				
				start = start.getNext();
			}
			
			// result is in closestNode
			if(closestNode == null) return;
			
			rightNode = closestNode.getParent();
		}
		else {
			rightNode = selectedNode1.getRight();
		}
		
		setSelectedNode1(rightNode);
	}
	
	public void selectBot() {
		if(PanGraph.isEmpty()) return;
		
		if(selectedNode1 == null) {
			setSelectedNode1(Scene.getActivePanorama());
			return;
		}
		
		PanNode botNode;
		if(selectedNode1.getBot() == null) {	
			PanMap selectedNode = selectedNode1.getMapNode();
			
			PanMap closestNode = null;
			double closestDistance = Double.MAX_VALUE;
			PanNode start = PanGraph.getHead();
			while(start != null) {
				PanMap node = start.getMapNode();
				
				// if node is below selected
				if(selectedNode.y < node.y) {
					double distance = getDistance(selectedNode, node);
					if(distance < closestDistance) {
						closestDistance = distance;
						closestNode = node;
					}
				}
				
				start = start.getNext();
			}
			
			// result is in closestNode
			if(closestNode == null) return;
			
			botNode = closestNode.getParent();
		}
		else {
			botNode = selectedNode1.getBot();
		}
		
		setSelectedNode1(botNode);
	}
	
	public void selectLeft() {
		if(PanGraph.isEmpty()) return;
		
		if(selectedNode1 == null) {
			setSelectedNode1(Scene.getActivePanorama());
			return;
		}
		
		PanNode leftNode;
		if(selectedNode1.getLeft() == null) {	
			PanMap selectedNode = selectedNode1.getMapNode();
			
			PanMap closestNode = null;
			double closestDistance = Double.MAX_VALUE;
			PanNode start = PanGraph.getHead();
			while(start != null) {
				PanMap node = start.getMapNode();
				
				// if node is left of selected
				if(selectedNode.x > node.x) {
					double distance = getDistance(selectedNode, node);
					if(distance < closestDistance) {
						closestDistance = distance;
						closestNode = node;
					}
				}
				
				start = start.getNext();
			}
			
			// result is in closestNode
			if(closestNode == null) return;
			
			leftNode = closestNode.getParent();
		}
		else {
			leftNode = selectedNode1.getLeft();
		}
		
		setSelectedNode1(leftNode);
	}
	
	private double getDistance(PanMap n1, PanMap n2) {
		int x1 = n1.x;
		int y1 = n1.y;
		int x2 = n2.x;
		int y2 = n2.y;
		return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
	}
	
	public void confirmSelection() {
		if(PanGraph.isEmpty()) return;
		
		setNextActivePanorama();
	}
	
	private void setSelectedNode1(PanNode selected) {
		// deselect prev
		if(selectedNode1 != null) {
			PanMap prevSelectedNode = selectedNode1.getMapNode();
			prevSelectedNode.selectNode(false);
		}
		
		// select new
		selectedNode1 = selected;
		
		// show on gui
		PanMap selectedNode = selected.getMapNode();
		selectedNode.selectNode(true);
		
		// center on selectedNode
		// node coords
		int x = selectedNode.x;
		int y = selectedNode.y;
		
		// node size
		int h = PanMap.HEIGHT / 2;
		int w = PanMap.WIDTH / 2;
		
		// panel size
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		
		setOrigin((x + w) - centerX, (y + h) - centerY);
	}

}
