
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
        this.connectionType = PASSIVE;
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
            this.connectionType = ACTIVE;
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
        System.out.println("Started Sending thread for my peer ");
        boolean handshakeSuccess = sendHandshake();

        if(handshakeSuccess){
            System.out.print("Handshake Successful! ");
            //peerProcess.showLog(ownPeerId + " HANDSHAKE has been sent...");
        }
        else {
            System.out.print("Handshake Failed! ");
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
                if(Handshake.received_header.equals(Handshake.HEADER)){ //VerifyHandshake
                    System.out.println("Handhshake Header is same! ");
                    remotePeerId = Integer.parseInt(Handshake.received_peerID);
                    System.out.println("Connection established with : "+remotePeerId);
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
            System.out.println("This is an active connection");
            ActualMessage msg = new ActualMessage(ActualMessage.BITFIELD, PeerProcess.myBitField.sendMessage());
            byte[] msgStream = msg.serialize();
            try {
                System.out.println("Writing the message stream : "+ msgStream[0]);
                outputStream.write(msgStream);
            } catch (IOException e) {
                System.out.println("Exception in sending bitfield");
                e.printStackTrace();
            }
//            peerProcess.remotePeerInfoHash.get(remotePeerId).state = 8;
        }
        else{
            //set remotePeer state to 2
        }
        //Keep receiving messages and put them in the message Q
        while(true){
            try {
                byte[] dataWithoutPayload = new byte[ActualMessage.MSG_LEN + 1];
                int headerlen =  inputStream.read(dataWithoutPayload);
                if(headerlen == -1)
                    break;
                byte[] len = new byte[ActualMessage.MSG_LEN];
                System.arraycopy(dataWithoutPayload, 0, len, 0, ActualMessage.MSG_LEN);
                byte type = dataWithoutPayload[ActualMessage.MSG_LEN];

                ActualMessage message = new ActualMessage();
                message.setMessageLen(len);
                message.setMessageType(type);
                int messageLengthInt = ActualMessage.byteArrayToInt(len,0);
                System.out.println("Message length is : "+messageLengthInt);
                //has payload
                ActualMessageWithPeer msgWithPeer;
                if(messageLengthInt==1){
                    System.out.println("Received message has no payload and message type is : "+ type);
                    msgWithPeer = new ActualMessageWithPeer(this.remotePeerId,message);
                    PeerProcess.addToQ(msgWithPeer);
                }
                else if(messageLengthInt>1) {
                    System.out.println("Received message has  payload and type is : "+type);
                    int bytesAlreadyRead = 0;
                    int bytesRead;
                    byte[] dataWithPayload = new byte[messageLengthInt-1];
                    while(bytesAlreadyRead < messageLengthInt-1)
                    {
                        bytesRead = inputStream.read(dataWithPayload, bytesAlreadyRead, messageLengthInt-1-bytesAlreadyRead);
                        if(bytesRead == -1)
                            return;
                        bytesAlreadyRead += bytesRead;
                    }

                    byte []dataBuffWithPayload = new byte [messageLengthInt+ActualMessage.MSG_LEN];
                    System.arraycopy(dataWithoutPayload, 0, dataBuffWithPayload, 0,ActualMessage.MSG_LEN + 1);
                    System.arraycopy(dataWithPayload, 0, dataBuffWithPayload, ActualMessage.MSG_LEN + 1, dataWithPayload.length);
                    ActualMessage dataMsgWithPayload = ActualMessage.deserialize(dataBuffWithPayload);
                    msgWithPeer = new ActualMessageWithPeer(this.remotePeerId,dataMsgWithPayload);
                    PeerProcess.addToQ(msgWithPeer);
                    dataWithPayload = null;
                    dataBuffWithPayload = null;
                    bytesAlreadyRead = 0;
                    bytesRead = 0;
                }
            } catch (IOException e) {
//                peerProcess.showLog(myPeerId + " run exception: " + e);
                e.printStackTrace();
            }


        }


    }
}
