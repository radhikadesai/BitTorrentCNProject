import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Configuration {

    //CommonProperties
    public static int numOfPreferredNeighbors;
    public static int unchokingInterval;
    public static int optUnchokingInterval;
    public static String fileName;
    public static int fileSize;
    public static int pieceSize;
    public static int numPieces;

    //PeerInformation
//    private final ArrayList<Integer> IDs;
//    private final ArrayList<String> addresses;
//    private final ArrayList<Integer> downloadPorts;
//    private final ArrayList<Boolean> flags;
//    private final int numPeers;
//    private final ArrayList<Integer> uploadPorts;
//    private final ArrayList<Integer> havePorts;
    public static ArrayList<PeerInformation> peers= new ArrayList<PeerInformation>();

    public Configuration(String commonConfig, String peersInfoConfig) throws FileNotFoundException {

        //Get the common configuration
//        Scanner sc= new Scanner(new FileReader(commonConfig));
//        Configuration.numOfPreferredNeighbors = Integer.parseInt(sc.nextLine().trim());
//        Configuration.unchokingInterval = Integer.parseInt(sc.nextLine().trim());
//        Configuration.optUnchokingInterval = Integer.parseInt(sc.nextLine().trim());
//        Configuration.fileName = sc.nextLine().trim();
//        Configuration.fileSize = Integer.parseInt(sc.nextLine().trim());
//        Configuration.pieceSize = Integer.parseInt(sc.nextLine().trim());
//
//        if (Configuration.fileSize%Configuration.pieceSize == 0) {
//            Configuration.numPieces = Configuration.fileSize/Configuration.pieceSize;
//        } else {
//            Configuration.numPieces = Configuration.fileSize/Configuration.pieceSize + 1;
//        }
//
//        sc.close();

        //Get peers information from the peersInformation file
    	String str;
        try
        {
        	BufferedReader buffread = new BufferedReader(new FileReader(new File("PeerInfo.cfg")));
        	for (str = buffread.readLine(); str!= null ; str = buffread.readLine())
        	{
        		String[] tokens = str.split(" ");
        		Peer temp = new Peer();
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