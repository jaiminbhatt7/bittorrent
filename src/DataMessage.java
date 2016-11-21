
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class DataMessage implements Constants {
    
    private byte[] msglength = new byte [DATA_MSG_LEN];
    private byte[] type = new byte [DATA_MSG_TYPE];
    private byte[] payload = null;
    
    public DataMessage(byte[] len, byte[] Type, byte[] Payload) {
        System.arraycopy(len, 0, this.msglength, 0, DATA_MSG_LEN);
        System.arraycopy(Type, 0, this.type, 0, DATA_MSG_TYPE);
        if(Payload!=null) {
            System.arraycopy(Payload, 0, this.payload, 0, Payload.length);
        }
        else {
            this.payload = null;
        }
    }
    
    
    public DataMessage() {
        msglength = null;
        type = null;
        payload = null;
    }
    
    
    public byte[] encodedmessage(String Type) throws UnsupportedEncodingException, Exception {
        
        if (!Type.equals(DATA_MSG_CHOKE) && !Type.equals(DATA_MSG_UNCHOKE) && !Type.equals(DATA_MSG_INTERESTED) && !Type.equals(DATA_MSG_NOTINTERESTED)) {
            throw new Exception("Error: Message type length not 1 byte");
        }
        this.settype(Type);
        this.setlength(1);
        byte[] encodedmessage = new byte[type.length + msglength.length];
        System.arraycopy(this.msglength, 0, encodedmessage, 0, DATA_MSG_LEN);
        System.arraycopy(this.type, 0, encodedmessage, DATA_MSG_LEN, DATA_MSG_TYPE);
        return encodedmessage;
    }
   
    public byte[] encodemessage(String Type, byte[] Payload) throws Exception {
        byte[] encodedmessage = new byte[payload.length + type.length + msglength.length];;
        this.settype(Type);
       
        if(this.type.length != DATA_MSG_TYPE) {
            throw new Exception("Error: Message type length not 1 byte");
        }
       
            this.setlength(Payload.length + 1);
           
            payload = new byte[Payload.length];
            System.arraycopy(Payload, 0, this.payload, 0, Payload.length);
            System.arraycopy(this.msglength, 0, encodedmessage, 0, DATA_MSG_LEN);
            System.arraycopy(this.type, 0, encodedmessage, DATA_MSG_LEN, DATA_MSG_TYPE);
            System.arraycopy(this.payload, 0, encodedmessage, DATA_MSG_LEN + DATA_MSG_TYPE, payload.length);
       
        
        return encodedmessage;
    }
    
    
    public DataMessage decodemessage(byte[] msg) throws Exception {
        
        byte[] len = new byte[DATA_MSG_LEN];
        byte[] Type = new byte[DATA_MSG_TYPE];
        byte[] Payload = null;
        
        if(msg == null) {
            throw new Exception("Error: Null Message");
        }
        
        if(msg.length < DATA_MSG_LEN + DATA_MSG_TYPE) {
            throw new Exception("Error: Too small message");
        }
        
        System.arraycopy(msg, 0, len, 0, DATA_MSG_LEN);
	System.arraycopy(msg, DATA_MSG_LEN, Type, 0, DATA_MSG_TYPE);
        
        if(msg.length > DATA_MSG_LEN + DATA_MSG_TYPE) {
            Payload = new byte[msg.length - (DATA_MSG_LEN + DATA_MSG_TYPE)];
            System.arraycopy(msg, DATA_MSG_LEN + DATA_MSG_TYPE, Payload, 0, Payload.length);
        }
        
        DataMessage decodedmessage = new DataMessage(len,Type,Payload);
        return decodedmessage;
    }
    
    
    public void setlength(int l) {
        this.msglength = ByteBuffer.allocate(DATA_MSG_LEN).putInt(l).array();
    }
    
    
    public void settype(String t) throws UnsupportedEncodingException {
        this.type = t.getBytes(MSG_CHARSET_NAME);
    }
    
    public int getmessagelength() {
        return (type.length + payload.length);
    }
    
    
    public String getmessagetype() throws UnsupportedEncodingException, Exception {
        String temp = new String(type,MSG_CHARSET_NAME);
        String ans = null;
        
        /*if(temp == "DATA_MSG_CHOKE")
        {
        	 ans = "DATA_MSG_CHOKE";
        }
        else if ( temp == "DATA_MSG_UNCHOKE")
        {
        	 ans = "DATA_MSG_UNCHOKE";
        }
        else if (temp == "DATA_MSG_INTERESTED")
        {
        	ans = "DATA_MSG_INTERESTED";
        }
        else if (temp == "DATA_MSG_NOTINTERESTED")
        {
        	ans = "DATA_MSG_NOTINTERESTED";
        }
        else if (temp == "DATA_MSG_HAVE")
        {
        	ans = "DATA_MSG_HAVE";
        }
        else if (temp == "DATA_MSG_BITFIELD")
        {
        	ans = "DATA_MSG_BITFIELD";
        }
        else if (temp =="DATA_MSG_REQUEST")
        {
        	 ans = "DATA_MSG_REQUEST";
        }
        else if (temp =="DATA_MSG_PIECE")
        {
        	ans = "DATA_MSG_PIECE";
        }
        else
        	throw new Exception("Error: Unknown Message Type");
        
        */
        switch(temp) {
            case DATA_MSG_CHOKE:
                ans = "DATA_MSG_CHOKE";
                break;
                
            case DATA_MSG_UNCHOKE:
                ans = "DATA_MSG_UNCHOKE";
                break;
                
            case DATA_MSG_INTERESTED:
                ans = "DATA_MSG_INTERESTED";
                break;
                
            case DATA_MSG_NOTINTERESTED:
                ans ="DATA_MSG_NOTINTERESTED";
                break;
                
            case DATA_MSG_HAVE:
                ans = "DATA_MSG_HAVE";
                break;
                
            case DATA_MSG_BITFIELD:
                ans = "DATA_MSG_BITFIELD";
                break;
                
            case DATA_MSG_REQUEST:
                ans = "DATA_MSG_REQUEST";
                break;
                
            case DATA_MSG_PIECE:
                ans = "DATA_MSG_PIECE";
                break;
                
            default:
                throw new Exception("Error: Unknown Message Type");
        }
        return ans;
    }
    
    public byte[] getmessagepayload() throws UnsupportedEncodingException { 
        return payload;
    }
}