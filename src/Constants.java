/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package bittorrent;

/**
 *
 * @author Jaimin Bhatt
 */
public interface Constants {
        public static final String MSG_CHARSET_NAME = "UTF8";

	// For Handshake message header
	public static final int HANDSHAKE_MSG_LEN = 32;

	public static final int HANDSHAKE_HEADER_LEN = 18;

	public static final int HANDSHAKE_ZEROBITS_LEN = 10;

	public static final int HANDSHAKE_PEERID_LEN = 4;

	// For Handshake message header
	public static final int DATA_MSG_LEN = 4;

	public static final int DATA_MSG_TYPE = 1;

	public static final String DATA_MSG_CHOKE = "0";

	public static final String DATA_MSG_UNCHOKE = "1";

	public static final String DATA_MSG_INTERESTED = "2";

	public static final String DATA_MSG_NOTINTERESTED = "3";

	public static final String DATA_MSG_HAVE = "4";

	public static final String DATA_MSG_BITFIELD = "5";

	public static final String DATA_MSG_REQUEST = "6";

	public static final String DATA_MSG_PIECE = "7";

	public static final String HANDSHAKE_HEADER = "CEN5501C2008SPRING";
        
        public static final String HANDSHAKE_ZERO = "0000000000";

	public static final int PIECE_INDEX_LEN = 4;
}
