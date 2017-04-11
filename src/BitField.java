
public class BitField {
	
	public static int noOfPieces;
	public static int msgLen;
	public static int headerLen = 4;
	public static byte[] header;
	public static byte[] messageType;
	public static byte[] messagePayload;


	public BitField()
	{
		header = new byte[headerLen];
		messageType = new byte[1];
		noOfPieces = (int)Math.ceil((double)Configuration.CommonProperties.FileSize/(double)Configuration.CommonProperties.PieceSize);
		messagePayload = new byte[noOfPieces/8];
	}
	
	public byte[] sendMessage()
	{
		msgLen = (noOfPieces/8)+1;
		header = (Integer.toString(msgLen)).getBytes();
		messageType = "5".getBytes();
		return null;
	}

}
