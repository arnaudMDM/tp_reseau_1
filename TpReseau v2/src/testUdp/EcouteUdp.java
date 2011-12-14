package testUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class EcouteUdp extends Thread {
	
	private static final int TAILLE_BUFFER = 2056;
	
	private int portEcoute;
	
	public EcouteUdp(int port) {
		this.portEcoute = port;
	}
	
	public void run() {
		DatagramSocket socket = null;
		try {
			socket = new DatagramSocket(portEcoute);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		byte[] buffer = new byte[TAILLE_BUFFER];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		
		for (;;) {
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
			String recu = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Taille du paquet : "+packet.getLength());
			System.out.println(recu);
		}
	}
}
