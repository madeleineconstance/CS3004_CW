import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.io.*;

/* Taken from Lab ActionServer by Simon Taylor 
/* Further modifications made to meet the assignment brief 
          Maddy Gardner Novemeber 2021 */

public class SharedActionState{
	
	private SharedActionState mySharedObj;
	private String myThreadName;
	private double clientA;
	private double clientB;
	private double clientC;
	private double value;
	private boolean accessing=false; // true a thread has a lock, false otherwise
	private String accessedThread;
	private int threadsWaiting=0; // number of waiting writers

// Constructor	
	
    private static final int WAITING = 0;
    private static final int ADD = 1;
    private static final int SUBTRACT = 2;
    private static final int TRANSFER_AMOUNT = 3;
    private static final int TRANSFER_ACCOUNT = 4;

    
    private int state = WAITING;
	
	
	SharedActionState(double sharedClientA, double sharedClientB, double sharedClientC) {
		clientA = sharedClientA;
		clientB = sharedClientB;
		clientC = sharedClientC;
	}

//Attempt to aquire a lock
	
	  public synchronized void acquireLock() throws InterruptedException{
	        Thread me = Thread.currentThread(); // get a ref to the current thread
	        System.out.println(me.getName()+" is attempting to acquire a lock!");	
	        ++threadsWaiting;
		    while (accessing) {  // while someone else is accessing or threadsWaiting > 0
		    	if (!checkLock()){
		    		System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
				      //wait for the lock to be released - see releaseLock() below
				      wait();
		    	} else {
		    		break;
		    	}
		    }
		    // nobody has got a lock so get one
		    --threadsWaiting;
		    accessing = true;
		    accessedThread = me.getName();
		    System.out.println(me.getName()+" got a lock!"); 
		  }

		  // Releases a lock to when a thread is finished
		  
		  public synchronized void releaseLock() {
			  //release the lock and tell everyone
		      accessing = false;
		      notifyAll();
		      Thread me = Thread.currentThread(); // get a ref to the current thread
		      System.out.println(me.getName()+" released a lock!");
		  }
		  
		public synchronized boolean checkLock() {
			Thread me = Thread.currentThread(); // get a ref to the current thread
			System.out.println(me.getName() + " checking if it has already acquired a lock");
			if (accessedThread.equals(me.getName())){
				return true;
			}
			return false;
		}
	
		
    /* The processInput method */

	public synchronized String processInput(String myThreadName, String theInput) {
    		System.out.println(myThreadName + " received "+ theInput);
    		String theOutput = null;
    		switch (state){
    		case WAITING:
    			System.out.println(theInput);
    			if (theInput.equalsIgnoreCase("Subtract")) {
    				state = SUBTRACT;
    				theOutput = "How much?";
    			}
    			else if (theInput.equalsIgnoreCase("Add")){
    				System.out.println("Hello");
    				theOutput = "How much?";
    				state = ADD;
    			}
    			else if (theInput.equalsIgnoreCase("Transfer")) {
    				state = TRANSFER_AMOUNT;
    				theOutput = "How much?";
    			}
    			else { //incorrect request
        			theOutput = "received incorrect request - only understand \"Subtract\" , \"Add\" , \"Transfer\"";
    		
        		}
    			break;
    		case SUBTRACT:
    			value = Double.parseDouble(theInput);
    			if (myThreadName.equals("ActionServerThread1")){
        			theOutput = Subtract_money("A", value);
        			
        		}
        		else if (myThreadName.equals("ActionServerThread2")){
        			theOutput = Subtract_money("B", value);
        		}
        		else {
        			theOutput = Subtract_money("C", value);
        		}
    		
    			state = WAITING;
    			break;
    		case ADD:
    			value = Double.parseDouble(theInput);
    			if (myThreadName.equals("ActionServerThread1")){
        			theOutput = Add_money("A", value);
        			
        		}
        		else if (myThreadName.equals("ActionServerThread2")){
        			theOutput = Add_money("B", value);
        		}
        		else {
        			theOutput = Add_money("C", value);
        		}
    			state = WAITING;
    			break;
    		case TRANSFER_AMOUNT:
    			value = Double.parseDouble(theInput);
    			theOutput ="To which Client: clientA, clientB, clientC";
    			state = TRANSFER_ACCOUNT;
    			break;
    		case TRANSFER_ACCOUNT:
    			if (theInput.equalsIgnoreCase("clientA")){
    				if (myThreadName.equals("ActionServerThread1")){
    					theOutput = Transfer_money("A", "A", value);	
            		}
            		else if (myThreadName.equals("ActionServerThread2")){
            			theOutput = Transfer_money("B", "A", value);	
            		}
            		else {
            			theOutput = Transfer_money("C", "A", value);	
            		}
    				state = WAITING;
    			}
    			else if (theInput.equalsIgnoreCase("clientB")){
    				if (myThreadName.equals("ActionServerThread1")){
    					theOutput = Transfer_money("A", "B", value);	
            		}
            		else if (myThreadName.equals("ActionServerThread2")){
            			theOutput = Transfer_money("B", "B", value);	
            		}
            		else {
            			theOutput = Transfer_money("C", "B", value);	
            		}
    				state = WAITING;
    			}
    			else if (theInput.equalsIgnoreCase("clientC")){
    				if (myThreadName.equals("ActionServerThread1")){
    					theOutput = Transfer_money("A", "C", value);	
            		}
            		else if (myThreadName.equals("ActionServerThread2")){
            			theOutput = Transfer_money("B", "C", value);	
            		}
            		else {
            			theOutput = Transfer_money("C", "C", value);	
            		}
    				state = WAITING;
    			}
    			else {
    				theOutput = myThreadName + " received incorrect request - only understand \"clientA\", \"clientB\", \"clientC\"";
    			}

    			break;
    		}
     		//Return the output message to the ActionServer
    		System.out.println(theOutput);
    		return theOutput;
    	}


	private String Add_money(String client, double value){
		String theOutput;
		if (client == "A"){
			clientA = clientA + value;
			theOutput = value + " has been added to your account. Making your total = " + clientA;
		}
		else if (client == "B"){
			clientB = clientB + value;
			theOutput = value + " has been added to your account. Making your total = " + clientB;
		}
		else {
			clientC = clientC + value;
			theOutput = value + " has been added to your account. Making your total = " + clientC;
		}
		return theOutput;
	}
		
		private String Subtract_money(String client, double value){
			String theOutput;
			if (client == "A"){
				clientA = clientA - value;
				theOutput = value + " has been subtracted from your account. Making your total = " + clientA;
			}
			else if (client == "B"){
				clientB = clientB - value;
				theOutput = value + " has been subtracted from your account. Making your total = " + clientB;
			}
			else {
				clientC = clientC - value;
				theOutput = value + " has been subtracted from your account. Making your total = " + clientC;
			}
			return theOutput;
		}
		private String Transfer_money(String accountA, String accountB, double value){
			Add_money(accountB, value);
			return Subtract_money(accountA, value);
		}
}

