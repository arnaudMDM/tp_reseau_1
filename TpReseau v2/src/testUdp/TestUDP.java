package testUdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class TestUDP {
	public static void main(String[] args) throws IOException {
		
		final int PORT_ENVOI = 11114;
		final int PORT_ECOUTE = 5555;
		final int TAILLE_FRAGMENT = 1024;
		
		new EcouteUdp(PORT_ECOUTE).start();
		
		String requete1 = "GET 6 \r\nLISTEN_PORT "+PORT_ECOUTE+" \r\nFRAGMENT_SIZE "+TAILLE_FRAGMENT+" \r\n\r\n";
		String requete2 = "GET -1\r\n\r\n";
		
		byte[] buffer;
		
		buffer = requete1.getBytes();
		
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), PORT_ENVOI);
		DatagramSocket socket = new DatagramSocket();
		
		socket.send(packet);
		
		buffer = requete2.getBytes();
		
		packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("127.0.0.1"), PORT_ENVOI);
		
		socket.send(packet);
	}
}
