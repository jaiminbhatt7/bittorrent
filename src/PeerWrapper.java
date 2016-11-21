public class PeerWrapper {
	int peerID;
	int numChunks;
	PeerWrapper(int peerID, int numOfChunks)
	{
		this.peerID = peerID;
		this.numChunks = numOfChunks;
	}
	
	int getNumChunks()
	{
		return numChunks;
	}
}
