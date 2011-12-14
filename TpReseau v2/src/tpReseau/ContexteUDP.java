package tpReseau;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class ContexteUDP {
	
	private InetAddress adresseClient;
	private int portClient;
	
	private int imgCourante;
	private int tailleFragment;
	
	private DatagramSocket socketEnvoi;
	private DatagramPacket packetEnvoi;
	
	public ContexteUDP(InetAddress adresseClient, int portClient) {
		this.adresseClient = adresseClient;
		this.portClient = portClient;
		
		try {
			socketEnvoi = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		packetEnvoi = null;
	}

	public InetAddress getAdresseClient() {
		return adresseClient;
	}

	public int getPortClient() {
		return portClient;
	}

	public int getImgCourante() {
		return imgCourante;
	}

	public int getTailleFragment() {
		return tailleFragment;
	}

	public DatagramSocket getSocketEnvoi() {
		return socketEnvoi;
	}

	public DatagramPacket getPacketEnvoi() {
		return packetEnvoi;
	}

	public void setImgCourante(int imgCourante) {
		this.imgCourante = imgCourante;
	}

	public void setTailleFragment(int tailleFragment) {
		this.tailleFragment = tailleFragment;
	}
	
	public void creerPacketEnvoi(int portDonnees) {
		packetEnvoi = new DatagramPacket(new byte[0], 0, adresseClient, portDonnees);
	}
}
