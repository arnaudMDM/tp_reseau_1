package tpReseau;

import java.io.IOException;

public abstract class Connection extends Thread {
	
	protected int portEcoute;
	protected boolean marche;
	protected Ihm ihm;
	
	public Connection(int portEcoute, Ihm ihm) {
		this.portEcoute = portEcoute;
		this.ihm = ihm;
		
		marche = true;
	}
	
	abstract void ouvrirSocket() throws IOException;
	
	abstract void ecoute() throws IOException;
	
	abstract void terminer() throws IOException;
	
	public void run() {
		try {
            ouvrirSocket();
        } catch (IOException e) {
            ihm.afficherErreur("Impossible d'ouvrir le port " + portEcoute);
            System.exit(1);
        }
        ihm.afficher("Serveur en écoute");
        
        while (marche) {
			try {
				ecoute();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }

		try {
			terminer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
