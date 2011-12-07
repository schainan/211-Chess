package edu.cmu.cs211.chess.tests;

import org.junit.Test;

/**
 * This class tests the version of alpha beta you are
 * required to write for the unit tests on FrontDesk.
 * <p/>
 * You should be able to add any tests that you are
 * failing on FrontDesk here and debug them locally.
 */
public class AlphaBetaTest
{

	@Test(timeout = 1000)
	public void alphaBetaDepth2Test()
	{
		TestUtil.alphaBetaTest("r1bq1b1r/pppkpppp/3p4/8/8/P2PP2P/1PP2PP1/RNB1KBNR b KQ -", 2,
				new String[]{"e7e5"});
	}

	@Test(timeout = 1000)
	public void frontDeskDepth2Test()
	{
		TestUtil.alphaBetaTest("3r1bnr/p1pk1ppp/n3b3/3p4/1pP3P1/1P3P2/P2PP2P/RN1QKBNR w KQ -", 2,
				new String[]{"c4d5"});
	}

	@Test(timeout = 1000)
	public void frontDeskCheckStalemateTest()
	{
		TestUtil.alphaBetaTest("5R2/8/8/8/7K/8/Q7/7k w - -", 2,
				new String[]{"f8f1"});
	}

	@Test(timeout = 1000)
	public void frontDeskDepth3Test()
	{
		TestUtil.alphaBetaTest("3r1b2/pk3p2/3Ppq1n/P1p4r/1p2b1p1/BP1B3P/2QPnP2/R4KR1 w h -", 3,
				new String[]{"d3e4"});
	}


	@Test(timeout = 1000)
	public void frontDeskDepth4Test()
	{
		TestUtil.alphaBetaTest("rN3Bn1/2p2k2/pp2p2r/2P2bpp/P3Pp1P/1P1P4/N4P2/RQ1K1B1R w - -", 4,
				new String[]{"f8h6"});
	}


}
