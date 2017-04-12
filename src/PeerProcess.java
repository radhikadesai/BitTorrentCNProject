import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
   
    public static Queue<ActualMessageWithPeer> Q = new LinkedList<ActualMessageWithPeer>();
	
    public static volatile Hashtable<String, PeerInformation> preferedNeighbors = new Hashtable<String, PeerInformation>();
    
    public static Hashtable<String, Socket> peerNsocket = new Hashtable<String, Socket>();
	
    
    

/**
 * @author dhruv
 *
 */

    
    public static class PreferedNeighbors extends TimerTask

    {
    	
    	
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

    						preferedNeighbors.put(Integer.toString(pv.get(i).getPeerID()), p);
    						
    						count++;
    						
    						strPref = strPref + pv.get(i).getPeerID() + ", ";
    						//peerProcess.showLog(peerProcess.peerID + " Selected preferred neighbor is " + pv.get(i).peerId + " data rate - " + pv.get(i).dataRate);
    						
    						if (p.isCh == 1)
    						{
    							//sendUnChoke(PeerProcess.peerNsocket.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
    							p.isCh = 0;
    							//sendHave(PeerProcess.peerNsocket.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
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
    							preferedNeighbors.put(Integer.toString(p.getPeerID()), p);
    							p.isPNeighbor = 1;
    							Configuration.peers.set(ijk, p);
    						}
    						if (prefered.isCh == 1)
    						{
    							//sendUnChoke(PeerProcess.peerNsocket.get(key), key);
    							p.isCh = 0;
    							//sendHave(PeerProcess.peerNsocket.get(key), key);
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
    
    
    public static void main(String[] args) throws FileNotFoundException {
        boolean isFirstPeer =false;
        //Initializkee Configuration
        Configuration config = new Configuration("/Users/radhikadesai/Desktop/BitTorrentCNProject/src/common.cfg","/Users/radhikadesai/Desktop/BitTorrentCNProject/src/PeerInfo.cfg"); //give paths of the common and peerInfo config files
        //Initialize peerProcess
        PeerProcess peerProcess = new PeerProcess();
        myProcessPeerID = Integer.parseInt(args[0]);
        //Initialize the preferred neighbors
        initializePreferredNeighbors();

        for(PeerInformation p :Configuration.peers){
            if(p.getPeerID()==myProcessPeerID){
                peerProcess.listeningPort = p.getPort();
                peerProcess.myIndex = p.getIndex();
                if(p.getIsFirstPeer()==1){
                    isFirstPeer=true;
                    break;
                }
            }
        }
        myBitField=new BitField();
//        myBitField.initOwnBitfield(peerID, isFirstPeer?1:0);

        if(!isFirstPeer) {
            //Create Empty file
            //emptyFile();
            for (PeerInformation p : Configuration.peers) {
                if (peerProcess.myIndex > p.getIndex()) {
                    System.out.println("Spawning sending threads for peer  "+ p.getPeerID());
                    //SendingThread for each client before me
                    Thread sendingThread = new Thread(new SendingThread(p.getAddress(), p.getPort(), myProcessPeerID));
//                    receivingThread.add(sendingThread);
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
        }
        catch(IOException ex){
            System.exit(0);
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
//                PeerProcess.sendingThread.add(sendingThread);
                sendingThread.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}







