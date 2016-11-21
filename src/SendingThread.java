//import bittorrent.DataMessage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SendingThread extends Thread
{
	CommonConfig cc;
	GetPeerInfo info;
	PeerInfo myInfo;
	Socket sendingConnection;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	int i,portToConnect,peerId,increment;
        DataMessage useForEncoding,recMessage;
        boolean interested = false, notinterested = false;
 	
 	public SendingThread(CommonConfig cc,GetPeerInfo info,PeerInfo myInfo,int peerId,int i,int increment)
	{
		this.cc=cc;
		this.info=info;
		this.myInfo = myInfo;
		this.i=i;
		int myIncrement = peerProcess.peerIncrement[myInfo.peerId%1000][peerId%1000];
		this.portToConnect=Integer.parseInt(info.peerInfoVector.get(i).peerPort)+100+myIncrement;
		this.peerId=peerId;      
		this.increment=increment;
		useForEncoding = new DataMessage();
		recMessage = new DataMessage();
	}
        
        public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
        
        public static byte[] intToByteArray(int a)
	{
	    return new byte[] {
	        (byte) ((a >> 24) & 0xFF),
	        (byte) ((a >> 16) & 0xFF),   
	        (byte) ((a >> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
 	
 	public void run()
	{
		try
		{
			System.out.println("I am the sending thread for "+myInfo.peerId);
			sendingConnection = new Socket(info.peerInfoVector.get(i).peerAddress, portToConnect);
		
			//System.out.println("Connected to localhost and port "+ portToConnect);
			//2. get Input and Output streams
			out = new ObjectOutputStream(sendingConnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sendingConnection.getInputStream());
			System.out.println(myInfo.peerId+" is ready to send to "+peerId);
	        
                        out.writeObject(myInfo.bMap.getBitMap());
                        out.flush();
                        //bitField receivedBitField = new bitField((byte[])in.readObject());
                        //peerProcess.bifFieldMap.put(peerId, receivedBitField);
                        bitField bf=peerProcess.bifFieldMap.get(myInfo.peerId);
                        boolean prev=false,first=true;
                        while (!peerProcess.allPeersHaveCompleteFile) {
                            String forMe="";
                            if(!peerProcess.isMasterRunning) {
                                
                                
                                if(peerProcess.myPeersHashMap.get(peerId) == 0) { //if choked
                                    
                                	if(prev==true)
                                	{
                                		byte[] sendMe = useForEncoding.encodedmessage("0");
                                        out.writeObject(sendMe);
                                        out.flush();
                                        prev=false;
                                        first=false;
                                	}
                                	
                                    if(in.available() > 0) {
                                        
                                        byte[] msg = (byte[])in.readObject();
                                        recMessage = recMessage.decodemessage(msg);
                                        String type = recMessage.getmessagetype();

                                        switch (type) {
                                            case "DATA_MSG_INTERESTED":
                                                interested = true;
                                                notinterested = false;
                                                break;
                                            case "DATA_MSG_NOTINTERESTED":
                                                notinterested = true;
                                                interested = false;
                                            default: break;
                                        }
                                        if(interested) {
                                                peerProcess.listOfInterestedNeighbours.add(peerId);
                                        }
                                        else if(notinterested) {
                                            //peerProcess.listOfInterestedNeighbours.remove(peerId);
                                        }
                                    }
                                }
                                else { //unchoked
                                	
                                	if(prev==false)
                                	{
                                		byte[] sendMe = useForEncoding.encodedmessage("1");
                                        out.writeObject(sendMe);
                                        out.flush();
                                        prev=true;
                                	}
                                	
                                    byte [] msg = (byte[])in.readObject();
                                    recMessage = recMessage.decodemessage(msg);
                                    String type = recMessage.getmessagetype();
                                    forMe=type;
                                    if (type.equals("DATA_MSG_REQUEST")) { //send requested file chunk
                                          byte[] payload = recMessage.getmessagepayload();
                                                int index = byteArrayToInt(payload);
                                                
                                                byte[] chunk = new byte[cc.PieceSize];
                                                chunk = peerProcess.getChunk(index);
                                                byte[] temp = new byte[chunk.length + payload.length];
                                                System.arraycopy(payload, 0, temp, 0, payload.length);
                                                System.arraycopy(chunk, 0, temp, payload.length, chunk.length); 
                                                byte[] sendMe = useForEncoding.encodemessage("7", temp);
                                                out.writeObject(sendMe);
                                                out.flush();
                                                //receivedBitField.setBit(index);
                                    }
                                    else if (type.equals("DATA_MSG_NOTINTERESTED")) {
                                        peerProcess.listOfInterestedNeighbours.removeElement(peerId);
                                    }
                            }
                            
                                bitField myBitMap = peerProcess.bifFieldMap.get(myInfo.peerId);
                                
                                for(int i = 0;i<myBitMap.bitMapSize;i++)
                                {
                                        if(myBitMap.getBit(i)!=bf.getBit(i) && !(forMe.equals("DATA_MSG_REQUEST"))) {
                                            byte[] sendMe = useForEncoding.encodemessage("4",intToByteArray(i));
                                            out.writeObject(sendMe);
                                            out.flush();
                                        }
                                }  
                            bf = myBitMap;
                     }
                }
                sendingConnection.close();
	}
        catch(Exception e)
	{
		System.out.println("Exception in connection at Sender : ");e.printStackTrace();
	}
    }
}