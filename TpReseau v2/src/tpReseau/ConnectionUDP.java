package tpReseau;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ConnectionUDP {
	
	public ConnectionUDP() {
		int port;
		InetAddress address;
		DatagramSocket socket = null;
		DatagramPacket packet;
		byte[] buf = new byte[256];
		
		try {
			socket = new DatagramSocket();
			packet = new DatagramPacket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		new ConnectionUDP();
	}
}
