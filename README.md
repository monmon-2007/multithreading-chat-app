 Multithreaded Chat Room 
Made at:  	Rutgers University 
Professor:  Brian Russell 
Class:   	INTERNET TECHNOLOGY F6 Su16 
Chat Room is a desktop application that allows multiple clients communicating through TCP/IP network connections. It contains of a server and clients. Each client has a different color code. No two clients can have the same name. 
•	Initially all messages entered by any client are public. 
•	Any client can have a private conversation with one or more other active users. 
•	A private conversation is initiated one active client at a time. 
•	An active client can leave any time. 
•	An active client is the target of private communication => tell originator of departure. 
•	Private communication from Client A to Client B does not imply private communication from Client B to Client A. 
•	Private communication does NOT get logged. 
•	Private communication is terminated by the same client that initiated private communication. 
•	End of all private communication from Client A returns Client A to public communication. 
 
 
•	JAVA Programing Language 
•	TCP/IP 
•	Mutex/Semaphore 
•	Socet/IO 
 
    // The default port number. 
    int portNumber = 8888;     if (args.length < 1)  
   { 
      System.out.println("Usage: java Server <portNumber>\n" 
          + "Now using port number=" + portNumber); 
    }     else      { 
      portNumber = Integer.valueOf(args[0]).intValue();     } 
Application Setup 
•	The application Runs on the local host by default. 
•	8888 is the default port. 
•	Any customization must be done in the source code • maxClientsCount = 20; by default. 
•	You may change the load on the server by changing maxClientsCount value. 
 
•	Mina Gadallah @monmon-2007 
 
 
** feedback to be provided by the TA** 
