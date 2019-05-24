package graph;

public class Node {
	public int id;
	public int connectionCount;
	public Edge edges[];
	
	public Node next;
	public Node prev;
	
	public int repetitionsAllowed = 0;
	
	private Path integratedPath;
	
	public Node(int id) {
		this.id = id;
		edges = new Edge[4];
	}
	
	/**
	 * Funkcija koja upisuje putanju koju cvor mora da obidje pre nego sto se predje na sledeci cvor glavne putanje
	 * @param path je putanja koju integrisemo
	 * @param nodeCount je broj cvorova na putanji
	 */
	public void integratePath(Path path) {
		if(integratedPath == null)
			integratedPath = path;
		else
			integratedPath.concat(path);
	}
	
	/**
	 * Vraca element integrisane putanje
	 */
	public int getIntegratedPath(int i) {
		return integratedPath.getPath()[i];
	}
	
	public int getIntegratedPathLength() {
		if(integratedPath != null)
			return integratedPath.getPath().length;
		else
			return 0;
	}
	
	public void setArticulationPoint(int adjAP) {
		if(adjAP < 2)
			repetitionsAllowed = 2;
		else if (adjAP < 4)
			repetitionsAllowed = 3;
		else
			repetitionsAllowed = 4;
	}
	
	public void resetArticulationPoint() {
		repetitionsAllowed ^= repetitionsAllowed;
	}
	
	public boolean isArticulationPoint() {
		if(repetitionsAllowed == 0)
			return false;
		return true;
	}
	
	public void print() {
		System.out.println("id: " + id);
		System.out.println("repetitions: " + repetitionsAllowed);
		if(integratedPath != null) {
			System.out.print("integrated path: [");
			for (int i = 0; i < integratedPath.getPath().length; i++) {
				if (i < integratedPath.getPath().length - 1)
					System.out.print(integratedPath.getPath()[i] + ", ");
				else
					System.out.println(integratedPath.getPath()[i] + "]");
			}
		}
		System.out.println("connNum: " + connectionCount);
		System.out.println("edges: ");
		for(int i = 0; i < connectionCount; i++) {
			edges[i].print();
		}
		System.out.println("\n---------------");
	}
}
