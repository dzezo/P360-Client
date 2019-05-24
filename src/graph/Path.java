package graph;

public class Path {
	private int path[];
	private int visitedNodeCount;
	
	/**
	 * Konstruktor koji kreira hamiltonovu putanju
	 */
	public Path(Node p[], int pLen) {
		path = new int[pLen];
		
		int k = 0;
		// upisi pocetni cvor
		path[k++] = p[0].id;
		// upisi njegovu integrisanu putanju ukoliko je ima
		for(int ii = 0; ii < p[0].getIntegratedPathLength(); ii++)
				path[k++] = p[0].getIntegratedPath(ii);
		
		for(int i = 0; i < p.length; i++) {
			for(int j = 0; j < p[i].connectionCount; j++) {
				// nadji poteg do suseda
				Node nextNode = p[(i+1)%p.length];
				if(p[i].edges[j].destination == nextNode) {
					// upisi medju cvorove
					for(int ii = 0; ii < p[i].edges[j].weight; ii++) {
						path[k++] = p[i].edges[j].path[ii];
					}
					// upisi suseda
					path[k++] = nextNode.id;
					// ukoliko je to bio pocetni cvor napusti petlju
					if(k == pLen)
						break;
					// upisi njegovu integrisanu putanju ukoliko je ima
					for(int ii = 0; ii < nextNode.getIntegratedPathLength(); ii++)
							path[k++] = nextNode.getIntegratedPath(ii);
					break;
				}
			}
		}
		
		visitedNodeCount = countVisitedNodes();
	}
	
	/**
	 * Kreira putanju na osnovu niza indeksa
	 * @param p je niz indeksa cvorova
	 */
	public Path(int p[]) {
		this.path = p;
		visitedNodeCount = countVisitedNodes();
	}
	
	/**
	 * Broji koliko cvorova je putanja obisla
	 */
	private int countVisitedNodes() 
	{ 
		if(path.length == 0) return 0;
		
	    int res = 1; 
	  
	    for (int i = 1; i < path.length; i++) { 
	        int j = 0; 
	        for (j = 0; j < i; j++) 
	            if (path[i] == path[j]) 
	                break; 
	  
	        if (i == j) 
	            res++; 
	    } 
	    return res; 
	} 
	
	public void concat(Path p) {
		int result[] = new int[path.length + p.getPath().length];
		
		int k = 0;
		for(int i = 0; i < path.length; i++)
			result[k++] = path[i];
		for(int i = 0; i < p.getPath().length; i++)
			result[k++] = p.getPath()[i];
		
		path = result;
		visitedNodeCount += p.getVisitedNodeCount();
	}
	
	public int[] getPath() {
		return this.path;
	}
	
	public int getVisitedNodeCount() {
		return this.visitedNodeCount;
	}
	
	public void print() {
		for(int i = 0; i < path.length; i++) {
			if(i%10 == 0)
				System.out.println();
			if(i != path.length - 1)
				System.out.print(path[i] + ", ");
			else
				System.out.println(path[i]);
		}
	}
}
