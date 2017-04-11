
import java.io.*;
import java.util.*;


/**
 * @author dhruv
 *
 */

public class PreferedNeighbors extends TimerTask


{
	public static volatile Hashtable<String, PeerInformation> preferedNeighbors = new Hashtable<String, PeerInformation>();
	
	int myProcessPeerID;

	
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
							sendUnChoke(peerProcess.peerIDToSocketMap.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
							p.isCh = 0;
							sendHave(peerProcess.peerIDToSocketMap.get(pv.get(i).getPeerID()), pv.get(i).getPeerID());
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
							sendUnChoke(peerProcess.peerIDToSocketMap.get(key), key);
							p.isCh = 0;
							sendHave(peerProcess.peerIDToSocketMap.get(key), key);
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
