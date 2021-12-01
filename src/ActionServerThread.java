
import java.net.*;
import java.io.*;

/* Taken from Lab ActionServer by Simon Taylor 
/* Further modifications made to meet the assignment brief 
          Maddy Gardner Novemeber 2021 */

public class ActionServerThread extends Thread {

	
  private Socket actionSocket = null;
  private SharedActionState mySharedActionStateObject;
  private String myActionServerThreadName;
//  private double mySharedVariable;
   
  //Setup the thread
  	public ActionServerThread(Socket actionSocket, String ActionServerThreadName, SharedActionState SharedObject) {
	
//	  super(ActionServerThreadName);
	  this.actionSocket = actionSocket;
	  mySharedActionStateObject = SharedObject;
	  myActionServerThreadName = ActionServerThreadName;
	}

  public void run() {
    try {
      System.out.println(myActionServerThreadName + "initialising.");
      PrintWriter out = new PrintWriter(actionSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(actionSocket.getInputStream()));
      String inputLine, outputLine;


      while ((inputLine = in.readLine()) != null) {
    	  // Get a lock first
    	  try { 
    		  mySharedActionStateObject.acquireLock();  
    		  outputLine = mySharedActionStateObject.processInput(myActionServerThreadName, inputLine);
    		  out.println(outputLine);
    		  if (outputLine.contains("Making your total")){
    			  mySharedActionStateObject.releaseLock();  
    		  }
    	  } 
    	  catch(InterruptedException e) {
    		  System.err.println("Failed to get lock when reading:"+e);
    	  }
      }

       out.close();
       in.close();
       actionSocket.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}