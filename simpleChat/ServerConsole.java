import java.util.Scanner;
import common.*;

public class ServerConsole implements ChatIF{
	
	final public static int DEFAULT_PORT = 5555;
	EchoServer server;
	Scanner fromConsole; 
	
	public ServerConsole(int port) {
		server = new EchoServer(port, this);	    
		// Create scanner object to read from console
		fromConsole = new Scanner(System.in);
	}
	
	public void accept() 
	  {
	    try
	    {

	      String message;

	      while (true) 
	      {
	        message = fromConsole.nextLine();
	        server.handleMessageFromServerUI(message);
	      }
	    } 
	    catch (Exception ex) 
	    {
	      System.out.println
	        ("Unexpected error while reading from console!");
	    }
	  }

	public void display(String message) {
		if(message.startsWith("#")) {
			  String command = message;
			  int count = 1;
			  for(int i = 1; i<command.length(); i++) {
				  if(command.charAt(i) != '#') {
					  count++;
				  } else {
					  break;
				  }
			  }
			  String loginID = command.substring(1, count);
			  message = command.substring(count+1, command.length());
		      System.out.println(loginID+"> " + message);
		  } else {
		      System.out.println("> " + message);
		  }
	}
	
	public static void main(String[] args) 
	  {
	    int port = 0; //Port to listen on
	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(Throwable t)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
	    ServerConsole console = new ServerConsole(port);
	    console.accept();
	  }

}
