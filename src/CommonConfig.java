
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class CommonConfig {
    public int PrefNeighbour;
    public int UnchokingInterval;
    public int OptimisticUnchokingInterval;
    public String FileName;
    public int FileSize;
    public int PieceSize;
    
    void GetCommon() throws FileNotFoundException, IOException {
        
        String s;
        int count = 0;
        
        String path = System.getProperty("user.dir");
   //     System.out.println("path is :"+path);
        BufferedReader in = new BufferedReader(new FileReader(path+"/src/Common.cfg"));
            while((s = in.readLine()) != null) {
               count++; 
               String[] temp = s.split(" ");
               if(count == 1) {
                   
                       PrefNeighbour = Integer.parseInt(temp[1]);
                      }
                       
               else if(count == 2){
                       UnchokingInterval = Integer.parseInt(temp[1]);
                    }
                       
               else if(count == 3){
                       OptimisticUnchokingInterval = Integer.parseInt(temp[1]);
               }                       
               else if(count == 4){
                       FileName = temp[1];
               }                       
               else if(count == 5){
                       FileSize = Integer.parseInt(temp[1]);
               }                       
               else if(count == 6){
                       PieceSize = Integer.parseInt(temp[1]);
               }                       
               else
                       System.out.println("Unknown Parameter");
               }
            }
        }
    
