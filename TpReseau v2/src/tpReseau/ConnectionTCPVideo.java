package tpReseau;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionTCPVideo extends ConnectionTCP {
	
	private String id;
	private ArrayList<File> lstImg;
	
	private Ihm ihm;
	
	public ConnectionTCPVideo(int portEcoute, String id, ArrayList<File> lstImg, Ihm ihm) {
		super(portEcoute, ihm);
		this.id = id;
		this.lstImg = lstImg;
		
		this.ihm = ihm;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadVideo(socket, id, lstImg, ihm);
	}
}