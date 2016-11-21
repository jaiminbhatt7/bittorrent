import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class filePartition {
	
	int fileSize;
	int chunkSize;	
	byte chunks[][];
	CommonConfig myConfig;
	filePartition(boolean hasFileFlag) throws IOException
	{	
		myConfig = new CommonConfig();
		myConfig.GetCommon();
		fileSize = myConfig.FileSize; //file size in bytes
		chunkSize = myConfig.PieceSize;		
		int numOfChunks = fileSize/chunkSize;		
		if(fileSize%chunkSize >0)
			numOfChunks++;
		
		chunks = new byte[numOfChunks][chunkSize];	
		
		if(hasFileFlag)
		{
		File file = new File(myConfig.FileName);
		FileInputStream fin = new FileInputStream(file);		
			for(int i=0;i<numOfChunks;i++)
			{
				fin.read(chunks[i], 0, chunkSize);
			}
		}
//This loop will write the entire file	
//		for(int i=0;i<numOfChunks;i++)
//		{
//			for(int j=0;j<chunkSize;j++)
//				System.out.print((char)chunks[i][j]);
//		}
	}
	synchronized byte[] giveChunk(int i)
	{
		return chunks[i];
	}
	
	synchronized void saveChunk(int chunkNum, byte[] data)
	{
		chunks[chunkNum] = data;
	}
	synchronized void writeToFile() throws IOException
	{
		FileWriter fstream = new FileWriter(myConfig.FileName);
		for(int i=0;i<chunks.length;i++)
		{
				for(int j=0;j<chunkSize;j++)
					fstream.write((char)chunks[i][j]);
		}		
		fstream.close();
	}
}
