/**
 * Created by radhikadesai on 08/04/2017.
 */
public class PeerInformation {
    int peerID;
    String address;
    int port;
    int isFirstPeer;
    int index;

    public int getPeerID() {
        return peerID;
    }

    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getIsFirstPeer() {
        return isFirstPeer;
    }

    public void setIsFirstPeer(int isFirstPeer) {
        this.isFirstPeer = isFirstPeer;
    }
}

