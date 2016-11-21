import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;
//import java.nio.ByteBuffer;
import java.util.Iterator;

//import java.io.FileNotFoundException;
//import java.io.IOException;

public class peerProcess {
	
	//Variable to keep track that if all the peers have received the file
	public static boolean allPeersHaveCompleteFile = false;
	// in myPeersHashMap PeerId is the key and 0 indicates that it is chocked and 1  indicates it is unchocked
	public static HashMap <Integer, Integer> myPeersHashMap = new HashMap<Integer,Integer>();
	//Structure that will hold bitmaps for all the peers
	public static HashMap <Integer,bitField> bifFieldMap = new HashMap<Integer,bitField>();
	//ArrayList of all the interested neighbors, Add interested neighbors computed to this list. 
	public static Vector<Integer> listOfInterestedNeighbours = new Vector<Integer>();
	//Controller for master and slave threads
	public static boolean isMasterRunning=true;
	//To keep track of whether the peer has entire file
	public static boolean hasCompleteFile=false;
	//Function to call to add interested neighbor
	public static void addToInterestedNieghborList(Integer addMe)
	{
		listOfInterestedNeighbours.add(addMe);
	}
	public static int[][] peerIncrement = { {1, 1, 1, 1, 1},
        {1, 2, 2, 2, 2},
        {2, 2, 3, 3, 3},
        {3, 3, 3, 4, 4},
        {4, 4, 4, 4, 5},
        {5, 5, 5, 5, 5}};
	public static filePartition fp ;
	public synchronized static void saveChunk(int chunkNo,byte[]data)//to save a chunk
	{
		fp.saveChunk(chunkNo, data);
	}
	public synchronized static byte[] getChunk(int chunkNo)//to get a chunk from file
	{
		return fp.giveChunk(chunkNo);
	}

	static PriorityQueueComparator myPriorityQueueComparator = new PriorityQueueComparator();
	public static PriorityQueue<PeerWrapper> myPriorityQueue=new PriorityQueue<PeerWrapper>(10,myPriorityQueueComparator);
	public static void addToPriorityQueue(int pID, int numChunks)
	{
		PeerWrapper temp = null;
		for(PeerWrapper p:myPriorityQueue)
		{
			if(p.peerID==pID)
				temp =p;
		}
		if(temp!= null)
		{
			numChunks = temp.getNumChunks() + numChunks;
			myPriorityQueue.remove(temp);
		}
		myPriorityQueue.add(new PeerWrapper(pID,numChunks));
	}
	
    public static void main(String[] args) throws Exception {
    	int myPeerId = Integer.parseInt(args[0]);
    	CommonConfig cc = new CommonConfig();
        cc.GetCommon();           
    	int rank=0,count=0;	
    	//Sets up peerInfoVector with details of each peer in order from the PeerInfo.cfg
    	GetPeerInfo info = new GetPeerInfo();
        info.GetConfig();
        // Test code for file chunks - getting and setting
        boolean hasFileFlag = info.getFlagForHasFile(myPeerId);      
        fp = new filePartition(hasFileFlag);   
        byte chunk[] = new byte[cc.PieceSize];
        chunk = fp.giveChunk(0);
	//	for(int j=0;j<5000;j++)
		//	System.out.print((char)chunk[j]);    
		fp.saveChunk(1, chunk);
		chunk = fp.giveChunk(1);
	//	for(int j=0;j<5000;j++)
		//	System.out.print((char)chunk[j]);
		// test code for file chunks end here		
        //Gets Rank of peer based on peerID supplied as command line argument
        rank = info.getRank(myPeerId);       
        //Total number of peers
        count = info.peerInfoVector.size();        
       //Set value to false for all peers in hashMap. So initially all the neighbors are chocked.
        for(int i=0;i<info.peerInfoVector.size();i++)
        {
        	int neighbourPeerID = info.peerInfoVector.get(i).peerId;
        	if( neighbourPeerID != myPeerId)
        	{
        		myPeersHashMap.put(neighbourPeerID,0);   		
        		 //Adding interested neighbours to arrayList here only. Need to put this in receiving thread when one receives
                //an interested message.
        		listOfInterestedNeighbours.add(neighbourPeerID);      		
        		//To be added by sender/receiver
        		addToPriorityQueue(neighbourPeerID,5);
        	}
        }       
        //Gets details of the peer running this program into myInfo
        PeerInfo myInfo = info.peerInfoVector.get(rank);
        boolean hasFile=false;
        //Initialize the bitMap
		if(myInfo.hasFile==1)
			hasFile=true;
		myInfo.bMap.initialize(hasFile);
		
		//Add bit map of self peer to the bifFieldMap
		peerProcess.bifFieldMap.put(myInfo.peerId, myInfo.bMap);
		
       
		hasCompleteFile = myInfo.bMap.isBitMapFull();
	    if(hasCompleteFile)
	        System.out.println("This peer has the entire file...");
	    else
    		System.out.println("This peer does not have the entire file...");
		
	    int increment = 1;
        for(int i=0;i<count;i++)
        {
        	//Connecting to all the peers that started before this peer.
        	if(i<rank)
        	{
        		//New thread. MakeConnection takes peerAddress and peerPort of all the peers that started before.
        		Thread t = new MakeConnection(cc,info,myInfo,i,increment);
        		t.start();
        		increment++;
        	}
        }
        
        int con = count - rank-1;
        ServerSocket providerSocket= new ServerSocket(Integer.parseInt(myInfo.peerPort));
        //int i=0;
        //Creating server sockets for the the peers that start after the current peer to connect
        while(con>0)
        {
        	System.out.println("Waiting for connection..");
        	con--;
        	Socket connection = providerSocket.accept();
        	Thread t = new MakeServer(connection,cc,info,myInfo,increment);
        	t.start();
        	increment++;
        }
        
        
        // Code to control choking and unchocking
        
        //Run loop until the bitfields of all the neighbours are fully set to one
        //This peer has the whole file, so it will randomly select p peers and unchoke them
        //Unchoke them and set their hash values to true   
        ArrayList<Integer> preferredNeighbours = new ArrayList<Integer>();            
        for(int i=0;i<listOfInterestedNeighbours.size();i++)
        	System.out.println("Interested Neighbours are " +listOfInterestedNeighbours.get(i) );
        
        //Value of numOfPreferredNeighbours to be retrieved from config file
        int numOfPreferredNeighbours = 1;
        
        //Test code to check Map entries. Does not add to logic.
        Iterator it = myPeersHashMap.keySet().iterator();
		while (it.hasNext()) {  
           Integer id = (Integer)it.next();
           Integer Flag = (Integer) myPeersHashMap.get(id);
           System.out.println("PeerID " + id + " Flag " + Flag); 
        }  
       //Test Code ends here.
		
		int fileWriteCounter =0;
		//int counter = 1;
        while(true)
        {
        	//Dummy Condition. To be replaced by allPeersHaveCompleteFile.
        	if(allPeersHaveCompleteFile)
        		break;
        	//counter++;
        	
        	//Terminating conditions
        	hasCompleteFile = myInfo.bMap.isBitMapFull();
        	if(hasCompleteFile && fileWriteCounter==0)
        	{
        		fp.writeToFile();
        		fileWriteCounter++;
        	}  	
        	
        	int numOfPeersHavingCompleteFile = 0;
        	Iterator ite = peerProcess.bifFieldMap.keySet().iterator();
			while (it.hasNext()) {  
	           Integer id = (Integer)it.next();
	           bitField bf = peerProcess.bifFieldMap.get(id);
	           if(bf.isBitMapFull())
	           {
	        	   System.out.println("For Peer" + id + "BitMap is full");
	        	   numOfPeersHavingCompleteFile++;
	           }	           
	        }
        	if(numOfPeersHavingCompleteFile == peerProcess.bifFieldMap.size())
        	{
        			System.out.println("All the peers are having complete file ");
        			allPeersHaveCompleteFile=true;
        	}
        	      	
        	if(hasCompleteFile)  //THie peer has the whole file so randomly choose amongst interested neoghbours to unchoke
        	{
        		Collections.shuffle(listOfInterestedNeighbours);    		
        		if(listOfInterestedNeighbours.size()>=numOfPreferredNeighbours)
        		{
        			for(int i=0;i<numOfPreferredNeighbours;i++) 
        				preferredNeighbours.add(listOfInterestedNeighbours.get(i));
        		}
        		else //There are not numOfPreferredNeighbours added to listOfInterestedNeighbours
        		{
        			for(int i=0;i<listOfInterestedNeighbours.size();i++)
        				preferredNeighbours.add(listOfInterestedNeighbours.get(i));
        		}		
      		
        		for(int i=0;i<preferredNeighbours.size();i++)
        		{
        	        	System.out.println("Preferred Neighbour is --"+ preferredNeighbours.get(i));
        	        	System.out.println("Pre unchoke: " + myPeersHashMap.get(preferredNeighbours.get(i)));
        	        	//Unchoke the preferred Neighbours
        	        	myPeersHashMap.put(preferredNeighbours.get(i), 1);
        	        	System.out.println("Post unchole: " + myPeersHashMap.get(preferredNeighbours.get(i)));   	        	    	        	
        		}    		
        		
        		//listOfInterestedNeighbours.clear(); // To be uncommented once we start populating this arraylist
        		isMasterRunning = false;
        		System.out.println("Master going to sleep.. Value of isMasterRunning is"+ isMasterRunning );
        		Thread.sleep(5000); //Value of sleep time to be retrieved from config file
        		isMasterRunning = true;
        		System.out.println("Master up now.. Value of isMasterRunning is"+ isMasterRunning );
        		for(int i=0;i<preferredNeighbours.size();i++) //Choke all neighbours
            		myPeersHashMap.put(preferredNeighbours.get(i),0);
            	preferredNeighbours.clear();
        	} //End of code for hashFile   	
        	else
        	{
        		// Code to check download rate and unchoke neighbours
        		if(myPriorityQueue.size() > numOfPreferredNeighbours)
        		{
        			int countTrack = 1;
        			while(countTrack<=numOfPreferredNeighbours)
        			{
        				PeerWrapper p= myPriorityQueue.poll();      				
        				System.out.println("Preferred Neighbour is --"+ p.peerID);
        				System.out.println("Pre unchoke: " + myPeersHashMap.get(p.peerID));
        				//if(p.getNumChunks()!=0)
        					myPeersHashMap.put(p.peerID, 1);
        				System.out.println("Post unchoke: " + myPeersHashMap.get(p.peerID));
        				countTrack++;
        			}
        		}
        		else
        		{
        			for(PeerWrapper p:myPriorityQueue)
        			{
        				System.out.println("Preferred Neighbour is --"+ p.peerID);
        				preferredNeighbours.add(p.peerID);
        				System.out.println("Pre unchoke: " + myPeersHashMap.get(p.peerID));
        				//if(p.getNumChunks()!=0)
        					myPeersHashMap.put(p.peerID, 1);
        				System.out.println("Post unchoke: " + myPeersHashMap.get(p.peerID));
        			}
        		}
        		myPriorityQueue.clear(); // To be uncommented once we start populating this queue
        		isMasterRunning = false;
        		Thread.sleep(15000); //Value of sleep time to be retrieved from config file
        		isMasterRunning = true;
        		System.out.println("Master up now.. Value of isMasterRunning is"+ isMasterRunning );
        		for(int i=0;i<preferredNeighbours.size();i++) //Choke all neighbours
            		myPeersHashMap.put(preferredNeighbours.get(i),0);
            	preferredNeighbours.clear();
        	}       	
        }    	
        providerSocket.close();
     //   System.out.println("Main process ended..");
    }
}