package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;
// constants for level 2 datagrams

public final class L2Codes {
	public static final int WHO_AM_I = 0;

	public static final int PLAYER_ARRIVED = 1;

	public static final int PLAYER_LEFT = 2;

	public static final int BULLET = 3;

	public static final int BLITZ = 4;

	public static final int STANDARD = 5;

	public static final int WILD = 6;

	public static final int BUGHOUSE = 7;

	public static final int TIMESTAMP = 8;

	public static final int TITLES = 9;

	public static final int OPEN = 10;

	public static final int STATE = 11;

	public static final int GAME_STARTED = 12;

	public static final int GAME_RESULT = 13;

	public static final int EXAMINED_GAME_IS_GONE = 14;

	public static final int MY_GAME_STARTED = 15;

	public static final int MY_GAME_RESULT = 16;

	public static final int MY_GAME_ENDED = 17;

	public static final int STARTED_OBSERVING = 18;

	public static final int STOP_OBSERVING = 19;

	public static final int PLAYERS_IN_MY_GAME = 20;

	public static final int OFFERS_IN_MY_GAME = 21;

	public static final int TAKEBACK = 22;

	public static final int BACKWARD = 23;

	public static final int SEND_MOVES = 24;

	public static final int MOVE_LIST = 25;

	public static final int KIBITZ = 26;

	public static final int PEOPLE_IN_MY_CHANNEL = 27;

	public static final int CHANNEL_TELL = 28;

	public static final int MATCH = 29;

	public static final int MATCH_REMOVED = 30;

	public static final int PERSONAL_TELL = 31;

	public static final int SHOUT = 32;

	public static final int MOVE_ALGEBRAIC = 33;

	public static final int MOVE_SMITH = 34;

	public static final int MOVE_TIME = 35;

	public static final int MOVE_CLOCK = 36;

	public static final int BUGHOUSE_HOLDINGS = 37;

	public static final int SET_CLOCK = 38;

	public static final int FLIP = 39;

	public static final int ISOLATED_BOARD = 40;

	public static final int REFRESH = 41;

	public static final int ILLEGAL_MOVE = 42;

	public static final int MY_RELATION_TO_GAME = 43;

	public static final int PARTNERSHIP = 44;

	public static final int SEES_SHOUTS = 45;

	public static final int CHANNELS_SHARED = 46;

	public static final int MY_VARIABLE = 47;

	public static final int MY_STRING_VARIABLE = 48;

	public static final int JBOARD = 49;

	public static final int SEEK = 50;

	public static final int SEEK_REMOVED = 51;

	public static final int MY_RATING = 52;

	public static final int LOGIN_FAILED = 69;

	public static final int MAX_L2_CODE = 70;
}