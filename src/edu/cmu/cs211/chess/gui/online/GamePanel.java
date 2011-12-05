package edu.cmu.cs211.chess.gui.online;
//package com.chessclub.easychess;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.engine.Engine;
import edu.cmu.cs211.chess.server.Hub;
import edu.cmu.cs211.chess.server.L2Codes;
import edu.cmu.cs211.chess.server.L2Item;

public final class GamePanel extends JPanel implements Observer {
	/**
   * 
   */
  private static final long serialVersionUID = -6731471373081937898L;

	private Hub hub;

	private Board board;

	private ChessClock lowerClock, upperClock;

	private JLabel lowerName, upperName;

	// private Scrollbar scrollbar;

	private JButton buttonDraw, /* buttonRematch, */buttonAccept;

	private Component button1 = null, button2 = null; // keeps track of what
														// button is visible

	private boolean lowersTurn = true;

	private boolean amPlaying = false;

	private int flipVariable = 0;

	private LinkedList<String> pendingMatches = new LinkedList<String>();

	private transient Engine m_player; // added by Pravir

	public GamePanel(Hub h) {
		this.setBackground(Config.colorGame);
		hub = h;

		if (!Config.boolGuest) {
			// m_player = new Engine(); //added by Pravir
			// m_player = new Engine(h); // added by Pravir
			// m_player.newGame(180000,2000); //added by Pravir
		}

		board = new Board(h);
		lowerClock = new ChessClock();
		upperClock = new ChessClock();
		lowerName = new JLabel(" \n ");
		upperName = new JLabel(" \n ");
		// scrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 1, 0, 0);
		// initial val, "bubble" width, min, max

		Font f = new Font("Helvetica", Font.PLAIN, 14);
		lowerName.setFont(f);
		upperName.setFont(f);
		f = new Font("Helvetica", Font.BOLD, 18);
		lowerClock.setFont(f);
		upperClock.setFont(f);

		this.setLayout(new GamePanelLayout(getSize().width, getSize().height));

		this.add("Board", board);
		this.add("UpperClock", upperClock);
		this.add("UpperName", upperName);
		// this.add("Scrollbar", scrollbar);
		this.add("LowerName", lowerName);
		this.add("LowerClock", lowerClock);

		buttonDraw = new JButton("Draw?");
		buttonDraw.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				hub.sendCommand("draw");
			}
		});
		// buttonRematch = new ColorButton("Rematch?");
		// buttonRematch.setBackground(Color.cyan);
		buttonAccept = new JButton("Accept");
		buttonAccept.setBackground(Color.yellow);
	}


	private void offerDraw() {
		buttonDraw.setText("DRAW?");
		buttonDraw.setBackground(Color.yellow);
	}

	private void rescindDraw() {
		if (amPlaying) {
			buttonDraw.setText("Draw?");
			buttonDraw.setBackground(Color.cyan);
		}
	}

	private void addMatch(String who) {
		if (who.equals(hub.myName))
			return;

		pendingMatches.addLast (who);
		updateAcceptButton();
	}

	private void removeMatch(String who) {
		if (who.equals(hub.myName))
			return;

		// remove string from pendingMatches stack
		pendingMatches.remove (who);
		
		updateAcceptButton();
	}

	private void updateAcceptButton() {
		if (pendingMatches.isEmpty()) {
			if (button1 == buttonAccept) {
				this.remove(buttonAccept);
				button1 = null;
				doLayout();
			}
		} else {
			String s = pendingMatches.getLast ();
			buttonAccept.setText("Accept " + s);
			// System.out.println("Game Panel - accept button");
			if (button1 != buttonAccept) {
				/*
				 * System.out.println("here1"); this.add("Button1",
				 * buttonAccept); buttonAccept.repaint(); button1 =
				 * buttonAccept; layout(); validate(); show(); repaint();
				 */
				hub.sendCommand("accept " + s);
			}
		}
	}

	public void receiveL2(L2Item item) {
		int code = item.getCode();

		// reset offers for almost all game-related L2's
		if (code != L2Codes.FLIP && code != L2Codes.ILLEGAL_MOVE
				&& code != L2Codes.SET_CLOCK) {
			rescindDraw();
		}

		switch (code) {
		case L2Codes.MY_GAME_STARTED:
			// System.out.println("MY_GAME_STARTED");
			amPlaying = (item.getInt(11) == 1);
			// System.out.println("played-game = " + item.getInt(11));
			this.add("Button1", buttonDraw);
			button1 = buttonDraw;
			// FALL THROUGH!

			// Pravir - this is run when match is set up
		case L2Codes.STARTED_OBSERVING:

			if (button2 != null) {
				this.remove(button2);
				button2 = null;
			}
			this.doLayout();

			if (!Config.boolGuest && amPlaying) {
				// m_player.newGame(180000,2000); //added by Pravir
				m_player = new Engine(hub, item.getInt(7) * 60000, item.getInt(8) * 1000);
				m_player.addBestMoveObserver(this);
				/*
				 * this reads whites initial time and increment. Assumes it's
				 * not time odds --sleator
				 */
			}
			board.newGame();
			board.startGame();
			lowersTurn = true;
			flipVariable = 0;
			String whiteRating = item.getString(13);
			String blackRating = item.getString(14);
			String whiteTitles = item.getString(16);
			String blackTitles = item.getString(17);

			lowerName.setText(item.getString(2) + "\n (" + whiteRating + ") "
					+ whiteTitles);
			upperName.setText(item.getString(3) + "\n (" + blackRating + ") "
					+ blackTitles);
			// set clocks
			// lowerClock.setClock(item.getInt(7) * 1000); by Pravir
			lowerClock.setClock(item.getInt(7) * 60 * 1000);

			if (amPlaying)
				lowerClock.startClock();
			// upperClock.setClock(item.getInt(9) * 1000); by Pravir
			upperClock.setClock(item.getInt(9) * 60 * 1000);

			upperClock.stopClock();
			board.repaint();

			// lowerName.repaint();
			// upperName.repaint();
			break;
		case L2Codes.FLIP:
			// Pravir:- it is 0 if you are white. It is 1 if you are black
			// System.out.println("In flip");
			int f = item.getInt(2);
			if (f == 1)
				board.setFlipped(true);
			else
				board.setFlipped(false);

			// exchange clocks & names, if necessary
			if (f != flipVariable) { // ur black
				flipVariable = f;
				ChessClock.swap(lowerClock, upperClock);
				lowerClock.repaint();
				upperClock.repaint();
				String hold = upperName.getText();
				upperName.setText(lowerName.getText());
				lowerName.setText(hold);

				lowersTurn = !lowersTurn;
			}
			// added by Pravir
			else {// ur white
				if (!Config.boolGuest && amPlaying) {
					doRun ();
				}
			}
			repaint();

			break;
		case L2Codes.SEND_MOVES:
			// System.out.println("in send move");
			int timeonclock = item.getInt(3);
			// Pravir - you are always the lower player
			if (lowersTurn) {
				/*
				 * try{ Thread.sleep(10000); } catch(Exception e){}
				 * System.out.println("here1");
				 */

				// you made the move
				lowerClock.stopClock();
				lowerClock.setClock(timeonclock * 1000);
				if (amPlaying)
					upperClock.startClock();
				lowersTurn = false;

				board.clearPending();
				String smith = item.getString(2);
				board.doSmithMove(smith);

				// make your move on ur board
				if (!Config.boolGuest && amPlaying) {
					m_player.applyMove(smith);
				}

				board.repaint();

			} else {
				// opponent made the move, so now you make
				upperClock.stopClock();
				upperClock.setClock(timeonclock * 1000);

				if (amPlaying)
					lowerClock.startClock();
				lowersTurn = true;

				board.clearPending();
				String smith = item.getString(2);
				board.doSmithMove(smith);
				board.repaint();
				// make opponent's move on ur board
				if (!Config.boolGuest && amPlaying) {
					m_player.applyMove(smith);

					// opponent already made the move, so now you calculate and
					// make yours
					doRun ();
				}

				board.repaint();
			}

			break;
		case L2Codes.ILLEGAL_MOVE:
			board.clearPending();
			board.repaint();
			hub.addMessage("Illegal move (" + item.getString(2) + ")\n");
			break;
		case L2Codes.SET_CLOCK:
			if (flipVariable == 0) {
				lowerClock.setClock(item.getInt(2) * 1000);
				upperClock.setClock(item.getInt(3) * 1000);
			} else {
				upperClock.setClock(item.getInt(2) * 1000);
				lowerClock.setClock(item.getInt(3) * 1000);
			}
			// hub.addMessage("Clocks have been adjusted\n");
			break;
		case L2Codes.MOVE_LIST:
			if (item.getSize() < 3)
				break;
			String s = item.getString(2);
			if (!"*".equals(s)) {
				if (!Config.boolGuest && amPlaying) {
					m_player = new Engine(hub, 180000, 2000);
					m_player.addBestMoveObserver(this);
				}
				board.newGame(s);
			}
			for (int i = 3; i < item.getSize(); i++) {
				StringTokenizer st = new StringTokenizer(item.getString(i), " ");
				board.doSmithMove(st.nextToken());
				lowersTurn = !lowersTurn;
			}
			board.repaint();
			break;
		case L2Codes.MY_RELATION_TO_GAME:
			// *** add stuff ***
			// *** do we really need this one? ***
			break;
		case L2Codes.MY_GAME_RESULT:
			// stop clocks, display result
			if (amPlaying) {
				this.remove(buttonDraw);
				button1 = null;
				// this.add("Button2", buttonRematch);
				// button2 = buttonRematch;
				doLayout();
			}

			amPlaying = false;
			lowerClock.stopClock();
			upperClock.stopClock();
			board.endGame();
			board.repaint();
			hub.addMessage("Game over: " + item.getString(5) + "\n");
			// System.out.println("MY_GAME_RESULT");
			if (hub.boolStayin211) {
				hub.sendCommand("211");
			}
			break;
		case L2Codes.OFFERS_IN_MY_GAME:
			if (item.getInt(2) + item.getInt(3) > 0) {
				// draw offer
				offerDraw();
			}
			if (item.getInt(6) + item.getInt(7) > 0) {
				// abort offer
			}
			// *** other offers ***
			break;
		case L2Codes.TAKEBACK:
		case L2Codes.BACKWARD:
			// *** trigger a board to be sent
			break;

		case L2Codes.MATCH:
			addMatch(item.getString(1));
			break;
		case L2Codes.MATCH_REMOVED:
			removeMatch(item.getString(1));
			break;
		default:
			// shouldn't be here
			hub.addMessage("Error in GamePanel: Received L2 " + item.getCode()
					+ "\n");
		}

	}

	public Dimension getPreferredSize() {
		return new Dimension(300, 300);
	}

	public void shutdown() {
		lowerClock.stopClock();
		upperClock.stopClock();
		lowerClock.setClock(3000 * 60);
		upperClock.setClock(3000 * 60);
		lowerName.setText(" \n ");
		upperName.setText(" \n ");

		if (!Config.boolGuest && amPlaying) {
			m_player = new Engine(hub, 180000, 2000); // added by Pravir
			m_player.addBestMoveObserver(this);
		}

		amPlaying = false;
		lowersTurn = true;
		flipVariable = 0;

		if (button1 != null)
			this.remove(button1);
		if (button2 != null)
			this.remove(button2);

		board.newGame();
		pendingMatches.clear ();
	}

	private void doRun () {
		Thread t = new  Thread (new MakeMoveRunner());
		t.setDaemon (true);
		t.start ();
	}
	

	@SuppressWarnings("unchecked")
	public void update(Observable o, Object arg) {
		// Presently, the only updates that are expected are notifications
		// of the new best move found during a search, but others
		// may be added later
		if(arg instanceof Move) {
			board.setBestMove(((Move) arg).serverString());
			board.repaint();
		}
	}
	
	// thread to call compute move.
	private class MakeMoveRunner implements Runnable {
		
		public void run ()
		{
			String ur_move = m_player.computeMove ((int) lowerClock.msecleft,
			        (int) upperClock.msecleft).serverString();
			if (ur_move != null && amPlaying) {
				hub.sendCommand (ur_move);
			}
		}

	}
}
