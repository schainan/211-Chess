package edu.cmu.cs211.chess.gui.online;
//package com.chessclub.easychess;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.cmu.cs211.chess.server.Hub;
import edu.cmu.cs211.chess.server.L2Codes;
import edu.cmu.cs211.chess.server.L2Item;

public final class MessagePanel extends JPanel {

	/**
     * 
     */
    private static final long serialVersionUID = -4281264808206657839L;

	private Hub hub;

	private JScrollPane taMessageScroll;
	private JTextArea taMessage;//, taChat;

	private JTextField tfCommand;

	private CheckboxGroup cbgTalk;

	private Checkbox cbTalkAll, cbTalkOpp, cbCommand;

	// private Ticker ticker;
	private Label labelSay;

	// private FontMetrics fm = null;

	// size at which a text area is considered overflowed
	private static final int nTextBufferLength = 5000;

	// amount of a text area to delete when overflowed
	private static final int nTextBufferDecrement = 1000;

	private boolean commandEnabled = true;

	public MessagePanel(Hub mhub) {
		hub = mhub;

		this.setBackground(Config.colorMessage);

		// create components
		taMessage = new JTextArea(8, 20);
		taMessage.setEditable(false);
		// This auto-scrolls the document
		taMessage.getDocument ().addDocumentListener (new DocumentListener () {
			private void doUpdate ()
			{
				taMessage.setCaretPosition (taMessage.getDocument ().getLength ());
			}

			public void changedUpdate (DocumentEvent e)
			{
				doUpdate ();
			}

			public void insertUpdate (DocumentEvent e)
			{
				doUpdate ();
			}

			public void removeUpdate (DocumentEvent e)
			{
				doUpdate ();
			}
		});

		//        taChat = new TextArea(5, 20);
		//        taChat.setEditable(false);

		tfCommand = new JTextField();

		cbgTalk = new CheckboxGroup();
		cbTalkAll = new Checkbox("Chat", cbgTalk, false);
		cbTalkOpp = new Checkbox("Talk to opponent", cbgTalk, false);
		if (commandEnabled)
			cbCommand = new Checkbox("Command", cbgTalk, true);

		// ticker = new Ticker();

		Font textfont = new Font("Courier", Font.PLAIN, 12);
		Font labelfont = new Font("Dialog", Font.BOLD, 12);

		taMessage.setFont(textfont);
		//taChat.setFont(textfont);
		tfCommand.setFont(textfont);
		tfCommand.setBackground(Config.colorCommandArea);
		tfCommand.setForeground(Config.colorCommandText);
		
		tfCommand.addActionListener (new ActionListener() {
			public void actionPerformed (ActionEvent e)
			{
				String s = tfCommand.getText ();
				if (s == null || s.length() == 0)
					return;
				issueCommand(s);
			    
			}
		});
		

		this.setLayout(new BoxLayout (this, BoxLayout.Y_AXIS));
		
		taMessageScroll = new JScrollPane (taMessage);
		
		JPanel upperPane = new JPanel ();

		upperPane.setMaximumSize (new Dimension(100000,tfCommand.getPreferredSize ().height));
		upperPane.setLayout(new BoxLayout (upperPane, BoxLayout.X_AXIS));
		labelSay = new Label("Do:");
		labelSay.setFont(labelfont);
		upperPane.add(labelSay);

		upperPane.add(tfCommand);

		tfCommand.setPreferredSize (new Dimension(100000,tfCommand.getPreferredSize ().height));

		upperPane.add(cbTalkAll);

		upperPane.add(cbTalkOpp);

		if (commandEnabled) {
			upperPane.add(cbCommand);
		}
		
		this.add (upperPane);
		this.add(taMessageScroll);
	}

	private int sizeWidth = 0;

	private int sizeColumns = 79;

	private int getColumns() {
		int w = taMessage.getSize().width;

		if (w == sizeWidth)
			return sizeColumns;

		FontMetrics fm = taMessage.getFontMetrics(taMessage.getFont());
		if (fm == null)
			return sizeColumns;

		sizeWidth = w;
		int tempColumns = (w - 25) / fm.charWidth(' ');
		if (tempColumns != sizeColumns) {
			sizeColumns = tempColumns;
			// hub.sendCommand("set width " + sizeColumns);
			hub.sendCommand("set-quietly width " + sizeColumns);

		}
		return sizeColumns;
	}

	// adds \n's to a long string
	// NOTE: assumes TextArea's are using a fixed-width font
	private String chop(String s) {
		int cols = getColumns();

		StringTokenizer st = new StringTokenizer(s, "\n\r");
		String retval = "";
		while (st.hasMoreTokens()) {
			/*
			 StringBuffer sb = new StringBuffer(st.nextToken());
			 
			 for (int i = sb.length() / cols; i>0; i--) {
			 sb.insert(i*cols, '\n');
			 }

			 retval = "\n" + retval + sb.toString();
			 */
			retval = "\n" + retval + chopLine(st.nextToken(), cols);
		}

		return retval;
	}

	private static String chopLine(String line, int cols) {
		String retval = "";
		int pos;

		while (line.length() > cols) {
			pos = line.lastIndexOf(' ', cols);
			if (pos < 3) {
				// no white space to split on, split at right margin
				retval += line.substring(0, cols) + '\n';
				line = " " + line.substring(cols);
			} else {
				// split at the whitespace
				retval += line.substring(0, pos) + '\n';
				line = "  " + line.substring(pos);
			}
		}
		retval += line;
		return retval;
	}

	public void addMessage(String s) {
		// check for overflow, delete some at front if so
		if (taMessage.getText().length() > nTextBufferLength) {
			taMessage.replaceRange("", 0, nTextBufferDecrement);
		}
		taMessage.append(chop(s));
	}

	/*
	 public void addChat(String s) {
	 // check for overflow
	 if (taChat.getText().length() > nTextBufferLength) {
	 taChat.replaceText("", 0, nTextBufferDecrement);
	 }
	 taChat.appendText(chop(s));
	 }
	 */
	public void addChat(String s) {
		addMessage(s);
	}

	public void receiveL2(L2Item item) {
		// handle tell messages, etc
		switch (item.getCode()) {
		case L2Codes.PERSONAL_TELL:
			// if (item.getString(1).equals(Config.handleTickerRobot) ) {
			// ticker.setText(item.getString(3) );
			// } else {
			addMessage(item.getString(1) + ": " + item.getString(3) + "\n");
			// }
			break;
		case L2Codes.CHANNEL_TELL:
			// if (item.getInt(1) == Config.chatChannel)
			addChat(item.getString(2) + "(" + item.getInt(1) + "): "
					+ item.getString(4) + "\n");
			break;
		default:
			// shouldn't be here
			hub.addMessage("Error in MessagePanel: Received L2 "
					+ item.getCode());
		}

	}

	@SuppressWarnings ("deprecation")
	public boolean action(Event evt, Object what) {
		if (evt.target == cbCommand && ((Boolean) what).booleanValue()) {
			labelSay.setText("Do:  ");
		} else if (evt.target == cbTalkAll && ((Boolean) what).booleanValue()) {
			labelSay.setText("Chat:");
		} else if (evt.target == cbTalkOpp && ((Boolean) what).booleanValue()) {
			labelSay.setText("Say: ");
		}

		return super.action(evt, what);
	}

	private void issueCommand(String s) {
		if (cbTalkAll.getState()) {
			hub.sendCommandAndEcho("tell " + Config.chatChannel + " " + s);
		} else if (cbTalkOpp.getState()) {
			hub.sendCommandAndEcho("say " + s);
		} else {
			hub.sendCommandAndEcho(s);
		}
		tfCommand.setText("");
	}

	public void giveFocus() {
		tfCommand.requestFocus();
	}

	public Dimension preferredSize() {
		return minimumSize();
	}

	public Dimension minimumSize() {
		return new Dimension(40, taMessage.getPreferredSize().height
				+ tfCommand.getPreferredSize().height);
	}

	public void shutdown() {
		// ticker.stop();
		taMessage.setText("");
	}

	public void paint(Graphics g) {
		getColumns(); // check for a resize that might require changing the width variable
		super.paint(g);
	}

	/*
	 public static void main(String args[]) {
	 Frame f = new Frame("stub");

	 f.setLayout(new GridLayout(1, 1) );
	 f.add(new MessagePanel(new Hub() ) );
	 f.resize(100, 200);
	 f.pack();
	 f.show();
	 }
	 */

}