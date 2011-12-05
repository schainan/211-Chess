package edu.cmu.cs211.chess.board;

import static edu.cmu.cs211.chess.board.ArrayBoard.*;

/**
 * Represents a move via the original source piece,
 * The original dest piece, and a possible piece
 * to promote into.
 * 
 * We need the capture boolean for enpassant captures.
 * 
 * @author Owen Durni (opd@andrew.cmu.edu)
 *
 */
public class ArrayMove implements Move<ArrayMove>
{
  public static final ArrayMove FACTORY = new ArrayMove();
  
  public  ArrayPiece source;
  public  ArrayPiece dest;
  public  ArrayPiece promote;
  public  ArrayPiece capture;
  
  public ArrayMove create()
  {
    return new ArrayMove();
  }
  
  public ArrayMove init(ArrayPiece source, ArrayPiece dest, ArrayPiece capture)
  {
    return init(source,dest,null,capture);
  }
  
  public ArrayMove init(ArrayPiece source, ArrayPiece dest, ArrayPiece promote, ArrayPiece capture)
  {
    this.source  = source.copy();
    this.dest    = dest.copy();
    this.promote = promote == null ? null : promote.copy();
    this.capture = capture;
    
    return this;
  }
  
  public ArrayMove copy()
  {
    ArrayMove copy = create();
    
    copy.source    = source.copy();
    copy.dest      = dest.copy();
    copy.promote   = promote == null ? null : promote.copy();
    copy.capture   = capture;
    
    return copy;
  }

  @Override
  public boolean equals(Object o)
  {
    if(this==o) return true;
    if(o==null) return false;
    if(!(o instanceof ArrayMove)) return false;
    
    ArrayMove move = (ArrayMove)o;
    
    return (source.equals(move.source) && dest.equals(move.dest)
            && (promote == null ? move.promote == null : promote.equals(move.promote)));
  }
  
  @Override
  public String toString()
  {
  	return serverString();
  }
  
  public String serverString()
  {
  	StringBuilder sb = new StringBuilder();
  	
  	sb.append(squareToString(source.square));
  	sb.append(squareToString(dest.square));
  	
  	if(isPromotion())
  	{
  		sb.append("=");
  		sb.append(promote.toString().toUpperCase());
  	}
  	
  	return sb.toString();
  }
  
  public String smithString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(squareToString(source.square));
    sb.append(squareToString(dest.square));
    
    if( isCapture() )
    {
      if(dest.isOccupied())
      {
        sb.append(dest.toString().toLowerCase());
      }
      else
      {
        //enpassant
        sb.append("E");
      }
    }
    
    if(isPromotion())
    {
      sb.append(promote.toString().toUpperCase());
    }
    
    return sb.toString();
  }

  @Override
  public int hashCode()
  {
    return source.hashCode() ^ dest.hashCode();
  }
  
  public boolean isPromotion()
  {
    return promote != null;
  }

  public boolean isCapture()
  {
    return capture.isOccupied();
  }

  public boolean isEnpassant()
  {
  	return capture.isOccupied() && dest.isEmpty();
  }

  public int destCol() {
    return colOfSquare(dest.square);
  }

  public int destRow() {
    return rowOfSquare(dest.square);
  }

  public int srcCol() {
    return colOfSquare(source.square);
  }

  public int srcRow() {
    return rowOfSquare(source.square);
  }
}
