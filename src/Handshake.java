/*
 * Created by jainayushi on 04/08/2017.
 */

public class Handshake {
	public static final int ZERO_BITS_LEN = 10;
	public static final int HEADER_LEN = 18;
	public static final int ID_LEN= 4;
	public static final int MESSAGE_LENGTH = 32;
	public static final String HEADER= "P2PFILESHARINGPROJ";
	
	public Handshake()
	{
		
	}
	
	public static byte[] sendMessage(int peerId)
	{
		byte[] message = new byte[MESSAGE_LENGTH];
		try 
		{
		byte[] header = HEADER.getBytes();
		byte[] zeros = "0000000000".getBytes();
		byte[] pid = (Integer.toString(peerId)).getBytes();
		System.arraycopy(header, 0, message, 0, HEADER_LEN);
		System.arraycopy(zeros, 0, message, HEADER_LEN, ZERO_BITS_LEN);
		System.arraycopy(pid, 0, message, HEADER_LEN + ZERO_BITS_LEN, ID_LEN);
		}
		catch (Exception e) 
		{
			message = null;
		}
		
		return message;
		
	}
	

}
