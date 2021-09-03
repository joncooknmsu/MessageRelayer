
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

/**
* Example Relay app that is a combined client and server. This 
* allows the client app to directly connect to another client app
* without running a server somewhere. This can be an easier model
* for users to start up. Clients start in two modes: one has to 
* be started with an command line argument of "host"; this one will
* act as the server for the others. Then the other clients have to
* start up with the IP address of the host client.
*
* It allows the user to type in messages
* and then it sends these to the server, which relays them to all other
* clients. A thread is used to asynchronously receive messages from the 
* server while waiting for user input, but other read mechanisms are 
* possible (e.g., polling). 
**/
public class ComboApp implements RelayReceiver
{
   private RelayClient relayer;
   private RelayServer server;
   private boolean debug = false;

   /**
   * Process a message received from the server. This will be called 
   * in the relayer thread and so it should not do something that takes
   * too long, since the relayer thread needs to get back to taking care
   * of incoming messages. This thread should extract the message info and
   * set up something for the main application thread, or some other thread,
   * do use at it runs the app.
   * @param msg is the string message that was received
   **/
   public void receiveMessage(String msg)
   {
      System.out.println("Received: (" + msg + ")");
   }
   
   /**
   * Send the message, either using the local server object if
   * we are the server, or the relay client object if we are not
   **/
   public void sendMessage(String msg)
   {
      if (server != null)
         // if we are the server, send it to our server object
         server.relayMessage(null,msg);
      else
         // else send it through our client message relayer
         relayer.send(msg);
   }
   
   /**
   * Helper method for main, just starts the relay client and connects
   * it to the server
   * @param serverHost is the hostname or IP address (as a string)
   * @param serverPort is the port number the server is using
   **/
   public boolean startRelayService(String serverHost, int serverPort)
   {
      relayer = new RelayClient(this); // needs RelayReceiver object
      if (!relayer.connect(serverHost,serverPort)) {
         System.err.println("Could not connect to server:9001");
         return false;
      }
      if (debug) System.out.println("Connected...");
      // RelayClient object MUST be run in its own thread
      (new Thread(relayer)).start();
      return true;
   }
   
   /**
   * Main application method, called from main.
   **/
   public void doApp()
   {
      // this "main" application simply reads input from stdin
      // and sends it to all other clients
      String line;
      BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
      while (true) {
         System.out.println("Enter a string to send:");
         try {
            line = stdin.readLine();
         } catch (Exception e) {
            System.err.println("Error reading stdin: " + e);
            continue;
         }
         if (line == null || line.equals(""))
            continue;
         if (debug) System.out.println("Sending: (" + line + ")");
         sendMessage(line);
      }
   }

   /**
   * Program entry point. 
   * @param args is either just hostname/IP or hostname/IP and port#
   **/
   public static void main(String[] args)
   {
      int port = 9001;
      String host = "localhost";
      if (args.length > 2) {
         System.out.println("Usage: java ComboApp ['server'|machine-name|IP-address] [port]");
         return;
      } else if (args.length > 0) {
         host = args[0];
      } else if (args.length > 1) {
         port = Integer.parseInt(args[1]);
      }
      ComboApp app = new ComboApp();
      if (host.equals("server")) {
         // we are the server, so start up a server thread
         app.server = new RelayServer(app,port);
         (new Thread(app.server)).start(); 
         System.out.println("Started as server host...");
      } else {
         // we are a client, so connect to server (if fail, exit)
         if (!app.startRelayService(host,port))
            return;
         System.out.println("Started as client...");
      }
      // relay stuff all set up, so do the app
      app.doApp();
   }

} // end class

