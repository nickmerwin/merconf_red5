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

    @Override
	public boolean appConnect(IConnection conn, Object[] params) {
    	
    	String streamName = (String) params[0];
    	conn.setAttribute("streamName", streamName);
    	ArrayList<String> streams = (ArrayList<String>) getSO().getAttribute("streams");
        if(streams == null) streams = new ArrayList<String>();
        if(streams.indexOf(streamName) != -1) return false;
        streams.add(streamName);
        getSO().setAttribute("streams", streams);
        
        log.info(streams);
       	
		return true;
	}
    
    //////////////////// User SO functions //////////////
	public ISharedObject getSO(){
		createSharedObject(Red5.getConnectionLocal().getScope(), "SO", false);
        return getSharedObject(Red5.getConnectionLocal().getScope(), "SO", false); 
	}

    public void streamBroadcastClose(IBroadcastStream stream) {
    	IConnection current = Red5.getConnectionLocal();
    	
    	ArrayList<String> streams = (ArrayList<String>) getSO().getAttribute("streams");
		streams.remove(current.getAttribute("streamName"));
		getSO().setAttribute("streams", streams);
	}

}