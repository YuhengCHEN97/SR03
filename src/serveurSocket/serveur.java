package serveurSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class serveur {

	public static class MessageRecepteur extends Thread {
		private Socket client;
		String msg;

		public MessageRecepteur(Socket client) {
			this.client = client;
		}

		public void run() {
			try {
				DataInputStream ins=new DataInputStream(client.getInputStream());
				InputStream in = client.getInputStream();
	//			byte []b = new byte[1024];
				while (true) {
					msg = ins.readUTF();
			//	ins.read(b);
				System.out.println("client a dit: " + msg);
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
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
