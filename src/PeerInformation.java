import java.util.Date;
/**
 * Created by radhikadesai on 08/04/2017.
 */

public class PeerInformation implements Comparable<PeerInformation> {
    int peerID;
    String address;
    int port;
    int isFirstPeer;
    int index;
    public double dataRate = 0;
	public int isInterested = 1;
	public int isPNeighbor = 0;
	public int isOUNeighbor = 0;
	public int isCh = 1;
	public BitField bitField;
	public int peerrelation = -1;
	public int peerIndex;
	public Date st;
	public Date ft;
	public int isCompleted = 0;
    public int isHandShaked = 0;
    public Date start;
    public Date end;

    public int getPeerID() 
    {
        return peerID;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setPeerID(int peerID)
    {
        this.peerID = peerID;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
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
	public int compareTo(PeerInformation o1) 
	{
		
		if (this.dataRate > o1.dataRate) 
			return 1;
		else if (this.dataRate == o1.dataRate) 
			return 0;
		else 
			return -1;
	}
}

