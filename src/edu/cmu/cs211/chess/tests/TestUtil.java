package edu.cmu.cs211.chess.tests;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayMove;
import edu.cmu.cs211.chess.unittested.AlphaBetaQuiescent;
import edu.cmu.cs211.chess.unittested.EndGameEvaluator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestUtil
{
	private int numPieces(String fen)
	{
		fen = fen.substring(0, fen.indexOf(' '));
		fen = fen.replace("/", "");
		fen = fen.replaceAll("\\d", "");
		return fen.length();
	}


	// todo: change back
	private static final EndGameEvaluator stu_evaluator = new EndGameEvaluator();

	public static void evaluatorTest(String fen, int value)
	{
		ArrayBoard student = ArrayBoard.FACTORY.create().init(fen);
		assertEquals(
				"Evaluation of boards not equal (" + fen + ")",
				value,
				stu_evaluator.eval(student)
		);
	}

	// todo: change back
	public static void alphaBetaTest(String fen, int depth, String[] validMoves)
	{
		ArrayBoard
				student = ArrayBoard.FACTORY.create().init(fen);
		AlphaBetaQuiescent<ArrayMove, ArrayBoard>
				ab = new AlphaBetaQuiescent<ArrayMove, ArrayBoard>();

		ab.setEvaluator(stu_evaluator);
		ab.setFixedDepth(depth);

		String studMove = ab.getBestMove(student, 10000, 10000).serverString().substring(0, 4);

		List<String> validMoveList = Arrays.asList(validMoves);

		assertTrue(student.toString() + "\n\nMove returned by depth " + depth + " search on [" + fen + "] was " + studMove
				+ " but we expected something from " + validMoveList, validMoveList.contains(studMove));
	}
}
