package edu.cmu.cs211.chess.search;

import java.util.Observable;
import java.util.Observer;

import edu.cmu.cs211.chess.board.Board;
import edu.cmu.cs211.chess.board.Move;
import edu.cmu.cs211.chess.evaluation.Evaluator;

public abstract class AbstractSearcher
<
  M extends Move<M>,
  B extends Board<M,B>
>
  implements Searcher<M,B>
{
  protected Evaluator<B> evaluator;
  protected Timer        timer;
  protected int          minDepth;
  protected int          maxDepth;
  protected long         leafCount;
  protected long         nodeCount;
  
  private BestMovePublisher<M>
    bestMovePublisher = new BestMovePublisher<M>();

  public void setEvaluator(Evaluator<B> e)
  {
    evaluator = e;
  }

  public void setFixedDepth(int depth)
  {
    setMaxDepth(depth);
    setMinDepth(depth);
  }

  public void setMaxDepth(int depth)
  {
    maxDepth = depth;
  }

  public void setMinDepth(int depth)
  {
    minDepth = depth;
  }

  public void setTimer(Timer t)
  {
    timer = t;
  }

  public long leafCount()
  {
    return leafCount;
  }

  public long nodeCount()
  {
    return nodeCount;
  }
  
  public void addBestMoveObserver(Observer o)
  {
    bestMovePublisher.addObserver(o);
  }
  
  protected void reportNewBestMove(M move)
  {
    bestMovePublisher.updateBestMove(move);
  }
  
  private static class BestMovePublisher
  <
    M extends Move<M>
  > extends Observable
  {
    public void updateBestMove( M move )
    {
      setChanged();
      notifyObservers(move);
    }
  }
}
