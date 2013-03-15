import java.util.Scanner;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.*;

public class SimpleChat1 {

	public static void main(String[] args) {

		ConnectionConfiguration config = new ConnectionConfiguration(
				"talk.google.com", 5222, "gmail.com");
		XMPPConnection connection = new XMPPConnection(config);
		try {
			System.out.println("Trying to connect...");
			connection.connect();
			
			System.out.println("Trying to login...");
			connection.login("cs2110test@gmail.com", "softwaredevelopment");
			// See if you are authenticated
			System.out.println("   Login successful? " + connection.isAuthenticated());
		} catch (XMPPException e1) {
			System.out.println("Error connecting");
			e1.printStackTrace();
		}
		
		Scanner stdin = new Scanner(System.in);
//		System.out.print("Chat with whom? ");
//		String myBuddy = stdin.next() + "@gmail.com";
		String myBuddy = "someuser@gmail.com";
		
		System.out.println("Instantiating ChatManager...");
		ChatManager chatmanager = connection.getChatManager();
		
		System.out.println("Starting new chat...");
		Chat newChat = chatmanager.createChat(myBuddy, new MessageListener() {
			public void processMessage(Chat chat, Message message) {
				System.out.println("\n[From: " + message.getFrom() + "] " + message.getBody());
			}
		});

		System.out.print("Enter message: ");
		String msg = stdin.nextLine();
		while ( ! msg.startsWith("bye")) {
			try {
				newChat.sendMessage(msg);
			} catch (XMPPException e) {
				System.out.println("Error Delivering message");
			}
			System.out.print("Enter message: ");
			msg = stdin.nextLine();
		}
	}
}
