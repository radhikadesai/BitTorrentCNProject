/**
 * Created by radhikadesai on 11/04/2017.
 */
public class ActualMessageWithPeer {
    private int fromPeerID;
    private ActualMessage message;

    public ActualMessageWithPeer(int fromPeerID, ActualMessage message) {
        fromPeerID = fromPeerID;
        this.message = message;
    }

    public int getfromPeerID() {
        return fromPeerID;
    }

    public void setfromPeerID(int fromPeerID) {
        fromPeerID = fromPeerID;
    }

    public ActualMessage getMessage() {
        return message;
    }

    public void setMessage(ActualMessage message) {
        this.message = message;
    }
}
