package serveurSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import clientSocket.client.MessageEnvoyer;

public class serveur {
	public static List<MessageEnvoyer> list = new ArrayList<MessageEnvoyer>();
	public static boolean quitter = false;

	public static class MessageRecepteur extends Thread {
		String nom;
		private Socket client;
		String msg;
		DataInputStream ins;
		DataOutputStream outs;

		public MessageRecepteur(Socket client) throws IOException {
			this.client = client;
			this.ins = new DataInputStream(client.getInputStream());
			this.outs = new DataOutputStream(client.getOutputStream());
		}

		public void ReplyToAll(String nom, String msg) {
			for (MessageEnvoyer client : list) {
				try {
					client.outs.writeUTF(this.nom + " a dit: " + this.msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void salut(String nom) {
			for (MessageEnvoyer client : list) {
				try {
					client.outs.writeUTF(nom + " a rejoint la conversation !");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void exit(String nom) {
			for (MessageEnvoyer client : list) {
				try {
					client.outs.writeUTF("L'utilisateur " + nom + " a quitté la conversation !");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		
		public boolean memeNom(MessageEnvoyer client, String nom) {
			for (MessageEnvoyer aclient : list) {
				if (aclient.getNom().equals(nom)) {
					return true;
				}
			}
			client.setMemeNom(false);
			return false;
		}

		public void run() {
			try {
				this.nom = ins.readUTF();
				MessageEnvoyer client1 = new MessageEnvoyer(nom, client);
				boolean hasMemeNom = memeNom(client1,this.nom);
				while (hasMemeNom==true) {
					client1.outs.writeUTF("Votre nom a déjà été utiliser! Entrer votre nom:");
					client1.setMemeNom(true);
					this.nom = ins.readUTF();
					hasMemeNom = memeNom(client1,this.nom);
				}
				list.add(client1);
				salut(this.nom);
				outs.writeUTF("\n---------------------------");
				// byte []b = new byte[1024];
				while (true) {
					this.msg = ins.readUTF();
					if (this.msg.equals("exit")) {
						exit(this.nom);
						list.remove(client1);
						ins.close();
						outs.close();
						quitter = true;
						break;
					}
					// ins.read(b);
					ReplyToAll(this.nom, this.msg);
					System.out.println(this.nom + " a dit: " + this.msg);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
				if (quitter == true) {
					client.close();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
