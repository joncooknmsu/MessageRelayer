
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;

/**
* Example Relay client code. This is a simple program that demonstrates
* what is expected of a relay client. It allows the user to type in messages
* and then it sends these to the server, which relays them to all other
* clients. A thread is used to asynchronously receive messages from the 
* server while waiting for user input, but other read mechanisms are 
* possible (e.g., polling). 
**/
public class RelayClient implements Runnable
{
   /** connection to server **/
   private Socket mySocket;
   /** incoming network message reader **/
   private BufferedReader nwin;
   /** outgoing network message sender **/
   private BufferedWriter nwout;
   /** callback object for app for when a message is received **/
   private RelayReceiver app;
   private boolean debug = false;

   /**
   * Constructor
   * @param rx is the application's RelayReceiver object
   **/
   public RelayClient(RelayReceiver rx)
   {
      app = rx;
   }

   /**
   * Connect to a relay server.
   * @param serverName is the hostname or IP address of the server
   * @param serverPort is the port number of the server
   * @return true if connection succeeded, false otherwise
   **/
   public boolean connect(String serverName, int serverPort)
   {
      try {
         mySocket = new Socket(serverName, serverPort);
      } catch (IOException e) {
         System.err.println("Error connecting: " + e);
         return false;
      }
      try {
         nwout = new BufferedWriter(new OutputStreamWriter(
                                    mySocket.getOutputStream()));
         nwin = new BufferedReader(new InputStreamReader(
                                   mySocket.getInputStream()));
      } catch (IOException e) {
         System.err.println("Error creating reader or writer: " + e);
         try {
            mySocket.close();
         } catch (Exception ce) {}
         return false;
      }
      return true;
   }

   /**
   * Send a message to the relay server. Called by application.
   * @param message is the message to send
   **/
   public void send(String message)
   {
      try {
         nwout.write(message + "\n"); // protocol is newline-based
         nwout.flush(); // important to flush buffers!
      } catch (IOException e) {
         System.err.println("Error sending: " + e);
      }
   }

   /**
   * Incoming message listening thread entry point. Will receive
   * any incoming messages, and invoke the receiveMessage() handler
   * on the application object (if available)
   **/
   public void run()
   {
      String inputLine;
      while (!mySocket.isInputShutdown()) {
         try {
            inputLine = nwin.readLine();
         } catch (IOException e) {
            System.err.println("Error receiving: " + e);
            continue;
         }
         if (inputLine != null) {
            if (app != null)
               app.receiveMessage(inputLine);
            else
               System.out.println("No App to Receive: (" + inputLine + ")");
         }
      }
      if (debug) System.out.println("Connection closed...");
      try {
         mySocket.close();
      } catch (Exception e) {}
      mySocket = null;
   }

} // end class

