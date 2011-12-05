package edu.cmu.cs211.chess.evaluation;

import edu.cmu.cs211.chess.board.Board;

/**
 * An evaluator that essentially does nothing.
 */
public class NoEvaluator
<
	B extends Board<?,B>
>
implements Evaluator<B>
{
	public int eval(B board)
	{
		return 0;
	}

	public int infty()
	{
		return 2;
	}

	public int mate()
	{
		return 1;
	}

  public int stalemate()
  {
    return 0;
  }
}
