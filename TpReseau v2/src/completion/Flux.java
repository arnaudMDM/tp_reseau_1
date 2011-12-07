package completion;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Flux {

	private File dossier;
	private File[] tabFile;
	private FileWriter outFile;

	public Flux(String nomDossier, String nomFichier) {
		File dossier = new File(nomDossier);
		File[] tabFile = dossier.listFiles();
		try {
			outFile = new FileWriter(nomFichier);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String entete;
		entete = "ID: \r\nName: \r\nType: \r\nAddress: \r\nPort: \r\nProtocol: \r\nIPS: \r\n";
		try {
			outFile.append(entete);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (File file : tabFile) {
			try {
				outFile.append(nomDossier + "\\" + file.getName() + "\r\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			outFile.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Fichier "+nomFichier+" créé");

	}

	public static void main(String[] args) {
		if (args.length > 1){
			new Flux(args[0], args[1]);
		}
		else {
			System.err.println("Nombres d'arguments insuffisants");
		}
	}
}