package edu.cmu.cs211.chess.evaluation;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayPiece;

/*
 * EndGameEvaluator
 *
 * This basically is same as the piece-counting TestedEvaluator that assigns
 * a value to each piece on the board. It changes in the endgame to make the
 * pieces more valuable and to make king positioning more intelligent. In the
 * endgame it is better for the king to be in the middle where it can better
 * avoid traps and mates.
 */
public class EndGameEvaluator implements Evaluator<ArrayBoard>
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
     * eval
     *
     * This function figures out if it's endgame. if so,
     * evaluate with that knowledge. Else, use the regular evaluator,
     * which just counts pieces.
     */
	public int eval(ArrayBoard board)
	{
		int whiteSum = 0, blackSum = 0;

		int numWhitePieces = board.countOfColor(ArrayBoard.WHITE);
		int numBlackPieces = board.countOfColor(ArrayBoard.BLACK);

		if (numWhitePieces <= 5 || numBlackPieces <= 5)
		{
			whiteSum = evalLow(board, ArrayBoard.WHITE);
			blackSum = evalLow(board, ArrayBoard.BLACK);
		}
		else
		{
			whiteSum = evalHigh(board, ArrayBoard.WHITE);
			blackSum = evalHigh(board, ArrayBoard.BLACK);
		}

		return (board.toPlay() == ArrayBoard.WHITE) ? (whiteSum - blackSum) : (blackSum - whiteSum);
	}


	/*
    * evalLow
    *
    * This function evaluates the board according to the above spec,
    * but makes pieces more valuable. It also uses the kingpos array to
    * encourage the king to move towards the middle of the board.
    */
	private int evalLow(ArrayBoard board, int color)
	{

		if (color == ArrayBoard.WHITE)
		{
			int whiteSum = 0;
			if (board.hasCastled[ArrayBoard.WHITE]) whiteSum += CASTLE_BONUS;

			for (ArrayPiece p : board.allPiecesOfColor(ArrayBoard.WHITE))
			{
				switch (p.type())
				{
					case ArrayPiece.KING:
						whiteSum += 4 * (calculateWhiteKingWeight(p, board.countOfColor(ArrayBoard.WHITE),
								board.countOfColor(ArrayBoard.BLACK)));
						break;
					case ArrayPiece.QUEEN:
						whiteSum += 2 * queenval;
						break;
					case ArrayPiece.ROOK:
						whiteSum += 2 * rookval;
						break;
					case ArrayPiece.BISHOP:
						whiteSum += 2 * (bishoppos[p.row()][p.col()] + bishopval);
						break;
					case ArrayPiece.KNIGHT:
						whiteSum += 2 * (knightpos[p.row()][p.col()] + knightval);
						break;
					case ArrayPiece.PAWN:
						whiteSum += 2 * (pawnpos[p.row()][p.col()] + pawnval);
						break;
				}
			}
			return whiteSum;
		}
		else
		{
			int blackSum = 0;
			for (ArrayPiece p : board.allPiecesOfColor(ArrayBoard.BLACK))
			{
				switch (p.type())
				{
					case ArrayPiece.KING:
						blackSum += 4 * (calculateBlackKingWeight(p, board.countOfColor(ArrayBoard.WHITE),
								board.countOfColor(ArrayBoard.BLACK)));
						break;
					case ArrayPiece.QUEEN:
						blackSum += 2 * queenval;
						break;
					case ArrayPiece.ROOK:
						blackSum += 2 * rookval;
						break;
					case ArrayPiece.BISHOP:
						blackSum += 2 * (bishoppos[7 - p.row()][p.col()] + bishopval);
						break;
					case ArrayPiece.KNIGHT:
						blackSum += 2 * (knightpos[7 - p.row()][p.col()] + knightval);
						break;
					case ArrayPiece.PAWN:
						blackSum += 2 * (pawnpos[7 - p.row()][p.col()] + pawnval);
						break;
				}
			}
			return blackSum;
		}
	}


	/*
    * evalHigh
    *
    * This function evaluates the board according to the above spec. It
    * loops through all the pieces on the board and assigns two sums:
    * whiteSum and blackSum, for the value of white's and black's pieces,
    * respectively. Then it adds them and sets the sign according to whose
    * turn it is.
    */
	private int evalHigh(ArrayBoard board, int color)
	{
		if (color == ArrayBoard.WHITE)
		{
			int whiteSum = 0;
			if (board.hasCastled[ArrayBoard.WHITE]) whiteSum += CASTLE_BONUS;
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
			return whiteSum;
		}
		else
		{
			int blackSum = 0;
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
			return blackSum;
		}
	}

	private int calculateWhiteKingWeight(ArrayPiece whiteKing, int whiteCount, int blackCount)
	{
		if (blackCount == 1) // black has only king left
			return kingpos[whiteKing.row()][whiteKing.col()] + kingval;

		if (whiteCount != 1) // not end game for white piece
			return kingval;
		else
			return kingval - kingpos[whiteKing.row()][whiteKing.col()];
	}

	private int calculateBlackKingWeight(ArrayPiece blackKing, int whiteCount, int blackCount)
	{

		if (whiteCount == 1) // white has only king left
			return kingpos[7 - blackKing.row()][blackKing.col()] + kingval;

		if (blackCount != 1) // not end game for black piece
			return kingval;
		else
			return kingval - kingpos[7 - blackKing.row()][blackKing.col()];
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
					{-15, 0, 0, -5, -5, 0, 0, -15},
					{-11, 2, 3, 4, 4, 3, 2, -11},
					{-8, 4, 6, 10, 10, 6, 4, -8},
					{-5, 5, 8, 10, 10, 8, 5, -5},
					{3, 7, 11, 15, 15, 11, 7, 3},
					{20, 22, 24, 26, 26, 24, 22, 20},
					{30, 30, 30, 30, 30, 30, 30, 30}
			};

	private static int kingpos[][] =

			{
					{1, 0, -1, -2, -2, -1, 0, 1},
					{0, -1, -2, -3, -3, -2, -1, 0},
					{-1, -2, -3, -4, -4, -3, -2, -1},
					{-2, -3, -4, -5, -5, -4, -3, -2},
					{-2, -3, -4, -5, -5, -4, -3, -2},
					{-1, -2, -3, -4, -4, -3, -2, -1},
					{0, -1, -2, -3, -3, -2, -1, 0},
					{1, 0, -1, -2, -2, -1, 0, 1}
			};


	/* Material value of a piece */
	private static final int kingval = 350;
	private static final int queenval = 900;
	private static final int rookval = 500;
	private static final int bishopval = 300;
	private static final int knightval = 300;
	private static final int pawnval = 100;


	/* The bonus for castling */
	private static final int CASTLE_BONUS = 10;
}
