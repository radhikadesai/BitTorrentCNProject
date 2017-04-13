import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Enumeration;


public class MessageQueueProcessor implements Runnable{
	
	private static String myId = null;
	public static int pstate = -1;
	RandomAccessFile raf;
	
	// constructor
	public MessageQueueProcessor(String pid)
	{
		myId = pid;
	}
	
	// constructor
	public MessageQueueProcessor()
	{
		myId = null;
	}
	
	public static PeerInformation util(int id)
	{
		PeerInformation temp =null;
		for(int i=0;i<Configuration.peers.size();i++)
		{
			temp= Configuration.peers.get(i);
			System.out.println("Peerid"+temp.getPeerID());
			if (temp.peerID==id)
			{
				System.out.println("temp.id = "+ temp.peerrelation);
				return temp;
			}
		}
		System.out.println("could not find peer");
		return temp;
	}
	
	public static void setUtil(int id, BitField b)
	{
		for(int i=0;i<Configuration.peers.size();i++)
		{
			PeerInformation temp= Configuration.peers.get(i);
			if (temp.peerID==id)
			{
				temp.bitField=b;
				Configuration.peers.set(i,temp);
			}
		}
	}

	public static void setUtil(int id, int b, String param)
	{
		for(int i=0;i<Configuration.peers.size();i++)
		{
			PeerInformation temp= Configuration.peers.get(i);
			if (temp.peerID==id)
			{
				if(param.equalsIgnoreCase("state"))
				{
					temp.peerrelation=b;
				}
				else if (param.equalsIgnoreCase("isInterested"))
				{
					temp.isInterested=b;
				}
				else if (param.equalsIgnoreCase("isHandshaked"))
				{
					temp.isHandShaked=b;
				}
				else if (param.equalsIgnoreCase("isChoked"))
				{
					temp.isCh=b;
				}
				else
				{
					System.out.println("Not sure what to do");
				}
				Configuration.peers.set(i,temp);
			}
		}
	}
	public void run()
	{
		ActualMessage msg;
		ActualMessageWithPeer msgWithPeer;
		byte msgType;
		int rId;
		int peerRelation;
		
	
		while(true)
		{
			msgWithPeer  = PeerProcess.removeFromQ();
			while(msgWithPeer == null)
			{
				Thread.currentThread();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					
					e.printStackTrace();
				}
				msgWithPeer  = PeerProcess.removeFromQ();
			}
			
			msg = msgWithPeer.getMessage();
			System.out.println("Message received is : "+ msg);
			
			msgType = msg.getMessageType();
			rId = msgWithPeer.getfromPeerID();
			System.out.println("rID : "+ rId);
			peerRelation = util(rId).peerrelation;
			System.out.println("Peer relation is : "+ peerRelation);
			
			if(msgType==ActualMessage.HAVE && peerRelation != 14)
			{
				// LOG 7: TODO HAVE MESSAGE FOR WHICH PIECE ??
				PeerProcess.consoleLog(PeerProcess.myProcessPeerID + " receieved the 'have' message from Peer " + rId + " for the piece " + "ff"); 
				if(isInterested(msg, rId))
				{
					//PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " received the �interested� message from " + rId));
					sendInterested(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,9,"state");
				}	
				else
				{
					//PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " received the �not interested� message from " + rId));
					//peerProcess.showLog(peerProcess.peerID + "not interesteded " + rPeerId);
					sendNotInterested(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,13,"state");
				}
			}
			else if(msgType==ActualMessage.BITFIELD && peerRelation == 2)
			{
				System.out.println("The msg tupe is bitfield and state is 2");
				//PeerProcess.showLog(peerProcess.peerID + " receieved a BITFIELD message from Peer " + rPeerId);
				sendBitField(PeerProcess.peerNsocket.get(rId), rId);
				setUtil(rId,3,"state");
			}
			else if(msgType==ActualMessage.NOT_INTERESTED && peerRelation == 3)
			{
				// LOG 9:
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " received the �not interested� message from " + rId));

				//peerProcess.showLog(peerProcess.peerID + " receieved a NOT INTERESTED message from Peer " + rPeerId);
				setUtil(rId,0,"isInterested");
				setUtil(rId, 5, "state");
				setUtil(rId,1,"isHandshaked");
			}
			else if(msgType==ActualMessage.INTERESTED && peerRelation == 3){	
				// LOG 8:
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " received the �interested� message from " + rId));

				//peerProcess.showLog(peerProcess.peerID + " receieved an INTERESTED message from Peer " + rPeerId);
				setUtil(rId,1,"isInterested");
				setUtil(rId,1,"isHandshaked");
				
				if(!PeerProcess.preferedNeighbors.containsKey(rId) && !PeerProcess.unchokedNeighbors.containsKey(rId))
				{
					sendChoke(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,1, "isChoked");
					setUtil(rId,6,"state");
				}
				else
				{
					setUtil(rId,0,"isChoked");
					sendUnChoke(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,4,"state");
				}
			}
			else if(msgType==ActualMessage.REQUEST && peerRelation == 4)
			{
				//peerProcess.showLog(peerProcess.peerID + " receieved a REQUEST message from Peer " + rPeerId);
				sendPiece(PeerProcess.peerNsocket.get(rId), msg, rId);

				
				// Decide to send CHOKE or UNCHOKE message
				if(!PeerProcess.preferedNeighbors.containsKey(rId) && !PeerProcess.unchokedNeighbors.containsKey(rId))
				{
					
					sendChoke(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,1,"isChoked");
					setUtil(rId,6,"state");
				} 
			}
			else if((msgType==ActualMessage.BITFIELD && peerRelation == 8)
					|| (msgType==ActualMessage.HAVE && peerRelation == 14))
			{
				System.out.println("The msg type is : bitfield and state is 8");
				if(isInterested(msg,rId))
				{
					System.out.println("It is interested");
					sendInterested(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,9,"state");
				}	
				else
				{
					System.out.println("It is not interested");
					sendNotInterested(PeerProcess.peerNsocket.get(rId), rId);
					setUtil(rId,13,"state");
				}
			}
			else if(msgType==ActualMessage.CHOKE && peerRelation == 9)
			{
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " is choked by " + rId));

				//peerProcess.showLog(peerProcess.peerID + " is CHOKED by Peer " + rPeerId);
				setUtil(rId,14,"state");
			}
			else if(msgType==ActualMessage.UNCHOKE && peerRelation == 9)
			{
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " is unchoked by " + rId));

				//peerProcess.showLog(peerProcess.peerID + " is UNCHOKED by Peer " + rPeerId);
				int firstdiff = PeerProcess.myBitField.whatsDifferent(util(rId).bitField);
				if(firstdiff != -1)
				{
					sendRequest(PeerProcess.peerNsocket.get(rId), firstdiff, rId);
					setUtil(rId,11,"state");
					util(rId).start = new Date();
				}
				else
					setUtil(rId,13,"state");
	
			}
			else if(msgType==ActualMessage.PIECE && peerRelation == 11)
			{
				byte[] buffer = msg.getPayload();
					
				
				util(rId).end = new Date();
				long timeLapse = util(rId).end.getTime() - util(rId).start.getTime() ;
				
				util(rId).dataRate = ((double)(buffer.length + 4 + 1)/(double)timeLapse) * 100;
				
				Piece p = Piece.receive(buffer);
				PeerProcess.myBitField.update(rId, p);			
				
				int toGetPieceIndex = PeerProcess.myBitField.whatsDifferent(util(rId).bitField);
				if(toGetPieceIndex != -1)
				{
					sendRequest(PeerProcess.peerNsocket.get(rId),toGetPieceIndex, rId);
					setUtil(rId,11,"state");
					util(rId).start = new Date();
				}
				else
					setUtil(rId,13,"state");
				PeerProcess.readAgain_peerinfo();
				
				for(PeerInformation pref :Configuration.peers){
			           if(pref.getPeerID()==PeerProcess.myProcessPeerID)
			        	   continue;
			                
			            
					if (pref.isCompleted == 0 && pref.isCh == 0 && pref.isHandShaked == 1)
					{
						sendHave(PeerProcess.peerNsocket.get(pref.getPeerID()), pref.getPeerID());
						setUtil(pref.getPeerID(),3, "state");
						
					} }
					
				
								
				buffer = null;
				msg = null;
	
			}
			else if(msgType==ActualMessage.CHOKE && peerRelation == 11)
			{
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " is choked by " + rId));

				//peerProcess.showLog(peerProcess.peerID + " is CHOKED by Peer " + rPeerId);
				setUtil(rId,14,"state");
			}
			else if(msgType==ActualMessage.UNCHOKE && peerRelation == 14)
			{
				PeerProcess.consoleLog((PeerProcess.myProcessPeerID + " is unchoked by " + rId));

				//peerProcess.showLog(peerProcess.peerID + " is UNCHOKED by Peer " + rPeerId);
				setUtil(rId,14,"state");
			}	
		}
	}


	private void sendRequest(Socket socket, int pieceNo, int rId) {

		byte[] pieceByte = new byte[4];
		for (int i = 0; i < 4; i++) {
			pieceByte[i] = 0;
		}

		byte[] pieceIndexByte = ByteBuffer.allocate(4).putInt(pieceNo).array();
		System.arraycopy(pieceIndexByte, 0, pieceByte, 0,
						pieceIndexByte.length);
		ActualMessage d = new ActualMessage(ActualMessage.REQUEST, pieceByte);
		byte[] b = d.serialize();
		SendData(socket, b);

		pieceByte = null;
		pieceIndexByte = null;
		b = null;
		d = null;
	}

	private void sendPiece(Socket socket, ActualMessage d, int rId)
	{
		byte[] bytePieceIndex = d.getPayload();
		int pieceIndex = ActualMessage.byteArrayToInt(bytePieceIndex, 0);
		
		//peerProcess.showLog(peerProcess.peerID + " sending a PIECE message for piece " + pieceIndex + " to Peer " + remotePeerID);
		
		byte[] byteRead = new byte[Configuration.CommonProperties.PieceSize];
		int noBytesRead = 0;
		
		File file = new File(Integer.toString(PeerProcess.myProcessPeerID),Configuration.CommonProperties.FileName);
		try 
		{
			raf = new RandomAccessFile(file,"r");
			raf.seek(pieceIndex*Configuration.CommonProperties.PieceSize);
			noBytesRead = raf.read(byteRead, 0, Configuration.CommonProperties.PieceSize);
		} 
		catch (IOException e) 
		{
			//peerProcess.showLog(peerProcess.peerID + " ERROR in reading the file : " +  e.toString());
		}
		if( noBytesRead == 0)
		{
			//peerProcess.showLog(peerProcess.peerID + " ERROR :  Zero bytes read from the file !");
		}
		else if (noBytesRead < 0)
		{
			//peerProcess.showLog(peerProcess.peerID + " ERROR : File could not be read properly.");
		}
		
		byte[] buffer = new byte[noBytesRead + 4];
		System.arraycopy(bytePieceIndex, 0, buffer, 0, 4);
		System.arraycopy(byteRead, 0, buffer, 4, noBytesRead);

		ActualMessage sendMessage = new ActualMessage(ActualMessage.PIECE, buffer);
		byte[] b =  sendMessage.serialize();
		SendData(socket, b);
		
		buffer = null;
		byteRead = null;
		b = null;
		bytePieceIndex = null;
		sendMessage = null;
		
		try{
			raf.close();
		}
		catch(Exception e){}
	}
	
	private void sendNotInterested(Socket socket, int rId) 
	{
		//peerProcess.showLog(peerProcess.peerID + " sending a NOT INTERESTED message to Peer " + remotePeerID);
		ActualMessage d =  new ActualMessage(ActualMessage.NOT_INTERESTED);
		byte[] msgByte = d.serialize();
		SendData(socket,msgByte);
	}

	private void sendInterested(Socket socket, int remotePeerID) {
		//peerProcess.showLog(peerProcess.peerID + " sending an INTERESTED message to Peer " + remotePeerID);
		ActualMessage d =  new ActualMessage(ActualMessage.INTERESTED);
		byte[] msgByte = d.serialize();
		SendData(socket,msgByte);
		
	}


	private boolean isInterested(ActualMessage msg, int rId) {
		
		BitField b = BitField.receiveMessage(msg.getPayload());
		System.out.println("in isinterested : "+b);
		setUtil(rId,b);
		System.out.println("");

		//peerProcess.showLog(peerProcess.peerID + " Bitfield of Peer " + rPeerId);
		if(PeerProcess.myBitField.isDifferent(b))
			return true;
		return false;
	}

	private void sendUnChoke(Socket socket, int rId) {

		//PeerProcess.showLog(peerProcess.peerID + " sending UNCHOKE message to Peer " + remotePeerID);
		ActualMessage d = new ActualMessage(ActualMessage.UNCHOKE);
		byte[] msgByte = d.serialize();
		SendData(socket,msgByte);
	}

	private void sendChoke(Socket socket, int rId) {
		//peerProcess.showLog(peerProcess.peerID + " sending CHOKE message to Peer " + remotePeerID);
		ActualMessage d = new ActualMessage(ActualMessage.CHOKE);
		byte[] msgByte = d.serialize();
		SendData(socket,msgByte);
	}

	private void sendBitField(Socket socket, int rId) {
		System.out.println("Sendgin bitfield");
		//peerProcess.showLog(peerProcess.peerID + " sending BITFIELD message to Peer " + remotePeerID);
		byte[] encodedBitField = PeerProcess.myBitField.sendMessage();

		ActualMessage d = new ActualMessage(ActualMessage.BITFIELD, encodedBitField);
		SendData(socket,d.serialize());
		
		encodedBitField = null;
	}
	
	
	private void sendHave(Socket socket, int i) {
		
		//peerProcess.showLog(peerProcess.peerID + " sending HAVE message to Peer " + remotePeerID);
		byte[] encodedBitField = PeerProcess.myBitField.sendMessage();
		ActualMessage d = new ActualMessage(ActualMessage.HAVE, encodedBitField);
		SendData(socket,d.serialize());
		
		encodedBitField = null;
	}
	
	private int SendData(Socket socket, byte[] encodedBitField) {
		try {
		OutputStream out = socket.getOutputStream();
		out.write(encodedBitField);
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return 1;
	}

}
