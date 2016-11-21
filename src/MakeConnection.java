import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class MakeConnection extends Thread implements Constants
{
	CommonConfig cc;
	GetPeerInfo info;
	PeerInfo myInfo;
	Socket sendingConnection;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	int i,portToConnect,peerId,increment;
 	//Logger log;
	public
		MakeConnection(CommonConfig cc,GetPeerInfo info,PeerInfo myInfo,int i,int increment)
		{
			this.cc=cc;
			this.info=info;
			this.myInfo = myInfo;
			this.i=i;
			this.portToConnect=Integer.parseInt(info.peerInfoVector.get(i).peerPort);
			this.peerId=00;
			this.increment=increment;
		}
	public void run()
	{
		try
		{
			//1. creating a socket to connect to the server
			sendingConnection = new Socket(info.peerInfoVector.get(i).peerAddress, portToConnect);
		//	log.writeLog(myPeerID, peerIDtoConnectTo, 1);
			
			System.out.println("Connected to localhost and port "+ portToConnect);
			//2. get Input and Output streams
			out = new ObjectOutputStream(sendingConnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sendingConnection.getInputStream());
		
	        

			HandshakeMessage handshake = new HandshakeMessage();
			byte[] peer = new byte[HANDSHAKE_PEERID_LEN];
	        byte[] encodedheader = new byte[HANDSHAKE_MSG_LEN];
	        peer=Integer.toString(myInfo.peerId).getBytes(MSG_CHARSET_NAME);
	        encodedheader = handshake.encodeheader(peer);
	        out.writeObject(encodedheader);
			out.flush();
			
			byte[] receivedHeader = (byte [])in.readObject();
			HandshakeMessage decodemessage = new HandshakeMessage();
	        byte[] pr = new byte[HANDSHAKE_PEERID_LEN];
	        pr = decodemessage.decodeheader(receivedHeader);
	        String temp = new String(pr,MSG_CHARSET_NAME);
	        peerId = Integer.parseInt(temp);
	        System.out.println(peerId+" is connected to me");
	        
	        sendingConnection.close();
	        
	        Thread t1 = new ReceivingThread(cc,info,myInfo,peerId,increment);
        	t1.start();
        	
        	Thread t2 = new SendingThread(cc,info,myInfo,peerId,i,increment);
        	t2.start();
	        
			
		}
		catch(Exception e)
		{
			System.out.println("Exception in connection\n"+e.getMessage());
		}
		
	}

}
