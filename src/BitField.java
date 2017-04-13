import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;


public class BitField {
	
	public int noOfPieces;
	public int msgLen;
	public int headerLen = 4;
	public byte[] header;
	public byte[] messageType;
	public byte[] messagePayload;
	public Piece pieces[];


	public BitField()
	{
		header = new byte[headerLen];
		messageType = new byte[1];
		noOfPieces = (int)Math.ceil((double)Configuration.CommonProperties.FileSize/(double)Configuration.CommonProperties.PieceSize);
		messagePayload = new byte[noOfPieces/8];
		this.pieces = new Piece[noOfPieces];
		for (int i = 0; i < this.noOfPieces; i++)
			this.pieces[i] = new Piece();
	}
	
	public void initial(int pid, int hasFile)
	{
		if(hasFile==1)
		{
			System.out.println("Has file! Printing pieces");
			int i=0;
			while(i<noOfPieces)
			{
				this.pieces[i].hasPiece=1;
				this.pieces[i].fromWho=pid;
				System.out.println(this.pieces[i].hasPiece);
				i++;
			}
		}
		else
		{
			int i=0;
			System.out.println("does not Have file! Printing pieces");
			while(i<noOfPieces)
			{
				this.pieces[i].hasPiece=0;
				this.pieces[i].fromWho=pid;
				System.out.println(this.pieces[i].hasPiece);
				i++;
			}
		}
	}
	
	public int ownpieces()
	{
		int count = 0;
		for (int i = 0; i < this.noOfPieces; i++)
			if (this.pieces[i].hasPiece == 1) 
				count++;

		return count; 
	}
	
	public synchronized void update(int rId, Piece p)
	{
		try
		{
			if(PeerProcess.myBitField.pieces[p.pieceIndex].hasPiece!=0)
			{
				String fileName = Configuration.CommonProperties.FileName;
				File file = new File(Integer.toString(PeerProcess.myProcessPeerID), fileName);
				int offset = p.pieceIndex * Configuration.CommonProperties.PieceSize;
				RandomAccessFile f = new RandomAccessFile(file, "rw");
				byte[] byteWrite;
	
				byteWrite = p.actualPiece;
				//PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " has downloaded the piece " + Piece.pieceIndex + "from peer " + rId));
				
				f.seek(offset);
				f.write(byteWrite);
				this.pieces[p.pieceIndex].hasPiece=1;
				this.pieces[p.pieceIndex].fromWho=rId;
				f.close();
				
				
				
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " has downloaded the piece " + p.pieceIndex
						+ "from peer " + rId +". Now the number of pieces it has is "+ PeerProcess.myBitField.ownpieces()));
				
				
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
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " has downloaded the complete file"));
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
	
	public synchronized boolean isDifferent (BitField other)
	{
		int otherSize= other.noOfPieces;
		for (int i = 0; i < otherSize; i++) {
			if (other.pieces[i].hasPiece ==1 && this.pieces[i].hasPiece==0)
			{
				System.out.println("Its interested");
				return true;
			} else
				continue;
		}
		return false;
	}
	
	public synchronized int whatsDifferent (BitField other)
	{
		if(this.noOfPieces>=other.noOfPieces)
		{
			int i=0;
			while(i< other.noOfPieces)
			{
				if(other.pieces[i].hasPiece==1 && this.pieces[i].hasPiece==0)
				{
					return i;
				}
			}
		}
		else
		{
			int i=0;
			while(i< this.noOfPieces)
			{
				if(other.pieces[i].hasPiece==1 && this.pieces[i].hasPiece==0)
				{
					return i;
				}
			}
		}
		return -1;
	}
	
	
	public byte[] sendMessage()
	{
//		msgLen = (noOfPieces/8)+1;
//		header = (Integer.toString(msgLen)).getBytes();
//		messageType = "5".getBytes();
		int sizeInBytes = this.noOfPieces/ 8;
		if (noOfPieces % 8 != 0)
			sizeInBytes = sizeInBytes + 1;
		byte[] payload = new byte[sizeInBytes];
		int temp = 0, count = 0;
		int i=1;
		while (i <= this.noOfPieces)
		{
			int x = this.pieces[i-1].hasPiece;
			temp = temp << 1;
			if (x == 1) 
			{
				temp = temp + 1;
			} else
				temp = temp + 0;

			if (i % 8 == 0 && i!=0) {
				payload[count] = (byte) temp;
				count++;
				temp = 0;
			}
			i++;
		}
		if ((i-1) % 8 != 0) 
		{
			int shift = ((noOfPieces) - (noOfPieces / 8) * 8);
			temp = temp << (8 - shift);
			payload[count] = (byte) temp;
		}
		return payload;
		
	}
	
	public static BitField receiveMessage(byte[] r)
	{
		BitField receivedBitField = new BitField();
		int size= r.length;
		int i=0;
		while(i < size)
		{
			int count = 7;
			while(count >=0)
			{
				int x = 1 << count;
				if(i * 8 + (8-count-1) < receivedBitField.noOfPieces)
				{
					if((r[i] & (x)) != 0)
						receivedBitField.pieces[i * 8 + (8-count-1)].hasPiece = 1;
					else
						receivedBitField.pieces[i * 8 + (8-count-1)].hasPiece = 0;
				}
				count--;
			}
			i++;
		}
		
		return receivedBitField;
	}

}
