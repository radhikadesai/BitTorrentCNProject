/**
 * Created by radhikadesai on 11/04/2017.
 */
public class ActualMessageWithPeer {
    private int PeerID;
    private ActualMessage message;

    public ActualMessageWithPeer(int peerID, ActualMessage message) {
        PeerID = peerID;
        this.message = message;
    }

    public int getPeerID() {
        return PeerID;
    }

    public void setPeerID(int peerID) {
        PeerID = peerID;
    }

    public ActualMessage getMessage() {
        return message;
    }

    public void setMessage(ActualMessage message) {
        this.message = message;
    }
}
