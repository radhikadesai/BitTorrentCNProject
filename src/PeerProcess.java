import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
        //Initializkee Configuration
        Configuration config = new Configuration("/Users/radhikadesai/Desktop/BitTorrentCNProject/src/common.cfg","/Users/radhikadesai/Desktop/BitTorrentCNProject/src/PeerInfo.cfg"); //give paths of the common and peerInfo config files
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
            System.out.println("Inside the loop for not first peer!  ");
            for (PeerInformation p : Configuration.peers) {
                System.out.println("Iterating through the peer list");
                if (peerProcess.myIndex > p.getIndex()) {
                    System.out.println("Spawning sending threads for peer  "+ p.getPeerID());
                    //SendingThread for each client before me
                    Thread sendingThread = new Thread(new SendingThread(p.getAddress(), p.getPort(), myProcessPeerID));
//                    receivingThread.add(sendingThread);
                    sendingThread.start();
                }
            }
        }
        //Listening thread for peerProcess
        try {
            System.out.println("Spawning listening Thread : ");
            peerProcess.listeningSocket = new ServerSocket(peerProcess.listeningPort);
            peerProcess.listeningThread = new Thread(new ListeningThread(peerProcess.listeningSocket, myProcessPeerID));
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
class ListeningThread implements Runnable{

    private ServerSocket listeningSocket;
    private int peerID;
    Socket remoteSocket;
    Thread sendingThread;

    public ListeningThread(ServerSocket socket, int peerID)
    {
        this.listeningSocket = socket;
        this.peerID = peerID;
    }
    @Override
    public void run() {
        //Keep listening for remote connections
        while (true){
            try {
                remoteSocket = listeningSocket.accept();
                //spawn a sending thread for each incoming connection
                sendingThread = new Thread(new SendingThread(remoteSocket,peerID));
                // Log connection is established
//                PeerProcess.sendingThread.add(sendingThread);
                sendingThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}


