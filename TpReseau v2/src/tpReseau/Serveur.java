package tpReseau;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Serveur {
	
	private static final int PORT_CATALOGUE = 5020;
	
	public Serveur() {
		String catalogue = lireCatalogue();
		
		ConnectionTCPCatalogue connectionCatalogue = new ConnectionTCPCatalogue(PORT_CATALOGUE, catalogue);
		connectionCatalogue.start();
		
		// autres lancements de threads
	}
	
	
	
	private String lireCatalogue() {
		File file = new File("catalogue.txt");
		FileReader fr = null;
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			System.err.println("Fichier catalogue introuvable");
			System.exit(1);
		}
		
		StringBuilder catalogue = new StringBuilder();
		int c;
		try {
			while ((c = fr.read()) != -1) {
				catalogue.append((char)c);
			}
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return catalogue.toString();
	}
	
	public static void main(String[] args) {
		new Serveur();
	}
}
