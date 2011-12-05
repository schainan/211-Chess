package edu.cmu.cs211.chess.gui.online;
//package com.chessclub.easychess;

import java.awt.Color;

//This is the class for containing various global variables and settings

public final class Config {

	public static boolean boolDebug = false;

	public static boolean boolGuest = false;// true;

	public static final String version = "0.15";

	// public static String tickerwelcome = "EasyChess v" + version;
	// public static final String handleTickerRobot = "ROBOadmin";

	public static final String urlHelp = "http://www-2.cs.cmu.edu/afs/cs.cmu.edu/academic/class/15211-f04/www/hw6/";

	public static final String urlICC = "http://www.chessclub.com";

	// The time (in minutes) of the available game options
	public static final int nTimeA = 2, nTimeB = 5, nTimeC = 10;

	// Channel for bannerchess chatting
	public static final int chatChannel = 50;

	// msec between clock updates
	public static final int clockSleepTime = 1000;

	public final static Color colorWhitePiece = Color.white;

	public final static Color colorWhiteOutline = Color.black;

	public final static Color colorBlackPiece = Color.black;

	public final static Color colorBlackOutline = Color.white;

	public final static Color colorBackground = Color.darkGray;

	public final static Color colorMessage = Color.gray;

	public final static Color colorGame = Color.gray;

	public final static Color colorSeek = Color.gray;

	public final static Color colorLogin = Color.white;

	// public static Color colorTicker = Color.blue;
	// public static Color colorTickerText = Color.white;

	public final static Color colorCommandArea = Color.white;

	public final static Color colorCommandText = Color.black;

	public final static Color colorDarkSquareActive = Color.blue;

	public final static Color colorDarkSquareIdle = Color.darkGray;

	public final static Color colorLightSquareActive = Color.lightGray;

	public final static Color colorLightSquareIdle = Color.lightGray;

	public final static Color colorHighlight = Color.red;

	public final static Color colorLegalMove = Color.green;

	public final static Color colorIllegalMove = Color.red;

	public final static Color colorClockRunning = Color.cyan;

	public final static Color colorClockIdle = Color.black;
	
	public final static Color colorBestMove = Color.red;

	// public static Color colorButtonText = Color.white;
	// public static Color colorButton1 = Color.blue;
	// public static Color colorButton2 = Color.red;
	// public static Color colorButtonFlash = Color.cyan;

}