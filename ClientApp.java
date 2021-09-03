
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
* Example Client Application that uses the relay client code and 
* interacts with the server and other clients
**/
public class ClientApp implements RelayReceiver
{
   private RelayClient relayer;
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
      System.out.println("Connected...");
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
         relayer.send(line);
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
         System.out.println("Usage: java ClientApp [machine-name|IP-address] [port]");
         return;
      } else if (args.length > 0) {
         host = args[0];
      } else if (args.length > 1) {
         port = Integer.parseInt(args[1]);
      }
      ClientApp app = new ClientApp();
      app.startRelayService(host,port);
      app.doApp();
   }
} // end class

