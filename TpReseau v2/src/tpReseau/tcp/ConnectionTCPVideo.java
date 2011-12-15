package tpReseau.tcp;

import ihm.Ihm;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class ConnectionTCPVideo extends ConnectionTCP {
	
	public final static int TYPE_PULL = 1, TYPE_PUSH = 2;
	
	private int type;
	
	private String id;
	private ArrayList<File> lstImg;
	private double ips;
	
	private Ihm ihm;
	
	public ConnectionTCPVideo(int type, int portEcoute, String id, ArrayList<File> lstImg, double ips, Ihm ihm) {
		super(portEcoute, ihm);
		this.type = type;
		this.id = id;
		this.lstImg = lstImg;
		this.ips = ips;
		
		this.ihm = ihm;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadVideo(type, socket, id, lstImg, ips, ihm);
	}
}