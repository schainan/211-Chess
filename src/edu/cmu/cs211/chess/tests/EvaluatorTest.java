package edu.cmu.cs211.chess.tests;

import org.junit.Test;

import static edu.cmu.cs211.chess.tests.TestUtil.evaluatorTest;

/**
 * This class tests the version of the evaluator you are
 * required to write for the unit tests on FrontDesk.
 * <p/>
 * This suite of tests tests the TestedEvaluator class.
 * Given a fen, the evaluatorTest method is the primary workhorse here,
 * and it takes the fen and an expected score, scores the board's representation
 * using TestedEvaluator's eval, and compares it with the expected value.
 * <p/>
 * A whole set of different board representations are tested, that cover almost all possible
 * cases.
 */
public class EvaluatorTest
{

	@Test(timeout = 1000)
	public void evaluatorTest0()
	{
		evaluatorTest("rn1k1bnr/p1q1p1p1/1pp2p2/P2p3p/2PP2b1/5PQ1/1P2P1PP/RNB1KBNR w KQ -", -7);
	}

	@Test(timeout = 1000)
	public void evaluatorTest1()
	{
		evaluatorTest("rnbqkbnr/pp1p1pp1/8/2p1p2p/2BPP3/2N5/PPP2PPP/R1BQK1NR b KQkq -", -39);
	}

	@Test(timeout = 1000)
	public void evaluatorTest2()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2P4p/4P3/2N5/P1PP1PPP/R1BQKBNR b KQkq -", -124);
	}

	@Test(timeout = 1000)
	public void evaluatorTest3()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2p4Q/1P2P3/2N5/P1PP1PPP/R1B1KBNR b KQkq -", -113);
	}

	@Test(timeout = 1000)
	public void evaluatorTest4()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2p4p/1P2P3/2N5/P1PPBPPP/R1BQK1NR b KQkq -", -26);
	}

	@Test(timeout = 1000)
	public void evaluatorTest5()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2p4Q/4P3/1PN5/P1PP1PPP/R1B1KBNR b KQkq -", -111);
	}

	@Test(timeout = 1000)
	public void evaluatorTest6()
	{
		evaluatorTest("rnbqkbnr/pp1pppp1/8/7p/3pP1P1/2N5/PPP2P1P/R1BQKBNR b KQkq g3", 71);
	}

	@Test(timeout = 1000)
	public void evaluatorTest7()
	{
		evaluatorTest("rnbqkbnr/pp1pppp1/8/7p/3pP1P1/2N5/PPP2P1P/R1BQKBNR b KQkq g3", 71);
	}

	@Test(timeout = 1000)
	public void evaluatorTest8()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2p4p/N3P3/3P4/PPP2PPP/R1BQKBNR b KQkq -", 0);
	}

	@Test(timeout = 1000)
	public void evaluatorTest9()
	{
		evaluatorTest("rnb1kbnr/ppqpppp1/8/2p4p/3PP3/2N5/PPP1BPPP/R1BQK1NR b KQkq -", -52);
	}

	@Test(timeout = 1000)
	public void evaluatorTest10()
	{
		evaluatorTest("r1bqkbnr/pp1pppp1/2n5/2p4p/4P3/1PN5/PBPP1PPP/R2QKBNR b KQkq -", -26);
	}


}