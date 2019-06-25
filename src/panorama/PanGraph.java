package panorama;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import touring.TourPath;
import utils.AutoLoad;
import utils.ChooserUtils;
import utils.DialogUtils;

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

	public static void setHome(PanNode node) {
		home = node;
	}
	
	public static boolean loadMap(String loadPath) {
		PanNode gHead, gHome;
		int gNodeCount;
		PanGraphSize gSize;
		TourPath gTour;
		
		try {
			FileInputStream fin = new FileInputStream(loadPath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			gHead = (PanNode) ois.readObject();
			gHome = (PanNode) ois.readObject();
			name = (String) ois.readObject();
			gNodeCount = (int) ois.readObject();
			
			gSize = (PanGraphSize) ois.readObject();
			gTour = (TourPath) ois.readObject();
			
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		// path checking
		PanNode start = gHead;
		
		// Path to the last manually loaded image/audio file
		String lastImageLoc = null;
		String lastAudioLoc = null;
		
		// Path to the map images directory
		int mapNameIndex = loadPath.lastIndexOf("\\");
		String mapImagesLoc = loadPath.substring(0, mapNameIndex + 1);
		
		while(start != null) {
			File imageFile, audioFile;
			
			imageFile = new File(start.getPanoramaPath());
			audioFile = (start.hasAudio()) ? new File(start.getAudioPath()) : null;

			// Image Not Found
			if(!imageFile.exists()) {
				// get image name
				String imageName = start.getMapNode().panName;
				
				// creating path: mapLocation\Images\imageName
				File imageFile1 = new File(mapImagesLoc.concat("\\Images\\").concat(imageName));
				
				// creating path: lastImageLocation\imageName
				File imageFile2 = null ;
				if(lastImageLoc != null) {
					int nameIndex = lastImageLoc.lastIndexOf("\\");
					imageFile2 = new File(lastImageLoc.substring(0, nameIndex + 1).concat(imageName));
				}
				
				String newPanoramaPath;
				if(imageFile1.exists()) {
					newPanoramaPath = imageFile1.getPath();
				}
				else if(imageFile2 != null && imageFile2.exists()) {
					newPanoramaPath = imageFile2.getPath();
				}
				else {
					// request replacement
					boolean replace = DialogUtils.replacePathDialog(imageFile.getPath());
					if(!replace) return false;
					
					// user requested replacement -> open image dialog 
					newPanoramaPath = lastImageLoc = ChooserUtils.openImageDialog();
				}
				
				if(newPanoramaPath != null) {
					start.setPanoramaPath(newPanoramaPath);
				}
			}
			// Audio Not Found
			else if(audioFile != null && !audioFile.exists()) {
				// get audio name
				String audioName = start.getMapNode().audioName;
				
				// creating path: mapLocation\Audio\audioName
				File audioFile1 = new File(mapImagesLoc.concat("\\Audio\\").concat(audioName));
				
				// creating path: lastAudioLocation\audioName
				File audioFile2 = null ;
				if(lastAudioLoc != null) {
					int nameIndex = lastAudioLoc.lastIndexOf("\\");
					audioFile2 = new File(lastAudioLoc.substring(0, nameIndex + 1).concat(audioName));
				}
				
				String newAudioPath;
				if(audioFile1.exists()) {
					newAudioPath = audioFile1.getPath();
				}
				else if(audioFile2 != null && audioFile2.exists()) {
					newAudioPath = audioFile2.getPath();
				}
				else {
					boolean replace = DialogUtils.replacePathDialog(audioFile.getPath());
					if(!replace) return false;
					
					// user requested replacement -> open audio dialog 
					newAudioPath = lastAudioLoc = ChooserUtils.openAudioDialog();
				}
				
				if(newAudioPath != null) {
					start.setAudioPath(newAudioPath);
				}
			}
			// Everything is OK
			else {
				start = start.getNext();	
			}
		}
		
		// set autoload destination
		AutoLoad.setLastUsedMap(loadPath);
		
		// success
		head = gHead;
		home = gHome;
		name = loadPath;
		nodeCount = gNodeCount;
		size = gSize;
		tour = gTour;
		
		return true;
	}
	
	public static void setTextMode(boolean b) {
		textMode = b;
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