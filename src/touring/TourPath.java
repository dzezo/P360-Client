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
	
	/**
	 * Dodaje putokaz na kraj liste putokaza
	 * @param wp - putokaz koji se dodaje
	 * @return redni broj lokacije na koju je dodan putokaz
	 */
	public int add(Waypoint wp) {
		List<Waypoint> path;
		
		if(list2.isEmpty())
			path = list1;
		else
			path = list2;
		
		path.add(wp);
		
		return path.size();
	}
	
	/**
	 * Dodaje putokaz na kraj liste putokaza ukoliko ne postoji takav putokaz 
	 * @param wp - putokaz koji se dodaje
	 * @return false - ukoliko vec postoji takav putokaz
	 */
	public boolean addWithCheck(Waypoint wp) {
		List<Waypoint> path;
		
		if(list2.isEmpty())
			path = list1;
		else
			path = list2;
		
		// Slucaj kada je prazna lista putokaza
		if(path.size() == 0) {
			path.add(wp);
			return true;
		}
		
		Iterator<Waypoint> iterator = path.iterator();
		while(iterator.hasNext()) {
			Waypoint waypoint = iterator.next();
			
			// Slucaj kada isti putokaz vec postoji
			if(waypoint.equals(wp)) {
				return false;
			}
		}
		
		// dodaje putokaz na kraj liste
		path.add(wp);
		return true;
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
		// Nothing to update
		if(list1.isEmpty() && list2.isEmpty()) 
			return;
		
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
		
		int pathIndex = 1;
		Waypoint currWaypoint = path.get(0);
		
		Iterator<Waypoint> iterator = path.iterator();
		while(iterator.hasNext()) {
			Waypoint nextWaypoint = iterator.next();
			
			if(currWaypoint.next.equals(nextWaypoint.node)) {
				currWaypoint.node.tourNum.add(pathIndex++);
				
				visited.add(currWaypoint);
				path.remove(currWaypoint);
				
				currWaypoint = nextWaypoint;
				
				iterator = path.iterator();
			}
		}
		
		currWaypoint.node.tourNum.add(pathIndex++);
		currWaypoint.next.tourNum.add(pathIndex);
		
		visited.addAll(path);
		path.clear();
	}
	
	public void printPath() {
		PanNode path[] = this.getPath();
		
		for(int i = 0; i < path.length; i++) {
			System.out.println(path[i].getID());
		}
		
		System.out.println();
	}
}
