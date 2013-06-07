package eu.optimis.utils.optimislogger;

	     import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
	      
	      public class RestAppender extends AppenderSkeleton
	      {
	          // The iterator across the "clients" Collection must
	          // support a "remove()" method.
	      

	         // private Collection   clients = new LinkedList();
	          private String       uri     =     "";
	         // private ServerSocket listenerSocket;
	         // private Thread       listenerThread;
          
	          public void setUri(String uri)
	          { 
	        	  System.out.println("Uri:"+uri);
	        	  this.uri = uri; 
	          }
	          public String  getUri()          
	          { 
	        	  return this.uri; 
	          }
	      
	          public boolean requiresLayout()
	          { 
	        	  return true; 
	          }
	          
	        // private static final String SERVICE_HOST = "gyver.cs.umu.se";
	        // private static final int SERVICE_PORT = 3000;
	     
	         //Called once all the options have been set.
	          public void activateOptions()
	          { 
	        	  //
	          }
	      
	          //Do the logging
	          public synchronized void append(LoggingEvent event)
	          {   
	              // If this Appender has been closed or if there are no
	              // clients to service, just return.
	        	  
	        	  if (event.getLevel() != DemoLog.DEMO)
        			  return;
	        	  
	        	  try
	        	  {
	        		  // expected format "message;progress;serviceid;done"
	        		  
	        		  
		        	  
		        	  String[] componentparts = event.getLocationInformation().getClassName().split("\\.");
		        	  String component = componentparts[componentparts.length-1];
		        	  
		        	  //uuugly hack for Y1 GUI demo
		        	  if (component.toLowerCase().equals("transferprogressreporter"))
		        	  {
		        		  component = "DataManagerClient";
		        	  }
		        	  //and for this one, Django owes us icecream :)
		        	  if (component.toLowerCase().equals("sdobase"))
		        	  {
		        		  component = "VM Contextualizer";
		        	  }
		        	  
		        	  String[] tempStrings = event.getMessage().toString().split(";");
		        	  
		        	  String operation = "";
		        	  try 
		        	  {
		        		  operation = tempStrings[0];
		        	  } 
		        	  catch (Exception e) 
		        	  {
						// TODO Auto-generated catch block
		        		  //e.printStackTrace();
		        	  }
		        	  
		        	  Double progress = 100.0;
		        	  try 
		        	  {
		        		  String tempProgress = tempStrings[1];
		        		  progress = Double.parseDouble(tempProgress);
		        	  } 
		        	  catch (Exception e) 
		        	  {
		        		  // TODO Auto-generated catch block
		        		  //e.printStackTrace();
		        	  }
		        	  
		        	  String serviceID = "";
		        	  try 
		        	  {
		        		  serviceID = tempStrings[2];
		        		  //System.out.println("serviceID: "+serviceID);
		        	  } 
		        	  catch (Exception e) 
		        	  {
						// TODO Auto-generated catch block
		        		  //e.printStackTrace();
		        	  }
		        	  
		        	  boolean done = false;
		        	  try 
		        	  {
		        		  String doneString = tempStrings[3];
		        		  done = Boolean.parseBoolean(doneString);
		        		  //System.out.println("done: "+done);
		        	  } 
		        	  catch (Exception e) 
		        	  {
						// TODO Auto-generated catch block
		        		  //e.printStackTrace();
		        	  }
		        	  
		        	  System.out.println("Component: "+component+" Message: "+operation+" Progress:"+progress+" ServiceID:"+serviceID+" Done:"+done);
		        	  System.out.println("URI:"+uri);
		        	  
		              Status statusObj = new Status(component, operation, progress, done);
		              StatusClient statusClient = new StatusClient(uri);

		              statusClient.storeStatus(serviceID, statusObj);
	        	  }
	        	  catch (Throwable t)
	        	  {
            // t.printStackTrace();
            System.out.println("Failed to call StatusClient");
	        	  }        
	          }
	      
	          //called on logger shutdown
	          public synchronized void close()
	          {  
	        	  //
	          }
	          
	      }

