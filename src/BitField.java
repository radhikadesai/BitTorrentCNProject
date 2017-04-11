import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;


public class BitField {
	
	public static int noOfPieces;
	public static int msgLen;
	public static int headerLen = 4;
	public static byte[] header;
	public static byte[] messageType;
	public static byte[] messagePayload;
	public static Piece pieces[];


	public BitField()
	{
		header = new byte[headerLen];
		messageType = new byte[1];
		noOfPieces = (int)Math.ceil((double)Configuration.CommonProperties.FileSize/(double)Configuration.CommonProperties.PieceSize);
		messagePayload = new byte[noOfPieces/8];
	}
	
	public void initial(String pid, int hasFile)
	{
		if(hasFile==1)
		{
			int i=0;
			while(i<noOfPieces)
			{
				this.pieces[i].hasPiece=1;
				this.pieces[i].fromWho=pid;
				i++;
			}
		}
		else
		{
			int i=0;
			while(i<noOfPieces)
			{
				this.pieces[i].hasPiece=0;
				this.pieces[i].fromWho=pid;
				i++;
			}
		}
	}
	
	public synchronized void update(String pid, Piece p)
	{
		try
		{
			if(PeerProcess.myBitField.pieces[Piece.pieceIndex].hasPiece!=0)
			{
				String fileName = Configuration.CommonProperties.FileName;
				File file = new File(Integer.toString(PeerProcess.myProcessPeerID), fileName);
				int offset = Piece.pieceIndex * Configuration.CommonProperties.PieceSize;
				RandomAccessFile f = new RandomAccessFile(file, "rw");
				byte[] byteWrite;
	
				byteWrite = Piece.actualPiece;
				
				f.seek(offset);
				f.write(byteWrite);
				this.pieces[Piece.pieceIndex].hasPiece=1;
				this.pieces[Piece.pieceIndex].fromWho= pid;
				f.close();
				
			}
			int i = 0; 
			boolean completed=false;
			while(i < this.noOfPieces) {
				if (this.pieces[i].hasPiece == 0) {
					completed= false;
				}
				else
					completed= true;
			}
			if(completed == true)
			{
				for(int j=0;j<Configuration.peers.size();j++)
				{
					PeerInformation temp= Configuration.peers.get(j);
					if (temp.peerID==PeerProcess.myProcessPeerID)
					{
						temp.isCompleted=1;
						temp.isInterested=0;
						temp.isCh=0;
						Configuration.peers.set(j, temp);
					}
				}
				updatePeerCfgFile(PeerProcess.myProcessPeerID, 1);
			}
		}
		catch (Exception e)
		{
			System.out.println("Error while updating the bit field");
		}
		
	}
	
	public void updatePeerCfgFile(int pid, int isCompleted)
	{
		BufferedWriter wr = null;
		BufferedReader rd = null;
		try 
		{
			rd= new BufferedReader(new FileReader("PeerInfo.cfg"));
		
			String line;
			StringBuffer buffer = new StringBuffer();
			
		
			while((line = rd.readLine()) != null) 
			{
				String[] tokens = line.split(" ");
	    		if (tokens[0].equalsIgnoreCase(Integer.toString(pid)))
	    				{
	    					buffer.append(tokens[0] + " " + tokens[1] + " " + tokens[2] + " " + isCompleted);
	    				}
				else
				{
					buffer.append(line);
				}
				buffer.append("\n");
			}
			
			rd.close();
		
			wr= new BufferedWriter(new FileWriter("PeerInfo.cfg"));
			wr.write(buffer.toString());	
			
			wr.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Error while updating PeerInfo.cfg file");
		}
	}
	
	public byte[] sendMessage()
	{
		msgLen = (noOfPieces/8)+1;
		header = (Integer.toString(msgLen)).getBytes();
		messageType = "5".getBytes();
		
		return null;
	}

}
