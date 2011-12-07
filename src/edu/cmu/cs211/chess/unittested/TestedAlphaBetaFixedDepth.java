package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.List;

/**
 * An implementation of Alpha Beta search.
 * <p/>
 * This is the class that will be unit tested by FrontDesk.
 */
public class TestedAlphaBetaFixedDepth<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>
{
	public M getBestMove(B board, int myTime, int opTime)
	{
		List<M> moves = board.generateMoves();
		int extreme = Integer.MAX_VALUE;
		M bestMoveSoFar = null;

		for (M move : moves)
		{
			board.applyMove(move);
			int negaValue = negaMax(board, maxDepth - 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
			if (negaValue < extreme)
			{
				extreme = negaValue;
				bestMoveSoFar = move;
			}
			board.undoMove();
		}
		return bestMoveSoFar;
	}

	private int negaMax(B board, int depth, int alpha, int beta)
	{
		if (depth == 0)
			return evaluator.eval(board);

		List<M> moves = board.generateMoves();

		if (moves.isEmpty())
		{
			if (board.inCheck())
				return -evaluator.mate() + depth;
			else
				return -evaluator.stalemate();
		}

		for (M move : moves)
		{
			board.applyMove(move);
			alpha = Math.max(alpha, -negaMax(board, depth - 1, -beta, -alpha));
			board.undoMove();

			if (alpha >= beta)
				break; // prune
		}
		return alpha;
	}
}