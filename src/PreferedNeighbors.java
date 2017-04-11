
import java.io.*;
import java.util.*;


/**
 * @author dhruv
 *
 */

public class PreferedNeighbors extends TimerTask

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
        		for(PeerInformation p :Configuration.peers)
        		{
        			if(p.getPeerID()==temp.peerID)
        			{
        				if(temp.isFirstPeer == 1)
        				{
        					p.isCompleted = 1;
        					p.isInterested = 0;
        					p.isCh= 0;
        				}
        			}
        			
        				
        		}
        	}
        }
        catch (IOException e)
        {
            System.out.println("File I/O error!");
        }
        
    	}
		public void run() 
		{
			
			
			readAgain_peerinfo();
			
			int countInterested = 0;
			
			Enumeration<String> keys = remotePeerInfoHash.keys();
			while(keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				PeerInformation pref = remotePeerInfoHash.get(key);
				
				if(key.equals(peerID))continue;
				
				if (pref.isCompleted == 0 && pref.isHandShaked == 1)
				{
					countInterested++;
				} 
				else if(pref.isCompleted == 1)
				{
					try
					{
						preferedNeighbors.remove(key);
					}
					catch (Exception e) {
					}
				}
			}
			String strPref = "";
			if(countInterested > CommonProperties.numOfPreferredNeighbr)
			{
				if(!preferedNeighbors.isEmpty())
					preferedNeighbors.clear();
						
				List <RemotePeerInfo> pv = new ArrayList <RemotePeerInfo>(remotePeerInfoHash.values());
				Collections.sort(pv, new PeerDataRateComparator(false));
				int count = 0;
				for (int i = 0; i < pv.size(); i++) 
				{
					if (count > CommonProperties.numOfPreferredNeighbr - 1)
						break;
					if(pv.get(i).isHandShaked == 1 && !pv.get(i).peerId.equals(peerID) 
							&& remotePeerInfoHash.get(pv.get(i).peerId).isCompleted == 0)
					{
						remotePeerInfoHash.get(pv.get(i).peerId).isPreferredNeighbor = 1;
						preferedNeighbors.put(pv.get(i).peerId, remotePeerInfoHash.get(pv.get(i).peerId));
						
						count++;
						
						strPref = strPref + pv.get(i).peerId + ", ";
						//peerProcess.showLog(peerProcess.peerID + " Selected preferred neighbor is " + pv.get(i).peerId + " data rate - " + pv.get(i).dataRate);
						
						if (remotePeerInfoHash.get(pv.get(i).peerId).isChoked == 1)
						{
							sendUnChoke(peerProcess.peerIDToSocketMap.get(pv.get(i).peerId), pv.get(i).peerId);
							peerProcess.remotePeerInfoHash.get(pv.get(i).peerId).isChoked = 0;
							sendHave(peerProcess.peerIDToSocketMap.get(pv.get(i).peerId), pv.get(i).peerId);
							peerProcess.remotePeerInfoHash.get(pv.get(i).peerId).state = 3;
						}
						
						
					}
				}
			}
			else
			{
				keys = remotePeerInfoHash.keys();
				while(keys.hasMoreElements())
				{
					String key = (String)keys.nextElement();
					
					PeerInformation prefered = remotePeerInfoHash.get(key);
					if(key.equals(peerID)) continue;
					
					if (prefered.isCompleted == 0 && prefered.isHandShaked == 1)
					{
						if(!preferedNeighbors.containsKey(key))
						{
							
							strPref = strPref + key + ", ";
							preferedNeighbors.put(key, remotePeerInfoHash.get(key));
							remotePeerInfoHash.get(key).isPreferredNeighbor = 1;
						}
						if (prefered.isChoked == 1)
						{
							sendUnChoke(peerProcess.peerIDToSocketMap.get(key), key);
							peerProcess.remotePeerInfoHash.get(key).isChoked = 0;
							sendHave(peerProcess.peerIDToSocketMap.get(key), key);
							peerProcess.remotePeerInfoHash.get(key).state = 3;
						}
						
					} 
					
				}
			}
			//LOG 3: Preferred Neighbors 
			//if (strPref != "")
				//peerProcess.showLog(peerProcess.peerID + " has selected the preferred neighbors - " + strPref);
		}
}
