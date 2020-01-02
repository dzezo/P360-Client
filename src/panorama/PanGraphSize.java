package panorama;

import java.io.Serializable;

public class PanGraphSize implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int NORTH;
	private int SOUTH;
	private int WEST;
	private int EAST;
	
	public PanGraphSize() {
		NORTH = SOUTH = WEST = EAST = 0;
	}
	
	public int getCenterX() {
		return (WEST + EAST) / 2;
	}
	
	public int getCenterY() {
		return (NORTH + SOUTH) / 2;
	}
	
	public void updateSize() {
		if(PanGraph.isEmpty()) {
			NORTH = SOUTH = WEST = EAST = 0;
			return;
		}
		
		PanNode node = PanGraph.getHead();
		
		WEST = EAST = node.getMapNode().x;
		NORTH = SOUTH = node.getMapNode().y;
		
		node = node.getNext();
		
		while(node != null) {
			int x = node.getMapNode().x;
			int y = node.getMapNode().y;
			
			if(x <= WEST) WEST = x;
			if(x >= EAST) EAST = x;
			if(y <= NORTH) NORTH = y;
			if(y >= SOUTH) SOUTH = y;
			
			node = node.getNext();
		}
	}
	
}
