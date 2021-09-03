
/**
* Callback interface for message relay client app
* - some application class needs to implement this interface
* - the receiveMessage() method will be called each time a
*   message is received from the server
* - the client app should not perform long processing in this
*   method or anything it calls, since it is likely not in an
*   application thread; rather the message should be handled,
*   data extracted, and anything else that can tell the application
*   to deal with it, but then this method should return
**/
public interface RelayReceiver
{
   public void receiveMessage(String msg);
}

