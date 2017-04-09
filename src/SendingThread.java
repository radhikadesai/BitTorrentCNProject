
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by radhikadesai on 09/04/2017.
 */
public class SendingThread implements Runnable {

    final int ACTIVE = 1;
    final int PASSIVE = 0;

    private int connectionType;
    private Socket peerSocket = null;
    private InputStream in;
    private OutputStream out;
    private Handshake handshakeMessage;
    private int ownPeerId;
    private int remotePeerId;

    public SendingThread(Socket peerSocket, int ownPeerID) {

        this.peerSocket = peerSocket;
        this.connectionType = 0;
        this.ownPeerId = ownPeerID;
        try
        {
            in = peerSocket.getInputStream();
            out = peerSocket.getOutputStream();
        }
        catch (Exception ex)
        {
//            PeerProcess.showLog(this.ownPeerId + " Error : " + ex.getMessage());
        }
    }
    public SendingThread(String add, int port,int ownPeerID)
    {
        try
        {
            this.connectionType = 1;
            this.ownPeerId = ownPeerID;
            //PeerProcess.showLog(ownPeerId + " Receiving Port = " + port + " Address = "+ add);
            this.peerSocket = new Socket(add, port);
        }catch (UnknownHostException e)
        {
//            PeerProcess.showLog(ownPeerID + " RemotePeerHandler : " + e.getMessage());
        }catch (IOException e)
        {
//            PeerProcess.showLog(ownPeerID + " RemotePeerHandler : " + e.getMessage());
        }
        try
        {
            in = peerSocket.getInputStream();
            out = peerSocket.getOutputStream();
        }catch (Exception ex)
        {
//            PeerProcess.showLog(ownPeerID + " RemotePeerHandler : " + ex.getMessage());
        }
    }
    @Override
    public void run() {

    }
}
