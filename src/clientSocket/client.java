package clientSocket;

import static java.lang.Math.floor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class client {
	public static boolean quitter = false;

	public static class MessageEnvoyer extends Thread {
		String nom;
		private Socket client;
		String msg;
		public DataOutputStream outs;
		public DataInputStream ins;
		public boolean memeNom = false;
		


		public MessageEnvoyer(String nom,Socket client) throws IOException {
			this.nom=nom;
			this.client = client;
			this.outs=new  DataOutputStream(client.getOutputStream());
			this.ins=new DataInputStream(client.getInputStream());
		}
		
		public String getNom () {
			return nom;
		}
		
		public void setMemeNom (boolean memeNom) {
			this.memeNom = memeNom;
		}

		public void run() {
			Scanner sc=new Scanner(System.in);
			while (nom==null) {
	        	System.out.println("Entrer votre nom:");
	        	nom = sc.nextLine();
			}
			try {
				outs.writeUTF(nom);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			while (this.memeNom==true) {
	        	nom = sc.nextLine();
	        	try {
					outs.writeUTF(nom);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    		while (true) {
    			try {
    			String msg = sc.nextLine();
    			if (msg.equals("exit")) {
    				outs.writeUTF(msg);
    				System.out.println("Vous avez quitté la conversation");
    				ins.close();
    				outs.close();
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
	
	public static class MessageRecepteur extends Thread {
		String nom;
		private Socket client;
		String msg;
		public DataOutputStream outs;
		public DataInputStream ins;
		
		public MessageRecepteur(String nom,Socket client) throws IOException {
			this.nom=nom;
			this.client = client;
			this.outs=new  DataOutputStream(client.getOutputStream());
			this.ins=new DataInputStream(client.getInputStream());
		}
		
		public void run() {
			while (true) {
    			String msgFromServeur;
    			if (quitter == true) {
    				break;
    			}
				try {
					msgFromServeur = ins.readUTF();
	    			System.out.println(msgFromServeur);
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
			String nom = null;
			Socket client = new Socket("localhost", 20000);
            /*DataOutputStream outs=new  DataOutputStream(client.getOutputStream());
			DataInputStream ins=new DataInputStream(client.getInputStream());
        	Scanner sc=new Scanner(System.in);
			while (nom==null) {
	        	System.out.println("Entrer votre nom:");
	        	nom = sc.nextLine();
			}
			outs.writeUTF(nom);*/
    		MessageEnvoyer Envoyeur = new MessageEnvoyer(nom,client);
    		Envoyeur.start();
    		MessageRecepteur Recepteur = new MessageRecepteur(nom,client);
    		Recepteur.start();
    		if (quitter ==true) {
    			client.close();
    		}
		} catch (IOException ex) {
			Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
		}


	}
}