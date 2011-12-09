package edu.cmu.cs211.chess.search;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Chessbot: AlphaBetaQuiescentRep
 *
 * dhavals and schainan
 *
 * This is our chessbot, jim-morrison. It uses a number of methods to be
 * significantly better than the tested negamax with alpha-beta pruning:
 *
 * 1. Quiescent searching
 * 2. repetition detection
 * 3. selective depth selection based on number of pieces
 */
public class AlphaBetaQuiescentRep
		<M extends Move<M>, B extends Board<M, B>>
		extends AbstractSearcher<M, B>
{
	// as the game progresses and the number of pieces on the board increases,
	// use the different depths in DEPTH_ARRAY. Uses cutoffs from CUTOFF_ARRAY
	private static final int[] DEPTH_ARRAY = {3, 4, 5, 6, 7};
	private static final int[] CUTOFF_ARRAY = {12, 8, 5, 3};
	private static final int QUIESCENT_DEPTH = 3;

	// stores the repetitions that have occurred in the game, to avoid them
	// while picking a move.
	Map<Long, Integer> repetitionMap = new HashMap<Long, Integer>();

	/*
     * getBestMove
     *
     * Finds the next best move for this board. Again, uses negamax with the
     * above improvements.
     *
     * The quiescent searching goes QUIESCENT_DEPTH down capture-move trees
     * to make sure captures and trades are made more intelligently.
     *
     * Repetition detection keeps track of board positions thus far and makes
     * sure that no position occurs more than twice in a game. This is to
     * avoid unnecessary stalemates.
     *
     * The depth progression increases the search depth as the number of
     * pieces on the board decreases. Because of the decreased branching
     * factor on such boards, we can afford to go to deeper depths and thus
     * make objectively better decisions.
     */
	public M getBestMove(B board, int myTime, int opTime)
	{
		int negaValue;
		List<M> moves = board.generateMoves();
		int extreme = Integer.MAX_VALUE;
		M bestMoveSoFar = null;
		int calculatedDepth = calculateDepth(numPieces(board.fen()));

		addDelta(board);
		for (M move : moves)
		{
			board.applyMove(move);


			if (board.generateMoves().isEmpty() && board.inCheck())
			{
				board.undoMove();
				return move;
			}
			// this is the same basic structure as our unit-tested negaMax.
			negaValue = negaMax(board, calculatedDepth - 1,
					Integer.MIN_VALUE + 1, Integer.MAX_VALUE);
			if (negaValue < extreme)
			{
				extreme = negaValue;
				bestMoveSoFar = move;
				reportNewBestMove(move);
			}
			board.undoMove();
		}
		board.applyMove(bestMoveSoFar);
		// we decided on this move, so record the resulting board.
		addDelta(board);
		board.undoMove();

		return bestMoveSoFar;
	}

	/*
     * These are the repetition table add and remove functions,
     * as described in Danny Sleator's chess programming lectures. We
     * basically record every move in the game thus far plus the in the game
     * tree branch currently being plied. If the same position occurs more
     * than twice in the game, then we don't want to pick it.
     */
	private void addDelta(B board)
	{

		Long signature = board.signature();

		// if the count of map is not null (0)
		if (repetitionMap.containsKey(signature))
			repetitionMap.put(signature, repetitionMap.get(signature) + 1);
		else
			repetitionMap.put(signature, 1);
	}

	/*
	 * Removes the counted board from the repetition map if its count is 1,
	 * otherwise reduces its count.
	 */
	public void removeDelta(B board)
	{
		Long signature = board.signature();

		if (repetitionMap.get(signature) != null)
			repetitionMap.put(signature, repetitionMap.get(signature) - 1);
		else return;

		if (repetitionMap.get(signature) <= 0)
			repetitionMap.remove(signature);
	}

	/*
     * this function calculates the number of pieces remaining on the board
     * from the board's fen. for some reason, unlike ARRAYBOARD,
     * the board interface does not define a countOfAllPieces method. for
     * this reason it was necessary to calculate the number of pieces another
     *  way.
     */
	private int numPieces(String fen)
	{
		fen = fen.substring(0, fen.indexOf(' '));
		fen = fen.replace("/", "");
		fen = fen.replaceAll("\\d", "");
		return fen.length();
	}

	/*
     * this function, given the number of pieces on the board,
     * calculates the depth to ply in the game tree. The motivation behind
     * this is that later in the game, the branching factor is much less and
     * thus plying deeper will not take as long.
     */
	private int calculateDepth(int numPieces)
	{
		if (numPieces > CUTOFF_ARRAY[0]) return DEPTH_ARRAY[0];
		else if (numPieces > CUTOFF_ARRAY[1]) return DEPTH_ARRAY[1];
		else if (numPieces > CUTOFF_ARRAY[2]) return DEPTH_ARRAY[2];
		else if (numPieces > CUTOFF_ARRAY[3]) return DEPTH_ARRAY[3];
		else return DEPTH_ARRAY[4];
	}

	/*
    * This function implements the negamax search algorithm. It is a version
    * of minimax that uses the following mathematical identity to avoid
    * having a min and max search method:
    *          max(a, b) == -min(-a, -b)
    *
    * It also calls quiesce() to make use of quiescent search,
    * which makes captures and trades more intelligent by plying those parts
    *  of the tree more deeply.
    */
	private int negaMax(B board, int depth, int alpha, int beta)
	{

		// this is the quiesce() call
		if (depth == 0)
			return quiesce(board, alpha, beta, QUIESCENT_DEPTH);

		long signature = board.signature();

		List<M> moves = board.generateMoves();

		// if can't move, then the game is over. either a stalemate
		// or a checkmate.
		if (moves.isEmpty())
		{
			// because exiting from function
			removeDelta(board);
			if (board.inCheck())
				return -evaluator.mate() + depth;
			else
				return -evaluator.stalemate();
		}


		// here we prune if this board has appeared twice before. doing this
		// move would result in a stalemate by the repetition rule. we give
		// this a 0 value so that the only thing it is preferred to is
		// checkmate.
		if ((repetitionMap.containsKey(signature)) &&
				(repetitionMap.get(signature) == 2))
			return 0;
		addDelta(board);

		// for each move, go one level deeper in the game tree. Maximize the values
		// that come out of negaMax.
		for (M move : moves)
		{
			board.applyMove(move);
			alpha = Math.max(alpha, -negaMax(board, depth - 1, -beta, -alpha));
			board.undoMove();

			if (alpha >= beta)
				break; // prune
		}
		removeDelta(board);
		return alpha;

	}

	/*
     * Quiescent search is a way to lessen the effect of the "horizon effect"
     * This effect is caused by significantly bad moves happening just
     * beyond the search depth. If these branches appear advantageous then
     * the engine will pick them, which is obviously bad. Quiescent searching
     * plies captures a few levels deeper to make sure that they're not
     * actually disadvantageous.
     */
	private int quiesce(B board, int alpha, int beta, int depth)
	{

		/*
          The choices are either to do a "standing pat", which is not make this
          move, or make any of the captures reachable from this board. Because
          of this, we choose between standingPat (the current board) or the
          captures.
         */
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

		// alpha-beta pruning in quiescent search as well
		if (standPat >= beta)
			return beta;

		if (alpha < standPat)
			alpha = standPat;


		int score;
		for (M move : moves)
		{
			// ply the game tree but only examine captures
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