// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI;
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
    sendToServer("#login "+loginID);
    this.loginID = loginID;
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
      clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.startsWith("#")) {
    	  handleCommand(message);
      }else {
    	  sendToServer(message);  
      }
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  /**
   * this method handles commands sent to the server.
   * @param command
   */
  private void handleCommand(String command) {
	  //sets up for commands that take variables
	  int count = 0;
	  boolean hasWhitespace = false;
	  for(int i = 0; i<command.length(); i++) {
		  if(command.charAt(i) != ' ') {
			  count++;
		  } else {
			  hasWhitespace = true;
			  break;
		  }
	  }
	  String setCommand = "";
	  String setCommandParam = "";
	  if(hasWhitespace == true) {
		  setCommand = command.substring(0, count);
		  setCommandParam = command.substring(count+1, command.length());
	  }
	  //commands here
	  if(command.equals("#quit")) {
		  quit();
	  } else if(command.equals("#logoff")) {
		  try {
			closeConnection();
			clientUI.display("Successfully logged off");
		} catch (IOException e) { }
	  } else if(setCommand.equals("#sethost")) {
		  if(isConnected()) {
			  clientUI.display("Please log off first.");
		  } else {
			  String host = setCommandParam;
			  setHost(host);
			  clientUI.display("Set host to: "+host);
		  }
	  } else if(setCommand.equals("#setport")) {
		  if(isConnected()) {
			  clientUI.display("Please log off first."); 
		  } else {
			  try {
				  int port = Integer.parseInt(setCommandParam);
				  if(port < 0 || port>65353) {
					  clientUI.display("Not a port number");
				  } else {
					  setPort(port);
					  clientUI.display("Set port number to: "+port);
				  }
			  } catch (NumberFormatException ne) {
				  clientUI.display("Not a port number");
			  }
		  }
	  } else if(command.equals("#sethost")) {
		  clientUI.display("No parameter added");
		  
	  } else if(command.equals("#setport")) {
		  clientUI.display("No parameter added");
		  
	  } else if(command.equals("#login")) {
		  if(!isConnected()) {
			  try {
					openConnection();
					sendToServer("#login "+loginID);
					clientUI.display("Successfully logged in");
				  } catch (IOException e) {
					clientUI.display("Failed to log in");
				  }
		  } else {
			  clientUI.display("Already Logged in");
		  }
		  
	  } else if(command.equals("#gethost")) {
		  String host = getHost();
		  clientUI.display("Host is currently set as: "+host);
		  
	  } else if(command.equals("#getport")) {
		  int port = getPort();
		  clientUI.display("Port is currently set as: "+port);
	  } else {
		  clientUI.display("This is not a command.");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  	/**
	* Hook method called each time an exception is thrown by the client's
	* thread that is waiting for messages from the server. The method may be
	* overridden by subclasses.
	* 
	* @param exception
	*            the exception raised.
	*/
  	public void connectionException(Exception exception) {
  		clientUI.display("The server has shut down.");
  		System.exit(0);
  	}
  	/**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	public void connectionClosed() {
  		clientUI.display("connection with server closed.");
  	}
}
//End of ChatClient class
