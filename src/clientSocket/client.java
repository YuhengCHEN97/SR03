package clientSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * C'est le class client qui simule l'action de client. Chaque fois que cette
 * classe est exécutée, un client est créé.
 * 
 * @author alexchen
 * @version final
 *
 */
public class client {

	public static boolean quitter = false;

	/*
	 * C'est le thread qui est utilisé pour envoyer le message.
	 */
	public static class MessageEnvoyer extends Thread {
		String nom;
		private Socket client;
		String msg;
		public DataOutputStream outs;
		public DataInputStream ins;

		/**
		 * constructeur
		 * 
		 * @param client le client socket
		 * @throws IOException
		 */

		public MessageEnvoyer(Socket client) throws IOException {
			this.client = client;
			this.outs = new DataOutputStream(client.getOutputStream());
			this.ins = new DataInputStream(client.getInputStream());
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

		public void run() {
			Scanner sc = new Scanner(System.in);
			System.out.println("Entrer votre nom:");
			this.setNom(sc.nextLine());
			try {
				outs.writeUTF(this.getNom());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (true) {
				try {
					String msg = sc.nextLine();
					/*
					 * Voir si le msg à envoyer equal 'exit'.Si oui, arrêter le boucle et finir le
					 * thread.
					 */
					if (msg.equals("exit")) {
						outs.writeUTF(msg);
						System.out.println("Vous avez quitté la conversation");
						ins.close();
						outs.close();
						sc.close();
						quitter = true;
						break;
					}
					outs.writeUTF(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * C'est le thread qui est utilisé pour recevoir le message.
	 */

	public static class MessageRecepteur extends Thread {
		String nom;
		private Socket client;
		String msg;
		public DataInputStream ins;

		/*
		 * constructeur
		 */
		public MessageRecepteur(Socket client) throws IOException {
			this.client = client;
			this.ins = new DataInputStream(client.getInputStream());
		}

		public void run() {
			while (true) {
				try {
					if (quitter == true) {
						ins.close();
						break;
					}
					System.out.println(ins.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			Socket client = new Socket("localhost", 20000);
			MessageEnvoyer Envoyeur = new MessageEnvoyer(client);
			Envoyeur.start();
			MessageRecepteur Recepteur = new MessageRecepteur(client);
			Recepteur.start();
			if (quitter == true) {
				client.close();
			}
		} catch (IOException ex) {
			Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}