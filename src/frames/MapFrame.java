package frames;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import panorama.PanGraph;
import panorama.PanMap;

@SuppressWarnings("serial")
public abstract class MapFrame extends Frame {
	protected ScheduledThreadPoolExecutor repaint = new ScheduledThreadPoolExecutor(5);;
	protected ScheduledFuture<?> repaintTasks;
	
	public static int mapWidth;
	public static int mapHeight;
	
	protected MapPanel mapPanel;
	
	public MapFrame(String title) {
		super(title);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		
		mapWidth = screenSize.width;
		mapHeight = screenSize.height;
	}
	
	public MapPanel getMapPanel() {
		return this.mapPanel;
	}
	
	protected void startFrameRepaint() {
		if(repaintTasks == null) {
			repaintTasks = repaint.scheduleAtFixedRate(new RepaintMap(this), 0, 20, TimeUnit.MILLISECONDS);
		}
	}
	
	protected void stopFrameRepaint() {
		repaintTasks.cancel(false);
		repaintTasks = null;
	}
	
	protected void setOrigin() {
		if(PanGraph.getHome() != null) {
			// map center
			int x = PanGraph.getCenterX();
			int y = PanGraph.getCenterY();
			
			// node size
			int h = PanMap.HEIGHT / 2;
			int w = PanMap.WIDTH / 2;
			
			// panel size
			int centerX = mapPanel.getWidth() / 2;
			int centerY = mapPanel.getHeight() / 2;
			
			mapPanel.setOrigin((x + w) - centerX, (y + h) - centerY);
		}
		else {
			mapPanel.setOrigin(0,0);
		}
	}
	
	public abstract void showFrame();
	
	public abstract void hideFrame();
	
}
