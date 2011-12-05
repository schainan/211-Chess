package edu.cmu.cs211.chess.playchess.online;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.cmu.cs211.chess.gui.online.Config;
import edu.cmu.cs211.chess.server.Hub;

public class EasyChess extends JFrame {

    private static final long serialVersionUID = -627544530172904783L;

	private Hub hub;

	public boolean connected = false;

	private JButton buttonConnect, buttonGuest;

	private JTextField text1;

	private JPasswordField password;

	private GridLayout grid1;

	private JLabel user_label, pass_label;

	private JLabel status_bar;

	public EasyChess() {
		grid1 = new GridLayout(5, 2, 5, 5);
		setTitle("15-211 Chess");
		Container c = getContentPane();

		c.setLayout(grid1);

		MyHandler handler = new MyHandler();

		user_label = new JLabel("User Name:");
		user_label.setToolTipText("Enter your User Name");
		c.add(user_label);

		text1 = new JTextField(10);
		c.add(text1);
		text1.addActionListener(handler);

		pass_label = new JLabel("Password:");
		pass_label.setToolTipText("Enter your Password");
		c.add(pass_label);
		password = new JPasswordField(20);
		c.add(password);
		password.addActionListener(handler);

		buttonConnect = new JButton("Connect!");
		c.add(buttonConnect);
		buttonConnect.addActionListener(handler);
		buttonGuest = new JButton("Login as Guest");
		c.add(buttonGuest);
		buttonGuest.addActionListener(handler);

		status_bar = new JLabel("Not Connected...");
		c.add(status_bar);

		hub = new Hub(this);

		setLocation(100, 100);
		setResizable(false);
		setDefaultCloseOperation (EXIT_ON_CLOSE);
		pack();
		validate();
		setVisible(true);
	}

	public void loginFailed(String msg) {
		status_bar.setText("Status: " + msg);
		buttonConnect.setEnabled(true);
		buttonGuest.setEnabled(true);
	}

	public static void main(String[] args) {

		// God only knows why this isn't the default...
		System.setProperty ("swing.aatext", "true");
		
		new EasyChess();
	}

	private class MyHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String u1 = "";
			String p1 = "";

			if (e.getSource() == buttonConnect) {
				u1 = text1.getText();
				p1 = new String(password.getPassword());

				buttonConnect.setEnabled(false);
				// buttonQuit.disable();
				buttonGuest.setEnabled(false);
				if (!connected) {
					status_bar
							.setText("Status: Connecting to the 15-211 server...");

					hub.connect();
					connected = true;
				}
				status_bar
						.setText("Status: Logging on to the 15-211 server...");
				hub.login(u1, p1);
				Config.boolGuest = "g".equals(u1);
				hub.setInfo(null);
			} else if (e.getSource() == buttonGuest) {
				u1 = "g"; // log in as guest if no handle specified
				p1 = "\n";

				buttonConnect.setEnabled(false);
				buttonGuest.setEnabled(false);
				if (!connected) {
					status_bar
							.setText("Status: Connecting to the 15-211 server...");

					hub.connect();
					connected = true;
				}
				status_bar
						.setText("Status: Logging on to the 15-211 server...");
				hub.login(u1, p1);
				Config.boolGuest = "g".equals(u1);
				hub.setInfo(null);

				// dispose();
			}

		}
	}
}
