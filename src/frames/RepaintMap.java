/**
 * When an object implementing interface Runnable is used to create a thread, 
 * starting the thread causes the object's run method to be called in that separately executing thread.
 * By calling JFrame.repaint() method we are calling paint method of all components within frame.
 * 
 */

package frames;

public class RepaintMap implements Runnable{

	MapFrame map;
	
	public RepaintMap(MapFrame map) {
		this.map = map;
	}
	
	public void run() {
		map.repaint();
	}

}
