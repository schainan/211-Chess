package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.List;

/**
 * An implementation of Alpha Beta search.   blahblah
 * <p/>
 * This is the class that will be unit tested by FrontDesk.
 */
public class TestedAlphaBetaFixedDepth<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>
{
	static final int MAXSTEP = ArrayBoard.WHITE;

	public M getBestMove(B board, int myTime, int opTime)
	{
		List<M> moves = board.generateMoves();
		int minOrMax = board.toPlay();
		int extreme = (minOrMax == MAXSTEP) ?
				Integer.MIN_VALUE + 1 : Integer.MAX_VALUE;
		M bestMoveSoFar = null;

		for (M move : moves)
		{
			board.applyMove(move);
			int negaValue = negaMax(board, maxDepth - 1);
			if (minOrMax == MAXSTEP)
			{
				if (negaValue > extreme)
				{
					extreme = negaValue;
					bestMoveSoFar = move;
				}
			}
			else
			{
				if (negaValue < extreme)
				{
					extreme = negaValue;
					bestMoveSoFar = move;
				}
			}
			board.undoMove();
		}
		return bestMoveSoFar;
	}

	private int negaMax(B board, int depth)
	{
		if (depth == 0)
			return evaluator.eval(board);

		List<M> moves = board.generateMoves();

		if (moves.isEmpty())
		{
			if (board.inCheck())
				return -evaluator.mate();
			else
				return -evaluator.stalemate();
		}

		int max = Integer.MIN_VALUE + 1;
		int score;
		for (M move : moves)
		{
			board.applyMove(move);
			score = -negaMax(board, depth - 1);
			if (score > max)
				max = score;
			board.undoMove();
		}
		return max;
	}
}