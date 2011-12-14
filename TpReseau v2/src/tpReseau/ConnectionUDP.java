package tpReseau;

import ihm.Ihm;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public abstract class ConnectionUDP extends Connection {
	
	private static final int TAILLE_BUFFER = 256;
	
	protected int timeout;
	private DatagramSocket udpSocket;
	private DatagramPacket packet;
	
	public ConnectionUDP(int portEcoute, Ihm ihm) {
		
		super(portEcoute, ihm);
		
		timeout = -1;
		udpSocket = null;
		
		byte[] buffer = new byte[TAILLE_BUFFER];
		packet = new DatagramPacket(buffer, buffer.length);
	}
	
	@Override
	protected void ouvrirSocket() throws IOException {
		udpSocket = new DatagramSocket(portEcoute);
		if (timeout > 0) {
			udpSocket.setSoTimeout(timeout);
		}
	}
	
	@Override
	protected void ecoute() throws IOException, SocketTimeoutException {
		udpSocket.receive(packet);  // mise en écoute
		String requete = new String (packet.getData(), 0, packet.getLength());
		
		traiterRequete(requete, packet.getAddress(), packet.getPort());
	}
	
	@Override
	protected void terminer() throws IOException {
		udpSocket.close();
	}
	
	protected abstract void traiterRequete(String requete, InetAddress adresseExpediteur, int portExpediteur);
}
