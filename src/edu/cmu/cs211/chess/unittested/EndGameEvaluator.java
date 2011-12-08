package edu.cmu.cs211.chess.unittested;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayPiece;
import edu.cmu.cs211.chess.evaluation.Evaluator;

/*
 * uses a position array for the king in the case when it is nearing end of game
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
					{0, 0, 0, -5, -5, 0, 0, 0},
					{0, 2, 3, 4, 4, 3, 2, 0},
					{0, 4, 6, 10, 10, 6, 4, 0},
					{0, 6, 9, 10, 10, 9, 6, 0},
					{4, 8, 12, 16, 16, 12, 8, 4},
					{5, 10, 15, 20, 20, 15, 10, 5},
					{0, 0, 0, 0, 0, 0, 0, 0}
			};

	private static int kingpos[][] =
			{
					{15, 15, 15, 15, 15, 15, 15, 15},
					{15, 10, 10, 10, 10, 10, 10, 15},
					{15, 10, 5, 5, 5, 5, 10, 15},
					{15, 10, 5, 0, 0, 5, 10, 15},
					{15, 10, 5, 0, 0, 5, 10, 15},
					{15, 10, 5, 5, 5, 5, 10, 15},
					{15, 10, 10, 10, 10, 10, 10, 15},
					{15, 15, 15, 15, 15, 15, 15, 15}
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
