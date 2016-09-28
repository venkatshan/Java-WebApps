package com.berylsys.proxy;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.utils.URIUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.mitre.dsmiley.httpproxy.ProxyServlet;


/**
 * @author VShanmugam
 *
 */
@WebServlet("/Proxy/*")
public class BerylSysProxyServlet extends ProxyServlet  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String restSvcProtocol =  "http";
	private final String HOST = "localhost";
	private final String PORT = "7001";
	private final String URI = "/wls/rest/query";
	
	@Override
	public void init() throws ServletException {
		serverURL = restSvcProtocol + "://" + HOST + ":" + PORT + URI ;
		super.init();
	}

	private  String serverURL = null;

	Logger log = Logger.getLogger(BerylSysProxyServlet.class);
	
	protected String getTargetUri(HttpServletRequest req) {
		
		return serverURL;

	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		if( null == getTargetUri(req) ){
			res.getWriter().println("config error");
			return;
		}
		
		
		
		req.setAttribute(ATTR_TARGET_URI, getTargetUri(req));
		super.service(req, res);
	}
	

	 
	 
	 
	 
	 static String headerValue;
	 
	 static {
		 

			try{			
				String authString = "admin" + ":" + "password";

				headerValue = "Basic " + new String(Base64.encodeBase64(authString.getBytes()));
			}catch( Exception e) { e.printStackTrace();  }
			
 	 }

	protected void initTarget() throws ServletException {
		doLog = true;
		targetUri = serverURL;
		if (targetUri == null)
		  throw new ServletException(P_TARGET_URI+" is required.");
		//test it's valid
		try {
		  targetUriObj = new URI(targetUri);
		} catch (Exception e) {
		  throw new ServletException("Trying to process targetUri init parameter: "+e,e);
		}
		targetHost = URIUtils.extractHost(targetUriObj);
	}
	
	public static void main(String[] args) throws Exception {
		
	       Server server = new Server(null != System.getProperty("port") ? Integer.valueOf(System.getProperty("port") ) : 9888);
	        server.addConnector(new ServerConnector(server));
	        ConnectHandler proxy = new ConnectHandler();
	        (new ServletContextHandler(proxy, "/", ServletContextHandler.NO_SESSIONS)).addServlet(new ServletHolder(BerylSysProxyServlet.class), "/");
	        


	        
	        HandlerCollection handlerCollection = new HandlerCollection();
	        handlerCollection.setHandlers(new Handler[] { proxy});
	        
	        server.setHandler(handlerCollection);
	        
	        server.start();		
	}
}
