package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.List;

/**
 * An implementation of Alpha Beta search.   (dhaval's commit)
 * <p/>
 * This is the class that will be unit tested by FrontDesk.
 */
public class TestedAlphaBetaFixedDepth
		<M extends Move<M>,
				B extends Board<M, B>
				>
		extends AbstractSearcher<M, B>
{

	public M getBestMove(B board, int myTime, int opTime)
	{
		List<M> moves = board.generateMoves();
		if (moves.isEmpty() || maxDepth == 0) return null;
		int minormax = board.toPlay();
		int extreme = (minormax == ArrayBoard.WHITE) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		M extremeMove = null;
		for (M move : moves)
		{
			board.applyMove(move);
			int value = minimax(board, maxDepth);
			if (minormax == ArrayBoard.WHITE)
			{
				if (value > extreme)
				{
					extreme = value;
					extremeMove = move;
				}
			}
			else
			{
				if (value < extreme)
				{
					extreme = value;
					extremeMove = move;
				}
			}
			board.undoMove();
		}
		return extremeMove;
	}


	private int minimax(B board, int depth)
	{
		if (depth == 0)
		{
			return evaluator.eval(board);
		}

		List<M> moves = board.generateMoves();

		if (moves.isEmpty())
		{
			if (board.inCheck())
			{
				return evaluator.mate();
			}
			else
			{
				return evaluator.stalemate();
			}
		}
		int minormax = board.toPlay();
		int extreme = (minormax == ArrayBoard.WHITE) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
		for (M move : moves)
		{
			board.applyMove(move);
			int value = minimax(board, depth - 1);

			if (minormax == ArrayBoard.WHITE)
			{
				if (value < extreme)
				{
					extreme = value;
				}
			}
			else
			{
				if (value > extreme)
				{
					extreme = value;
				}
			}
			board.undoMove();
		}
		return extreme;


	}

}