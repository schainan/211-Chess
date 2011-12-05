package edu.cmu.cs211.chess.board;

import java.util.List;

import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.util.Creatable;

public interface Board
<
  M extends Move<M>,
  B extends Board<M,B>
>
extends Creatable<B>
{
	/**
	 * Initializes the state of the board according to the
	 * specified fen.
	 * 
	 * @param fen the fen to initialize the board with.
	 * @return a reference to this.
	 */
    public B       init                 (String fen);
    
    /**
     * Generates a list of valid moves from this board position.
     * Will not contain any duplicates.
     * 
     * @return the list of moves.
     */
    public List<M> generateMoves        ();
    /**
     * Applies the specified move to the board.
     * 
     * @param move the move to apply.
     */
    public void    applyMove            (M move);
    /**
     * Creates a move from the specified String according to the
     * state of the board.
     * 
     * !!! WARNING !!!
     * This is the format that the server emits to clients,
     * but the server does not accept this format from clients. 
     * !!! WARNING !!!
     * 
     * The String must be in Smith Notation
     * https://www.chessclub.com/chessviewer/smith.html
     * 
     * The behavior of this function is undefined if the move
     * String does not represent a legal move from the current
     * board position.
     * 
     * @see Move.serverString
     * @param move the string.
     * @return the move corresponding to the specified string.
     */
    public M       createMoveFromString (String move);
    
    /**
     * Quickly generates a list of moves which are probably legal if
     * not for checking rules. May contain duplicate moves.
     * 
     * @return the list of moves.
     */
    public List<M> generatePseudoMoves  ();
    /**
     * Returns whether or not a pseudo-move is legal.
     * 
     * This method is only guaranteed to work on moves which were
     * generated by the generatePsuedoMoves() method.
     * 
     * @param move the move to apply.
     * @return true iff the move was legal.
     */
    public boolean isLegalPseudoMove    (M move);
    
    /**
     * Returns whether or not the move is valid and legal.
     * 
     * @param move the move to apply.
     * @return true iff the move was legal.
     */
    public boolean isLegalMove          (M move);
    
    /**
     * Reverts the state of the board to what it was previously.
     */
    public void    undoMove             ();
    
    /**
     * @return true iff the king of the player to move is in check.
     */
    public boolean inCheck              ();
    /**
     * @return the player who would make the next move.
     */
    public int     toPlay               ();
    /**
     * @return the number of times white and black have
     *  moved since the beginning of the game.
     */
    public int     plyCount             ();
    
    public boolean equals               (Object o);
    public int     hashCode             ();
    /**
     * The signature of a board should be suitable for using in place of
     * the full blown equals method in the interest of speed.
     * 
     * Typically, instead of mapping from boards to useful information,
     * you will map from signatures to useful information.
     * 
     * @return the signature.
     */
    public long    signature            ();
    
    /**
     * Returns an ASCII picture of a chess board.
     * 
     * p=pawn, n=knight b=bishop,
     * r=rook, q=queen, k=king.
     * Lower case letters are the black pieces,
     * upper case for white.
     * 
     * Example:
     * 
     *     a b c d e f g h
     *    +---------------+
     *  8 |r n b q k b n r| 8
     *  7 |p p p p p p p p| 7
     *  6 |- - - - - - - -| 6
     *  5 |- - - - - - - -| 5
     *  4 |- - - - - - - -| 4
     *  3 |- - - - - - - -| 3
     *  2 |P P P P P P P P| 2
     *  1 |R N B Q K B N R| 1
     *    +---------------+
     *     a b c d e f g h
     * 
     * @return an ascii picture of the board.
     */
    public String  toString             ();
    /**
     * Returns an ascii representation of a board that is easy
     * to use for file i/o.
     * 
     * Ex: (The starting position)
     * "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -"
     * 
     * @see http://en.wikipedia.org/wiki/Forsyth-Edwards_Notation
     * 
     * @return the fen.
     */
    public String  fen                  ();
    
    public static String STARTING_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq -";
}
