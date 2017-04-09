import java.io.FileNotFoundException;

/**
 * Created by radhikadesai on 08/04/2017.
 */
public class PeerProcess {

    public int listeningPort;
    public int myIndex;

    public static void main(String[] args) throws FileNotFoundException {
        boolean isFirstPeer =false;
        //Initialize Configuration
        Configuration config = new Configuration("",""); //give paths of the common and peerInfo config files
        //Initialize peerProcess
        PeerProcess peerProcess = new PeerProcess();
        int peerID = Integer.parseInt(args[0]);
        //Initialize the preferred neighbors
//        initializePreferredNeighbors();

        for(PeerInformation p :Configuration.peers){
            if(p.getPeerID()==peerID){
                peerProcess.listeningPort = p.getPort();
                peerProcess.myIndex = p.getIndex();
                if(p.getIsFirstPeer()==1){
                    isFirstPeer=true;
                    break;
                }
            }
        }
        
    }
}
