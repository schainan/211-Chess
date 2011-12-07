package edu.cmu.cs211.chess.tests;

import org.junit.Test;

/**
 * This class tests the version of alpha beta you are
 * required to write for the unit tests on FrontDesk.
 * 
 * You should be able to add any tests that you are
 * failing on FrontDesk here and debug them locally.
 */
public class AlphaBetaTest {

	@Test (timeout = 1000)
	public void alphaBetaDepth2Test () {
		TestUtil.alphaBetaTest ("r1bq1b1r/pppkpppp/3p4/8/8/P2PP2P/1PP2PP1/RNB1KBNR b KQ -",2,
			new String[] {"e7e5"});
	}

   	@Test (timeout = 1000)
	public void frontDeskDepth2Test () {
		TestUtil.alphaBetaTest ("3r1bnr/p1pk1ppp/n3b3/3p4/1pP3P1/1P3P2/P2PP2P/RN1QKBNR w KQ -",2,
			new String[] {"c4d5"});
	}
}
