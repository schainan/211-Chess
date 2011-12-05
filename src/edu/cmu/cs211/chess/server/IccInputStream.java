package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;

// input stream to read ICC level 1 and level 2

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Timer;
import java.util.TimerTask;


@SuppressWarnings ("deprecation")
public final class IccInputStream extends FilterInputStream {

	// window for output
	private Hub mhub;

	// our parent connection object
	private Iconn connection;

	// create a new stream for readinf from the icc
	public IccInputStream(Iconn aconn, InputStream ain, Hub ahub, Encryptor e) {
		super(ain);
		connection = aconn;
		got_eof = false;
		mhub = ahub;
		receive_encryptor = e;
	}

	// timestamp encryptor
	private Encryptor receive_encryptor;

	private boolean decrypting = true;

	// eof flag
	private boolean got_eof;

	// initial line buffer size
	private static final int SBUF_initial_size = 400;

	// bell byte from server
	private static final int BELL_byte = 7;

	// escape byte sent before open/close l1/l2
	private static final int MARK_byte = 25;

	// open/close level 1
	private static final int L1_open_byte = '[';

	private static final int L1_close_byte = ']';

	// open/close level 2
	private static final int L2_open_byte = '(';

	private static final int L2_close_byte = ')';

	// open/close quote
	private static final int QUOTE_open_byte = '{';

	private static final int QUOTE_close_byte = '}';

	// constants returned for special character sequnces
	public final static int FIRST_non_char = 256;

	public final static int L1_open = FIRST_non_char;

	public final static int L1_close = L1_open + 1;

	public final static int L2_open = L1_close + 1;

	public final static int L2_close = L2_open + 1;

	public final static int QUOTE_open = L2_close + 1;

	public final static int QUOTE_close = QUOTE_open + 1;

	public final static int BOGUS_mark = QUOTE_close + 1;

	public final static int ICC_eof = BOGUS_mark + 1;

	// Timestamp constants
	// server to client
	public final static int TS_BOARD_FOLLOWS = 238;

	public final static int TS_END_OF_BOARD = 237;

	public final static int TS_PING = 236;

	// client to server
	public final static int TS_ACK_BOARD = 235;

	public final static int TS_ACK_PING = 234;

	public final static int TS_TIMEUP = 233;

	// at eof?
	public boolean eof() {
		return got_eof;
	}

	// return the next byte of input
	// drop \r characters
	// strip parity
	private final int read_byte_or_eof() throws IOException {
		byte[] b = new byte[1];
		while (true) {
			int result = in.read();
			if (result < 0) {
				got_eof = true;
				return ICC_eof;
			}
			if (decrypting) {
				b[0] = (byte) result;
				receive_encryptor.encrypt_string(b, 1);
				result = (int) b[0] & 0xff; // was result &= 0x7f; need to keep
											// high bit for timestamp
				// System.out.print((char) result);
			}
			if (result == BELL_byte) {
				continue;
			}
			if (result == '\r')
				continue;
			return result;
		}
	}

	// marked characters are returned as one input byte
	private final int read_datum() throws IOException {
		int result = read_byte_or_eof();
		if (result != MARK_byte)
			return result;
		// got a mark byte read next character to encode a result
		result = read_byte_or_eof();
		switch (result) {
		case L1_open_byte:
			return L1_open;
		case L1_close_byte:
			return L1_close;
		case L2_open_byte:
			return L2_open;
		case L2_close_byte:
			return L2_close;
		case QUOTE_open_byte:
			return QUOTE_open;
		case QUOTE_close_byte:
			return QUOTE_close;
		}
		return BOGUS_mark;
	}

	// return a string of the next line of input
	final String read_string(boolean break_on_eol) throws IOException {
		StringBuilder lbuf = new StringBuilder(SBUF_initial_size);
		while (true) {
			int c = read_flat_datum();
			if (c >= FIRST_non_char)
				break;
			lbuf.append((char) c);
			// wake up on eol or the : of login
			// if(break_on_eol && ((c=='\n')||((c==':')&&(lbuf.length()<8))))
			if (break_on_eol && (c == '\n'))
				break;
		}
		return lbuf.toString();
	}

	// an opening of a l1 item has been found
	private final void parse_L1() throws IOException {
		try {
			//String l1_value = 
			read_string(false);
			if (connection.get_want_death())
				return;

			/*
			 * -- banner chess doesn't respond to L1's.
			 * 
			 * L1Item l1=new L1Item(l1_value); String l1_text=l1.getText(); //
			 * just after login set variables if(l1.i_did_it()) {
			 * if(l1.getCmdNum()==L1Codes.SCN_LOGOUT) mhub.time_to_die();
			 * if(l1.getCmdNum()==L1Codes.SCN_REALLY_LOG_IN)
			 * mhub.send_initial_settings(); } switch (l1.getCmdNum() ) { case
			 * L1Codes.CN_TELL: case L1Codes.CN_SAY: mhub.addOutput(l1_text,
			 * "### "); break; case L1Codes.CN_CHANNELTELL:
			 * mhub.addOutput(l1_text, "@@@ "); break; case L1Codes.CN_WHISPER:
			 * case L1Codes.CN_KIBITZ: mhub.addOutput(l1_text, "^^^ "); break;
			 * default: mhub.addOutput(l1_text); }
			 */
		} catch (Exception e) {
			// OkBox.DoOkException("Parse level 1 error",e);
		}
	}

	// read a quoted string
	private String read_quoted_string(int break_on) throws IOException {
		StringBuilder lbuf = new StringBuilder(SBUF_initial_size);
		while (true) {
			int c = read_datum();
			if (c >= FIRST_non_char) // read till close quote (or worse)
				break;
			if (c == break_on)
				break;
			lbuf.append((char) c);
		}
		return lbuf.toString();
	}

	// an opening of a l2 item has been found
	private final void parse_L2() throws IOException {
		StringBuilder lbuf = new StringBuilder(SBUF_initial_size);
		L2Item anitem = new L2Item();
		while (true) {

			int c = read_datum();
			if (c == QUOTE_open)
				anitem.add(read_quoted_string(FIRST_non_char));
			else if (c == '{')
				anitem.add(read_quoted_string((int) '}'));
			else if (Character.isWhitespace((char) c) || (c >= FIRST_non_char)) {
				if (lbuf.length() > 0)
					anitem.add(lbuf.toString());
				lbuf.setLength(0);
				if (c >= FIRST_non_char)
					break;
			} else
				lbuf.append((char) c);
		}
		if (!connection.get_want_death())
			mhub.receiveL2(anitem);
	}

	// an opening of a quoted item has been found
	private final void parse_quote() throws IOException {
		mhub.addMessage("~got a L2 quote outside of L2~\n");
	}

	private void parse_TS_ping() throws IOException {
		StringBuilder lbuf = new StringBuilder(SBUF_initial_size);

		// System.out.println("In parse_TS_ping()");

		while (true) {
			int c = read_byte_or_eof();
			lbuf.append((char) c);
			if (c == '\n') {
				// System.out.println("Received ping: " + lbuf.toString());
				lbuf.setLength(lbuf.length() - 1); // remove "\n"
				mhub.sendCommand("" + (char) TS_ACK_PING + lbuf.toString()
						+ "\r");
				break;
			}
		}
	}

	private void parse_TS_board_follows() throws IOException {
		StringBuilder lbuf = new StringBuilder();
		long myclock = 0;
		boolean gettingclock = true;

		// System.out.println("In parse_TS_board_follows()");

		while (true) {
			int c = read_byte_or_eof();
			lbuf.append((char) c);
			if (gettingclock && c == '\n') {
				// read myclock from server, prepare to eat board
				try {
					lbuf.setLength(lbuf.length() - 1); // remove "\n"
					myclock = Long.parseLong(lbuf.toString());
				} catch (NumberFormatException e) {
					mhub.addMessage("~TS_boardfollows could not read clock~\n");
				}
				gettingclock = false;
				lbuf.setLength(0);
				// System.out.println("Clock = " + myclock);
			}
			if (!gettingclock && c == TS_END_OF_BOARD) {
				// parse the contents of the buffer in the normal manner
				// this requires spoofing the input stream to be the
				// already read characters in the StringBuilder,
				// calling read_flat_datum, and restoring the input stream
				// Problem: the data has already been decrypted.

				// System.out.println("Received board.");

				InputStream hold = in;
				in = new StringBufferInputStream(lbuf.toString());

				// acknowledge receipt of board
				killtimer();
				mhub.sendCommand("" + (char) TS_ACK_BOARD + "\r");
				starttimer(myclock);

				decrypting = false;
				int read_in;
				StringBuilder read_in_buf = new StringBuilder();
				while ((read_in = read_flat_datum()) != ICC_eof) {
					if (read_in < 128)
						read_in_buf.append((char) read_in);
				}

				if (read_in_buf.length() > 0)
					mhub.addMessage(read_in_buf.toString());

				decrypting = true;

				got_eof = false;
				in = hold;

				break;
			}
		}
	}

	// the following code implements the TS_TIMEUP protocol
	private Timer timer;

	private static final long maxTimer = 3600000;

	public synchronized void killtimer() {
		if (timer == null)
			return;
		timer.cancel ();
	}

	private synchronized void starttimer(long msec) {
		// create thread that sleeps for msec, then sends TS_TIME_UP to server
		if (msec > maxTimer)
			return;
		
		timer = new Timer (true);
		timer.schedule (new TimerTask () {
			@Override
			public void run ()
			{
				mhub.sendCommand("" + (char) TS_TIMEUP + "\r");
			}
		}, msec);
	}


	// read a byte
	// if it starts a quoted sequence silently read that
	private final int read_flat_datum() throws IOException {
		while (true) {

			if (connection.get_want_death())
				return 0;
			int result = read_datum();
			switch (result) {
			case L1_open:
				parse_L1();
				continue;
			case L2_open:
				parse_L2();
				continue;
			case QUOTE_open:
				parse_quote();
				continue;
			case TS_PING:
				parse_TS_ping();
				continue;
			case TS_BOARD_FOLLOWS:
				parse_TS_board_follows();
				continue;
			default:
				return result;
			}
		}
	}
}
