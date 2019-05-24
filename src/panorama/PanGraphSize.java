package panorama;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PanGraphSize implements Serializable {
	protected int NORTH;
	protected int SOUTH;
	protected int WEST;
	protected int EAST;
	
	public PanGraphSize() {
		NORTH = SOUTH = WEST = EAST = 0;
	}
	
	public int getCenterX() {
		return (WEST + EAST) / 2;
	}
	
	public int getCenterY() {
		return (NORTH + SOUTH) / 2;
	}
	
	public void updateSize(int x, int y) {
		if(x <= WEST) WEST = x;
		if(x >= EAST) EAST = x;
		if(y <= NORTH) NORTH = y;
		if(y >= SOUTH) SOUTH = y;
	}
}
