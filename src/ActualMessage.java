import java.nio.ByteBuffer;

/**
 * Created by radhikadesai on 09/04/2017.
 */
public class ActualMessage {
    public static final int  MSG_LEN = 4;
    public static final byte CHOKE = 0;
    public static final byte UNCHOKE = 1;
    public static final byte INTERESTED = 2;
    public static final byte NOT_INTERESTED = 3;
    public static final byte HAVE = 4;
    public static final byte BITFIELD = 5;
    public static final byte REQUEST = 6;
    public static final byte PIECE = 7;

    private byte[] messageLen;
    private byte messageType;
    private byte[] payload = null;

    public byte[] getMessageLen() {
        return messageLen;
    }

    public void setMessageLen(byte[] messageLen) {
        this.messageLen = messageLen;
    }

    public byte getMessageType() {
        return messageType;
    }

    public void setMessageType(byte messageType) {
        this.messageType = messageType;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public ActualMessage(){}

    public ActualMessage(byte messageType, byte[] payload) {
        this.messageType = messageType;
        this.payload = payload;
        this.messageLen=ByteBuffer.allocate(4).putInt(payload.length+1).array();
    }

    public ActualMessage(byte messageType) {
        this.messageType = messageType;
        this.messageLen =ByteBuffer.allocate(4).putInt(1).array();

    }
    public byte[] serialize()
    {
        byte[] msgStream = null;
        int type;

        try
        {
            type = this.messageType;
            //Check for valid message attributes
            if (this.getMessageLen() == null)
                throw new Exception("Invalid message length.");
            else if (this.getMessageLen().length > MSG_LEN)
                throw new Exception("Invalid message length.");
            else if (type < 0 || type > 7)
                throw new Exception("Invalid message type.");

            if (this.getPayload() != null) {
                msgStream = new byte[MSG_LEN + 1 + this.getPayload().length];
                System.arraycopy(this.getMessageLen(), 0, msgStream, 0, this.getMessageLen().length);
                msgStream[MSG_LEN]=this.getMessageType();
                System.arraycopy(this.getPayload(), 0, msgStream, MSG_LEN + 1, this.getPayload().length);
            } else {
                msgStream = new byte[MSG_LEN +1];
                System.arraycopy(this.getMessageLen(), 0, msgStream, 0, this.getMessageLen().length);
                msgStream[MSG_LEN]=this.getMessageType();
            }

        }
        catch (Exception e)
        {
//            PeerProcess.showLog(e.toString());
            msgStream = null;
        }

        return msgStream;
    }
    public static ActualMessage deserialize(byte[] msgStream) {

        ActualMessage msg = new ActualMessage();
        byte[] msgLength = new byte[MSG_LEN];
        byte[] payload = null;

        try
        {
            // Verify correctness
            if (msgStream == null)
                throw new Exception("Invalid data.");
            else if (msgStream.length < MSG_LEN + 1)
                throw new Exception("Byte array length is too small...");

            // deserialize
            System.arraycopy(msgStream, 0, msgLength, 0, MSG_LEN);
            msg.setMessageLen(msgLength);
            msg.setMessageType(msgStream[MSG_LEN]);
            int messageLenInt =  byteArrayToInt(msgLength, 0);
            if (messageLenInt > 1)
            {
                payload = new byte[messageLenInt-1];
                System.arraycopy(msgStream, MSG_LEN + 1,payload, 0, msgStream.length - MSG_LEN - 1);
                msg.setPayload(payload);
            }
        }
        catch (Exception e)
        {
//            peerProcess.showLog(e.toString());
            msg = null;
        }
        return msg;
    }
    public static int byteArrayToInt(byte[] b, int offset)
    {
        int value = 0;
        for (int i = 0; i < 4; i++)
        {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }
}
