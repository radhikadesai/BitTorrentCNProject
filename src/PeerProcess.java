import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

/**
 * Created by radhikadesai on 08/04/2017.
 */
public class PeerProcess {

    public static int myProcessPeerID;
    public int listeningPort;
    public int myIndex;
    public ServerSocket listeningSocket;
    public Thread listeningThread;

    public static void main(String[] args) throws FileNotFoundException {
        boolean isFirstPeer =false;
        //Initialize Configuration
        Configuration config = new Configuration("",""); //give paths of the common and peerInfo config files
        //Initialize peerProcess
        PeerProcess peerProcess = new PeerProcess();
        myProcessPeerID = Integer.parseInt(args[0]);
        //Initialize the preferred neighbors
        initializePreferredNeighbors();

        for(PeerInformation p :Configuration.peers){
            if(p.getPeerID()==myProcessPeerID){
                peerProcess.listeningPort = p.getPort();
                peerProcess.myIndex = p.getIndex();
                if(p.getIsFirstPeer()==1){
                    isFirstPeer=true;
                    break;
                }
            }
        }
        if(!isFirstPeer) {
            //Create Empty file
            //emptyFile();
            for (PeerInformation p : Configuration.peers) {
                if (peerProcess.myIndex > p.getIndex()) {
                    //SendingThread for each client before me
                }
            }
        }
        //Listening thread for peerProcess
        try {
            peerProcess.listeningSocket = new ServerSocket(peerProcess.listeningPort);
//            peerProcess.listeningThread = new Thread();
            peerProcess.listeningThread.start();
        }
        catch(IOException ex){
            System.exit(0);
        }

    }

    private static void initializePreferredNeighbors() {
        int i=0;
        for (PeerInformation p : Configuration.peers){
            if(p.getPeerID()!=myProcessPeerID){
                //add p to the list of preferred neighbors
                i++;
                if(i==Configuration.CommonProperties.NumberOfPreferredNeighbors){
                    break;
                }
            }
        }
    }
}
