package touring;

import java.io.Serializable;

import panorama.PanNode;

@SuppressWarnings("serial")
public class Waypoint implements Serializable{
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
