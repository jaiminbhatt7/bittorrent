import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;



public class ReceivingThread extends Thread
{
	ServerSocket receivingConnection;
	ObjectOutputStream out;
	ByteArrayOutputStream out1;
	ObjectInputStream in;
	ByteArrayInputStream in1;
	CommonConfig cc;
	GetPeerInfo info;
	PeerInfo myInfo;
	int peerId, interestedPiece,increment;
	boolean choked,sentInterested,needRequest;
	DataMessage useForEncoding,recMessage;
	
	public
	ReceivingThread(CommonConfig cc,GetPeerInfo info,PeerInfo myInfo,int peerId,int increment)
	{
		this.cc=cc;
		this.info=info;
		this.myInfo = myInfo;
		this.peerId = peerId;
		interestedPiece=0;
		choked=true;
		sentInterested=false;
		needRequest=false;
		useForEncoding = new DataMessage();
		recMessage = new DataMessage();
		this.increment=increment;
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
			System.out.println("I am the receiving thread for "+myInfo.peerId);
			ServerSocket receivingConnection= new ServerSocket(Integer.parseInt(myInfo.peerPort)+100+increment);

			Socket connection = receivingConnection.accept();
			//receivingConnection.close();
			out = new ObjectOutputStream(connection.getOutputStream());
			//out1 = new ByteArrayOutputStream(connection);
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			
			System.out.println(myInfo.peerId+" is ready to receive from "+peerId);

			bitField receivedBitField = new bitField((byte[])in.readObject());
			peerProcess.bifFieldMap.put(peerId, receivedBitField);
			/*System.out.println("Received bitField of my peer "+peerId);
			for(int i = 0;i<10;i++)
				System.out.print(receivedBitField.getBit(i));
			
			
			System.out.println(" \n Iterating through bitMaps received ---------------");
			
			peerProcess.bifFieldMap.put(peerId, receivedBitField);
			
			Iterator it = peerProcess.bifFieldMap.keySet().iterator();
			while (it.hasNext()) {  
	           Integer id = (Integer)it.next();
	           bitField bf = peerProcess.bifFieldMap.get(id);
	           System.out.println("Received PeerID" + id + "'s Bitfield. BitField is ");           
	           for(int i = 0;i<10;i++)
					System.out.print(receivedBitField.getBit(i));
	           
	           System.out.println();
	        }  */
	        //out.flush();
		
	        while(!peerProcess.allPeersHaveCompleteFile)
	        {
	        	if(!peerProcess.isMasterRunning)
	        	{
	        		if(choked)
	        		{
	        			bitField bf = peerProcess.bifFieldMap.get(peerId);
        				bitField myBitMap = peerProcess.bifFieldMap.get(myInfo.peerId);
        				int i=0;
        				for(i = 0;i<bf.bitMapSize;i++)
        				{
        					if((bf.getBit(i)==true) && (myBitMap.getBit(i)==false))
        						break;
        				}
        				interestedPiece = i;
        				if(i<bf.bitMapSize && !sentInterested)
        				{
        					byte[] sendMe = useForEncoding.encodedmessage("2");
        					out.writeObject(sendMe);
        					out.flush();
        					sentInterested=true;
        				}	
        				else
        				{
        					if(!sentInterested)
        					{
        						byte[] sendMe = useForEncoding.encodedmessage("3");
        						out.writeObject(sendMe);
        						out.flush();
        						sentInterested=true;
        					}
        				}
        				
        				if(in.available()>0)
        				{
        					byte[] msg = (byte[])in.readObject();
        	        		recMessage = recMessage.decodemessage(msg);
        	        		String type = recMessage.getmessagetype();
        	        		switch(type)
        	        		{
        	        			case "DATA_MSG_CHOKE":
        	        				System.out.println("Choked by "+myInfo.peerId);
        	        				break;
        	        			case "DATA_MSG_UNCHOKE":
        	        				System.out.println("Unchoked by "+myInfo.peerId);
        	        				choked=false;
        	        				needRequest=true;
        	        				break;
        	        			case "DATA_MSG_HAVE":
        	        				System.out.println(myInfo.peerId+" has "+byteArrayToInt(recMessage.getmessagepayload()));
        	        				peerProcess.bifFieldMap.get(myInfo.peerId).setBit(byteArrayToInt(recMessage.getmessagepayload()));
        	        				//I don't send have messages of my own since that should be sent by sender
        	        				//sender should check at each iteration what new pieces it has acquired and send have messages for those
        	        			default: break;
        	        		}
        				}
	        		}
	        		
	        		else
	        		{
	        			if(needRequest)
	        			{
	        				byte[] pieceIndex = ByteBuffer.allocate(4).putInt(interestedPiece).array();
	        				byte[] sendMe = useForEncoding.encodemessage("6", pieceIndex);
	        				out.writeObject(sendMe);
	        				out.flush();
	        				needRequest=false;
	        			}
	        			
	        			byte[] msg = (byte[])in.readObject();
    	        		recMessage = recMessage.decodemessage(msg);
    	        		String type = recMessage.getmessagetype();
    	        		switch(type)
    	        		{
    	        		case "DATA_MSG_CHOKE":
    	        			choked=true;
    	        			System.out.println("Choked by "+myInfo.peerId);
	        				break;
    	        		case "DATA_MSG_HAVE":
    	        			System.out.println(myInfo.peerId+" has "+byteArrayToInt(recMessage.getmessagepayload()));
	        				peerProcess.bifFieldMap.get(peerId).setBit(byteArrayToInt(recMessage.getmessagepayload()));
	        				break;
    	        		case "DATA_MSG_PIECE":
    	        			byte[] payload = recMessage.getmessagepayload();
    	        			byte[] pieceNo = Arrays.copyOfRange(payload, 0, 4);
    	        			int chunkNo = byteArrayToInt(pieceNo);
    	        			byte[] data = Arrays.copyOfRange(payload, 4, payload.length);
    	        			peerProcess.saveChunk(chunkNo, data);
    	        			peerProcess.bifFieldMap.get(myInfo.peerId).setBit(chunkNo);
    	        			
    	        			System.out.println(myInfo.peerId+" has sent me "+chunkNo);
    	        			
    	        			bitField bf = peerProcess.bifFieldMap.get(peerId);
            				bitField myBitMap = peerProcess.bifFieldMap.get(myInfo.peerId);
            				int i=0;
            				for(i = 0;i<bf.bitMapSize;i++)
            				{
            					if((bf.getBit(i)==true) && (myBitMap.getBit(i)==false))
            						break;
            				}
            				interestedPiece = i;
            				if(i<bf.bitMapSize)
            				{
            					needRequest=true;
            				}	
            				else
            				{
            					byte[] sendMe = useForEncoding.encodedmessage("3");
            					out.writeObject(sendMe);
            					out.flush();
            				}
            				peerProcess.addToPriorityQueue(peerId, 1);
            				
            				break;
    	        		}
	        		}
	        				
	        				
	        		
	        	}
	        }
	        
	        //int counter = 0;
	        //sendingConnection.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception in connection Receiver : ");e.printStackTrace();
		}
	}
}
