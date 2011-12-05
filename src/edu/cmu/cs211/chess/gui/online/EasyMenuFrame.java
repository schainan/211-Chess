package edu.cmu.cs211.chess.gui.online;
//package com.chessclub.easychess;

import java.awt.CheckboxMenuItem;
import java.awt.Event;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;

import javax.swing.JFrame;

import edu.cmu.cs211.chess.server.Hub;

public class EasyMenuFrame extends JFrame {

	/**
     * 
     */
    private static final long serialVersionUID = 93543371734115264L;

	private Hub hub;

	private MenuBar mb;

	private MenuItem fileExit;

	private MenuItem infoFinger, infoPing; // add "help event" and "help
											// event2"

	private MenuItem /* gameMoretime, *//* gameFlip, */gameDraw, gameAbort,
			gameResign/* , gameRematch */;

	private CheckboxMenuItem promoteQueen, promoteRook, promoteKnight,
			promoteBishop;

	private CheckboxMenuItem stayin211pool;

	private CheckboxMenuItem optionsBell;

	private MenuItem optionsRefresh;

	private MenuItem helpAbout;

	public EasyMenuFrame(String title, Hub hub) {
		super(title);
		this.hub = hub;
		mb = new MenuBar();
		initFile();
		initInfo();
		initGame();
		initOptions();
		initHelp();
		this.setMenuBar(mb);
	}

	private void initFile() {
		Menu m = new Menu("File");
		fileExit = new MenuItem("Exit");
		m.add(fileExit);
		mb.add(m);
	}

	private void initInfo() {
		Menu m = new Menu("Info");
		infoFinger = new MenuItem("Show personal stats");
		m.add(infoFinger);
		infoPing = new MenuItem("Show internet lag");
		m.add(infoPing);

		mb.add(m);
	}

	private void initGame() {
		Menu m = new Menu("Game");
		// gameMoretime = new MenuItem("Add 60 sec to opponent's clock");
		// m.add(gameMoretime);
		/*
		 * gameFlip = new MenuItem("Flip perspective"); m.add(gameFlip);
		 */
		gameDraw = new MenuItem("Suggest a draw");
		m.add(gameDraw);
		gameAbort = new MenuItem("Suggest an abort");
		m.add(gameAbort);
		gameResign = new MenuItem("Resign");
		m.add(gameResign);

		m.addSeparator();
		promoteQueen = new CheckboxMenuItem("Promote to queen");
		promoteQueen.setState(true);
		m.add(promoteQueen);
		promoteRook = new CheckboxMenuItem("Promote to rook");
		m.add(promoteRook);
		promoteKnight = new CheckboxMenuItem("Promote to knight");
		m.add(promoteKnight);
		promoteBishop = new CheckboxMenuItem("Promote to bishop");
		m.add(promoteBishop);

		m.addSeparator();
		stayin211pool = new CheckboxMenuItem("Stay in the 211 pool");
		stayin211pool.setState(hub.boolStayin211);
		m.add(stayin211pool);
		/*
		 * m.addSeparator(); gameRematch = new MenuItem("Rematch same
		 * opponent"); m.add(gameRematch);
		 */
		mb.add(m);
	}

	private void initOptions() {
		Menu m = new Menu("Options");
		optionsBell = new CheckboxMenuItem("Bell");
		optionsBell.setState(hub.boolBeepOn);
		m.add(optionsBell);
		optionsRefresh = new MenuItem("Repaint window");
		m.add(optionsRefresh);

		mb.add(m);
	}

	private void initHelp() {
		Menu m = new Menu("Help");
		helpAbout = new MenuItem("About...");
		m.add(helpAbout);

		mb.add(m);
	}

	public boolean action(Event evt, Object arg) {
		// if (arg instanceof String)
		// System.out.println("action: " + arg);

		// check promotion menu
		if (evt.target == promoteQueen) {
			hub.setPromote('q');
			promoteQueen.setState(true);
			promoteRook.setState(false);
			promoteBishop.setState(false);
			promoteKnight.setState(false);
		} else if (evt.target == promoteRook) {
			hub.setPromote('r');
			promoteQueen.setState(false);
			promoteRook.setState(true);
			promoteBishop.setState(false);
			promoteKnight.setState(false);
		} else if (evt.target == promoteBishop) {
			hub.setPromote('b');
			promoteQueen.setState(false);
			promoteRook.setState(false);
			promoteBishop.setState(true);
			promoteKnight.setState(false);
		} else if (evt.target == promoteKnight) {
			hub.setPromote('n');
			promoteQueen.setState(false);
			promoteRook.setState(false);
			promoteBishop.setState(false);
			promoteKnight.setState(true);

			// check info menu
		} else if (evt.target == infoFinger) {
			hub.sendCommand("finger");
		} else if (evt.target == infoPing) {
			hub.sendCommand("multi lagstat; ping");

			// check game menu
		} /*
			 * else if (evt.target == gameMoretime) { hub.sendCommand("moretime
			 * 60"); }
			 *//*
			 * else if (evt.target == gameFlip) { hub.sendCommand("flip"); }
			 */else if (evt.target == gameDraw) {
			hub.sendCommand("draw");
		} else if (evt.target == gameAbort) {
			hub.sendCommand("abort");
		} else if (evt.target == gameResign) {
			hub.sendCommand("resign");
		} else if (evt.target == stayin211pool) {
			boolean b = !(hub.boolStayin211);
			stayin211pool.setState(b);
			hub.boolStayin211 = b;
		}
		/*
		 * else if (evt.target == gameRematch) { hub.sendCommand("rematch"); //
		 * check options menu }
		 */else if (evt.target == optionsBell) {
			boolean b = !(hub.boolBeepOn);
			optionsBell.setState(b);
			hub.boolBeepOn = b;
		} else if (evt.target == optionsRefresh) {
			hub.repaint();

		} else if (evt.target == fileExit) {
			hub.sendCommand("exit");
		} else if (evt.target == helpAbout) {
			hub.visitPage(Config.urlHelp);
		} else {
			return false;
		}
		return true;
	}
}