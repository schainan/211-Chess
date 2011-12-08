package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;
import edu.cmu.cs211.chess.search.SimpleTimer;
import edu.cmu.cs211.chess.search.Timer;

import java.util.HashMap;
import java.util.List;

/*
 * quiescent searching implemented
 */
public class AlphaBetaIterDeep<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>
{
	private static final int QUIESCENT_DEPTH = 1;
	private static final int INITIAL_DEPTH = 3;

	public M getBestMove(B board, int myTime, int opTime)
	{
		SimpleTimer simpleTimer = new SimpleTimer(3, 2);
		simpleTimer.start(myTime, opTime);

		M bestMoveSoFar = null;
		int currentDepth = 2;
		while (!simpleTimer.timeup())
		{
			System.out.println("depth: " + currentDepth);
			M tempMove;
			if ((tempMove = useIterativeDeepening(board, currentDepth, bestMoveSoFar, simpleTimer)) == null)
				break;
			else
				bestMoveSoFar = tempMove;
			currentDepth++;
		}
		System.out.println();
		return bestMoveSoFar;
	}


	private M useIterativeDeepening(B board, int depth, M bestPreviousMove, Timer timer)
	{
		int negaValue;
		List<M> moves = board.generateMoves();
		int extreme = Integer.MAX_VALUE;
		M bestMoveSoFar = null;

		// if it's not the first call to it
		if (timer.timeup()) return null;

		if (bestPreviousMove != null)
		{
			board.applyMove(bestPreviousMove);
			HashMap<Long, Integer> repetitionMap = new HashMap<Long, Integer>();
			repetitionMap.put(board.signature(), 1);
			negaValue = negaMax(board, depth - 1, Integer.MIN_VALUE + 1,
					Integer.MAX_VALUE, repetitionMap);

			if (negaValue < extreme)
			{
				extreme = negaValue;
				bestMoveSoFar = bestPreviousMove;
				reportNewBestMove(bestPreviousMove);
			}
			board.undoMove();
		}

		if (timer.timeup()) return null;

		for (M move : moves)
		{
			if (timer.timeup()) return null;

			// don't check already checked node
			if (move != bestPreviousMove)
			{
				board.applyMove(move);
				HashMap<Long, Integer> repetitionMap = new HashMap<Long, Integer>();
				repetitionMap.put(board.signature(), 1);
				negaValue = negaMax(board, depth - 1, Integer.MIN_VALUE + 1,
						Integer.MAX_VALUE, repetitionMap);
				if (negaValue < extreme)
				{
					extreme = negaValue;
					bestMoveSoFar = move;
					reportNewBestMove(move);
				}
				board.undoMove();
			}
		}
		return bestMoveSoFar;
	}


	private int negaMax(B board, int depth, int alpha, int beta, HashMap<Long, Integer> repetitionMap)
	{

		if (depth == 0)
			return quiesce(board, alpha, beta, QUIESCENT_DEPTH);
		long signature = board.signature();
		Integer boardCount = repetitionMap.get(signature);

		if (boardCount != null && boardCount.equals(2))
		{
			return 0;
		}
		if (boardCount == null)
			repetitionMap.put(signature, 1);
		else
			repetitionMap.put(signature, boardCount + 1);

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
			HashMap<Long, Integer> newRepetitionTable = new HashMap<Long, Integer>(repetitionMap);
			alpha = Math.max(alpha, -negaMax(board, depth - 1, -beta, -alpha, newRepetitionTable));
			board.undoMove();

			if (alpha >= beta)
				break; // prune
		}
		repetitionMap.put(signature, repetitionMap.get(signature) - 1);

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