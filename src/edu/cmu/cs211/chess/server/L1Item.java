package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;
// a level 1 item from ICC

import java.util.StringTokenizer;

public final class L1Item {
	//    public final String copyright="Copyright (C) 1996 Aaron Wohl";

	// it's command number
	private int cmdNum;

	// who did the command
	private String who;

	// command text
	private String text;

	// create a new L1Item
	public L1Item(int cmdNum, String who, String text) {
		this.cmdNum = cmdNum;
		this.who = who;
		this.text = text;
	}

	// create a L1Item from it's ICC output
	public L1Item(String itext) {
		StringTokenizer t = new StringTokenizer(itext);
		cmdNum = Integer.parseInt(t.nextToken());
		who = t.nextToken();
		text = itext.substring(itext.indexOf('\n') + 1);
	}

	// return the cmdNum
	public int getCmdNum() {
		return cmdNum;
	}

	// return the name of this command
	// L1DebugItem returns more stuff for this
	public String getCmdName(int cmdNum) {
		return "#" + String.valueOf(cmdNum);
	}

	// return the who did the command
	public String getWho() {
		return who;
	}

	// was this command done by me?
	public boolean i_did_it() {
		return who.equals("*");
	}

	// return the result text
	// L1DebugItem returns more stuff for this
	public String getVerboseText() {
		return text;
	}

	// return the result text
	public String getText() {
		return text;
	}

}