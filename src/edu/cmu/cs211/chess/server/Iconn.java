package edu.cmu.cs211.chess.server;
//package com.chessclub.easychess;
// a thread to read input from icc

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;


//import java.util.*;

public final class Iconn extends Thread {

	private Encryptor send_encryptor;

	// send this at end of lines
	// private final String sent_eol = "\015"; // what is ^O for???

	// socket for talking to icc
	private Socket csock;

	// output data to icc
	private PrintStream outs;

	// stream to consume data from
	private IccInputStream instream;

	// my main window for output
	private Hub mhub;

	// hostname to talk to
	private String hostname;

	// tcp port to connect to
	private int port;

	// are fully open
	private boolean are_open;

	// want listener to exit?
	private boolean want_death;

	// thread to do shutdown
	// does shutdown if want death is true
	public Iconn(Hub ahub, Socket acsock) {
		mhub = ahub;
		csock = acsock;
		want_death = true;
		this.start();
	}

	// construct a lister to consume this input stream
	public Iconn(Hub ahub, String ahostname, int aport) {
		are_open = false;
		want_death = false;
		hostname = ahostname;
		port = aport;
		mhub = ahub;
		this.start();
	}

	// do we want to die?
	public boolean get_want_death() {
		return want_death;
	}

	// set that we want to die
	// note: call this from a thread that can block
	// a while in the close
	public void set_want_death() {
		if (want_death) //already want to die
			return;
		want_death = true;

		if (instream != null) //added by Pravir
			instream.killtimer(); // kill the Timestamp timer
		// shutdown in a seperate thread
		if (csock != null)
			new Iconn(mhub, csock);
	}

	// are we open?
	public boolean connectionEstablished() {
		return are_open;
	}

	// send a command
	public synchronized void send_cmd(String acmd, boolean echo) {
		// if connection dropped don't try to send unob
		//  when closing a game window
		if (!connectionEstablished()) {
			mhub.addMessage("No connection yet {" + acmd + "}\n");
			return;
		}

		if (outs.checkError())
			mhub.killConnection();
		if (get_want_death()) {
			mhub.addMessage("Closing connection {" + acmd + "}\n");
			return;
		}
		if (echo)
			mhub.addMessage("{" + acmd + "}\n");
		// acmd=acmd+sent_eol;
		// outs.println(acmd);
		//outs.print(send_encryptor.applyTimestamp(acmd + "\n"));
		//outs.flush();
//		try {
	//        outs.write (send_encryptor.applyTimestamp(acmd + "\n"));
      //  } catch (IOException e) {
	        // TODO Auto-generated catch block
	    //    e.printStackTrace();
        //}
		byte[] myCmd = send_encryptor.applyTimestamp(acmd + "\n");
		for (int i = 0; i < myCmd.length; i++) {
			outs.write(myCmd[i]);
		}
		
		outs.flush ();

		if (outs.checkError())
			mhub.killConnection();
	}

	// open the connection at startup
	private void open_connection() throws IOException {
		Encryptor encs[];

		csock = new Socket(hostname, port);
		outs = new PrintStream(csock.getOutputStream());

		encs = Timestamp.authenticate(csock.getInputStream(), outs, mhub);
		send_encryptor = encs[0];

		DataInputStream ins = new DataInputStream(csock.getInputStream());
		instream = new IccInputStream(this, ins, mhub, encs[1]);
		are_open = true;

		// mhub.sendCommand("level1=1");
		// mhub.sendCommand("level2settings="+mhub.L2_settings());
	}

	// consume input from icc, throw errors
	private void private_run() throws IOException {
		open_connection();
		while (!get_want_death()) {
			String aline = instream.read_string(true);
			if (get_want_death())
				return;
			if (instream.eof())
				throw new IOException("Connection closed");
			else
				mhub.addMessage(aline);
		}
	}

	// close connection
	private void close_csock() {
		Socket close_me = csock;
		csock = null;
		try {
			if (close_me != null)
				close_me.close();
		} catch (Exception e) {
			;
		}
	}

	// consume input from icc, handle any errors
	public void run() {
		try {
			if (want_death)
				close_csock();
			else
				private_run();
		} catch (Exception e) {
			// only notify if user doesn't already know
			if (!want_death) {
				mhub.killConnection();
				// OkBox.DoOkException("Input listener died",e);
				//System.out.println("Input listener died");
				//e.printStackTrace(System.out);
				//System.out.println("Server probably down - Pravir Gupta"); //added by Pravir
			}
		}
	}
}