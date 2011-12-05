package edu.cmu.cs211.chess.search;

public class SimpleTimer implements Timer
{
	//private int  initialTime;
	private int  increment;
	private long startTime;
	private int  allocated;
	
	private boolean noTimeup = false;

	public SimpleTimer(int initialTime, int increment)
	{
		//this.initialTime = initialTime;
		this.increment   = increment;
	}
	
	public void start(int myTime, int opTime)
	{
		startTime = System.currentTimeMillis();
		allocated = allocateTime(myTime, opTime);
	}
	
	public boolean timeup() {
		if (noTimeup)
			return false;
		if ((System.currentTimeMillis() - startTime) > allocated) {
			return true;
		}
		return false;
	}
	
	/*
	 * This method computes and returns an amount of time to allocate to the
	 * current move. It takes as parameters my time left, and the opponent's
	 * time left. However this implementation only makes use of my time left,
	 * and the increment (increment). All times are in milliseconds.
	 * 
	 * The way it computes this is that it assumes the game is going to last 30
	 * more moves, so it allocates 1/30th of the remaining time, plus the
	 * increment, to the current move.
	 */
	private int allocateTime(int timeLeft, int opTimeLeft) {
		double t = .9 * increment + timeLeft / 30.0;
		if(t > timeLeft)
			t = .9 * timeLeft;
		return (int) t;
	}

	public void notOkToTimeup() {
		noTimeup = true;
	}

	public void okToTimeup() {
		noTimeup = false;
	}
}
