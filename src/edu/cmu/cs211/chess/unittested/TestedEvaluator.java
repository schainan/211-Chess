package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayPiece;
import edu.cmu.cs211.chess.evaluation.Evaluator;

public class TestedEvaluator implements Evaluator<ArrayBoard>
{

    private static final int INFINITY = 1000000;
    private static final int MATE = 300000;
    private static final int STALEMATE = 0;

    public int infty()
    {
        return INFINITY;
    }

    public int mate()
    {
        return MATE;
    }

    public int stalemate()
    {
        return STALEMATE;
    }

    /*
          * This is the evaluator. It simply returns a score for the board position
          * with respect to the player to move. It must function precisely as
          * described here in order to pass the unit tests. [If you want to use a
          * different evaluation function for a more advanced version of your
          * program, you can do that, but the eval() method here must function as
          * described.]
          *
          * The evaluation function gives a score for each piece according to the
          * pieceValue array below, and an additional amount for each piece depending
          * on where it is (see comment below). A bonus of 10 points should be given
          * if the current player has castled (and -10 for the opponent castling)
          *
          * The eval of a position is the value of the pieces of the player whose
          * turn it is, minus the value of the pieces of the other player (plus the
          * castling points thrown in).
          *
          * If it's WHITE's turn, and white is up a queen, then the value will be
          * roughly 900. If it's BLACK's turn and white is up a queen, then the value
          * returned should be about -900.
          */

    /*
     * eval
     *
     * This function evaluates the board according to the above spec. It
     * loops through all the pieces on the board and assigns two sums:
     * whiteSum and blackSum, for the value of white's and black's pieces,
     * respectively. Then it adds them and sets the sign according to whose
     * turn it is.
     */
    public int eval(ArrayBoard board)
    {
        int whiteSum = 0, blackSum = 0;
        //add castle bonuses here...
        if (board.hasCastled[ArrayBoard.BLACK]) blackSum += CASTLE_BONUS;
        if (board.hasCastled[ArrayBoard.WHITE]) whiteSum += CASTLE_BONUS;

        //first we assign whiteSum
        for (ArrayPiece p : board.allPiecesOfColor(ArrayBoard.WHITE))
        {
            switch (p.type())
            {
                case ArrayPiece.KING:
                    whiteSum += kingval;
                    break;
                case ArrayPiece.QUEEN:
                    whiteSum += queenval;
                    break;
                case ArrayPiece.ROOK:
                    whiteSum += rookval;
                    break;
                //for bishops, knights, and pawns, add a bonus according to
                //their board position.
                case ArrayPiece.BISHOP:
                    whiteSum += bishoppos[p.row()][p.col()] + bishopval;
                    break;
                case ArrayPiece.KNIGHT:
                    whiteSum += knightpos[p.row()][p.col()] + knightval;
                    break;
                case ArrayPiece.PAWN:
                    whiteSum += pawnpos[p.row()][p.col()] + pawnval;
                    break;
            }
        }

        //now assign blackSum
        for (ArrayPiece p : board.allPiecesOfColor(ArrayBoard.BLACK))
        {
            switch (p.type())
            {
                case ArrayPiece.KING:
                    blackSum += kingval;
                    break;
                case ArrayPiece.QUEEN:
                    blackSum += queenval;
                    break;
                case ArrayPiece.ROOK:
                    blackSum += rookval;
                    break;
                case ArrayPiece.BISHOP:
                    blackSum += bishoppos[7 - p.row()][p.col()] + bishopval;
                    break;
                case ArrayPiece.KNIGHT:
                    blackSum += knightpos[7 - p.row()][p.col()] + knightval;
                    break;
                case ArrayPiece.PAWN:
                    blackSum += pawnpos[7 - p.row()][p.col()] + pawnval;
                    break;
            }
        }

        //depending on whose turn it is, set the sign of the returned value
        // accordingly.
        return (board.toPlay() == ArrayBoard.WHITE) ? (whiteSum - blackSum) : (blackSum - whiteSum);
    }

    /*
          * Piece value tables modify the value of each piece according to where it
          * is on the board.
          *
          * To orient these tables, each row of 8 represents one row (rank) of the
          * chessboard.
          *
          * !!! The first row is where white's pieces start !!!
          *
          * So, for example
          * having a pawn at d2 is worth -5 for white. Having it at d7 is worth
          * 20. Note that these have to be flipped over to evaluate black's pawns
          * since pawn values are not symmetric.
          */
    private static int bishoppos[][] =
            {
                    {-5, -5, -5, -5, -5, -5, -5, -5},
                    {-5, 10, 5, 8, 8, 5, 10, -5},
                    {-5, 5, 3, 8, 8, 3, 5, -5},
                    {-5, 3, 10, 3, 3, 10, 3, -5},
                    {-5, 3, 10, 3, 3, 10, 3, -5},
                    {-5, 5, 3, 8, 8, 3, 5, -5},
                    {-5, 10, 5, 8, 8, 5, 10, -5},
                    {-5, -5, -5, -5, -5, -5, -5, -5}
            };
    private static int knightpos[][] =
            {
                    {-10, -5, -5, -5, -5, -5, -5, -10},
                    {-8, 0, 0, 3, 3, 0, 0, -8},
                    {-8, 0, 10, 8, 8, 10, 0, -8},
                    {-8, 0, 8, 10, 10, 8, 0, -8},
                    {-8, 0, 8, 10, 10, 8, 0, -8},
                    {-8, 0, 10, 8, 8, 10, 0, -8},
                    {-8, 0, 0, 3, 3, 0, 0, -8},
                    {-10, -5, -5, -5, -5, -5, -5, -10}
            };
    private static int pawnpos[][] =
            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, -5, -5, 0, 0, 0},
                    {0, 2, 3, 4, 4, 3, 2, 0},
                    {0, 4, 6, 10, 10, 6, 4, 0},
                    {0, 6, 9, 10, 10, 9, 6, 0},
                    {4, 8, 12, 16, 16, 12, 8, 4},
                    {5, 10, 15, 20, 20, 15, 10, 5},
                    {0, 0, 0, 0, 0, 0, 0, 0}
            };

    /* Material value of a piece */
    private static final int kingval = 350;
    private static final int queenval = 900;
    private static final int rookval = 500;
    private static final int bishopval = 300;
    private static final int knightval = 300;
    private static final int pawnval = 100;
    //private static final int emptyval     = 0;

    /* The bonus for castling */
    private static final int CASTLE_BONUS = 10;
}