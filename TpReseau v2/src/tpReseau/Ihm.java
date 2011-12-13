package tpReseau;

public interface Ihm {

	public void afficher(String message);
	
	public void afficherErreur(String message);

	public void clientDeconnecte(String adresse);
}
