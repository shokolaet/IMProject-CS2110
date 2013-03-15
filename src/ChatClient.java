/* Team 48
 * Yiqi Cao bac5rc
 * Bethany Connor yc8ur
 * Xiaoxiao Xu xx5xm
 * Lab Section: Tues 9am
 */
import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

public class ChatClient {

	private App app;
	protected String username;
	protected char[] password;
	protected ChatClient client;
	protected XMPPConnection connection;
	protected boolean received;
	protected String recMess;
	protected String receiver;
	private TreeMap<String, RosterEntry> buddyMap = new TreeMap<String, RosterEntry>();
	protected Roster roster;

	public ChatClient(App gui) {
		this.app = gui;
		received = false;
		recMess = "";
	}

	// Establishes a connection with Google
	protected boolean connect() {
		ConnectionConfiguration config = new ConnectionConfiguration(
				"talk.google.com", 5222, "gmail.com");
		this.connection = new XMPPConnection(config);
		try {
			this.connection.connect();
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	// Logs the user into the Google server
	protected boolean login(String username, String pass) {
		try {
			this.connection.login(username, pass);
			return true;
		} catch (XMPPException e) {
			return false;
		}
	}

	public String getUsername() {
		return username;
	}

	public boolean receivedMessage() {
		return received;
	}

	public String getReceivedMessage() {
		received = false;
		return recMess;
	}

	public Roster getRoster() {
		return roster;
	}

	public XMPPConnection getConnection() {
		return connection;
	}

	// Loads the buddy list from the Google Server into a Map
	public void loadBuddyList() {
		roster = this.connection.getRoster(); // http://www.igniterealtime.org/builds/smack/docs/latest/documentation/roster.html
		Collection<RosterEntry> entries = roster.getEntries();
		for (RosterEntry entry : entries) {
			buddyMap.put(entry.getUser(), entry);
		}
	}

	// Updates the users status to stat
	public void updateStatus(String stat) {
		// http://community.igniterealtime.org/thread/41274
		Presence presence = new Presence(Presence.Type.available);
		presence.setMode(Presence.Mode.available);
		presence.setStatus(stat);
		presence.setPriority(24);
		// Sending new presence
		connection.sendPacket(presence);
	}

	public TreeMap<String, RosterEntry> getBuddyMap() {
		return buddyMap;
	}

	// Returns the buddy list of the user as an array
	public RosterEntry[] getBuddyArray() {
		RosterEntry[] buddyArray = new RosterEntry[buddyMap.keySet().size()];
		Set<String> keySet = buddyMap.keySet();
		Object[] keys = keySet.toArray();
		for (int i = 0; i < buddyMap.keySet().size(); i++) {
			buddyArray[i] = buddyMap.get(keys[i]);
		}
		return buddyArray;
	}

	// Establishes listeners for chats and messages
	public void startChat() {
		ChatManager manager = this.connection.getChatManager();
		manager.addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(Chat chat, boolean arg1) {

				chat.addMessageListener(new MessageListener() {

					@Override
					public void processMessage(Chat chat, Message message) {
						PacketFilter filter = new AndFilter(
								new PacketTypeFilter(Message.class));
						// Registers packet using filter
						PacketCollector myCollector = connection
								.createPacketCollector(filter);
						// Normally, you'd do something with the collector, like
						// wait for new packets.

						// Next, create a packet listener. We use an anonymous
						// inner class for brevity.
						PacketListener myListener = new PacketListener() {
							public void processPacket(Packet packet) {
								received = true;
								// Do something with the incoming packet here.
							}
						};
						// Register the listener.
						connection.addPacketListener(myListener, filter);
						// Assume we've created a Connection name "connection".
						received = true;
						recMess = message.getBody();
						int index = message.getFrom().indexOf("/");
						String from = message.getFrom().substring(0, index);
						app.displayMessage(from, message.getBody());
					}

				});

			}

		});
	}

	// Sends a message to rec. The message content is specified by the parameter
	// m
	public boolean sendMessage(String rec, String m) {
		int i = rec.indexOf(':');
		if (i != -1) {
			rec = rec.substring(i + 2);
		}
		Chat chat = this.connection.getChatManager().createChat(rec, null);

		Message newMessage = new Message();
		newMessage.setBody(m);
		try {
			chat.sendMessage(newMessage);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Adds a buddy to the buddy list of the user
	public boolean addBuddy(String user, String name) {
		if (buddyMap.keySet().contains(user)) {
			app.resetFields();
			return false;
		}

		try {
			roster.createEntry(user, name, null);
			buddyMap.put(user, roster.getEntry(user));
			return true;
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	// Removes a buddy from the user's buddy list
	public RosterEntry removeBuddy(String user) {
		// System.out.println(buddyMap.keySet());
		if ((!buddyMap.keySet().contains(user))) {
			return null;
		}
		try {
			RosterEntry toBeRemoved = roster.getEntry(user);
			roster.removeEntry(toBeRemoved);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return buddyMap.remove(user);

	}

	/*
	 * public static void main(String[] args) {
	 * 
	 * client = new ChatClient();
	 * 
	 * ConnectionConfiguration config = new ConnectionConfiguration(
	 * "talk.google.com", 5222, "gmail.com");
	 * 
	 * client.connection = new XMPPConnection(config); Scanner scnr = new
	 * Scanner(System.in); try { client.connection.connect(); // we forgot to
	 * connect earlier.... System.out.println("Username?"); client.username =
	 * scnr.next(); System.out.println("Password?"); //String s = scnr.next();
	 * //for (char c : s.t) String p = scnr.next(); client.password =
	 * p.toCharArray();
	 * 
	 * // while (client.username == null && client.password == null) { // // }
	 * client.connection.login(client.username, p);
	 * System.out.println("   Login successful? " +
	 * client.connection.isAuthenticated()); // Chat chat = //
	 * connection.getChatManager().getThreadChat("bconnor.11@gmail.com"); //
	 * Chat chat = new Chat(connection.getChatManager(), password, // password);
	 * System.out.println("Who do you want to talk to?"); String rec =
	 * scnr.next(); Chat chat = client.connection.getChatManager().createChat(
	 * rec, new MessageListener() {
	 * 
	 * @Override public void processMessage(Chat chat, Message message) { //
	 * TODO Auto-generated method stub try { chat.sendMessage(message); } catch
	 * (XMPPException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * }); System.out.println("Message?"); String message = scnr.next();
	 * 
	 * Message newMessage = new Message(); newMessage.setBody(message);
	 * chat.sendMessage(newMessage); // above code from //
	 * http://www.igniterealtime
	 * .org/builds/smack/docs/latest/documentation/messaging.html
	 * System.out.println(chat); Roster roster = client.connection.getRoster();
	 * /
	 * /http://www.igniterealtime.org/builds/smack/docs/latest/documentation/roster
	 * .html Collection<RosterEntry> entries = roster.getEntries(); for
	 * (RosterEntry entry : entries) { System.out.println(entry); } //this is
	 * also quite messy, but the code is there
	 * 
	 * // above code from //
	 * http://www.igniterealtime.org/builds/smack/docs/latest
	 * /documentation/messaging.html } catch (XMPPException e1) {
	 * e1.printStackTrace(); System.out.println("Error connecting");
	 * client.connection.disconnect();
	 * 
	 * } client.connection.disconnect(); }
	 */

}
