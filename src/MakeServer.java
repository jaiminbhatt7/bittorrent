//import java.net.ServerSocket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
//import java.util.Arrays;


public class MakeServer extends Thread implements Constants
{
	Socket sendingConnection;
	ObjectOutputStream out;
	ObjectInputStream in;
	CommonConfig cc;
	GetPeerInfo info;
	PeerInfo myInfo;
	int peerId,increment;
	public
	MakeServer(Socket s,CommonConfig cc,GetPeerInfo info,PeerInfo myInfo,int increment)
	{
		this.sendingConnection = s;
		this.cc=cc;
		this.info=info;
		this.myInfo = myInfo;
		this.peerId = 00;
		this.increment=increment;
	}
	public void run()
	{
		try
		{
			System.out.println("Connection received from " + sendingConnection.getInetAddress().getHostName());
			out = new ObjectOutputStream(sendingConnection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sendingConnection.getInputStream());
			
			
			byte[] receivedHeader = (byte [])in.readObject();
			HandshakeMessage decodemessage = new HandshakeMessage();
	        byte[] pr = new byte[HANDSHAKE_PEERID_LEN];
	        pr = decodemessage.decodeheader(receivedHeader);
	        String temp = new String(pr,MSG_CHARSET_NAME);
	        peerId = Integer.parseInt(temp);
	        System.out.println(peerId+" is connected to me");
	        
	        HandshakeMessage handshake = new HandshakeMessage();
			byte[] peer = new byte[HANDSHAKE_PEERID_LEN];
	        byte[] encodedheader = new byte[HANDSHAKE_MSG_LEN];
	        peer=Integer.toString(myInfo.peerId).getBytes(MSG_CHARSET_NAME);
	        encodedheader = handshake.encodeheader(peer);
	        out.writeObject(encodedheader);
	        out.flush();
		
		
	        sendingConnection.close();
	        
	        Thread t1 = new ReceivingThread(cc,info,myInfo,peerId,increment);
        	t1.start();
        	
        	int i=0;
        	for(i=0;i<info.peerInfoVector.size();i++)
        	{
        		if(info.peerInfoVector.get(i).peerId == peerId)
        			break;
        	}
        			        	
        	Thread t2 = new SendingThread(cc,info,myInfo,peerId,i,increment);
        	t2.start();
		}
		catch(Exception e)
		{
			System.out.println("Exception in connection : "+e.getMessage());
		}
	}
	
	
}
