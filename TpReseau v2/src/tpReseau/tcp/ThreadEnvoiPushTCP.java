package tpReseau.tcp;

import tpReseau.ThreadEnvoiPush;

public class ThreadEnvoiPushTCP extends ThreadEnvoiPush {

	private ConnectionTCPThreadVideo ev;
	
	public ThreadEnvoiPushTCP(ConnectionTCPThreadVideo ev, double ips) {
		super(ips);
		this.ev = ev;
	}

	@Override
	protected void envoyer() {
		ev.envoyerImage();
	}
	
}
