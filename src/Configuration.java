import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Configuration {

    public static ArrayList<PeerInformation> peers= new ArrayList<PeerInformation>();
    public static CommonCfg CommonProperties = new CommonCfg();

    public Configuration(String commonConfig, String peersInfoConfig) throws FileNotFoundException {

        //Get the common configuration
    	String str1;
        try
        {
        	BufferedReader buffread1 = new BufferedReader(new FileReader(new File("common.cfg")));
        	for (str1 = buffread1.readLine(); str1!= null ; str1 = buffread1.readLine())
        	{
        		String[] tokens = str1.split(" ");
        		if (tokens[0].equalsIgnoreCase("NumberOfPreferredNeighbors"))
        		{
        			CommonProperties.NumberOfPreferredNeighbors= Integer.parseInt(tokens[1]);
        		}
        		else if (tokens[0].equalsIgnoreCase("UnchokingInterval"))
        		{
        			CommonProperties.UnchokingInterval= Integer.parseInt(tokens[1]);
        		}
        		else if (tokens[0].equalsIgnoreCase("OptimisticUnchokingInterval"))
        		{
        			CommonProperties.OptimisticUnchokingInterval= Integer.parseInt(tokens[1]);
        		}
        		else if (tokens[0].equalsIgnoreCase("FileName"))
        		{
        			CommonProperties.FileName= tokens[1];
        		}
        		else if (tokens[0].equalsIgnoreCase("FileSize"))
        		{
        			CommonProperties.FileSize = Integer.parseInt(tokens[1]);
        		}
        		else if (tokens[0].equalsIgnoreCase("PieceSize"))
        		{
        			CommonProperties.PieceSize= Integer.parseInt(tokens[1]);
        		}
        		else
        		{
        			
        		}
        	}
        }
        catch (IOException e)
        {
            System.out.println("File I/O error!");
        }
        //Get peers information from the peersInformation file
    	String str;
        try
        {
        	BufferedReader buffread = new BufferedReader(new FileReader(new File("PeerInfo.cfg")));
        	for (str = buffread.readLine(); str!= null ; str = buffread.readLine())
        	{
        		String[] tokens = str.split(" ");
        		PeerInformation temp = new PeerInformation();
        		temp.peerID = Integer.parseInt(tokens[0]);
        		temp.address = tokens[1];
        		temp.port= Integer.parseInt(tokens[2]);
        		temp.isFirstPeer= Integer.parseInt(tokens[3]);
        		peers.add(temp);
//        		System.out.println("Number of tokens in line " + ": " + tokens.length);
//                System.out.println("The tokens are:");
//                for (String token : tokens)
//                {
//                    System.out.println(token);
//                }
        	}

        }
        catch (IOException e)
        {
            System.out.println("File I/O error!");
        }
        
        
    }
}