package tpReseau;

public abstract class ThreadEnvoiPush extends Thread {

	private long tpsAttente; // en ms

	private boolean pause;
	private boolean marche;

	private final Object sync = new Object();

	public ThreadEnvoiPush(double ips) {
		tpsAttente = (long) (1000 / ips);
		
		pause = false;
		marche = true;
	}

	public void run() {
		long t1, duree;
		while (marche) {

			t1 = System.currentTimeMillis();

			envoyer();

			if (pause) {
				synchronized (sync) {
					try {
						sync.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} else {
				duree = System.currentTimeMillis() - t1;
				if (tpsAttente > duree) {
					try {
						Thread.sleep(tpsAttente - duree);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	protected abstract void envoyer();

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
