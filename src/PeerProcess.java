import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import java.io.*;
import java.util.*;

/**
 * Created by radhikadesai on 08/04/2017.
 */
public class PeerProcess {

	public static int myProcessPeerID;
	
    public int listeningPort;
    public int myIndex;
    public ServerSocket listeningSocket;
    public Thread listeningThread;
    public static BitField myBitField;
   
	public static volatile Timer tPref;
	
    public static Queue<ActualMessageWithPeer> Q = new LinkedList<ActualMessageWithPeer>();
	
    public static volatile Hashtable<Integer, PeerInformation> preferedNeighbors = new Hashtable<Integer, PeerInformation>();
    
    public static Hashtable<Integer, Socket> peerNsocket = new Hashtable<Integer, Socket>();
	
	public static volatile Hashtable<Integer, PeerInformation> unchokedNeighbors = new Hashtable<Integer, PeerInformation>();
    public static Vector<Thread> rcvThread = new Vector<Thread>();
    public static Vector<Thread> sndingThread = new Vector<Thread>();
	
	public static void createEmptyFile()
	{
		
		try {
			File dir = new File(Integer.toString((myProcessPeerID)));
			dir.mkdir();

			File newfile = new File(Integer.toString(myProcessPeerID), Configuration.CommonProperties.FileName);
			OutputStream os = new FileOutputStream(newfile, true);
			byte b = 0;
			
			//showLog(peerID + " Size of file = " + CommonProperties.fileSize);
			
			for (int i = 0; i < Configuration.CommonProperties.FileSize; i++)
				os.write(b);
			os.close();
		} 
		catch (Exception e) {
			//showLog(myProcessPeerID + " ERROR in creating the file : " + e.getMessage());
		}

	}
	
	public static void readAgain_peerinfo()
	{
	
        String str;
		try
        {
			BufferedReader buffread = new BufferedReader(new FileReader(new File("PeerInfo.cfg")));
        	for (str = buffread.readLine(); str!= null ; str = buffread.readLine())
        	{
        		String[] tokens = str.split(" ");
        		PeerInformation temp = new PeerInformation();
        		temp.peerID = Integer.parseInt(tokens[0]);
        		temp.isFirstPeer= Integer.parseInt(tokens[3]);
        		int jk=0;
        		for(PeerInformation p :Configuration.peers)
        		{
        			if(p.getPeerID()==temp.peerID)
        			{
        				if(temp.isFirstPeer == 1)
        				{
        					p.isCompleted = 1;
        					p.isInterested = 0;
        					p.isCh= 0;
        					Configuration.peers.set(jk, p);
        				}
        			}
        			
        		jk++;		
        		}
        	}
        	buffread.close();
        }
        catch (IOException e)
        {
            System.out.println("File I/O error!");
        }
        
    	}
    

/**
 * @author dhruv
 *
 */

    
    public static class PreferedNeighbors extends TimerTask

    {
    	
    	
    		public void run() 
    		{
    			
    			
    			readAgain_peerinfo();
    			
    			int Interestedcount = 0;
    			int jk=0;
    			for(PeerInformation p :Configuration.peers)
    			{
    				if(p.getPeerID()==myProcessPeerID)
    		
    					continue;
    				if (p.isCompleted == 0 && p.isHandShaked == 1)
    				{
    					Interestedcount++;
    				}
    			
    			//Enumeration<String>  = remotePeerInfoHash.keys();
    			//while(keys.hasMoreElements())
    			//{
    			//	String key = (String)keys.nextElement();
    			//	PeerInformation pref = remotePeerInfoHash.get(key);
    				
    				
    				
    				//if (pref.isCompleted == 0 && pref.isHandShaked == 1)
    				//{
    				//	countInterested++;
    				//} 
    				else if(p.isCompleted == 1)
    				{
    					try
    					{
    						preferedNeighbors.remove(Integer.toString(p.getPeerID()));
    					}
    					catch (Exception e) {
    					}
    				}
    			}
    			
    			String strPref = "";
    			
    			if(Interestedcount > Configuration.CommonProperties.NumberOfPreferredNeighbors)
    			{
    				if(!preferedNeighbors.isEmpty())
    					preferedNeighbors.clear();
    						
    				ArrayList <PeerInformation> pv = new ArrayList <PeerInformation>();
    				Collections.copy(pv,Configuration.peers);
    				Collections.sort(pv, new DataRComparitor(false));
    				int count = 0;
    				for (int i = 0; i < pv.size(); i++) 
    				{
    					if (count > Configuration.CommonProperties.NumberOfPreferredNeighbors - 1)
    						break;
    		
    					int ijk=0;
    					for(PeerInformation p :Configuration.peers)
    					{
    						if(p.getPeerID()== pv.get(i).getPeerID())
    					
    						if(pv.get(i).isHandShaked == 1 && !(pv.get(i).getPeerID()==(myProcessPeerID))
    							&& p.isCompleted == 0)
    							{
    								p.isPNeighbor = 1;
    	        					Configuration.peers.set(ijk, p);

    						preferedNeighbors.put(pv.get(i).getPeerID(), p);
    						
    						count++;
    						
    						strPref = strPref + pv.get(i).getPeerID() + ", ";
    						//peerProcess.showLog(peerProcess.peerID + " Selected preferred neighbor is " + pv.get(i).peerId + " data rate - " + pv.get(i).dataRate);
    						
    						if (p.isCh == 1)
    						{
    							sendUnChoke(PeerProcess.peerNsocket.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
    							p.isCh = 0;
    							sendHave(PeerProcess.peerNsocket.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
    							p.peerrelation= 3;
            					Configuration.peers.set(ijk, p);

    						}
    						
    						
    					}
    						ijk++;
    				}
    			}
    			}
    			else
    			{
    				int ijk=0;
    				for(PeerInformation p :Configuration.peers)
    				{
    					
    				//}
    				//keys = remotePeerInfoHash.keys();
    				//while(keys.hasMoreElements())
    				//{
    				//	String key = (String)keys.nextElement();
    					
    					PeerInformation prefered = p;
    					
    					if(p.getPeerID()==(myProcessPeerID))
    						continue;
    					
    					if (prefered.isCompleted == 0 && prefered.isHandShaked == 1)
    					{
    						if(!preferedNeighbors.containsKey(Integer.toString(p.getPeerID())))
    						{
    							
    							strPref = strPref + Integer.toString(p.getPeerID()) + ", ";
    							preferedNeighbors.put(p.getPeerID(), p);
    							p.isPNeighbor = 1;
    							Configuration.peers.set(ijk, p);
    						}
    						if (prefered.isCh == 1)
    						{
    							int key = p.getPeerID();
    							sendUnChoke(PeerProcess.peerNsocket.get(key), key);
    							p.isCh = 0;
    							sendHave(PeerProcess.peerNsocket.get(key), key);
    							p.peerrelation = 3;
    							Configuration.peers.set(ijk, p);
    						}
    						
    					} 
    					
    				}
    			}
    			//LOG 3: Preferred Neighbors 
    			//if (strPref != "")
    				//peerProcess.showLog(peerProcess.peerID + " has selected the preferred neighbors - " + strPref);
    		}
    }

    
    
    
    
    
    
    //aaaaa//

    
    
    
    
    
    
    

	private static void sendUnChoke(Socket socket, Integer remotePeerID)
	{

		//showLog( myProcessPeerID + " is sending UNCHOKE message to remote Peer " + remotePeerID);
		ActualMessage d = new ActualMessage(ActualMessage.UNCHOKE);
		byte[] Bytemsg = d.serialize();
		SendData(socket, Bytemsg);

	}
	private static void sendHave(Socket socket, Integer remotePeerID)
	{
		
		//showLog(myProcessPeerID + " sending HAVE message to Peer " + remotePeerID);
		byte[] encodedBitField = PeerProcess.myBitField.sendMessage();
		ActualMessage d = new ActualMessage(ActualMessage.HAVE, encodedBitField);
		SendData(socket,d.serialize());
		
		encodedBitField = null;
	}
	private static int SendData(Socket socket, byte[] encodedBitField) {
		try {
		OutputStream out = socket.getOutputStream();
		out.write(encodedBitField);
		} catch (IOException e) {
			
			e.printStackTrace();
			return 0;
		}
		return 1;
	}


public static class UnChokedNeighbors extends TimerTask 
	{

	public void run() 
		{
			//updates remotePeerInfoHash
			
			readAgain_peerinfo();
			
			if(!unchokedNeighbors.isEmpty())
				unchokedNeighbors.clear();
			
			//Enumeration<String> keys = remotePeerInfoHash.keys();
			Vector<PeerInformation> peeps = new Vector<PeerInformation>();
			//while(keys.hasMoreElements())
			
			for(PeerInformation p :Configuration.peers)
			{
				//String key = (String)keys.nextElement();
				PeerInformation pref = p;
				
				if (pref.isCh == 1 
						&& !(p.getPeerID()==(myProcessPeerID)) 
						&& pref.isCompleted == 0 
						&& pref.isHandShaked == 1)
					peeps.add(pref);
			}
			
			// Randomize the vector elements 	
			if (peeps.size() > 0)
			{
				Collections.shuffle(peeps);
				PeerInformation cp = peeps.firstElement();
				int pq=0;
				for(PeerInformation p :Configuration.peers)
				{
					if(p.getPeerID()==cp.getPeerID())
					{
						p.isOUNeighbor=1;
						unchokedNeighbors.put(cp.getPeerID(),p);
						Configuration.peers.set(pq, p);
					}
				pq++;	
				}
				// LOG 4:
				//PeerProcess.showLog(PeerProcess.peerID + " has the optimistically unchoked neighbor " + p.peerId);
				
				//remotePeerInfoHash.get(cp.getPeerID()).isOptUnchokedNeighbor = 1;
				
				int xy=0;
				for(PeerInformation p :Configuration.peers)
				{
					if(p.getPeerID()==cp.getPeerID()&&(p.isCh==1))
					{
						p.isCh = 0;
						sendUnChoke(PeerProcess.peerNsocket.get(cp.getPeerID()), cp.getPeerID());
						sendHave(PeerProcess.peerNsocket.get(cp.getPeerID()), cp.getPeerID());
						p.peerrelation = 3;	
						Configuration.peers.set(xy, p);
					}
						
				xy++;
					
				}
			}
			
		}

	}


	public static void startUnChokedNeighbors() 
	{
		tPref = new Timer();
		tPref.schedule(new UnChokedNeighbors(),
				Configuration.CommonProperties.OptimisticUnchokingInterval * 1000 * 0,
				Configuration.CommonProperties.OptimisticUnchokingInterval * 1000);
	}

	public static void stopUnChokedNeighbors() {
		tPref.cancel();
	}

	public static void startPreferredNeighbors() {
		tPref = new Timer();
		tPref.schedule(new PreferedNeighbors(),
				Configuration.CommonProperties.UnchokingInterval * 1000 * 0,
				Configuration.CommonProperties.UnchokingInterval * 1000);
	}

	public static void stopPreferredNeighbors() {
		tPref.cancel();
	}
    
    public static void main(String[] args) throws IOException {
        boolean isFirstPeer = false;
        //Initializkee Configuration
        Configuration config = new Configuration("/Users/radhikadesai/Desktop/BitTorrentCNProject/src/common.cfg", "/Users/radhikadesai/Desktop/BitTorrentCNProject/src/PeerInfo.cfg"); //give paths of the common and peerInfo config files
        //Initialize peerProcess
        PeerProcess peerProcess = new PeerProcess();
        myProcessPeerID = Integer.parseInt(args[0]);
        try{
            //Start Logging
            Logger.initial("log_peer_" + Integer.toString(myProcessPeerID) + ".log");
            consoleLog(Integer.toString(myProcessPeerID) + " is started");
            //Initialize the preferred neighbors
            initializePreferredNeighbors();

            for (PeerInformation p : Configuration.peers) {
                if (p.getPeerID() == myProcessPeerID) {
                    peerProcess.listeningPort = p.getPort();
                    peerProcess.myIndex = p.getIndex();
                    if (p.getIsFirstPeer() == 1) {
                        isFirstPeer = true;
                        break;
                    }
                }
            }
            //My own bitfield is initialized
            myBitField = new BitField();
            myBitField.initial(Integer.toString(myProcessPeerID), isFirstPeer ? 1 : 0);
            //Message Queue processor thread is started
            Thread messageProcessor = new Thread(new MessageQueueProcessor(Integer.toString(myProcessPeerID)));
            messageProcessor.start();

            if (!isFirstPeer) {
                //Create Empty file
                createEmptyFile();
                for (PeerInformation p : Configuration.peers) {
                    if (peerProcess.myIndex > p.getIndex()) {
                        System.out.println("Spawning sending threads for peer  " + p.getPeerID());
                        //SendingThread for each client before me
                        Thread sendingThread = new Thread(new SendingThread(p.getAddress(), p.getPort(), myProcessPeerID));
                        rcvThread.add(sendingThread);
                        sendingThread.start();
                    }
                }
            }
            //Listening thread for peerProcess
            try {
                System.out.println("Spawning listening Thread : ");
                peerProcess.listeningSocket = new ServerSocket(peerProcess.listeningPort);
                peerProcess.listeningThread = new Thread(new ListeningThread(peerProcess.listeningSocket, myProcessPeerID));
                peerProcess.listeningThread.start();
            } catch (SocketTimeoutException tox) {
                consoleLog(myProcessPeerID + " gets time out exception in Starting the listening thread: " + tox.toString());
                Logger.end();
                System.exit(0);
            } catch (IOException ex) {
                consoleLog(myProcessPeerID + " gets exception in Starting the listening thread: " + peerProcess.listeningPort + " " + ex.toString());
                Logger.end();
                System.exit(0);
            }
            startPreferredNeighbors();
            startUnChokedNeighbors();
            while (true) {
                //For program ending
                boolean haveAllpeersDownloaded = allPeersDownloaded();
                if (haveAllpeersDownloaded) {
                    consoleLog("All peers have downloaded the file!");
                    stopPreferredNeighbors();
                    stopUnChokedNeighbors();

                    try {
                        Thread.currentThread();
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }

                    if (peerProcess.listeningThread.isAlive())
                        peerProcess.listeningThread.stop();

                    if (messageProcessor.isAlive())
                        messageProcessor.stop();

                    for (int i = 0; i < rcvThread.size(); i++)
                        if (rcvThread.get(i).isAlive())
                            rcvThread.get(i).stop();

                    for (int i = 0; i < sndingThread.size(); i++)
                        if (sndingThread.get(i).isAlive())
                            sndingThread.get(i).stop();
                    break;
                } else {
                    try {
                        Thread.currentThread();
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
        catch(Exception ex)
        {
            consoleLog(Integer.toString(myProcessPeerID) + " Exception in ending : " + ex.getMessage() );
        }
        finally
        {
            consoleLog(Integer.toString(myProcessPeerID) + " Peer process is exiting..");
            Logger.end();
            System.exit(0);
        }
    }
    public static synchronized boolean allPeersDownloaded() {
        String line;
        int count = 1;
        try {
            BufferedReader in = new BufferedReader(new FileReader(
                    "PeerInfo.cfg"));

            while ((line = in.readLine()) != null) {
                count = count
                        * Integer.parseInt(line.trim().split("\\s+")[3]);
            }
            if (count == 0) {
                in.close();
                return false;
            } else {
                in.close();
                return true;
            }

        } catch (Exception e) {
            consoleLog(e.toString());
            return false;
        }

    }
    public static synchronized void addToQ(ActualMessageWithPeer msg)
    {
        Q.add(msg);
    }
    public static synchronized ActualMessageWithPeer removeFromQ()
    {
        ActualMessageWithPeer msg = null;
        if(!Q.isEmpty())
        {
            msg = Q.remove();
        }
        return msg;
    }
    public static String getTime() {
        Date d = new Date();
        return d.toString();

    }
    public static void consoleLog(String message)
    {
        Logger.log(getTime() + ": Peer " + message);
        System.out.println(getTime() + ": Peer " + message);
    }
    private static void initializePreferredNeighbors() {
        int i=0;
        for (PeerInformation p : Configuration.peers){
            if(p.getPeerID()!=myProcessPeerID){
                //add p to the list of preferred neighbors
                i++;
                if(i==Configuration.CommonProperties.NumberOfPreferredNeighbors){
                    break;
                }
            }
        }
    }
}
class ListeningThread implements Runnable{

    private ServerSocket listeningSocket;
    private int peerID;
    Socket remoteSocket;
    Thread sendingThread;

    public ListeningThread(ServerSocket socket, int peerID)
    {
        this.listeningSocket = socket;
        this.peerID = peerID;
    }
    @Override
    public void run() {
        //Keep listening for remote connections
        while (true){
            try {
                remoteSocket = listeningSocket.accept();
                //spawn a sending thread for each incoming connection
                sendingThread = new Thread(new SendingThread(remoteSocket,peerID));
                // Log connection is established
                PeerProcess.consoleLog(peerID + " Connection is established");
                PeerProcess.sndingThread.add(sendingThread);
                sendingThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}







