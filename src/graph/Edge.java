package graph;

public class Edge {
	private static int availID = 0;
	public int id;
	
	public Node destination;
	public int weight = 0;
	public int path[];
	
	/**
	 * Konstruktor za slucaj kada se pravi prvi poteg izmedju dva cvora
	 */
	public Edge(Node n) {
		this.id = availID++;
		this.destination = n;
	}
	
	/**
	 * Konstruktor za slucaj kada izmedju dva cvora vec postoji poteg
	 */
	public Edge(Node n, int id) {
		this.id = id;
		this.destination = n;
	}
	
	public void print() {
		System.out.print("[ id: " + id + ", w: " + weight + ", dst: " + destination.id + ", (");
		for(int j = 0; j < weight; j++)
			if(j < weight - 1)
				System.out.print(path[j] + ", ");
			else
				System.out.print(path[j]);
		System.out.println(")];");
	}
}
