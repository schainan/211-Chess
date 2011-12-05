package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;
//timestamp DAT

import java.util.Random;

public final class DAT {
	// public final String copyright="Copyright (C) 1996 Aaron Wohl";
	public int x[];

	// random number generator
	private static Random generator = null;

	// constructor to init fields
	public DAT(int n0, int n1) {
		x = new int[2];
		x[0] = n0;
		x[1] = n1;
	}

	public DAT(int n) {
		x = new int[2];
		x[0] = n;
		x[1] = 0;
	}

	public DAT(byte[] s) throws NumberFormatException {
		// translate hexadecimal string to DAT
		x = new int[2];
		x[0] = Integer.parseInt(new String(s, 0, 8), 16);
		x[1] = Integer.parseInt(new String(s, 8, 8), 16);
	}

	public static DAT generate_random_DAT() {
		if (generator == null)
			generator = new Random();
		return new DAT(generator.nextInt(), generator.nextInt());
	}

	public boolean equal(DAT a) {
		return ((x[0] == a.x[0]) && (x[1] == a.x[1]));
	}

	private static String toZeroPrefixedHexString(int y) {
		// convert y to hex, with leading zeroes
		String result = "";
		int i, digit;

		for (i = 0; i < 8; i++) {
			digit = y & (0xf0000000 >>> (i * 4));
			result += Integer.toHexString(digit >>> (28 - (i * 4)));
		}
		return result;
	}

	public String toString() {
		return toZeroPrefixedHexString(x[0]) + toZeroPrefixedHexString(x[1]);
	}

	/*
	 *  // stub to test DAT routines
	 * 
	 * public static void main(String args[]) throws Exception {
	 * System.out.println("Random DAT = " + generate_random_DAT() );
	 * System.out.println("Zero DAT = " + new DAT(0, 0) );
	 * System.out.println("(1,16) DAT = " + new DAT(1, 16)); byte[] b = new
	 * byte[20]; while (true) { System.out.print("Enter hex number: "); //while
	 * (System.in.available() != 8); System.in.read(b); System.out.println("You
	 * entered " + new DAT(b) ); } }
	 */

}
