package com.berylsys.proxy;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.proxy.ConnectHandler;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.core.io.ClassPathResource;

public class BerylSysJettyProxy  {

	
	/**
	 * 
	 * Command Line options : -Dtarget=venkat.com:9080 -Dport=8080
	 * 
	 * 
	 */
	public static void main(String[] args) throws Exception {
        Server server = new Server(null != System.getProperty("port") ? Integer.valueOf(System.getProperty("port") ) : 9988);
        server.addConnector(new ServerConnector(server));
        ConnectHandler proxy = new ConnectHandler();
        (new ServletContextHandler(proxy, "/", ServletContextHandler.NO_SESSIONS)).addServlet(new ServletHolder(BerylSysProxyServlet.class), "/");
        

        
	    WebAppContext webAppContext = new WebAppContext();
		webAppContext.setServer(server);
		webAppContext.setContextPath("/swaggerui");
		webAppContext.setWelcomeFiles(new String[]{ "index.html" });
		webAppContext.setResourceBase(new ClassPathResource("webapp/swaggerui").getURI().toString());
        
        
        
        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[] {webAppContext, proxy});
        
        server.setHandler(handlerCollection);
        
        server.start();
	}

	
	public static class BerylSysProxyServlet extends ProxyServlet {
		private String target = System.getProperty("target");
		@Override
		protected String rewriteTarget(HttpServletRequest clientRequest) {
			
		    String url = "http://" + target + clientRequest.getRequestURI();;

	        String query = clientRequest.getQueryString();
	        url = query != null ? url += "?" + query.trim()  : url;
	        String ret = URI.create(url).normalize().toString();
	        System.out.println(" proxy rewrite => " + ret);
	        return 		ret;
			
		}
		
	}
	
}
