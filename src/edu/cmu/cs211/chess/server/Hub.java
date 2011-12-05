package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import edu.cmu.cs211.chess.gui.online.Config;
import edu.cmu.cs211.chess.gui.online.EasyMenuFrame;
import edu.cmu.cs211.chess.gui.online.GamePanel;
import edu.cmu.cs211.chess.gui.online.MessagePanel;
import edu.cmu.cs211.chess.playchess.online.EasyChess;

//import java.net.*;

public class Hub extends JPanel {
	/**
     * 
     */
    private static final long serialVersionUID = -47361648262042927L;

	private Frame frame;

	// subsystems
	// private EasyChess applet;
	private EasyChess applet;

	// private SeekPanel seekPanel;
	private MessagePanel messagePanel;

	private GamePanel gamePanel;

	// private ButtonPanel buttonPanel;
	private transient Iconn iconn;

	private char cPromote = 'Q';

	private boolean boolLoggedIn = false;

    public static final boolean boolTimestampPossible = false;

	public boolean boolBeepOn = true;

	public boolean boolStayin211 = false;

	public String myName = "";

	public Hub(EasyChess app) {

		applet = app;
		messagePanel = new MessagePanel(this);

		gamePanel = new GamePanel(this);

		this.setBackground(Config.colorBackground);
		this.setLayout(new BoxLayout (this, BoxLayout.Y_AXIS));

		gamePanel.setPreferredSize (new Dimension (500,500));
		
		this.add (new JSplitPane (JSplitPane.VERTICAL_SPLIT, gamePanel, messagePanel));
		
	}

	//
	// output system
	//
	public synchronized void sendCommand(String cmd) {
		iconn.send_cmd(cmd, Config.boolDebug); // echo only in debug mode
	}

	public synchronized void sendCommandAndEcho(String cmd) {
		messagePanel.addMessage("> " + cmd);
		iconn.send_cmd(cmd, Config.boolDebug);
	}

	public void addMessage(String s) {
		if (boolLoggedIn) // dump pre-login spam
			messagePanel.addMessage(s);
	}

	public void addChat(String s) {
		messagePanel.addChat(s);
	}

	public void setL2(int code, int value) {
		sendCommand("set-2 " + code + " " + value);
	}

	//
	// message handling
	//
	public void receiveL2(L2Item item) {
		switch (item.getCode()) {

		case L2Codes.SEEK:
		case L2Codes.SEEK_REMOVED:
		case L2Codes.PEOPLE_IN_MY_CHANNEL:
		case L2Codes.MY_RATING:
			// System.out.println("MY_RATING");
			// seekPanel.receiveL2(item);
			break;

		case L2Codes.PERSONAL_TELL: // includes "say" commands
		case L2Codes.CHANNEL_TELL:
			messagePanel.receiveL2(item);
			break;

		case L2Codes.WHO_AM_I: // login successful
			// switch from login screen to main screen
			// ((CardLayout) applet.getLayout()).show(applet, "hub");
			myName = item.getString(1);
			frame = new EasyMenuFrame("EasyChess: " + myName, this);
			frame.add(this);
			frame.setSize(800, 600);
			frame.setVisible(true);

			boolLoggedIn = true;

			sendInitialSettings();
			messagePanel.giveFocus();

			break;
		case L2Codes.LOGIN_FAILED:
			applet.loginFailed(item.getString(2));
			break;

		case L2Codes.MY_GAME_STARTED:
		case L2Codes.STARTED_OBSERVING:
			// case L2Codes.ISOLATED_BOARD:
		case L2Codes.MY_GAME_RESULT:
		case L2Codes.OFFERS_IN_MY_GAME:
		case L2Codes.TAKEBACK:
		case L2Codes.BACKWARD:
		case L2Codes.SEND_MOVES:
		case L2Codes.MOVE_LIST:
		case L2Codes.SET_CLOCK:
		case L2Codes.FLIP:
		case L2Codes.ILLEGAL_MOVE:
		case L2Codes.MY_RELATION_TO_GAME:
		case L2Codes.MATCH:
		case L2Codes.MATCH_REMOVED:
			gamePanel.receiveL2(item);
			break;

		default:
			addMessage("Hub: Received L2 " + item.getCode());
		}
	}

	//
	//
	// connecting & login
	//
	public void connect() {
		boolLoggedIn = false;
		// iconn = new Iconn(this, "chessclub.com", 23);
		iconn = new Iconn(this, "boojum.link.cs.cmu.edu", 5000);
		while (!iconn.connectionEstablished()) {
			try {
				Thread.sleep(50);
			} // wait .05 sec
			catch (InterruptedException e) {
				break;
			}
		}
		// set L1 and L2
		// accept WHO_AM_I=0 datagram, so we can know when logged in
		// ^^^^^^ changed 1/11/98, so login spam would be avoided
		// ^^^^^^ restored 2/7/98, so login_failed can work correctly
		// accept SEEK and SEEK_REMOVED (50, 51) so we don't get ugly seek
		// messages on login
		// also accept new datagram LOGIN_FAILED=69 so we know if failed

		sendCommand(
		// 0 1 2 3 4 5 6
		// 0123456789012345678901234567890123456789012345678901234567890123456789
		"level2settings=1000000000000000000000000000000000000000000000000011000000000000000001");
	}

	public void login(String name, String password) {
		if (name == null || "".equals(name)) {
			applet.loginFailed("Please enter a name");
			return;
		}
		if ("r".equals(name)) {
			applet
					.loginFailed("This client does not support online registration");
			return;
		}
		if ("g".equals(name)) {
			password = "\n";
		} else {
			if (name.length() < 2 || name.length() > 15) {
				applet.loginFailed("Choose a name between 2 and 15 characters");
				return;
			}
		}
		if (password == null || "".equals(password)) {
			// logging in as guest or named unreg, need extra newline
			password = "\n";
		}
		sendCommand(name + " " + password);
		// setL2(L2Codes.WHO_AM_I, 1);
	}

	private String info = null;

	public void setInfo(String s) {
		info = s;
	}

	public void sendInitialSettings() { // should be called by WHO_AM_I handler
		// these were in login to reduce spam, but that screwed up LOGIN_FAILED
		sendCommand("set-quietly prompt 0");
		if (Config.boolGuest) {
			sendCommand("+channel " + Config.chatChannel);
			sendCommand("-channel 1");
			sendCommand("set-quietly shout 0");
			sendCommand("set-quietly sshout 0");
			sendCommand("set-quietly autoflag 1");
		}

		setL2(L2Codes.PERSONAL_TELL, 1);
		setL2(L2Codes.CHANNEL_TELL, 1);

		sendCommand("set-quietly width 200");

		sendCommand("set-quietly style 13");
		sendCommand("set-quietly highlight 0");
		sendCommand("set-quietly unobserve 2");
		// set finger info
		if (info != null && !"".equals(info))
			sendCommand("set-quietly 1 " + info);

		Toolkit tkit = this.getToolkit();
		Dimension screen_size = tkit.getScreenSize();
		int dpi = tkit.getScreenResolution();

		sendCommand("set-quietly interface EasyChess|"
				+ Config.version
				+ "|"
				+
				// Integer.getInteger("java.version")+"|"+
				System.getProperty("java.vendor") + "|"
				+ System.getProperty("java.class.version") + "|"
				+ System.getProperty("os.name") + "|"
				+ System.getProperty("os.arch") + "|"
				+ System.getProperty("os.version") + "|" + screen_size.width
				+ "x" + screen_size.height + "|" + dpi);

		setL2(L2Codes.MY_RATING, 1); // should be set before SEEK, but oh
										// well
		// setL2(L2Codes.SEEK, 1); // added to login l2settings
		// setL2(L2Codes.SEEK_REMOVED, 1); // ditto
		setL2(L2Codes.MY_GAME_STARTED, 1);
		setL2(L2Codes.STARTED_OBSERVING, 1);
		setL2(L2Codes.MY_GAME_RESULT, 1);
		setL2(L2Codes.OFFERS_IN_MY_GAME, 1);
		setL2(L2Codes.TAKEBACK, 1);
		setL2(L2Codes.BACKWARD, 1);
		// setL2(L2Codes.ISOLATED_BOARD, 1);
		setL2(L2Codes.MOVE_SMITH, 1);
		setL2(L2Codes.MOVE_CLOCK, 1);
		setL2(L2Codes.SEND_MOVES, 1);
		setL2(L2Codes.MOVE_LIST, 1);
		setL2(L2Codes.SET_CLOCK, 1);
		setL2(L2Codes.FLIP, 1);
		setL2(L2Codes.ILLEGAL_MOVE, 1);
		setL2(L2Codes.MY_RELATION_TO_GAME, 1);
		setL2(L2Codes.PEOPLE_IN_MY_CHANNEL, 1);
		setL2(L2Codes.MATCH, 1);
		setL2(L2Codes.MATCH_REMOVED, 1);
	}

	//
	// disconnecting & errors
	//
	public void killConnection() {
		addMessage("Shutting down...");
		if (iconn != null) // added by Pravir
			iconn.set_want_death(); // kill the socket thread
		messagePanel.shutdown(); // kill the ticker thread
		gamePanel.shutdown(); // kill the chessclock threads
		// seekPanel.shutdown();
		applet.connected = false;
		applet.loginFailed("Disconnected");
		// ((CardLayout) applet.getLayout()).show(applet, "login");
		if (frame != null) { // added by Pravir
			frame.setVisible(false);
			frame.dispose();
		}

	}

	//
	// pawn promotion
	//
	public void setPromote(char c) {
		cPromote = c;
	}

	public char getPromote() {
		return cPromote;
	}

	//
	// browser control
	//
	public void visitPage(String s) {
		System.out.println("Visiting page " + s);
		/*
		 * try { applet.getAppletContext().showDocument(new URL(s), "_blank"); }
		 * catch (Exception e) { // error("visitPage: " + e); }
		 */
	}



}
