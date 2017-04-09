
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
    private InputStream inputStream;
    private OutputStream outputStream;
    private Handshake handshakeMessage;
    private int myPeerId;
    private int remotePeerId;

    public SendingThread(Socket peerSocket, int ownPeerID) {

        this.peerSocket = peerSocket;
        this.connectionType = 0;
        this.myPeerId = ownPeerID;
        try
        {
            inputStream = peerSocket.getInputStream();
            outputStream = peerSocket.getOutputStream();
        }
        catch (Exception ex)
        {
//            PeerProcess.showLog(this.myPeerId + " Error : " + ex.getMessage());
        }
    }
    public SendingThread(String add, int port,int ownPeerID)
    {
        try
        {
            this.connectionType = 1;
            this.myPeerId = ownPeerID;
            //PeerProcess.showLog(myPeerId + " Receiving Port = " + port + " Address = "+ add);
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
            inputStream = peerSocket.getInputStream();
            outputStream = peerSocket.getOutputStream();
        }catch (Exception ex)
        {
//            PeerProcess.showLog(ownPeerID + " RemotePeerHandler : " + ex.getMessage());
        }
    }
    public boolean sendHandshake(){
        try
        {
            outputStream.write(Handshake.sendMessage(this.myPeerId));
        }
        catch (IOException e)
        {
//            PeerProcess.showLog(this.myPeerId + " SendHandshake : " + e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public void run() {
        boolean handshakeSuccess = sendHandshake();

        if(handshakeSuccess){
            //peerProcess.showLog(ownPeerId + " HANDSHAKE has been sent...");
        }
        else {
//            peerProcess.showLog(ownPeerId + " HANDSHAKE has been failed...");
            System.exit(0);
        }
        //Read handshake message
        while (true){
            try {
                byte[] message = new byte[Handshake.MESSAGE_LENGTH];
                //Read handshake message
                inputStream.read(message);
                Handshake.receiveMessage(message);
                if(Handshake.received_header.equals(Handshake.HEADER)){
                    remotePeerId = Integer.parseInt(Handshake.received_peerID);
                    // Log
                    //peerID to socket mapping
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(this.connectionType==ACTIVE){
            //Send bitfield and change remotepeer state to 8
        }
        else{
            //set remotePeer state to 2
        }
        

    }
}
