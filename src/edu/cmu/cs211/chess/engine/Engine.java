package edu.cmu.cs211.chess.engine;

import java.util.Observer;

import edu.cmu.cs211.chess.board.ArrayBoard;
import edu.cmu.cs211.chess.board.ArrayMove;
import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.search.Searcher;
import edu.cmu.cs211.chess.server.Hub;
import edu.cmu.cs211.chess.unittested.TestedAlphaBetaFixedDepth;
import edu.cmu.cs211.chess.unittested.TestedEvaluator;

/**
 * A new Engine is created each time you start a game.
 */
public class Engine
{
	/*
	 * You will want to change these to whatever classes you want your bot
	 * to use when it plays a game of chess.  
	 */
	private ArrayBoard
		board    = ArrayBoard.FACTORY.create().init(Board.STARTING_POSITION);

	
	private Searcher <ArrayMove,ArrayBoard>
		searcher = new TestedAlphaBetaFixedDepth<ArrayMove,ArrayBoard>();
	
	private TestedEvaluator
	  //FIXME Change back
	  eval     = new TestedEvaluator();
	
	private int
	  plyCount = 0;
	
	/*
	 * Here are the public methods for Engine. You may change these methods, but
	 * do not alter their signatures.
	 */
	public String getName()
	{
		return "Name of Bot";
	}

	public Engine(int time, int inc)
	{
		searcher.setFixedDepth(4);
		searcher.setEvaluator(eval);
	}

	/**
	 * Converts the string representation of a move into a move
	 * and then applies it to the current board.
	 * 
	 * @param m the move string.
	 */
	public void applyMove(String m)
	{
	  if( board.plyCount() != plyCount++ )
	  {
	    throw new IllegalStateException(
	      "Did you forget to call undoMove() somewhere?"
	    );
	  }
	  
		board.applyMove(board.createMoveFromString(m));
	}

	/**
	 * Return the player's board state
	 */
	public ArrayBoard getBoard()
	{
		return board;
	}

	/**
	 * Compute and return a move in the current position.
	 * 
	 * The returned move must be in the String format accepted
	 * by the server.
	 * 
	 * @param myTime number of seconds left on the player's clock
	 * @param opTime number of seconds left on the opponent's clock
	 */
	public ArrayMove computeMove(int myTime, int opTime)
	{
	  assert(false) : "Assertions should be disabled when playing competitively.";
	  

    System.out.println(eval.eval(board));
	  
		ArrayMove move = searcher.getBestMove(getBoard(), myTime, opTime);
		
		
		return move;
	}

	/* These are for operating with the EasyChess GUI. */
		
	public Hub theHub;

	public Engine(Hub h, int time, int inc)
	{
		this(time, inc);
		theHub = h;
	}
	
	// This can be expanded so that the Observer is notified of other
	// events as well.
	/**
	 * Adds an Observer to the Searcher so that when a new best move
	 * is found, the Observer will be notified. 
	 * @param o the new Observer
	 */
	public void addBestMoveObserver(Observer o)
	{
		searcher.addBestMoveObserver(o);
	}
}
