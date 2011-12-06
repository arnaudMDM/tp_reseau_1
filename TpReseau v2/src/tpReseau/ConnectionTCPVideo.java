package tpReseau;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionTCPVideo extends ConnectionTCP {
	
	private String id;
	private ArrayList<File> lstImg;
	
	public ConnectionTCPVideo(int portEcoute, String id, ArrayList<File> lstImg) {
		super(portEcoute);
		this.id = id;
		this.lstImg = lstImg;
	}
	
	Thread creerThreadClient(Socket socket) throws IOException {
		return new ConnectionTCPThreadVideo(socket, id, lstImg);
	}
}