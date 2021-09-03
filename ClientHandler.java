
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;

/**
* A helper server class: an object of this class is created for
* each client connection, and handles that client's communication
* - the run method itself listens for data coming in from the 
*   client process, and invokes server code to relay it to others
* - the send method is called from the server code to send to this
*   client
**/
public class ClientHandler implements Runnable
{
   private RelayServer server;
   private Socket mySocket;
   private BufferedReader nwin;
   private BufferedWriter nwout;
   private boolean debug = false;

   /**
   * Constructor
   **/
   public ClientHandler(RelayServer s, Socket c)
   {
      server = s; mySocket = c;
   }

   /**
   * Send the message to the connected client
   * @param message is the String message to send
   **/
   public void send(String message)
   {
      if (debug) System.out.println("Sending to client: (" + message + ")");
      try {
         nwout.write(message+"\n");
         nwout.flush();
      } catch (IOException e) {
         System.err.println("Error sending: " + e);
      }
   }

   /**
   * Thread entry point
   **/
   public void run()
   {
      String inputLine;
      try {
         nwout = new BufferedWriter(new OutputStreamWriter(
                                    mySocket.getOutputStream()));
         nwin = new BufferedReader(new InputStreamReader(
                                   mySocket.getInputStream()));
      } catch (IOException e) {
         System.err.println("Error creating reader or writer: " + e);
         return;
      }
      while (!mySocket.isInputShutdown()) {
         try {
            inputLine = nwin.readLine();
         } catch (IOException e) {
            System.err.println("Error receiving: " + e);
            continue;
         }
         if (inputLine != null) {
            if (debug) 
               System.out.println("Client incoming: (" + inputLine + ")");
            server.relayMessage(this,inputLine);
         } else {
            if (debug) System.out.println("Client incoming is null!");
            break; // isInputShutdown doesn't seem to end loop, but this will
         }
      }
      server.remove(this); // remove this client
      if (debug) System.out.println("Client connection closed");
   }

} // end class

