package ihm;


public class IhmConsole implements Ihm {
	
	@Override
	public void afficher(String message) {
		System.out.println(message);
	}

	@Override
	public void afficherErreur(String message) {
		System.err.println(message);
	}
	
	@Override
	public void clientDeconnecte(String adresse) {
		afficherErreur("Client déconnecté depuis " + adresse);
	}

}
