package loaders;

import java.util.concurrent.Semaphore;

public abstract class Loader extends Thread {
	
	protected static Semaphore SYNC_LOCK = new Semaphore(1, true);
	
	protected Object LOAD_LOCK = new Object();
	
	protected boolean doStop = false;
	
	/**
	 * Deblokira nit i postavlja flag za terminaciju
	 */
	public void doStop() {
		synchronized(LOAD_LOCK) {
			doStop = true;
			LOAD_LOCK.notify();
		}
	}
	
	/**
	 * Ispituje flag za terminaciju.
	 * @return
	 * <br> <b>False</b> ukoliko je podnet zahtev za terminaciju.
	 */
	protected boolean keepRunning() {
		return doStop == false;
	}
}
