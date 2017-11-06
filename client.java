/* References: https://stackoverflow.com/questions/22719106/java-client-server-chatting-program
   Name: Subah Mehrotra
   Course: SENG 360
*/


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.*;

public class client implements Runnable {

  BufferedReader br1, br2;
  PrintWriter pr1;
  Socket socket;
  Thread t1, t2;
  String in = "", out = "";
  static String CON = "", INTE = "", AUTH = "";
  static boolean C, I, A = false;

  public static boolean Authenticate(String username, String password){
    if (username.equals("seng360")){
      if(password.equals("assignment3")){
        return true;
      }
    }
    return false;
  }

  public static void checkAuthentication()throws java.io.IOException{
    BufferedReader usr = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader pass = new BufferedReader(new InputStreamReader(System.in));
    int attempts = 3;
    String auth1, auth2;
    while(attempts > 0){
      System.out.println("Enter username");
      auth1 = usr.readLine();
      System.out.println("Enter password");
      auth2 = pass.readLine();
      if(Authenticate(auth1, auth2)){
        new client();
        break;
      }else{
        attempts--;
        System.out.println("Invalid Attempt. " +attempts+ " attempts remaining!");
      }
    }
    if(attempts == 0)
    System.out.println("You are not authorized to log in.");
  }

public client() {
    try {
        t1 = new Thread(this);
        t2 = new Thread(this);
        socket = new Socket("localhost", 5000);
          t1.start();
          t2.start();
          System.out.println("Client is connected to server. Start chatting. Send 'END' to end the chat.");
    } catch (Exception e) {
    }
}

public static void CIA()throws java.io.IOException{
  BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
  BufferedReader inte = new BufferedReader(new InputStreamReader(System.in));
  BufferedReader auth = new BufferedReader(new InputStreamReader(System.in));
  System.out.println("Do you want Confidentiality? (y/n)");
  CON = con.readLine();
  System.out.println("Do you want Integrity? (y/n)");
  INTE = inte.readLine();
  System.out.println("Do you want Authentication? (y/n)");
  AUTH = auth.readLine();
  if(CON.equals("y"))
    C = true;
  if(INTE.equals("y"))
    I = true;
  if(AUTH.equals("y"))
    A = true;
}


public void run() {

    try {
        if (Thread.currentThread() == t2) {
            do {
                br1 = new BufferedReader(new InputStreamReader(System.in));
                pr1 = new PrintWriter(socket.getOutputStream(), true);
                in = br1.readLine();
                pr1.println(in);
            } while (!in.equals("END"));
        } else {
            do {
                br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = br2.readLine();
                System.out.println("Server says : : : " + out);
            } while (!out.equals("END"));
        }
    } catch (Exception e) {
    }

 }

 public static void main(String[] args)throws java.io.IOException{
   CIA();
   if(A == true){
     checkAuthentication();
   }
   else{
     new client();
   }
 }
}
