

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class HandshakeMessage implements Constants {
    private byte[] header = new byte[HANDSHAKE_HEADER_LEN];
    private byte[] zero = new byte[HANDSHAKE_ZEROBITS_LEN];
    private byte[] peerID = new byte[HANDSHAKE_PEERID_LEN];
    
    // set headerv = "CEN5501C2008SPRING"
    public void setheader() throws UnsupportedEncodingException {
        this.header = HANDSHAKE_HEADER.getBytes(MSG_CHARSET_NAME);
    }
    
    //set zero = "0000000000"
    public void setzero() throws UnsupportedEncodingException {
        this.zero = HANDSHAKE_ZERO.getBytes(MSG_CHARSET_NAME);
    }
    
    //set peerID
    public void setpeerid(byte[] pid) throws UnsupportedEncodingException {
        System.arraycopy(pid, 0, this.peerID, 0, HANDSHAKE_PEERID_LEN);
    }
    
    
    public byte[] encodeheader(byte[] pid) throws UnsupportedEncodingException, Exception {
        byte[] encodedmessage = new byte[HANDSHAKE_MSG_LEN];
        
        this.setheader();
        this.setzero();
        this.setpeerid(pid);
        
        //create bytearray of encodedmessage from header,zero,peerID
        System.arraycopy(header, 0, encodedmessage, 0, header.length);
        System.arraycopy(zero, 0, encodedmessage, HANDSHAKE_HEADER_LEN, HANDSHAKE_ZEROBITS_LEN);
        System.arraycopy(peerID, 0, encodedmessage, HANDSHAKE_HEADER_LEN + HANDSHAKE_ZEROBITS_LEN, peerID.length);
        
        if(encodedmessage.length != HANDSHAKE_MSG_LEN) {
            throw new Exception("Error in Header Length");
        }
        return encodedmessage;
    }
    
    // Returns peer ID; Checks if header = "CEN5501C2008SPRING" and zero = "0000000000"
    public byte[] decodeheader(byte[] receivedMessage) throws Exception {
        
        byte[] hdr = new byte[HANDSHAKE_HEADER_LEN];
        byte[] peer = new byte[HANDSHAKE_PEERID_LEN];
        
        //Checks if Handshake message length is 32 bytes
        if(receivedMessage.length != HANDSHAKE_MSG_LEN) {
            throw new Exception("Error in Header Length");
        }
        
        //Extracts header field
        System.arraycopy(receivedMessage, 0, hdr, 0,HANDSHAKE_HEADER_LEN);
        String temp = new String(hdr,MSG_CHARSET_NAME);
        //Checks if header = "CEN5501C2008SPRING"
        if(!temp.equals(HANDSHAKE_HEADER)) {
            throw new Exception("Error in Header Field");
        }
        
        //Extracts peerID
	System.arraycopy(receivedMessage, HANDSHAKE_HEADER_LEN + HANDSHAKE_ZEROBITS_LEN, peer, 0,HANDSHAKE_PEERID_LEN);
        //decodedmessage.setheader();
        //decodedmessage.setpeerid(peer);
        //decodedmessage.setzero();
        
        return peer;
    }
}
