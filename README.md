# MessageRelayer
Simple message relay server, client, and combo, in Java

Code in this directory implements a network message relayer for use in a
multiprocess system such as a multiplayer game or other application. 
This code supports two different usage models:

1. Creating and using a standalone server with clients that connect to it.
In this mode the "ClientApp" class is the client application and the
"RelayServer" is the server application. 
- in one terminal or machine, run "java RelayServer 9001" (or other port #)
- in other terminals or machines, run "java ClientApp hostname 9001",
  where hostname is the host machine name your server is running on, which
  could be "localhost" if it is local, and it could be the IP address if
  you want
- then enter strings in the client(s) and watch them arrive at the other
  clients!

2. Including the server ability in the client app, and starting up one
of the clients as the hosting "server". This way a separate server is
not needed to be run. In this mode, "ComboApp" is the client (and server)
application.
- in one terminal, start the first client as the server/client, doing
  "java ComboApp server 9001". This client will be both a user app and
  the server.
- then in other terminal or machines, start clients that connect to the
  first client; do "java ComboApp hostname 9001", where hostname is the
  name where the first client+server is running (just like in mode 1)
- then type in strings and watch them go to other clients!
- note that you could also use the "ClientApp" for any of the non-server
  clients.

All the other classes are helpers to either the client and/or server. You
can look at the code and read the code comments for more info. You can also
run javadoc to generate class documentation.

