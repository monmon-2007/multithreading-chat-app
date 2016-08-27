/*
	Mina Gadallah
	Internet Technology
*/


import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.Semaphore;


public class Server implements java.io.Serializable {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maxClientsCount = 20;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

	private static LinkedList<String> history = new LinkedList<String>();
  private static LinkedList<String> ll = new LinkedList<String>();
	private static LinkedList<String> activeUser = new LinkedList<String>();
	private static LinkedList<String> privateUser = new LinkedList<String>();

  public static void main(String args[]) {

    // The default port number.
    int portNumber = 8888;
    if (args.length < 1) {
      System.out.println("Usage: java Server <portNumber>\n"
          + "Now using port number=" + portNumber);
    }
    else
    {
      portNumber = Integer.valueOf(args[0]).intValue();
    }


    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }


    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null)
          {
            (threads[i] = new clientThread(clientSocket, threads, ll, history, activeUser, privateUser)).start();
            break;
          }
        }
        if (i >= maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}



class clientThread extends Thread
{

  private String clientName = null;
  private String line = null;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;
  private LinkedList<String> ll;
  private LinkedList<String> history;
  private LinkedList<String> activeUser;
  private LinkedList<String> privateUser;
  private LinkedList<clientThread> privateUser1 = new LinkedList<clientThread>();

  String usr;
  int check=0;

  	public clientThread(Socket clientSocket, clientThread[] threads, LinkedList<String> ll, LinkedList<String> history, LinkedList<String> activeUser, LinkedList<String> privateUser ) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
    this.ll = ll;
    this.history = history;
    this.activeUser = activeUser;
    this.privateUser = privateUser;
    String ANSI_RESET = "\u001B[0m";


  }

  public void run()
  {
	 int maxClientsCount = this.maxClientsCount;
     clientThread[] threads = this.threads;
 	 Semaphore semaphore = new Semaphore(0);
     Semaphore mutex = new Semaphore(1);
	 final String ANSI_RESET = "\u001B[0m";
	 String[] colors = new String[20];
	 String str;
	 String name;

	 colors[0] = "\u001B[31m";colors[10] = "\u001B[44m";
	 colors[1] = "\u001B[32m";colors[11] = "\u001B[45m";
	 colors[2] = "\u001B[33m";colors[12] = "\u001B[46m";
	 colors[3] = "\u001B[34m";colors[13] = "\u001B[31m";
	 colors[4] = "\u001B[35m";colors[14] = "\u001B[32m";
	 colors[5] = "\u001B[36m";colors[15] = "\u001B[33m";
	 colors[6] = "\u001B[37m";colors[16] = "\u001B[34m";
	 colors[7] = "\u001B[41m";colors[17] = "\u001B[35m";
	 colors[8] = "\u001B[42m";colors[18] = "\u001B[36m";
	 colors[9] = "\u001B[43m";colors[19] = "\u001B[34m";



    try
    {
      /*
       * Create input and output streams for this client.
       */
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());

      while (true)
      {
       		os.println("Enter your name.");
       		name = is.readLine().trim();

        for(int i=0; i<=ll.size()-1; i++)
		{
			usr=ll.get(i);
			while(usr.equals(name))
			{
				os.println("user Exists");
				check =1;
				name = is.readLine().trim();
			}
		}
		ll.add(name);		/* add the name to a linkList */
		activeUser.add(name);


        if (name.indexOf('@') == -1)
        {
          	break;
        }
        else
        {
          os.println("The name should not contain '@' character.");
        }
      }

      /* Welcome the new the client. */
      os.println("Welcome " + name + " to our chat room.\nTo leave enter @exit in a new line.");


		/* Chat History printing */

          for(int f=0; f <= history.size()-1; f++)
          {
			  os.println(history.get(f));
		  }

		int curr = 0;

      synchronized (this)
      {
        for (int i = 0; i < maxClientsCount; i++)
        {
          if (threads[i] != null && threads[i] == this)
          {
            clientName = name;
            curr=i;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++)
        {
          if (threads[i] != null && threads[i] != this)
          {
			  threads[i].os.println("*** A new user " + name + " entered the chat room !!! ***");
          }
        }
      }

      /* Start the conversation. */
      while (true)
      {
		 privateUser1.clear();
         line = is.readLine();
        if (line.startsWith("@exit"))
        {
			activeUser.remove(this.clientName);
          	break;
        }


        if(line.startsWith("@who"))
		{
			for(int k=0; k <= activeUser.size()-1; k++)
			{
				os.println(activeUser.get(k));
			}
			os.println("*****End of Active Users*****");
		}


        /* If the message is private sent it to the given client. */

        else if (line.startsWith("@private"))
        {

          String[] words = line.split(" ", 2);
          if (words.length > 1 && words[1] != null)
          {
            words[1] = words[1].trim();
            int conv = 0;
            for(int k=0; k <= activeUser.size()-1; k++)
      			{
      				if(words[1].equals(activeUser.get(k)))
              		{
                		conv = 1;
              		}
      			}
      			while(conv != 1)
      			{
					os.println("wait while the private conversation ends!");
					for(int k=0; k <= activeUser.size()-1; k++)
					{
					      if(words[1].equals(activeUser.get(k)))
					      {
					  	 		conv = 1;
					      }
      				}

					try
					{
					  Thread.sleep(2000);
					}
					catch(InterruptedException ex)
					{
					  System.out.println("fs");//do stuff
					}
				}//while conv

            if (!words[1].isEmpty() && conv == 1)
            {
             synchronized (this)
             {
                for (int i = 0; i < maxClientsCount; i++)
                {
                  if (threads[i] != null && threads[i] != this && threads[i].clientName != null && threads[i].clientName.equals(words[1]))
                  {
					  	  privateUser1.add(threads[i]);
						  activeUser.remove(threads[i].clientName);



						  os.println("**Private conversation has started!");

						  String lol = "@end private";

						  while(!line.equals(lol.toString()))
						  {
							  line = is.readLine();
							  if (line.startsWith("@private"))
							  {
								  String[] words2 = line.split(" ", 2);
								  words2[1] = words2[1].trim();
								  for (int kg = 0; kg < maxClientsCount; kg++)
								  {
								  		if (threads[kg] != null && threads[kg] != this && threads[kg].clientName != null && threads[kg].clientName.equals(words2[1]))
								  		{
											os.println("**Private conversation has started!");
											privateUser1.add(threads[kg]);
											activeUser.remove(threads[kg].clientName);
										}

							  	  }

							  	  line = is.readLine();

							  }




							  if(!line.equals(lol.toString()))
							  {
                    		  		for(int ff=0; ff<privateUser1.size(); ff++)
							  			{

											if(line.startsWith("@end"))//***********
											{

												String[] words3 = line.split(" ", 2);
												if(privateUser1.get(ff).clientName.equals(words3[1]))
												{
													os.println(privateUser1.get(ff).clientName+ "has left the Private conversation!");
													privateUser1.remove(ff);
													activeUser.add(words3[1]);
												}
											}
											else
							  				privateUser1.get(ff).os.println("<" + name + "> " + line);
						  				}
							  }

							  if(privateUser1.size()==0)
							  {
									os.println("No users in this conversation.. **you're back to public**");
									break;
							   }

						  }//while keeps conversation going!

						  //remove all private users



						  if(line.equals(lol.toString()))
						  {
							  os.println("Private conversation has ended with all users**");
							  for(int ff=0; ff<privateUser1.size(); ff++)
							  {
								  activeUser.add(privateUser1.get(ff).clientName);
							  }
						  }

                     break;
                  }
                }
              }

            }else os.println("user is busy or dosen't exist");
          }
        }



        else
        {
          /* The message is public, broadcast it to all other clients. */

          synchronized (this)
          {
			  int colorMe = 0;
			  int once=0;
			  String usr2 = "";
            for (int i = 0; i < maxClientsCount; i++)
            {


              if (threads[i] != null && threads[i].clientName != null)
              {
				  for(int j=0; j<=ll.size()-1; j++)
				  		{
				  			usr2=ll.get(j);

				  			if(usr2.equals(name))
				  			{
				  				colorMe = j;
				  			}
			}
				str = colors[colorMe] + "<" + name + "> " + line + ANSI_RESET;
				while(once==0)
				{
					history.add(str);
					once =1;
				}
                threads[i].os.println(colors[colorMe] + "<" + name + "> " + line + ANSI_RESET );			//*********************** look at This *************
              }
            }
          }
        }
      }
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].clientName != null) {
            threads[i].os.println("*** The user " + name
                + " is leaving the chat room !!! ***");
          }
        }
      }
      os.println("*** Bye " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      /*
       * Close the output stream, close the input stream, close the socket.
       */
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}