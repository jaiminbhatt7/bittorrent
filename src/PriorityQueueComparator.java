import java.util.Comparator;
public  class PriorityQueueComparator implements Comparator{
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		PeerWrapper p1 = (PeerWrapper)o1;
		PeerWrapper p2 = (PeerWrapper)o2;
		if(p1.getNumChunks() < p2.getNumChunks())
		{
			return +1;
		}
		else
		{
			return -1;
		}
	}
}
