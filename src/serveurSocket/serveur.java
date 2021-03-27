package serveurSocket;

import java.io.IOException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * C'est le class serveur qui simule l'action de serveur. Il faut d'abord
 * exécuter cette classe pour créer un serveur.
 * 
 * @author alexchen
 * @version final
 *
 */
public class serveur {
	/*
	 * Créer une mapper pour stocker le client socket et le nom.
	 */
	public static HashMap<Socket, String> clientTable = new HashMap<Socket, String>();

	/*
	 * C'est le thread de serveur qui est utilisé pour recevoir le message.
	 */

	public static class MessageRecepteur extends Thread {
		String nom;
		private Socket client;
		String msg;
		DataInputStream ins;
		DataOutputStream outs;

		/**
		 * constructeur
		 * 
		 * @param client le client socket
		 * @throws IOException
		 */

		public MessageRecepteur(Socket client) throws IOException {
			this.client = client;
			this.ins = new DataInputStream(client.getInputStream());
			this.outs = new DataOutputStream(client.getOutputStream());
		}
		
		public void setNom(String nom) {
			this.nom = nom;
		}

		public void setClient(Socket client) {
			this.client = client;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getNom() {
			return this.nom;
		}

		public Socket getClient() {
			return this.client;
		}

		public String getMsg() {
			return this.msg;
		}

		/*
		 * ReplyToAll va diffuser les messages à tous les clients dans clientTable
		 */

		public void ReplyToAll(String nom, String msg) throws IOException {
			for (Socket key : clientTable.keySet()) {
				DataOutputStream out = new DataOutputStream(key.getOutputStream());
				out.writeUTF(nom + " a dit: " + msg);
			}
		}

		/*
		 * Une fois le client est créer, cette fonction va diffueser un message de
		 * rappel à tous les clients
		 */

		public void salut(String nom) throws IOException {
			for (Socket key : clientTable.keySet()) {
				DataOutputStream out = new DataOutputStream(key.getOutputStream());
				out.writeUTF(nom + " a rejoint la conversation !");
			}
		}

		/*
		 * Une fois le message reçu est 'exit', cette fonction va diffueser un message
		 * de rappel à tous les clients
		 */

		public void exit(String nom) throws IOException {
			for (Socket key : clientTable.keySet()) {
				DataOutputStream out = new DataOutputStream(key.getOutputStream());
				out.writeUTF("L'utilisateur " + nom + " a quitté la conversation !");
			}
		}

		/*
		 * Traverser le client table pour garantir qu’un pseudonyme est unique
		 */

		public boolean memeNom(String nom) {
			for (String clientNom : clientTable.values()) {
				if (clientNom.equals(nom)) {
					return true;
				}
			}
			return false;
		}

		public void run() {
			try {
				this.setNom(ins.readUTF());
				while (this.memeNom(getNom()) == true || this.getNom().length()==0) {
					/*
					 * Verifier si le nom est vide ou déjà utilisé.
					 */
					outs.writeUTF("Votre nom a déjà été utiliser ou il est vide! Entrer votre nom:");
					this.setNom(ins.readUTF());
				}
				clientTable.put(client, this.getNom());
				this.salut(getNom());
				outs.writeUTF("\n---------------------------");
				while (true) {
					this.setMsg(ins.readUTF());
					if (this.getMsg().equals("exit")) {
						this.exit(this.getNom());
						clientTable.remove(client);
						break;
					}
					ReplyToAll(this.getNom(), this.getMsg());
					System.out.println(this.nom + " a dit: " + this.msg);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param args the command line arguments
	 */

	public static void main(String[] args) {
		try {
			ServerSocket conn = new ServerSocket(20000);
			while (true) {
				Socket client = conn.accept();
				MessageRecepteur msgrecepteur = new MessageRecepteur(client);
				msgrecepteur.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
