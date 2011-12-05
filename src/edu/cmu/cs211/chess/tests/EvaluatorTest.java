package edu.cmu.cs211.chess.tests;

import static edu.cmu.cs211.chess.tests.TestUtil.evaluatorTest;
import org.junit.Test;

/**
 * This class tests the version of the evaluator you are
 * required to write for the unit tests on FrontDesk.
 * 
 * You should be able to add any tests that you are
 * failing on FrontDesk here and debug them locally.
 */
public class EvaluatorTest {

	@Test (timeout = 1000)
	public void evaluatorTest0 () {
		evaluatorTest ("rn1k1bnr/p1q1p1p1/1pp2p2/P2p3p/2PP2b1/5PQ1/1P2P1PP/RNB1KBNR w KQ -",-7);
	}
}
