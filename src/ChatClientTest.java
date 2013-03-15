import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.TreeMap;

import org.jivesoftware.smack.RosterEntry;
import org.junit.Before;
import org.junit.Test;

public class ChatClientTest {

	private ChatClient client;

	@Before
	public void setUp() throws Exception {
		client = new ChatClient(new App());
		client.connect();

	}

	@Test
	public void testConnect() {
		boolean bool = client.connect();
		assertTrue(bool);
	}

	@Test
	public void testLogin1() {
		assertTrue(client.login("cs2110test@gmail.com", "softwaredevelopment"));

	}

	@Test
	public void testLogin2() {
		assertFalse(client.login("cs2110test@gmail.com", "badpassword"));

	}

	@Test
	public void testLoadBuddyList() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		TreeMap<String, RosterEntry> buddies = client.getBuddyMap();
		assertTrue(buddies.keySet().contains("bconnor.11@gmail.com")
				&& buddies.keySet().contains("yiqi.s.cao@gmail.com"));
	}

	@Test
	public void testLoadBuddyArray() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();

		RosterEntry[] buddies = client.getBuddyArray();
		String user = buddies[0].getUser();
		assertTrue(user.equals("bconnor.11@gmail.com"));

	}

	 @Test
	public void testAddBuddy1() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		assertFalse(client.addBuddy("bconnor.11@gmail.com", "Bethany"));
	}

	 @Test
	public void testAddBuddy2() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		assertTrue (client.addBuddy("yc8ur@virginia.edu", "Yiqi"));

	}

	 @Test
	public void testAddBuddy3() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		client.addBuddy("yc8ur@virginia.edu", "Yiqi");
		assertFalse(client.addBuddy("yc8ur@virginia.edu", "Yiqi"));
	}

	 @Test
	public void testRemoveBuddy() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		assertFalse(client.removeBuddy("nonexistantbuddy@gmail.com"));
	}

	 @Test
	public void testRemoveBuddy2() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		assertTrue(client.removeBuddy("bconnor.11@gmail.com"));
	}

	@Test
	public void testRemoveBuddy3() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		client.addBuddy("yc8ur@virginia.edu", "Yiqi");
		assertTrue(client.removeBuddy("yc8ur@virginia.edu"));
	}
	
	 @Test
	public void testRemoveBuddy4() {
		client.login("csimprojecttest@gmail.com", "cs2110project");
		client.loadBuddyList();
		client.getBuddyArray();
		client.removeBuddy("bconnor.11@gmail.com");
		assertFalse(client.getBuddyMap().keySet().contains("bconnor.11@gmail.com"));
	}

}
