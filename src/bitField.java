import java.io.FileNotFoundException;
import java.io.IOException;

public class bitField {
	int  bitMapSize;
	byte[] bitMap;
	int numOfBytes;
	
	bitField(byte[] bitMap)
	{
		this.bitMap=bitMap;
		this.bitMapSize = bitMap.length*8;
		this.numOfBytes=bitMap.length;
	}
	
    bitField() throws FileNotFoundException, IOException
    {
    	CommonConfig cc = new CommonConfig();
        cc.GetCommon();  	
    	int size  = cc.FileSize/cc.PieceSize;
    	if(cc.FileSize%cc.PieceSize >0)
    	{
    		bitMapSize = size+1;
    	}
    	else
    	{
    		bitMapSize = size;
    	}    	
    	size = bitMapSize/8;
        if(bitMapSize%8 > 0)
        {
        	numOfBytes= size+1;
        	bitMap = new byte[numOfBytes];
        }
        else
        {
        	numOfBytes = size;
        	bitMap = new byte[numOfBytes];
        }
        
    }
    
    byte[] getBitMap()
    {
    	return bitMap;
    }
 
    synchronized boolean getBit(int pieceNum)
    {
    	int e = pieceNum%8;
    	int currentByte = pieceNum/8;
    	
    	if((bitMap[currentByte] & (1<<e)) >0 )
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    synchronized void setBit(int pieceNum)
    {
    	int e = pieceNum%8;
    	int currentByte = pieceNum/8;
    	bitMap[currentByte] = (byte) (bitMap[currentByte] | (1<<e));
    }
    
    void unsetBit(int pieceNum)
    {
    	int e = pieceNum%8;
    	int currentByte = pieceNum/8;
    	bitMap[currentByte] = (byte) (bitMap[currentByte] & ~(1 << e));
    }
    
    void initialize(boolean hasFile)
    {
    	if(hasFile)
    	{
    		for(int i=0;i<bitMapSize;i++)
    			setBit(i);
    	}
    	else
    	{
    		for(int i=0;i<bitMapSize;i++)
    			unsetBit(i);
    	}
    }
    boolean isBitMapFull()
    {
    	int counter=0;
    	for(int i=0;i<bitMapSize;i++)
    	{
    		if(getBit(i))
    		{
    			counter++;
    		}
    		else
    		{
    			break;
    		}
    	}
    	if(counter==bitMapSize)
    		return true;
    	else
    		return false;
    }
}
