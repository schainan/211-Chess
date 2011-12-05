package edu.cmu.cs211.chess.server;



public final class Encryptor {

	public void encrypt_string(byte[] s, int len) {
		if (!Hub.boolTimestampPossible)
			return; // do nothing
	}

	@SuppressWarnings("deprecation")
	public byte[] applyTimestamp(String acmd) {
	    //		if (!Hub.boolTimestampPossible) {
			byte[] blah = new byte[acmd.length()];
			acmd.getBytes(0, blah.length, blah, 0); // this only takes lower order bits
			return blah;
            //		}
	}
}
