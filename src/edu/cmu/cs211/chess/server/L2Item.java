package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;
// a level 2 datagram

import java.util.ArrayList;
import java.util.List;

// import java.io.*;

public final class L2Item {

	// the fields of a level2 datagram
	private List<String> items;

	// construct a new L2Item
	public L2Item() {
		items = new ArrayList<String>();
	}

	// read an integer
	public int getInt(int n) {
		int x;
		try {
			x = Integer.parseInt(getString(n));
		} catch (NumberFormatException e) {
			x = 0;
		}
		return x;
	}

	// return the requested item
	public String getString(int n) {
		if (n >= items.size())
			return "";
		return items.get(n);
	}

	// return the size
	public int getSize() {
		return items.size();
	}

	// return L2 datagram type
	public int getCode() {
		return getInt(0);
	}

	// add a new item
	public void add(String s) {
		items.add(s);
	}
	/*
	 // return a string of this L2 for debugging
	 public String toString() {
	 ByteArrayOutputStream os=new ByteArrayOutputStream(40000);
	 PrintStream ps=new PrintStream(os);
	 ps.println("Game L2 size="+get_size());
	 for(int i=0;i<get_size();i++)
	 ps.println("  ["+i+"]="+get_str(i));
	 return os.toString();
	 }
	 */
}