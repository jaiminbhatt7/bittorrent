import java.io.FileNotFoundException;
import java.io.IOException;

public class PeerInfo {
    
    public int peerId;
    public String peerAddress;
    public String peerPort;
    public int hasFile;
    public bitField bMap;
    
    public PeerInfo(String pid, String add, String port, String hf) throws FileNotFoundException, IOException {
        peerId = Integer.parseInt(pid);
        peerAddress = add;
        peerPort = port;
        hasFile = Integer.parseInt(hf);
        bMap = new bitField();
        
        if(hasFile ==1)
        {
        	for(int i=0;i<bMap.bitMapSize;i++)
        	{
        		bMap.setBit(i);
        	}
        }
        else
        {
        	for(int i=0;i<bMap.numOfBytes;i++)
        	{
        		bMap.bitMap[i]=0;
        	} 	 
        }
        
    }
}
