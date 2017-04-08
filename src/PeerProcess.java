import java.io.FileNotFoundException;

/**
 * Created by radhikadesai on 08/04/2017.
 */
public class PeerProcess {
    public static void main(String[] args) throws FileNotFoundException {
        //Initialize Configuration
        Configuration config = new Configuration("",""); //give paths of the common and peerInfo config files
        //Initialize peerProcess
        PeerProcess peerProcess = new PeerProcess();
        int peerID = Integer.parseInt(args[0]);
        //Initialize the preferred neighbors

    }
}
