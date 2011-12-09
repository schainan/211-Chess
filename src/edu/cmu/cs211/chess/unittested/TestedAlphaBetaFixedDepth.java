package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.search.AbstractSearcher;

import java.util.List;

/**
 * An implementation of Alpha Beta search.
 * <p/>
 * This is the class that will be unit tested by FrontDesk.
 *
 * This class uses the Negamax algorithm to find the best next move for the
 * given board. It uses alpha-beta pruning to make the search time faster.
 * It calls the negamax function for each board reachable from this one and
 * tries to minimize the result. The move that causes this minimum board is
 * the one that is returned.
 */
public class TestedAlphaBetaFixedDepth<M extends Move<M>, B extends Board<M, B>> extends AbstractSearcher<M, B>
{
    public M getBestMove(B board, int myTime, int opTime)
    {
        List<M> moves = board.generateMoves();
        int extreme = Integer.MAX_VALUE;  //trying to minimize,
                                          //so can only go down from Int.MAX_VAL
        M bestMoveSoFar = null;

        for (M move : moves)
        {
            board.applyMove(move);
            //negamax call here with the specified weight
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


    /*
     * This function implements the negamax search algorithm. It is a version
     * of minimax that uses the following mathematical identity to avoid
     * having a min and max search method:
     *          max(a, b) == -min(-a, -b)
     */
    private int negaMax(B board, int depth, int alpha, int beta)
    {
        if (depth == 0)
            return evaluator.eval(board);

        List<M> moves = board.generateMoves();

        //if can't move, then the game is over. either a stalemate
        //or a checkmate.
        if (moves.isEmpty())
        {
            if (board.inCheck())
                return -evaluator.mate() + depth;
            else
                return -evaluator.stalemate();
        }
        //for each move, go one level deeper in the game tree. Maximize the values
        //that come out of negamax.
        for (M move : moves)
        {
            board.applyMove(move);
            alpha = Math.max(alpha, -negaMax(board, depth - 1, -beta, -alpha));
            board.undoMove();

            if (alpha >= beta)
                break; // alpha-beta pruning here
        }
        return alpha;
    }
}