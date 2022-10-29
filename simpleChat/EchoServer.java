// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  ChatIF serverUI;
  
  private boolean stopped;
  private boolean closed;
 
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    try 
    {
      listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      serverUI.display("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client){
	  String command = msg.toString();
	  if(command.startsWith("#")) {
		  serverUI.display("Message received: " + msg + " from " + client.getInfo("loginID"));
		  if(client.getInfo("loginID") != null) {
			  try {
				serverUI.display("ERROR - already has login ID");
				client.sendToClient("ERROR - already has login ID");
				client.close();
				return;
			} catch (IOException e) {
				return;
			}
		  }
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
		  String setCommandParam = "";
		  if(hasWhitespace == true) {
			  setCommandParam = command.substring(count+1, command.length());
		  }
		  client.setInfo("loginID", setCommandParam);
		  serverUI.display(setCommandParam+" has logged on.");
		  this.sendToAllClients(setCommandParam+" has logged on.");
	  } else {
		  serverUI.display("Message received: " + msg + " from " + client.getInfo("loginID"));
		  this.sendToAllClients("#"+client.getInfo("loginID").toString()+"#"+msg);
	  }
  }
  
  public void handleMessageFromServerUI(Object msg){
	  String message = msg.toString();
	  if(message.startsWith("#")) {
		  handleCommand(message);
	  } else {
		  serverUI.display("#"+"SERVER MSG"+"#"+msg.toString());
		  this.sendToAllClients("#"+"SERVER MSG"+"#"+msg);
	  }
  }
  
  private void handleCommand(String command) {
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
	  if(command.equals("#quit")) {
		  try {
			close();
			System.exit(0);
		} catch (IOException e) {}
	  } else if(command.equals("#stop")) {
		  stopListening();
	  } else if(command.equals("#close")) {
		  if(closed == true) {
			  serverUI.display("Server already closed");
		  } else {
			  try {
				  close();
			  } catch (IOException e) {}
			  serverUI.display("Server disconnected clients.");
		  }  
	  } else if(setCommand.equals("#setport")) {
		  if(stopped == false) {
			  serverUI.display("Please stop the server first."); 
		  } else {
			  try {
				  int port = Integer.parseInt(setCommandParam);
				  if(port < 0 || port>65353) {
					  serverUI.display("Not a port number");
				  } else {
					  setPort(port);
					  serverUI.display("Set port number to: "+port);
				  }
			  } catch (NumberFormatException ne) {
				  serverUI.display("Not a port number");
			  }
		  }
	  } else if(command.equals("#setport")) {
		  serverUI.display("No parameter added");
	  } else if(command.equals("#start")) {
		  if(stopped == true) {
			  try {
				listen();
			} catch (IOException e) {}
		  } else {
			  serverUI.display("server already started.");
		  }
	  } else if(command.equals("#getport")) {
		  int port = getPort();
		  serverUI.display("Server port is currently set as: "+port);
	  } else {
		  serverUI.display("This is not a command.");
	  }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  serverUI.display
      ("Server listening for connections on port " + getPort());
    stopped = false;
    closed = false;
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  serverUI.display
      ("Server has stopped listening for connections.");
    stopped = true;
  }
  
  /**
   * Hook method called when the server is clased.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  protected void serverClosed() 
  {
	  closed = true;
  }
  /**
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client){
	  serverUI.display("A new client has connected to the server.");
  }
  
  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client){
	  serverUI.display(client.getInfo("loginID")+" has disconnected.");
  }
  
  /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception){
	  serverUI.display("connection lost: "+client.getInfo("loginID"));
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
}
//End of EchoServer class
