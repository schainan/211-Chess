package edu.cmu.cs211.chess.server;

import java.io.InputStream;
import java.io.PrintStream;


final class Timestamp {

	// returns the send and receive encryptors on successful
	// timestamp authentication
	// returns null if an error occurs
	public static Encryptor[] authenticate(InputStream in, PrintStream out,
			Hub mhub) {

		try { // surrounds entire function
			Encryptor[] encryptors = new Encryptor[2];

			StringBuilder logintextbuf = new StringBuilder(1000);
			String login = "login: ";

			int len, loginlen = login.length();

			while (true) {
				int i = in.read();
				if (i < 0) // error, end-of-stream
					return null;
				i &= 0x7f;
				logintextbuf.append((char) i);

				// check to see if we've received "login: " yet
				if ((len = logintextbuf.length()) < loginlen)
					continue;
				if (logintextbuf.toString().substring(len - loginlen).equals(
						login)) {
					break;
				}
			}
			//System.out.print(logintextbuf.toString());
			//System.out.flush();

			//			if (!Hub.boolTimestampPossible) {
				encryptors[0] = new Encryptor();
				encryptors[1] = encryptors[0];
				return encryptors;
			//			}

		} catch (Exception e) {
			// System.out.println("\nConnection failed: " + e);
			mhub.addMessage("\nConnection failed: " + e + "\n");
			e.printStackTrace();
			try {
				Thread.sleep(20000);
			} catch (Exception e2) {
			}
			return null; // error connecting
		}
	}
}
