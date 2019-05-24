package panorama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import graph.NodeList;
import touring.TourPath;
import touring.Waypoint;
import utils.AutoLoad;
import utils.AutoSave;
import utils.ChooserUtils;
import utils.DialogUtils;
import utils.ImageCipher;

public class PanGraph {
	public static final String DEFAULT_NAME = "New Map";
	
	private static PanNode head;
	private static PanNode home;
	private static String name;
	private static int nodeCount = 0;
	
	// graph scale
	private static PanGraphSize size = new PanGraphSize();
	
	// graph path
	private static TourPath tour = new TourPath();
	
	// graph display mode
	private static boolean textMode = false;
	
	/* map creation functionality */
	
	public static void addNode(String panoramaPath, int originX, int originY) {
		// Create node
		PanNode newNode = new PanNode(panoramaPath, originX, originY);
		
		// Incase there is no home(starting) panorama set;
		if(home == null)
			setHome(newNode);
		// Check if there is root node (linked list head)
		// if the root exist add this node to existing list
		// else set this node as root for the entire class
		if(head != null) {
			setTail(newNode);
		}
		else
			setHead(newNode);
		
		// Update node count
		nodeCount++;
		
		// Update map size
		updateMapSize();
	}
	
	public static void deleteNode(PanNode selectedPanorama) {
		// Remove his connections
		if(selectedPanorama.getLeft() != null) {
			selectedPanorama.getLeft().setRight(null);
			selectedPanorama.setLeft(null);
		}
		if(selectedPanorama.getRight() != null) {
			selectedPanorama.getRight().setLeft(null);
			selectedPanorama.setRight(null);
		}
		if(selectedPanorama.getTop() != null) {
			selectedPanorama.getTop().setBot(null);
			selectedPanorama.setTop(null);
		}
		if(selectedPanorama.getBot() != null) {
			selectedPanorama.getBot().setTop(null);
			selectedPanorama.setBot(null);
		}
		// Remove node from linked list
		// If selected node is head of linked list, new head would be next node after head
		// else its somewhere in the list and needs to be skiped
		if(head == selectedPanorama) {
			head = head.getNext();
		}
		else {
			PanNode start = head;
			// if the current node is not next to selected node go further
			while(start.getNext() != selectedPanorama) {
				start = start.getNext();
			}
			// when this loop breaks next node from the start would be the one that should be skiped
			// set next node as the next node of a selected node
			start.setNext(start.getNext().getNext());
		}
		if(home == selectedPanorama) {
			// If it was starting panorama then assign head as a starting panorama
			// If it was last node then head would be null by now which will make home null
			if(head != null)
				setHome(head);
			else
				home = null;
		}
		
		// Update node count
		nodeCount--;
		
		// Update map size
		updateMapSize();
	}

	public static void setHome(PanNode node) {
		home = node;
	}
	
	public static void connectNodes(PanNode node1, PanNode node2) {
		// Check if this nodes are already connected
		boolean alreadyConnected = false;
		if(node1.getLeft() == node2)
			alreadyConnected = true;
		else if(node1.getRight() == node2)
			alreadyConnected = true;
		else if(node1.getTop() == node2)
			alreadyConnected = true;
		else if(node1.getBot() == node2)
			alreadyConnected = true;
		if(alreadyConnected) {
			DialogUtils.showMessage("Connection between selected nodes already exists.", "Connection Aborted");
			return;
		}
		// Determine connection port
		int width = PanMap.WIDTH;
		int height = PanMap.HEIGHT;
		int x1 = node1.getMapNode().x;
		int y1 = node1.getMapNode().y;
		int x2 = node2.getMapNode().x;
		int y2 = node2.getMapNode().y;
		
		boolean left = x1 > x2 + width;
		boolean right = x2 > x1 + width;
		boolean top = y1 > y2 + height;
		boolean bot = y2 > y1 + height;
		boolean TopOrBot = x2 < x1 + width && x1 < x2 + width;
		boolean LeftOrRight = y1 < y2 + height && y2 < y1 + height;
		
		if(LeftOrRight) {
			if(left) {
				// Check if port is taken
				if(node1.getLeft() != null || node2.getRight() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setLeft(node2);
				node2.setRight(node1);
			}
			else if(right) {
				// Check if port is taken
				if(node1.getRight() != null || node2.getLeft() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setRight(node2);
				node2.setLeft(node1);
			}
			else {
				DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
			}
		}
		else if(TopOrBot) {
			if(top) {
				// Check if port is taken
				if(node1.getTop() != null || node2.getBot() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setTop(node2);
				node2.setBot(node1);
			}
			else if(bot) {
				// Check if port is taken
				if(node1.getBot() != null || node2.getTop() != null) {
					DialogUtils.showMessage("Port is already taken.", "Connection Aborted");
					return;
				}
				node1.setBot(node2);
				node2.setTop(node1);
			}
			else {
				DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
			}
		}
		else {
			DialogUtils.showMessage("Connection is not possible, try changing position", "Connection Aborted");
		}
	}

	public static void disconnectNode(PanNode node1, PanNode node2) {
		// Check if these nodes are connected on some port
		int port = -1;
		if(node1.getLeft() == node2) port = 0;
		else if(node1.getRight() == node2) port = 1;
		else if(node1.getTop() == node2) port = 2;
		else if(node1.getBot() == node2) port = 3;
		else {
			DialogUtils.showMessage("Connection does not exist.", "No Connection Found");
			return;
		}
		
		// Removing path between them
		int waypointIndex1 = tour.remove(new Waypoint(node1, node2));
		int waypointIndex2 = tour.remove(new Waypoint(node2, node1));
		
		if(waypointIndex1 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node2.getMapNode().setArrow(node1, false);
		}
		if(waypointIndex2 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node1.getMapNode().setArrow(node2, false);
		}
		
		// update tourNum if path existed
		if(waypointIndex1 != TourPath.WAYPOINT_NOT_FOUND || waypointIndex2 != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset TourNum on every node
			PanNode node = head;
			while(node != null) {
				node.tourNum.clear();
				node = node.getNext();
			}
			
			// Assign new TourNum on every node
			tour.updateTourNumbers();
		}
		
		// null their connection
		switch(port) {
		case 0:
			node1.setLeft(null);
			node2.setRight(null);
			break;
		case 1:
			node1.setRight(null);
			node2.setLeft(null);
			break;
		case 2:
			node1.setTop(null);
			node2.setBot(null);
			break;
		case 3:
			node1.setBot(null);
			node2.setTop(null);
			break;
		default:
			break;
		}
	}
	
	public static boolean saveMap(String savePath) {
		// Writing to file
		try {
			FileOutputStream fs = new FileOutputStream(savePath);
			ObjectOutputStream oos = new ObjectOutputStream(fs);
			oos.writeObject(head);
			oos.writeObject(home);
			oos.writeObject(name);
			oos.writeObject(nodeCount);
			
			oos.writeObject(size);
			oos.writeObject(tour);
			
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		// set graph name
		name = savePath;
		
		// set autoload destination
		AutoLoad.setLastUsedMap(savePath);
		
		// set autosave destination
		AutoSave.setSavingPath(savePath);
		
		// success
		return true;
	}
	
	public static boolean loadMap(String loadPath) {
		try {
			FileInputStream fin = new FileInputStream(loadPath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			head = (PanNode) ois.readObject();
			home = (PanNode) ois.readObject();
			name = (String) ois.readObject();
			nodeCount = (int) ois.readObject();
			
			size = (PanGraphSize) ois.readObject();
			tour = (TourPath) ois.readObject();
			
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// path checking
		PanNode start = head;
		while(start != null) {
			File imageFile, audioFile;
			
			imageFile = new File(start.getPanoramaPath());
			audioFile = (start.hasAudio()) ? new File(start.getAudioPath()) : null;

			if(!imageFile.exists()) {
				boolean replace = DialogUtils.replacePathDialog(imageFile.getPath());
				if(!replace) return false;
				
				// user requested replacement -> open image dialog 
				String newPanoramaPath = ChooserUtils.openImageDialog();
				if(newPanoramaPath != null) 
					start.setPanoramaPath(newPanoramaPath);
			}
			else if(audioFile != null && !audioFile.exists()) {
				boolean replace = DialogUtils.replacePathDialog(audioFile.getPath());
				if(!replace) return false;
				
				// user requested replacement -> open audio dialog 
				String newAudioPath = ChooserUtils.openAudioDialog();
				if(newAudioPath != null) 
					start.setAudioPath(newAudioPath);
			}
			else {
				start = start.getNext();
			}
		}
		
		// set autoload destination
		AutoLoad.setLastUsedMap(loadPath);
				
		// set autosave destination
		AutoSave.setSavingPath(loadPath);
		
		// success
		return true;
	}
	
	public static void genPath() {
		// empty map
		if(head == null) return;
		
		// clear prev path
		if(tour.hasPath()) clearPath();
		
		// generate id path
		NodeList graph = new NodeList(head, home);
		int p[] = graph.generatePath();
		
		// create tour and set graphics
		PanNode node;
		PanNode next;
		
		node = getNode(p[0]);
		for(int i = 1; i < p.length; i++) {
			next = getNode(p[i]);
			
			// Adding to path
			int tourNum = tour.add(new Waypoint(node, next));
			
			// Add tourNum
			node.tourNum.add(tourNum);
			// On last waypoint add final tourNum
			if(i == p.length - 1) next.tourNum.add(tourNum + 1);
			
			// Set graphics
			next.getMapNode().setArrow(node, true);
			
			node = next;
		}
	}
	
	public static void clearPath() {
		tour.clearPath();
		
		PanNode node = head;
		while(node != null) {
			node.tourNum.clear();
			node.getMapNode().clearArrows();
			
			node = node.getNext();
		}
	}
	
	public static void addToPath(PanNode node1, PanNode node2) {
		// Check if nodes are connected
		boolean connected = false;
		if(node1.getLeft() == node2)
			connected = true;
		else if(node1.getRight() == node2)
			connected = true;
		else if(node1.getTop() == node2)
			connected= true;
		else if(node1.getBot() == node2)
			connected = true;
		if(!connected) {
			DialogUtils.showMessage("Connection between selected nodes does not exist.", "Path Creation Aborted");
			return;
		}
		
		// Adding to path
		int tourNum = tour.add(new Waypoint(node1, node2));
		
		// Add tourNum
		if(!node1.tourNum.contains((Object) tourNum)) 
			node1.tourNum.add(tourNum);
		node2.tourNum.add(tourNum + 1);
		
		// Set graphics
		node2.getMapNode().setArrow(node1, true);
	}
	
	public static void removeFromPath(PanNode node1, PanNode node2) {
		// Check if path exists
		if(!tour.hasPath()) {
			DialogUtils.showMessage("Path does not exist.", "Path Deletion Aborted");
			return;
		}
		
		// Check if nodes are connected
		boolean connected = false;
		if(node1.getLeft() == node2)
			connected = true;
		else if(node1.getRight() == node2)
			connected = true;
		else if(node1.getTop() == node2)
			connected= true;
		else if(node1.getBot() == node2)
			connected = true;
		if(!connected) {
			DialogUtils.showMessage("Connection between selected nodes does not exist.", "Path Deletion Aborted");
			return;
		}
		
		// Removing from path
		int tourNum = tour.remove(new Waypoint(node1, node2));
		System.out.println(tourNum);
		if(tourNum != TourPath.WAYPOINT_NOT_FOUND) {
			// Reset graphics
			node2.getMapNode().setArrow(node1, false);	
			
			// Reset TourNum on every node
			PanNode node = head;
			while(node != null) {
				node.tourNum.clear();
				node = node.getNext();
			}
			
			// Assign new TourNum on every node
			tour.updateTourNumbers();
		}
	}
	
	public static void setTextMode(boolean b) {
		textMode = b;
	}
	
	public static void encryptMap(String key, JProgressBar progressBar) {
		// encryption canceled
		if(key == null) return;
		
		// defining background thread for encryption
		SwingWorker<Boolean, Integer> encryptMap = new SwingWorker<Boolean, Integer>(){
			// encryption logic
			protected Boolean doInBackground() throws Exception {
				int progress = 1;
				try {
					PanNode node = head;
					while(node != null) {
						// check node encryption status
						// node is of type .pimg
						if(ImageCipher.isEncrypted(node.getPanoramaPath())) {
							// re-encrypt node with new key
							ImageCipher.imageReEncrypt(node.getPanoramaPath(), key);
						}
						// node is not yet encrypted
						else if (!ImageCipher.isEncrypted(node.getPanoramaPath())){
							// encrypt node
							String newPanPath = ImageCipher.imageEncrypt(node.getPanoramaPath(), key);
							// update path to encrypted image path
							node.setPanoramaPath(newPanPath);
						}
						
						// update progress
						publish(progress++);
						
						// get next node
						node = node.getNext();
					}
				} catch (Exception e) {
					// encryption failed (abort)
					e.printStackTrace();
					return false;
				}
				
				// map encrypted
				return true;
			}
			
			// updates progress bar
			protected void process(List<Integer> encryptedCount) {
				int latestCount = encryptedCount.get(encryptedCount.size() - 1);
				progressBar.setValue(latestCount);
			}
			
			// code that runs after doInBackground is finished
			protected void done() {
				try {
					// get encryption result
					boolean success = get();
					
					// display message
					if(success) 
						DialogUtils.showMessage("Encryption is successfull!", "Encryption");
					else 
						DialogUtils.showMessage("Encryption failed!", "Encryption");
					
					// hide progress bar
					progressBar.setVisible(false);
				} 
				catch (InterruptedException ignore) {}
				catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
		};
		
		// encrypt map
		encryptMap.execute();
	}
	
	/* class related functionality */
	
	public static PanNode getHead() {
		return head;
	}
	
	public static void setHead(PanNode node) {
		head = node;
	}
	
	private static void setTail(PanNode newTail) {
		PanNode tail = head;
		
		while(tail.getNext() != null)
			tail = tail.getNext();
		
		tail.setNext(newTail);
	}
	
	public static PanNode getHome() {
		return home;
	}
	
	public static void removeMap() {
		head = null;
		home = null;
		name = DEFAULT_NAME;
		nodeCount = 0;
		
		tour = new TourPath();
		size = new PanGraphSize();
	}
	
	public static PanNode getNode(int id) {
		PanNode node = head;
		while(id != 0) {
			node = node.getNext();
			id--;
		}
		
		return node;
	}

	public static boolean hasTour() {
		return tour.hasPath();
	}
	
	public static PanNode[] getTour() {
		return tour.getPath();
	}
	
	public static int getCenterX() {
		return size.getCenterX();
	}
	
	public static int getCenterY() {
		return size.getCenterY();
	}
	
	public static void updateMapSize() {
		if(isEmpty()) return;
		
		PanNode node = head;
		
		size.WEST = size.EAST = node.getMapNode().x;
		size.NORTH = size.SOUTH = node.getMapNode().y;
		
		while(node != null) {
			int x = node.getMapNode().x;
			int y = node.getMapNode().y;
			
			size.updateSize(x, y);
			
			node = node.getNext();
		}
	}

	public static String getName() {
		if(name == null) return DEFAULT_NAME;
		
		return name;
	}
	
	public static void setName(String newName) {
		name = newName;
	}	
	
	public static boolean isEmpty() {
		return head == null;
	}

	public static boolean isTextMode() {
		return textMode;
	}

	public static int getNodeCount() {
		return nodeCount;
	}
}
