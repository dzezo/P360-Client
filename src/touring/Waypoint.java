package touring;

import java.io.Serializable;

import panorama.PanNode;

public class Waypoint implements Serializable{
	private static final long serialVersionUID = 1L;
	
	protected PanNode node;
	protected PanNode next;
	
	public Waypoint(PanNode node, PanNode next) {
		this.node = node;
		this.next = next;
	}
	
	public boolean equals(Waypoint wp) {
		return (this.node.equals(wp.node)
				&& this.next.equals(wp.next));
	}
}
