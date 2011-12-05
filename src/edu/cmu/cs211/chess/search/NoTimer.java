package edu.cmu.cs211.chess.search;

/**
 * This timer never times up.
 */
public class NoTimer implements Timer
{
	public void notOkToTimeup()
	{
	  //nothing
	}

	public void okToTimeup()
	{
	  //nothing
	}

	public void start(int myTime, int opTime)
	{
	  //nothing
	}

	public boolean timeup()
	{
	  return false;
	}
}
