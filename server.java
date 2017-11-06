/* References: https://stackoverflow.com/questions/22719106/java-client-server-chatting-program
   Name: Subah Mehrotra
   Course: SENG 360
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class server implements Runnable {

ServerSocket serversocket;
BufferedReader br1, br2;
PrintWriter pr1;
Socket socket;
Thread t1, t2, thread;
String in="",out="";

//server
public server() {
    try {
          t1 = new Thread(this);
          t2 = new Thread(this);
          serversocket = new ServerSocket(5000);
          System.out.println("Server is waiting. . . . ");
          /*while(true){
            socket = serversocket.accept();
            thread = new Thread(this);
            thread.start();
          }*/
          socket = serversocket.accept();
          System.out.println("Client connected with Ip " + socket.getInetAddress().getHostAddress());
          t1.start();
          t2.start();

          //reconnect();

    } catch (Exception e) {
    }
 }


//reconnect: Reconnects the client if the connection is lost
public void reconnect(){
  try {
    t1 = new Thread(this);
    t2 = new Thread(this);
    System.out.println("Server is waiting. . . . ");
    socket = serversocket.accept();
    System.out.println("Client connected with Ip " + socket.getInetAddress().getHostAddress());
    t2.start();
    t1.start();
  } catch (Exception e){
  }
}

//Runnable
 public void run() {
    try {
        if (Thread.currentThread() == t1) {
            do {
                br1 = new BufferedReader(new InputStreamReader(System.in));
                pr1 = new PrintWriter(socket.getOutputStream(), true);
                in = br1.readLine();
                pr1.println(in);
            } while (!in.equals("END"));
        } else if(Thread.currentThread() == t2) {
            do {
                br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = br2.readLine();
                if(out == null){
                  System.out.println("Client is disconnected");
                  reconnect();
                  break;
                }
                System.out.println("Client says : : : " + out);
            } while (!out.equals("END"));
        }
    } catch (Exception e) {
    }
}

//main
public static void main(String[] args) {
    new server();
}
}
