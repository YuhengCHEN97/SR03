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

	public static class MessageEnvoyer extends Thread {
		private Socket client;
		String msg;

		public MessageEnvoyer(Socket client,String msg) {
			this.client = client;
			this.msg = msg;
		}

		public void run() {
			try {
				DataOutputStream outs=new  DataOutputStream(client.getOutputStream());
				OutputStream out = client.getOutputStream();
				outs.writeUTF(msg);
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
			Socket client = new Socket("localhost", 20000);
            DataOutputStream outs=new  DataOutputStream(client.getOutputStream());
			OutputStream out = client.getOutputStream();
			InputStream in = client.getInputStream();
        	Scanner sc=new Scanner(System.in);
    		while (true) {
    			System.out.println("Envoyer le message:");
    			String msg = sc.nextLine();
    			MessageEnvoyer client1 = new MessageEnvoyer(client,msg);
    			client1.start();
    		}
		} catch (IOException ex) {
			Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
		}


	}
}