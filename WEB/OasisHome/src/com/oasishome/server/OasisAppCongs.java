package com.oasishome.server;
import javax.servlet.ServletContextEvent;  
import javax.servlet.ServletContextListener;  
public class OasisAppCongs implements ServletContextListener {  
  
    @Override  
    public void contextInitialized(ServletContextEvent sce) {  
        /* Sets the context in a static variable */  
        DBUtil.setServletContext(sce.getServletContext());  
        
        // start the udpater
     //   OasisUpdater.startUpdater();
    }  
  
    @Override  
    public void contextDestroyed(ServletContextEvent sce) { 
    	System.out.println("stoppping");
    //	OasisUpdater.stopUpdater();
    	
    }  
  
}  