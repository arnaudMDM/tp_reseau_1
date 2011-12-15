package tpReseau.udp;

import tpReseau.ThreadEnvoiPush;

public class ThreadEnvoiPushUDP extends ThreadEnvoiPush {

	private ConnectionUDPVideoPush ev;
	private ContexteUDP contexte;
	
	public ThreadEnvoiPushUDP(ConnectionUDPVideoPush ev, ContexteUDP contexte, double ips) {
		super(ips);
		this.ev = ev;
		this.contexte = contexte;
	}
	
	@Override
	protected void envoyer() {
		ev.envoyerImage(contexte, -1);
	}
}
