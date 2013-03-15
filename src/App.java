/* Team 48
 * Yiqi Cao bac5rc
 * Bethany Connor yc8ur
 * Xiaoxiao Xu xx5xm
 * Lab Section: Tues 9am
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JList;

import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.packet.Presence;

public class App {

	protected static ChatClient client;
	protected static App window;

	private JFrame frame;
	private JLabel usernameLB;
	private JTextField usernameTF;
	private JLabel passwordLB;
	private JPasswordField passwordField;
	private JButton loginBTN;
	private JPanel loginPanel;
	private JPanel convoPanel;
	private JTextPane convoWindow;
	private JTextArea textArea;
	private static JList buddyList;
	private static DefaultListModel bList;
	private JLabel errorLabel;
	private JScrollPane bListScrollBar;
	private JTextField updateStatusBox;
	private static Map<String, ChatWindow> chatWindows;
	private JButton btnAddBuddy;
	private static JTextField addBuddyEmailTF;
	private static JTextField addBuddyNameTF;
	private JLabel error2Label;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window = new App();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public App() {
		client = new ChatClient(this);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		chatWindows = new HashMap<String, ChatWindow>();

		// Writer output = new BufferedWriter(new FileWriter(aFile));

		frame = new JFrame(client.getUsername());
		frame.getContentPane().setBackground(new Color(173, 216, 230));
		frame.setBounds(100, 100, 650, 401);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		convoPanel = new JPanel();
		convoPanel.setVisible(false);
		convoPanel.setBackground(new Color(173, 216, 230));
		convoPanel.setBounds(0, 0, 634, 363);
		frame.getContentPane().add(convoPanel);
		convoPanel.setLayout(null);

		bList = new DefaultListModel();
		buddyList = new JList(bList);
		buddyList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent click) {
				handleBuddyListMouseClicked(click);
			}
		});
		buddyList.setCellRenderer(new BuddyCellRenderer());
		convoPanel.add(buddyList);

		bListScrollBar = new JScrollPane(buddyList);
		bListScrollBar.setBackground(new Color(255, 255, 255));
		bListScrollBar.setBorder(new LineBorder(new Color(100, 149, 237)));
		bListScrollBar.setBounds(449, 7, 175, 351);
		convoPanel.add(bListScrollBar);

		JLabel lblUpdateStatus = new JLabel("Update Status:");
		lblUpdateStatus.setBounds(40, 41, 105, 14);
		convoPanel.add(lblUpdateStatus);

		updateStatusBox = new JTextField();
		updateStatusBox.setBorder(new CompoundBorder(new LineBorder(new Color(
				51, 153, 153)), new EmptyBorder(3, 3, 3, 3)));
		updateStatusBox.setBounds(128, 34, 203, 28);
		convoPanel.add(updateStatusBox);
		updateStatusBox.setColumns(10);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.setForeground(new Color(47, 79, 79));
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				client.updateStatus(updateStatusBox.getText());
				updateStatusBox.setText("");
			}
		});
		btnUpdate.setBounds(341, 37, 89, 23);
		convoPanel.add(btnUpdate);

		btnAddBuddy = new JButton("Add Buddy");
		btnAddBuddy.setForeground(new Color(47, 79, 79));
		btnAddBuddy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				handleBtnAddBuddyActionPerformed(arg0);
			}
		});
		btnAddBuddy.setBounds(90, 166, 119, 23);
		convoPanel.add(btnAddBuddy);

		addBuddyEmailTF = new JTextField();
		addBuddyEmailTF.setBorder(new CompoundBorder(new LineBorder(new Color(
				51, 153, 153)), new EmptyBorder(3, 3, 3, 3)));
		addBuddyEmailTF.setBounds(90, 127, 119, 28);
		convoPanel.add(addBuddyEmailTF);
		addBuddyEmailTF.setColumns(10);

		addBuddyNameTF = new JTextField();
		addBuddyNameTF.setBorder(new CompoundBorder(new LineBorder(new Color(
				51, 153, 153)), new EmptyBorder(3, 3, 3, 3)));
		addBuddyNameTF.setBounds(90, 88, 119, 28);
		convoPanel.add(addBuddyNameTF);
		addBuddyNameTF.setColumns(10);

		JLabel nameLabel = new JLabel("Name:");
		nameLabel.setBounds(40, 95, 46, 14);
		convoPanel.add(nameLabel);

		JLabel emailLabel = new JLabel("E-mail:");
		emailLabel.setBounds(40, 134, 46, 14);
		convoPanel.add(emailLabel);

		JButton btnRemoveBuddy = new JButton("Remove Selected Buddy");
		btnRemoveBuddy.setForeground(new Color(47, 79, 79));
		btnRemoveBuddy.setBounds(258, 329, 181, 23);
		convoPanel.add(btnRemoveBuddy);

		error2Label = new JLabel("");
		error2Label.setBounds(37, 186, 172, 14);
		convoPanel.add(error2Label);
		btnRemoveBuddy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (buddyList.getSelectedValue() != null) {
					String buddy = buddyList.getSelectedValue().toString();
					if (buddy.indexOf(':') != -1) {
						buddy = buddy.substring(buddy.indexOf(':') + 2);
					}
					bList.removeElement(client.removeBuddy(buddy));
				}
			}
		});

		loginPanel = new JPanel();
		loginPanel.setBounds(162, 82, 321, 242);
		frame.getContentPane().add(loginPanel);
		loginPanel.setBackground(new Color(173, 216, 230));
		loginPanel.setLayout(null);

		usernameLB = new JLabel("Username:");
		usernameLB.setBounds(44, 54, 89, 15);
		loginPanel.add(usernameLB);
		usernameLB.setForeground(new Color(178, 34, 34));
		usernameLB.setFont(new Font("Tahoma", Font.BOLD, 14));

		usernameTF = new JTextField();
		usernameTF.setBounds(143, 47, 142, 32);
		loginPanel.add(usernameTF);
		usernameTF.setDisabledTextColor(new Color(51, 153, 153));
		usernameTF.setBorder(new CompoundBorder(new LineBorder(new Color(0,
				153, 153)), new EmptyBorder(5, 5, 5, 5)));
		usernameTF.setColumns(10);

		passwordLB = new JLabel("Password:");
		passwordLB.setBounds(44, 107, 89, 15);
		loginPanel.add(passwordLB);
		passwordLB.setForeground(new Color(178, 34, 34));
		passwordLB.setFont(new Font("Tahoma", Font.BOLD, 14));

		passwordField = new JPasswordField();
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == e.VK_ENTER) {
					login();
				}
			}
		});
		passwordField.setBounds(143, 100, 142, 32);
		loginPanel.add(passwordField);
		passwordField.setBorder(new CompoundBorder(new LineBorder(new Color(0,
				153, 153)), new EmptyBorder(5, 5, 5, 5)));

		loginBTN = new JButton("Login");
		loginBTN.setForeground(new Color(47, 79, 79));
		loginBTN.setBounds(159, 155, 101, 32);
		loginPanel.add(loginBTN);

		errorLabel = new JLabel("");
		errorLabel.setBounds(44, 187, 150, 21);
		loginPanel.add(errorLabel);
		loginBTN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				login();
			}
		});
	}

	// Logs the client in
	protected boolean login() {
		if (usernameTF.getText().contains("@")) {
			client.username = usernameTF.getText();
		} else {
			client.username = usernameTF.getText() + "@gmail.com";
		}
		client.password = passwordField.getPassword();
		String pass = "";
		for (char c : client.password) {
			pass += c;
		}
		client.connect();
		if (client.connection.isConnected()) {
			client.login(client.username, pass);
			pass = "0";
			if (client.connection.isAuthenticated()) {
				loginPanel.setVisible(false);
				convoPanel.setVisible(true);
				client.loadBuddyList();
				RosterEntry[] bl = client.getBuddyArray();
				for (RosterEntry entry : bl) {
					bList.addElement(entry);
				}
				client.startChat();
				return true;
			} else {
				errorLabel.setText("Login error.");
			}
		} else {
			errorLabel.setText("Connection error.");
			return false;
		}
		return false;
	}

	/*
	 * Displays a message. If no chat window is currently open, it opens one for
	 * the user. If one is open, it displays the message in that window
	 */
	public void displayMessage(String user, String mess) {
		if (chatWindows.containsKey(user)) {
			chatWindows.get(user).open();
			chatWindows.get(user).displayMessage(user, mess);
		} else {
			ChatWindow newWindow = new ChatWindow(user);
			newWindow.open();
			chatWindows.put(user, newWindow);
			chatWindows.get(user).displayMessage(user, mess);
		}
	}

	// Sends the message in the message box using Enter key
	protected void handleTextAreaKeyPressed(KeyEvent e) {

		if (e.getKeyChar() == e.VK_ENTER) {
			if (client.sendMessage(buddyList.getSelectedValue().toString(),
					textArea.getText())) {
				convoWindow.setText(convoWindow.getText() + "\n"
						+ client.username + ": " + textArea.getText());
				textArea.setText("");
			}
		}

	}
	
	// Makes buddylist look more pleasant and shows user presence
	class BuddyCellRenderer extends JLabel implements ListCellRenderer {

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			RosterEntry rEntry = (RosterEntry) value;
			String text = "";
			try {
				if (rEntry.getName() == null) {
					text = rEntry.getUser();
				} else {
					text = rEntry.getName();
				}
				Presence p = client.getRoster().getPresence(rEntry.getUser());
				String stat = p.getStatus();
				if (p.getType().toString().equals("available")) {
					ImageIcon icon = new ImageIcon("Online.png");
					Image img = icon.getImage();
					BufferedImage bi = new BufferedImage(30, 20,
							BufferedImage.TYPE_INT_ARGB);
					Graphics g = bi.createGraphics();
					g.drawImage(img, 0, 0, 30, 20, null);
					ImageIcon newIcon = new ImageIcon(bi);
					setIcon(newIcon);
					try {
						if (!stat.equals(null)) {
							text = text + ", Status: " + stat;
						}
					} catch (NullPointerException e) {

					}
				} else {
					ImageIcon icon = new ImageIcon("Offline2.jpg");
					Image img = icon.getImage();
					BufferedImage bi = new BufferedImage(30, 20,
							BufferedImage.TYPE_INT_ARGB);
					Graphics g = bi.createGraphics();
					g.drawImage(img, 0, 0, 30, 20, null);
					ImageIcon newIcon = new ImageIcon(bi);
					setIcon(newIcon);
				}

				setText(text);
				setEnabled(list.isEnabled());
				setFont(list.getFont());
				setForeground(new Color(165, 42, 42));
				setBackground(new Color(255, 255, 255));
				setOpaque(true);
				setBounds(483, 7, 132, 344);
				setBorder(null);

				// setIcon((s.length() > 10) ? longIcon : shortIcon);
				if (isSelected) {
					setBackground(new Color(100, 149, 237));
					setForeground(Color.WHITE);
				} else {
					setBackground(Color.WHITE);
					setForeground(new Color(165, 42, 42));
				}
				return this;
			} catch (NullPointerException e) {
				// System.out.println(rEntry);
			}
			return null;
		}

	}

	// A nested class that controls/allows multiple chat windows
	class ChatWindow {
		private JTextPane cWindow;
		private JTextArea tArea;
		private JFrame windowFrame;
		private String name;
		private String filename;

		public ChatWindow(String buddy) {
			name = buddy;
			if (name.indexOf(':') != -1) {
				filename = client.getUsername().substring(0,
						client.getUsername().indexOf('@'))
						+ "to"
						+ name.substring(name.indexOf(':') + 2,
								name.indexOf('@'));
			} else {
				filename = client.getUsername().substring(0,
						client.getUsername().indexOf('@'))
						+ "to" + name.substring(0, name.indexOf('@'));
			}
			initialize();
		}

		// Opens the chat window
		protected void open() {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						windowFrame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

		// Initializes the frame
		private void initialize() {
			windowFrame = new JFrame(name);
			windowFrame.getContentPane()
					.setBackground(new Color(173, 216, 230));
			windowFrame.setBounds(100, 100, 485, 428);
			windowFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			windowFrame.getContentPane().setLayout(null);

			cWindow = new JTextPane();
			cWindow.setBounds(10, 11, 449, 287);
			cWindow.setDisabledTextColor(new Color(0, 0, 0));
			cWindow.setEditable(false);
			cWindow.setBorder(new CompoundBorder(new LineBorder(new Color(100,
					149, 237)), new EmptyBorder(5, 5, 5, 5)));
			cWindow.setBackground(new Color(255, 255, 255));
			windowFrame.getContentPane().add(cWindow);
			loadHistory();

			tArea = new JTextArea();
			tArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					handleTAreaKeyPressed(e);
				}
			});
			tArea.setBounds(10, 303, 449, 76);
			tArea.setBackground(new Color(255, 255, 255));
			tArea.setBorder(new CompoundBorder(new LineBorder(new Color(100,
					149, 237)), new EmptyBorder(5, 5, 5, 5)));
			windowFrame.getContentPane().add(tArea);
		}

		// Loads past history of the chat
		protected void loadHistory() {
			File f = new File(filename);
			if (!f.exists()) {
				int i = filename.indexOf("to");
				String temp = filename.substring(i + 2) + "to"
						+ filename.substring(0, i);
				f = new File(temp);
			}
			/*
			 * try { Scanner sc = new Scanner(new File(filename)); while
			 * (sc.hasNext()) { cWindow.setText(cWindow.getText() +
			 * sc.nextLine() + "\n"); } } catch (FileNotFoundException e) { try
			 * { System.out.println(filename);
			 * 
			 * System.out.println(temp); Scanner sc = new Scanner(new
			 * File(filename)); while (sc.hasNext()) {
			 * cWindow.setText(cWindow.getText() + sc.nextLine() + "\n");
			 * filename = temp; }
			 * 
			 * } catch(FileNotFoundException f) {
			 * 
			 * }
			 */
			if (f.exists()) {
				filename = f.getName();
				Scanner sc;
				try {
					sc = new Scanner(f);
					while (sc.hasNext()) {
						cWindow.setText(cWindow.getText() + sc.nextLine()
								+ "\n");
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
				}
			}

		}

		// Displays a message
		protected void displayMessage(String user, String mess) {
			if (mess != null) {
				cWindow.setText(cWindow.getText() + user + ": " + mess + "\n");
				writeHistory(cWindow.getText());
			}
		}

		// Writes the new history into a text file
		protected void writeHistory(String text) {
			Writer output = null;
			try {
				output = new BufferedWriter(new FileWriter(filename));
				output.write(text + "\n");
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Sends a chat using Enter key
		protected void handleTAreaKeyPressed(KeyEvent e) {

			if (e.getKeyChar() == e.VK_ENTER) {
				if (client.sendMessage(name, tArea.getText())) {
					cWindow.setText(cWindow.getText() + client.username + ": "
							+ tArea.getText() + "\n");
					writeHistory(cWindow.getText());
					tArea.setText("");
					tArea.repaint();
				}
			}

		}
	}

	// Loads a new window if a buddy is double clicked on the BuddyList
	protected static void handleBuddyListMouseClicked(MouseEvent click) {

		if (click.getClickCount() == 2) {
			App.ChatWindow cW = window.new ChatWindow(buddyList
					.getSelectedValue().toString());
			String buddy = buddyList.getSelectedValue().toString();
			int i = buddy.indexOf(':');
			// System.out.println(buddy + " " + i);
			if (i != -1) {
				buddy = buddy.substring(i + 2);
			}
			chatWindows.put(buddy, cW);
			chatWindows.get(buddy).open();
		}

	}

	// Adds a buddy
	protected void handleBtnAddBuddyActionPerformed(ActionEvent arg0) {
		error2Label.setText("");
		String name = addBuddyNameTF.getText();
		String email = addBuddyEmailTF.getText();

		if (!email.contains("@")) {
			email += "@gmail.com";
		}
		if (!name.equals("") && !email.equals("@gmail.com")) {
			if (client.addBuddy(email, name)) {
				addBuddyNameTF.setText("");
				addBuddyEmailTF.setText("");
				bList.addElement(client.getBuddyMap().get(email));
			}
		}
		name = "";
		email = "";
	}

	// Resets fields if addBuddy fails
	protected void resetFields() {
		addBuddyNameTF.setText("");
		addBuddyEmailTF.setText("");
		error2Label.setText("Buddy already exists.");
	}
}
