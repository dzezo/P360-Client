package touring;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import panorama.PanNode;

@SuppressWarnings("serial")
public class TourPath implements Serializable {
	public static final int WAYPOINT_NOT_FOUND = -1;
	
	private List<Waypoint> list1 = new LinkedList<Waypoint>();
	private List<Waypoint> list2 = new LinkedList<Waypoint>();
	
	public int add(Waypoint wp) {
		List<Waypoint> path;
		
		if(list2.isEmpty())
			path = list1;
		else
			path = list2;
		
		path.add(wp);
		
		return path.size();
	}
	
	public int remove(Waypoint waypointToRemove) {
		int removeIndex = 1;
		
		List<Waypoint> path;
		if(list2.isEmpty())
			path = list1;
		else
			path = list2;
		
		Iterator<Waypoint> iterator = path.iterator();
		while(iterator.hasNext()) {
			Waypoint waypoint = iterator.next();
			
			if(waypoint.equals(waypointToRemove)) {
				path.remove(waypoint);
				return removeIndex;
			}
			
			removeIndex++;
		}
		
		// PATH NOT FOUND
		return WAYPOINT_NOT_FOUND;
	}
	
	public boolean hasPath() {
		if(list1.isEmpty() && list2.isEmpty())
			return false;
		else
			return true;
	}
	
	public PanNode[] getPath() {
		List<Waypoint> path;
		List<Waypoint> visited;
		
		if(list2.isEmpty()) {
			path = list1;
			visited = list2;
		}
		else {
			path = list2;
			visited = list1;
		}
			
		List<PanNode> p = new ArrayList<PanNode>();
		
		Waypoint currWaypoint = path.get(0);
		
		Iterator<Waypoint> iterator = path.iterator();
		while(iterator.hasNext()) {
			Waypoint nextWaypoint = iterator.next();
			
			if(currWaypoint.next.equals(nextWaypoint.node)) {
				p.add(currWaypoint.node);
				
				visited.add(currWaypoint);
				path.remove(currWaypoint);
				
				currWaypoint = nextWaypoint;
				
				iterator = path.iterator();
			}
		}
		
		p.add(currWaypoint.node);
		p.add(currWaypoint.next);
		
		visited.addAll(path);
		path.clear();
		
		PanNode tour[] = new PanNode[p.size()];
		tour = p.toArray(tour);
		
		return tour;
	}
	
	public void clearPath() {
		list1.clear();
		list2.clear();
	}
	
	public void updateTourNumbers() {
		List<Waypoint> path;
		if(list2.isEmpty())
			path = list1;
		else
			path = list2;
		
		int newIndex = 1;
		
		Iterator<Waypoint> iterator = path.iterator();
		while(iterator.hasNext()) {
			Waypoint nextWaypoint = iterator.next();
			
			nextWaypoint.node.tourNum.add(newIndex++);
			
			// On last waypoint add final tourNum
			if(!iterator.hasNext()) nextWaypoint.next.tourNum.add(newIndex);
		}
	}
	
	public void printPath() {
		PanNode path[] = this.getPath();
		
		for(int i = 0; i < path.length; i++) {
			System.out.println(path[i].getID());
		}
		
		System.out.println();
	}
}
