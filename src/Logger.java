import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
public class Logger {

		public  void writeLog(int peerID1, int peerID2,int logCode)
		{
			/* Codes :
			 * 1 : Makes TCP Connection
			 * 2 : Receives connection request
			 * 3 : change of optimistically unchoked neighbor
			 * 4 : unchoking
			 * 5 : choking
			 * 6 : receiving ‘have’ message
			 * 7 : receiving ‘interested’ message
			 * 8 : receiving ‘not interested’ message
			 * 9 : downloading a piece
			 * 10 : completion of download
			 * For change of preferred neighbors a different function is called.
			 */
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			try{
				FileWriter fstream = new FileWriter("log_peer_"+peerID1+".log",true);
				switch(logCode)
				{
				case 1: 	
				fstream.write(timeStamp + " Peer "+peerID1+" makes connection to Peer "+ peerID2+ ".\n");
				break;
				case 2:
				fstream.write(timeStamp + " Peer "+peerID1+" is connected from Peer  "+ peerID2+ ".\n");
				break;
				case 3:
				fstream.write(timeStamp + " Peer "+peerID1+" has the optimistically unchoked neighbor  "+ peerID2+ ".\n");
				break;
				case 4:
				fstream.write(timeStamp + " Peer "+peerID1+" is unchocled by  "+ peerID2+ ".\n");
				break;
				case 5:
				fstream.write(timeStamp + " Peer "+peerID1+" is chocked by  "+ peerID2+ ".\n");
				break;
				case 6:
				fstream.write(timeStamp + " Peer "+peerID1+" received the ‘have’ message from  "+ peerID2+ ".\n");
				// For peice to be added
				break;
				case 7:
				fstream.write(timeStamp + " Peer "+peerID1+" received the ‘interested’ message from  "+ peerID2+ ".\n");
				break;
				case 8:
				fstream.write(timeStamp + " Peer "+peerID1+" received the ‘not interested’ message from  "+ peerID2+ ".\n");
				break;
				case 9:
				fstream.write(timeStamp + " Peer "+peerID1+" has downloaded the piece [piece index] from  "+ peerID2+
						"Now the number of pieces it has is [number of pieces]."+".\n");
				// piece index, number of pieces to be added
				break;
				case 10:
				fstream.write(timeStamp + " Peer "+peerID1+" has downloaded the complete file" + ".\n");
				
				}
				fstream.close();
				}
				catch (Exception e){//Catch exception if any
					  System.err.println("Error: " + e.getMessage());
				}
		}
		//Following writer is for change of preferred neighbors
		public void writeLog(int peerID1, ArrayList<Integer> newPreferredPeers)
		{
			System.out.println("Hello World");
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			try{
				FileWriter fstream = new FileWriter("log_peer_11"+peerID1+".log",true);
				fstream.write(timeStamp + " has the preferred neighbors ");
				for(int peerID : newPreferredPeers)
				{
					System.out.println(peerID);	
					fstream.write(peerID + ", ");
				}
				fstream.write("\n");
				fstream.close();
			}
			catch (Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
			}
		}	
}
