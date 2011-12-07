package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.List;

/*
 * quiescent searching implemented
 */
public class AlphaBetaQuiescent<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>
{
	private static final int QUIESCENT_DEPTH = 2;
	private static final int INITIAL_DEPTH = 3;
	private static final int SECOND_DEPTH = 4;
	private static final int THIRD_DEPTH = 5;

	public M getBestMove(B board, int myTime, int opTime)
	{
		int negaValue;
		List<M> moves = board.generateMoves();
		int extreme = Integer.MAX_VALUE;
		M bestMoveSoFar = null;
		int calculatedDepth = INITIAL_DEPTH;

		for (M move : moves)
		{
			board.applyMove(move);
			negaValue = negaMax(board, calculatedDepth - 1, Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
			if (negaValue < extreme)
			{
				extreme = negaValue;
				bestMoveSoFar = move;
				reportNewBestMove(move);
			}
			board.undoMove();
		}
		return bestMoveSoFar;
	}

//	private int calculateDepth(int numPieces)
//	{
//		if (numPieces > 13) return INITIAL_DEPTH;
//		else if (numPieces > 6) return SECOND_DEPTH;
//		else return THIRD_DEPTH;
//	}

	private int negaMax(B board, int depth, int alpha, int beta)
	{
		if (depth == 0)
			return quiesce(board, alpha, beta, QUIESCENT_DEPTH);

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

	private int quiesce(B board, int alpha, int beta, int depth)
	{
		int standPat = evaluator.eval(board);

		if (depth == 0) return standPat;

		List<M> moves = board.generateMoves();

		if (moves.isEmpty())
		{
			if (board.inCheck())
				return -evaluator.mate() + depth;
			else
				return -evaluator.stalemate();
		}

		if (standPat >= beta)
			return beta;

		if (alpha < standPat)
			alpha = standPat;


		int score;
		for (M move : moves)
		{
			if (move.isCapture())
			{
				board.applyMove(move);
				score = -quiesce(board, -beta, -alpha, depth - 1);
				board.undoMove();

				if (score >= beta)
					return beta;
				if (score > alpha)
					alpha = score;
			}
		}
		return alpha;
	}
}