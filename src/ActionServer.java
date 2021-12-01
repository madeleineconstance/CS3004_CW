import java.net.*;
import java.io.*;

/* Taken from Lab ActionServer by Simon Taylor 
/* Further modifications made to meet the assignment brief 
          Maddy Gardner Novemeber 2021 */

public class ActionServer {
  public static void main(String[] args) throws IOException {

	ServerSocket ActionServerSocket = null;
    boolean listening = true;
    String ActionServerName = "ActionServer";
    int ActionServerNumber = 4455;
    
    double clientA = 1000;
    double clientB = 1000;
    double clientC = 1000;

    //Create the shared object in the global scope...
    
    SharedActionState ourSharedActionStateObject = new SharedActionState(clientA, clientB, clientC);
        
    // Make the server socket

    try {
      ActionServerSocket = new ServerSocket(ActionServerNumber);
    } catch (IOException e) {
      System.err.println("Could not start " + ActionServerName + " specified port.");
      System.exit(-1);
    }
    System.out.println(ActionServerName + " started");

    //Got to do this in the correct order with only four clients!  Can automate this...
    
    while (listening){
      new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread1", ourSharedActionStateObject).start();
      new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread2", ourSharedActionStateObject).start();
      new ActionServerThread(ActionServerSocket.accept(), "ActionServerThread3", ourSharedActionStateObject).start();
      System.out.println("New " + ActionServerName + " thread started.");
    }
    ActionServerSocket.close();
  }
}