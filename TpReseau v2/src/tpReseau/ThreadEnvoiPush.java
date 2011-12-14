package tpReseau;

public class ThreadEnvoiPush extends Thread {

	private EnvoiImage ev;
	private boolean pause;
	private boolean marche;
	
	private final Object sync = new Object();
	
	public ThreadEnvoiPush(EnvoiImage ev) {
		this.ev = ev;
		pause = false;
		marche = true;
	}
	
	public void run() {
		while (marche) {
			
			ev.envoyerImage();
			
			if (pause) {
				synchronized (sync) {
					try {
						sync.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void mettreEnPause() {
		pause = true;
	}
	
	public void reprendre() {
		pause = false;
		synchronized (sync) {
			sync.notify();
		}
	}
	
	public void arreter() {
		marche = false;
	}
}
