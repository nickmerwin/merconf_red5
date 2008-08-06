/*
=====================================
MerConf Lite v.1 - Red5 App
	Nick Merwin (nick@lemurheavy.com)
=====================================
 */

package com.merconf;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.red5.server.adapter.ApplicationAdapter;

import org.red5.server.api.IConnection;
import org.red5.server.api.Red5;
import org.red5.server.api.stream.IBroadcastStream;
import org.red5.server.api.so.ISharedObject;

public class Application extends ApplicationAdapter {

	private static final Log log = LogFactory.getLog(Application.class);

	// called when a client connects
	public boolean appConnect(IConnection conn, Object[] params) {
    	
    	// get stream name from passed in connection parameter
    	String streamName = (String) params[0];
    	
    	// keep streamName for later (conn is unique for each connected user)
    	conn.setAttribute("streamName", streamName);
    	
    	// grab array of stream from our scope-wide shared object
    	ISharedObject so = getSO();
    	ArrayList<String> streams = (ArrayList<String>) so.getAttribute("streams");
        if(streams == null) streams = new ArrayList<String>();
        
        // add our new streamName to the array
        if(streams.indexOf(streamName) != -1) return false;
        streams.add(streamName);
        
        // store to shared object, this will send a SyncEvent.SYNC event to each connected client
        so.setAttribute("streams", streams);
        
        log.info(streams);
       	
		return true;
	}
	
	// called when a client's stream drops
    public void streamBroadcastClose(IBroadcastStream stream) {
  
    	// get our client-specific connection object 
    	IConnection current = Red5.getConnectionLocal();
    	
    	// grab the streamName of this disconnecting client
    	String streamName = (String) current.getAttribute("streamName");
    	
    	// remove the stream from the streams array
    	ISharedObject so = getSO();
    	ArrayList<String> streams = (ArrayList<String>) so.getAttribute("streams");
		streams.remove(streamName);
		
		// update the shared object and notify the connected clients
		so.setAttribute("streams", streams);
	}
    
    //////////////////// Shared Object helper //////////////
	public ISharedObject getSO(){
		createSharedObject(Red5.getConnectionLocal().getScope(), "SO", false);
        return getSharedObject(Red5.getConnectionLocal().getScope(), "SO", false); 
	}

}