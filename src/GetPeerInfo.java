import java.io.BufferedReader;
//import java.io.FileNotFoundException;
import java.io.FileReader;
//import java.io.IOException;
import java.util.Vector;

public class GetPeerInfo {
    
    public Vector<PeerInfo> peerInfoVector = new Vector<PeerInfo>();
    
    
    //Setting up parameters for each peer and pushing it on to the vector
    void GetConfig() throws Exception {
    	CommonConfig cc = new CommonConfig();
        cc.GetCommon();
        String s;
        String path = System.getProperty("user.dir");
        BufferedReader in = new BufferedReader(new FileReader(path+"/src/PeerInfo.cfg"));
            while((s = in.readLine()) != null) {
               String[] temp = s.split("\\s+");
               peerInfoVector.addElement(new PeerInfo(temp[0],temp[1],temp[2],temp[3]));
            }
        }
    
    int getRank(int id)
    {
    	for(int i=0;i<peerInfoVector.size();i++)
    	{
    		if(peerInfoVector.elementAt(i).peerId == id)
    			return i;
    	}
    	return -1;
    }  
    
    boolean getFlagForHasFile(int id)
    {
    	for(int i=0;i<peerInfoVector.size();i++)
    	{
    		if(peerInfoVector.elementAt(i).peerId == id)
    		{
    			if(peerInfoVector.elementAt(i).hasFile ==1)
    			{
    				return true;
    			}
    		}
    	}
		return false;
    }
}
    
