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

    //PeerInformation
//    private final ArrayList<Integer> IDs;
//    private final ArrayList<String> addresses;
//    private final ArrayList<Integer> downloadPorts;
//    private final ArrayList<Boolean> flags;
//    private final int numPeers;
//    private final ArrayList<Integer> uploadPorts;
//    private final ArrayList<Integer> havePorts;
    public static ArrayList<PeerInformation> peers;

    public Configuration(String commonConfig, String peersInfoConfig) throws FileNotFoundException {

        //Get the common configuration
        Scanner sc= new Scanner(new FileReader(commonConfig));
        this.numberOfPreferredNeighbors = Integer.parseInt(sc.nextLine().trim());
        this.unchokingInterval = Integer.parseInt(sc.nextLine().trim());
        this.optimisticUnchokingInterval = Integer.parseInt(sc.nextLine().trim());
        this.fileName = sc.nextLine().trim();
        this.fileSize = Integer.parseInt(sc.nextLine().trim());
        this.pieceSize = Integer.parseInt(sc.nextLine().trim());

        if (this.fileSize%this.pieceSize == 0) {
            this.numPieces = this.fileSize/this.pieceSize;
        } else {
            this.numPieces = this.fileSize/this.pieceSize + 1;
        }

        sc.close();

        //Get peers information from the peersInformation file
        Scanner sc2 = new Scanner(new FileReader(peersInformation));

        IDs = new ArrayList<Integer>();
        addresses = new ArrayList<String>();
        downloadPorts = new ArrayList<Integer>();
        flags = new ArrayList<Boolean>();

        uploadPorts = new ArrayList<Integer>();
        havePorts = new ArrayList<Integer>();

        int count = 0;
        while (sc2.hasNextLine()) {

            String s = sc2.nextLine();
            String[] split = s.split(" ");
            this.IDs.add(Integer.parseInt(split[0].trim()));
            this.addresses.add(split[1].trim());
            this.downloadPorts.add(Integer.parseInt(split[2].trim()));
            this.uploadPorts.add(Integer.parseInt(split[2].trim()) + 1);
            this.havePorts.add(Integer.parseInt(split[2].trim()) + 2);
            if (split[3].trim().equals("1")) {
                this.flags.add(true);
            } else {
                this.flags.add(false);
            }
            count++;
        }

        this.numPeers = count;

    }
}