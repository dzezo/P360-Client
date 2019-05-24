package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import panorama.PanNode;

public class NodeList {
	private Node head;
	private Node tail;
	
	private int rootID;
	private Node rootNode;
	
	private int totalNodeCount; // broj cvorova u grafu
	private int reducedNodeCount; // broj cvorova u grafu nakon redukcije
	private int pathNodeCount;
	
	private Node path[];
	private Path bestPath;
	private int pathLength;
	private boolean pathFound;
	
	public NodeList(PanNode firstNode, PanNode rootNode) {
		this.rootID = rootNode.getID();
		this.reducedNodeCount = 0;
		this.totalNodeCount = 0;
		// napravi graf
		createNode(firstNode);
		// redukuj graf
		boolean simplification = false;
		boolean reduction = false;
		do {
			// izbaci nepotrebne cvorove
			simplification = simplifyGraph();
			// izbaci listove
			reduction = removeLeaves();
			if(!reduction) {
				findArticulationPoint();
				reduction = removeExtraConnections();
			}
		} while(simplification || reduction);
	}
	
	private void add(Node newNode) {
		// prvo dodavanje
		if(head == null) {
			head = newNode;
			tail = newNode;
		}
		// svako sledece
		else {
			tail.next = newNode;
			newNode.prev = tail;
			tail = newNode;
		}
		reducedNodeCount++;
		totalNodeCount++;
	}
	
	private void remove(Node node) {
		// ako uklanjamo glavu
		if(node.prev == null)
			head = node.next;
		else
			node.prev.next = node.next;
			
		// ako uklanjamo rep
		if(node.next == null)
			tail = node.prev;
		else
			node.next.prev = node.prev;
			
		
		reducedNodeCount--;
	}
	
	private Node searchForNode(int id) {
		Node result = null;
		
		Node n = head;
		while(n != null) {
			if(n.id == id) {
				result = n;
				break;
			}
			n = n.next;
		}
		
		return result;
	}
	
	/**
	 * Ukoliko cvor ne postoji kreira se i dodaje u graf. Zatim se spaja sa svojim potomcima.
	 * Pre spajanja se proverava da li potomak postoji, ukoliko ne postoji kreira se sa svim svojim potomcima pa se tek onda spoji
	 * @return - kreirani cvor
	 */
	private Node createNode(PanNode pnode) {
		Node n = new Node(pnode.getID());
		this.add(n);
		
		if(n.id == rootID)
			rootNode = n;
		
		PanNode connections[] = new PanNode[4];
		connections[0] = pnode.getTop();
		connections[1] = pnode.getRight();
		connections[2] = pnode.getBot();
		connections[3] = pnode.getLeft();
		
		int connectionCount = 0;
		for(int i=0; i < 4; i++) {
			if(connections[i] != null) {
				Node connectedNode = this.searchForNode(connections[i].getID());
				// ukoliko cvor sa kojim se cvor n spaja ne postoji
				if(connectedNode == null) {
					// kreiraj cvor
					connectedNode = createNode(connections[i]);
				}
				if(connectedNode.connectionCount == 0)
					n.edges[connectionCount] = new Edge(connectedNode);
				else
					// kada se cvor kreira nadji ID konekcije do cvora n
					for(int j = 0; j < connectedNode.connectionCount; j++)
						if(connectedNode.edges[j].destination == n) {
							// napravi konekciju sa cvora n do kreiranog cvora
							n.edges[connectionCount] = new Edge(connectedNode, connectedNode.edges[j].id);
							break;
						}
					
				connectionCount++;
			}
		}
		
		n.connectionCount = connectionCount;
		return n;
	}
	
	/**
	 * Obilazi se graf i izabacuju se cvorovi sa dve veze
	 */
	private boolean simplifyGraph() {
		boolean isSimplified = false;
		
		Node node = head;
		
		while(node != null) {
			if(node.connectionCount == 2 && node.id != rootID) {
				int weight = node.edges[0].weight + node.edges[1].weight + 1;
				
				// obe veze cvora koga izbacujemo ukazuju na isti cvor (loop)
				if(node.edges[0].destination == node.edges[1].destination) {
					// odredisni cvor koji azuriramo
					Node nodeDst = node.edges[0].destination;
					
					// kreiramo novu putanju i nove potege
					int path[] = new int[weight + node.getIntegratedPathLength() + 1];
					int newPathIndex = 0;
					Edge newEdges[] = new Edge[nodeDst.connectionCount - 2];
					int newEdgeIndex = 0;
					for(int i = 0; i < nodeDst.connectionCount; i++) {
						if(nodeDst.edges[i].destination != node) {
							newEdges[newEdgeIndex++] = nodeDst.edges[i];
						}
						// prva veza => od nodeDst do cvora koji se brise
						else if(newPathIndex == 0) {
							for(int j = 0; j < nodeDst.edges[i].weight; j++)
								path[newPathIndex++] = nodeDst.edges[i].path[j];
							// upisi cvor koji se brise i njegovu integrisanu putanju
							path[newPathIndex++] = node.id;
							for(int k = 0; k < node.getIntegratedPathLength(); k++)
								path[newPathIndex++] = node.getIntegratedPath(k);
						}
						// druga veza => od cvora koji se brise do nodeDst
						else {
							for(int k = nodeDst.edges[i].weight-1; k >= 0; k--)
								path[newPathIndex++] = nodeDst.edges[i].path[k];
							// polazni/kranji cvor (loop)
							path[newPathIndex++] = nodeDst.id;
						}
					}
					
					// azuriranje odredisnog cvora
					nodeDst.integratePath(new Path(path));
					nodeDst.connectionCount -= 2;
					nodeDst.edges = newEdges;
				}
				// u suprotnom je potrebno azurirati cvorove na oba kraja
				else {
					int newEdgeID = node.edges[0].id;
					for (int edgeIndex = 0; edgeIndex < node.connectionCount; edgeIndex++) {
						Node nodeDst = node.edges[edgeIndex].destination;
						int oppositeEdgeIndex = (edgeIndex+1)%node.connectionCount;
						
						// kreiramo novu putanju
						int path[] = new int[weight + node.getIntegratedPathLength()];
						int newPathIndex = 0;
						// upisi putanju od cvora koga azuriramo do cvora koji se brise
						for(int k = node.edges[edgeIndex].weight-1; k >= 0; k--)
							path[newPathIndex++] = node.edges[edgeIndex].path[k];
						// upisi cvor koji se brise i njegovu integrisanu putanju
						path[newPathIndex++] = node.id;
						for(int k = 0; k < node.getIntegratedPathLength(); k++)
							path[newPathIndex++] = node.getIntegratedPath(k);
						// upisi putanju od cvora koji se brise do nove destinacije
						for(int k = 0; k < node.edges[oppositeEdgeIndex].weight; k++)
							path[newPathIndex++] = node.edges[oppositeEdgeIndex].path[k];

						// trazimo vezu ka cvoru koga izbacujemo
						for (int j = 0; j < nodeDst.connectionCount; j++) {
							if(nodeDst.edges[j].destination == node) {
								// azuriramo vezu
								nodeDst.edges[j].id = newEdgeID;
								nodeDst.edges[j].weight = path.length;
								nodeDst.edges[j].destination = node.edges[oppositeEdgeIndex].destination;
								nodeDst.edges[j].path = path;
								
								// napusti petlju
								break;
							}
						}	
					}
				}
				
				// izbaci cvor
				remove(node);
				if(!isSimplified) 
					isSimplified = true;
			}
			// sledeci cvor
			node = node.next;
		}
		
		return isSimplified;
	}
	
	/**
	 * Funkcija uklanja cvorove sa jednom konekcijom, 
	 * tako sto u roditeljskom cvoru upisuje putanju do cvora koji se uklanja.
	 * Vraca true ukoliko je neki cvor uklonjen
	 */
	private boolean removeLeaves() {
		boolean isNodeRemoved = false;
		
		Node n = head;
		while (n != null) {
			if(n.connectionCount == 1 && n.id != rootID) {
				Node parent = n.edges[0].destination;
				Node leaf = n;
				
				Edge newParentEdges[] = new Edge[parent.connectionCount - 1];
				Edge edgeToLeaf, edgeToParent;
				
				int k = 0;
				for(int i = 0; i < parent.connectionCount; i++) {
					if(parent.edges[i].destination != leaf) {
						newParentEdges[k++] = parent.edges[i];
					}
					else {
						edgeToLeaf = parent.edges[i];
						edgeToParent = leaf.edges[0];
						
						// generisanje putanje
						int pathIndex = 0;
						int path[] = new int[2*(edgeToLeaf.weight + 1) + leaf.getIntegratedPathLength()];
						// putanja do lista
						for(int j = 0; j < edgeToLeaf.weight; j++)
							path[pathIndex++] = edgeToLeaf.path[j];
						// list
						path[pathIndex++] = leaf.id;
						// integrisana putanja ovog lista
						for(int j = 0; j < leaf.getIntegratedPathLength(); j++)
							path[pathIndex++] = leaf.getIntegratedPath(j);
						// putanja nazad do roditelja
						for(int j = 0; j < edgeToParent.weight; j++)
							path[pathIndex++] = edgeToParent.path[j];
						// roditelj
						path[pathIndex++] = parent.id;
						
						parent.integratePath(new Path(path));
					}
				}
				
				// smanji broj potega
				parent.edges = newParentEdges;
				parent.connectionCount--;
				
				// izbaci list
				remove(leaf);
				if(!isNodeRemoved)
					isNodeRemoved = true;
			}
			
			n = n.next;
		}
		
		return isNodeRemoved;
	}
	
	/**
	 * Funkcija koja stampa sve cvorove grafa
	 */
	public void print() {
		Node n = head;
		while(n != null) {
			n.print();
			// stampaj sledeci
			n = n.next;
		}
	}
	
	public int[] generatePath() {	
		int i = 0;
		path[i++] = rootNode;
		pathLength = i;
		pathFound = false;
		
		long time = System.currentTimeMillis();
		hamiltonian(i);
		System.out.println("Putanja je generisana.");
		System.out.println("Broj neposecenih cvorova je: " + (totalNodeCount - bestPath.getVisitedNodeCount()));
		System.out.println("Vreme generisanje je: " + ((System.currentTimeMillis() - time) / 1000.0f));
		
		return bestPath.getPath();
	}
	
	/**
	 * Funkcija za nalazenje Hamiltonove putanje. Hamiltonova putanja je putanja u grafu koja obilazi svaki cvor tacno jednom.
	 * @param next - indeks na kome se smesta sledeci cvor putanje
	 */
	private void hamiltonian(int next) {
		int midPathLen;
		 
		if(next == pathNodeCount) {
			// u slucaju da se graf sastoji od jednog cvora onda nema potega
			if(pathNodeCount == 1) {
				// na duzinu putanje se dodaje duzina putanje do listova
				midPathLen = (path[next-1].getIntegratedPathLength());
				pathLength += midPathLen;
			}
			else {
				// Trazi se indeks potega koji povezuje poslednji cvor sa pocetnim, kako bi se izracunala duzina medju-putanje
				int edgeIndex = 0;
				while(path[next-1].edges[edgeIndex].destination != rootNode)
					edgeIndex++;
				
				// na duzinu putanje se dodaje duzina putanje izmedju poslednjeg cvora path[next-1] i startnog cvora path[0]
				midPathLen = (path[next-1].getIntegratedPathLength() + path[next-1].edges[edgeIndex].weight + 1);
				pathLength += midPathLen;
			}
			
			// putanja je prvi put generisana
			if(bestPath == null) {
				bestPath = new Path(path, pathLength);
			}
			// putanja ukoliko je nova putanja obisla vise cvorova
			else {
				Path newPath = new Path(path, pathLength);
				if(newPath.getVisitedNodeCount() > bestPath.getVisitedNodeCount())
					bestPath = newPath;
			}
			
			// da li su svi cvorovi poseceni
			if(totalNodeCount == bestPath.getVisitedNodeCount()) {
				pathFound = true;
				return;
			}
			
			// probaj drugaciju putanju
			pathLength -= midPathLen;
			return;
		}
		
		for(int edgeIndex = 0; edgeIndex < path[next-1].connectionCount; edgeIndex++) {
			if(nextNode(next, edgeIndex)) {
				// na duzinu putanje se dodaje duzina putanje izmedju cvora path[next-1] i odabranog cvora path[next]
				midPathLen = (path[next-1].getIntegratedPathLength() + path[next-1].edges[edgeIndex].weight + 1);
				pathLength += midPathLen;
				
				hamiltonian(next + 1);
				// kod koji se izvrsava nakon rekurzivnog vracanja
				if(pathFound) return;
				
				// ukoliko putanja nije pronadjena vrati vrednosti
				pathLength -= midPathLen;
			}
		}
	}
	
	/**
	 * Dodaje novi cvor na putanju
	 * @param next - indeks na kome se smesta novi cvor putanje
	 * @param edgeIndex - indeks potega koji vodi do cvora koga zelimo da dodamo na putanju
	 * @return - dodani cvor ili null (ukoliko su ti cvorovi vec poseceni)
	 */
	private boolean nextNode(int next, int edgeIndex) {
		path[next] = path[next-1].edges[edgeIndex].destination;
		
		// sprecava pojavu petlje izmedju dve artikulacione tacke
		if(next > 1)
			if(path[next-2] == path[next]) 
				return false;
				
		boolean visited = false;
		int repetitionCount = 0;
		for(int j = 0; j < next; j++) {
			if(path[j] == path[next]) {
				repetitionCount++;
				if(repetitionCount >= path[next].repetitionsAllowed) {
					visited = true;
					break;
				}
			}
		}
		
		if(!visited) {
			if(next < pathNodeCount-1 || (next == pathNodeCount-1) && isConnectedToRoot(path[next]))
				return true;
		}
		
		// probaj sledeci poteg
		return false;
	}
	
	/**
	 * Proverava da li prosledjeni cvor ima poteg ka izvorisnom cvoru i vraca rezultat
	 */
	private boolean isConnectedToRoot(Node n) {
		for(int i = 0; i < n.connectionCount; i++)
			if(n.edges[i].destination == rootNode)
				return true;
		return false;
	}

	/**
	 * Funkcija koja trazi tacke artikulacije u grafu. Tacka artikulacije je tacka u grafu bez koje bi se graf raspao
	 */
	private void findArticulationPoint(){
		int time = 0;
		
		Set<Node> visited = new HashSet<>();
		Set<Node> articulationPoints = new HashSet<>();
		
		Map<Node, Integer> visitedTime = new HashMap<>();
		Map<Node, Integer> lowTime = new HashMap<>();
		Map<Node, Node> parent = new HashMap<>();
		
		DFS(visited, articulationPoints, head, time, visitedTime, lowTime, parent);
		
		// Postavi broj mogucih ponavljanja
		Iterator<Node> iterator = articulationPoints.iterator();
		while(iterator.hasNext()) {
			Node node = iterator.next();
			
			int i,j;
			int adjAP = 0;
			if(articulationPoints.contains(node.edges[0].destination))
				adjAP++;
			for(i = 1; i < node.connectionCount; i++) {
				for(j = 0; j < i; j++)
					if(node.edges[j].destination == node.edges[i].destination)
						break;
				if(i == j && articulationPoints.contains(node.edges[i].destination))
					adjAP++;
			}
			
			node.setArticulationPoint(adjAP);
			
		}
		
		// Postavi ocekivanu duzinu putanje
		Node start = head;
		pathNodeCount = reducedNodeCount;
		while (start != null) {
			if(start.isArticulationPoint())
				pathNodeCount += start.repetitionsAllowed - 1;
			start = start.next;
		}
		path = new Node[pathNodeCount];
	}
	
	/**
	 * Funkcija koja obilazi graf po dubini i ispituje da li neki od cvorova ispunjava uslov da bude tacka artikulacije.
	 * Ukoliko postoje takve tacke, one se na kraju smestaju u kolekciju articulationPoints 
	 */
	private void DFS(Set<Node> visited, Set<Node> articulationPoints, Node node, int time, 
			Map<Node, Integer> visitedTime, Map<Node, Integer> lowTime, Map<Node, Node> parent) 
	{
		visited.add(node);
		visitedTime.put(node, time);
		lowTime.put(node, time);
		time++;
		
		int childCount = 0;
		boolean isArticulationPoint = false;
		node.resetArticulationPoint();
		
		for(int i = 0; i < node.connectionCount; i++) {
			Node adj = node.edges[i].destination;
			
			if(adj.equals(parent.get(node))) {
				continue;
			}
			
			if(!visited.contains(adj)) {
				parent.put(adj, node);
				childCount++;
				
				DFS(visited, articulationPoints, adj, time, visitedTime, lowTime, parent);
				
				if(visitedTime.get(node) <= lowTime.get(adj)) {
					isArticulationPoint = true;
				}
				else {
					lowTime.compute(node, (n, t) -> Math.min(t, lowTime.get(adj)));
				}
			}
			else {
				lowTime.compute(node, (n, t) -> Math.min(t, visitedTime.get(adj)));
			}
		}
		
		// Cvor je tacka artikulacije:
		// 1. Ukoliko je koren grafa sa dva suseda
		// 2. visitedTime cvora <= lowTime nekog suseda
		if((parent.get(node) == null && childCount >= 2) || 
				(parent.get(node) != null && isArticulationPoint)) 
		{
			articulationPoints.add(node);
		}
		
	}
	
	/**
	 * Funkcija koja nalazi visestruke konekcije izmedju dva cvora i otklanja ih, 
	 * vraca true ukoliko je neka veza uklonjena
	 */
	private boolean removeExtraConnections() {
		boolean isConnectionRemoved = false;
		
		Node node = head;
		
		int connCount;
		Edge connections[] = new Edge[4];
		
		while(node != null) {
			if(node.connectionCount > 2) {
				int i = 0;
				while(i < node.connectionCount-1) {
					connCount = 0;
					connections[connCount++] = node.edges[i];
					
					for(int j = i+1; j < node.connectionCount; j++)
						if(node.edges[i].destination == node.edges[j].destination)
							connections[connCount++] = node.edges[j];
					
					boolean rem = false;
					if(connCount > 1)
						rem = removeConnections(node, connections, connCount);
					
					if(rem) {
						if(!isConnectionRemoved)
							isConnectionRemoved = true;
						
						if(connCount > 2)
							break;
						else
							i = 0;
					}
					else {
						i++;
					}
				}
			}
			
			// sledeci cvor
			node = node.next;
		}
		
		return isConnectionRemoved;
	}
	
	/**
	 * Funkcija koja iz skupa visestrukih konekcija ka istom cvoru (connections) bira konekciju/konekcije za brisanje,
	 * izbor zavisi od tipa povezanih cvorova (da li su cvorovi artikulacione tacke ili ne) i broja visestrukih konekcija.
	 * Vraca true ukoliko je ova f-ja pozvala f-ju za brisanje potega
	 */
	private boolean removeConnections(Node nodeA, Edge[] connections, int connCount) {
		Node nodeB = connections[0].destination;
		
		if(connCount == 2) {
			if(!nodeA.isArticulationPoint() && !nodeB.isArticulationPoint()) {
				if(connections[0].weight < connections[1].weight)
					removeConnection(nodeA, nodeB, connections[0]);
				else
					removeConnection(nodeA, nodeB, connections[1]);
				return true;
			}
		}
		else if(connCount == 3) {
			if(!nodeA.isArticulationPoint() && !nodeB.isArticulationPoint() &&
					nodeA.connectionCount == 4 && nodeB.connectionCount == 4) 
			{
				Edge conn1, conn2;
				conn1 = connections[0];
				conn2 = null;
				
				for(int i = 1; i < connCount; i++) {
					if(conn1.weight > connections[i].weight) {
						conn2 = conn1;
						conn1 = connections[i];
					}
					else if(conn2 == null)
						conn2 = connections[i];
					else if(conn2.weight > connections[i].weight)
						conn2 = connections[i];
				}
				
				removeConnection(nodeA, nodeB, conn1);
				removeConnection(nodeA, nodeB, conn2);
				return true;
			}
			else {
				Edge conn1;
				conn1 = connections[0];
				
				for(int i = 1; i < connCount; i++)
					if(conn1.weight > connections[i].weight)
						conn1 = connections[i];
				
				removeConnection(nodeA, nodeB, conn1);
				return true;
			}
		}
		else if(connCount == 4) {
			int pathLen = 0;
			for(int i = 0; i < connCount; i++)
				pathLen += connections[i].weight + 1;
			
			Node start, end;
			if(nodeA.equals(rootNode)) {
				start = nodeA;
				end = nodeB;
			}
			else {
				start = nodeB;
				end = nodeA;
			}
			
			int pi = 0;
			int path[] = new int[pathLen];
			
			for(int i = 0; i < connCount; i++) {
				for(int j = 0; j < start.edges[i].weight; j++) {
					if(i%2 == 0) {
						path[pi++] = start.edges[i].path[j];
					}
					else {
						int ii = (start.edges[i].weight-1)-j;
						path[pi++] = start.edges[i].path[ii];
					}
				}
				
				if(i%2 == 0) {
					path[pi++] = end.id;
				}
				else {
					path[pi++] = start.id;
				}
			}
			
			start.connectionCount = 0;
			start.edges = new Edge[4];
			start.integratePath(new Path(path));
			
			end.connectionCount = 0;
			remove(end);
			return true;
		}
		
		// naredba za brisanje nije izdata
		return false;
	}
	
	/**
	 * Funkcija koja uklanja konekciju (connection) izmedju dva cvora
	 */
	private void removeConnection(Node nodeA, Node nodeB, Edge connection) {
		Edge newEdgesA[] = new Edge[nodeA.connectionCount - 1];
		Edge newEdgesB[] = new Edge[nodeB.connectionCount - 1];
		Edge edgeA = null;
		
		int k = 0;
		for(int i = 0; i < nodeA.connectionCount; i++) {
			if(nodeA.edges[i].id == connection.id)
				edgeA = nodeA.edges[i];
			else
				newEdgesA[k++] = nodeA.edges[i];
		}
		
		k = 0;
		for(int i = 0; i < nodeB.connectionCount; i++) {
			if(nodeB.edges[i].id != connection.id)
				newEdgesB[k++] = nodeB.edges[i];
		}
		
		nodeA.connectionCount--;
		nodeA.edges = newEdgesA;
		nodeB.connectionCount--;
		nodeB.edges = newEdgesB;
		
		if(connection.weight != 0) {
			int path[] = new int[2*edgeA.weight];
			
			int pi = 0;
			for(int i = 0; i < edgeA.weight; i++)
				path[pi++] = edgeA.path[i];
			for(int i = edgeA.weight-2; i >= 0; i--)
				path[pi++] = edgeA.path[i];
			path[pi] = nodeA.id;
			
			nodeA.integratePath(new Path(path));
		}
		
	}
	
}
