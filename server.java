/*
   Name: Subah Mehrotra
   Course: SENG 360
*/

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.concurrent.TimeUnit;
import javax.crypto.*;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Base64.Decoder;
import javax.crypto.Mac;


public class server implements Runnable {

ServerSocket serversocket;
BufferedReader br1, br2;
PrintWriter pr1;
Socket socket;
Thread t1, t2, thread;
String in="",out="";
static String CON = "", INTE = "", AUTH = "";
static boolean C, I, A = false;
String algorithm="AES";
byte[]  key = "!@#$!@#$%^&**&^%".getBytes();
String mac = "!@#$!@#$%";

//server
public server() {
    try {
          t1 = new Thread(this);
          t2 = new Thread(this);
          serversocket = new ServerSocket(5000);
          System.out.println("Server is waiting. . . . ");
          socket = serversocket.accept();
          System.out.println("Client connected with Ip " + socket.getInetAddress().getHostAddress());
          t1.start();
          t2.start();
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

//CIA: Asks the user if the want Confidentiality, Integrity and/or Authentication
public static void CIA()throws java.io.IOException{
  BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
  BufferedReader inte = new BufferedReader(new InputStreamReader(System.in));
  BufferedReader auth = new BufferedReader(new InputStreamReader(System.in));
  System.out.println("PICK SAME CIA DETAILS FOR THE SERVER AND THE CLIENT\n");
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

//encrypt: Encrypt the message
public String encrypt(String data){
  try{
    byte[] dataToSend = data.getBytes();
    Cipher c = null;
    c = Cipher.getInstance(algorithm);
    SecretKeySpec k =  new SecretKeySpec(key, algorithm);
    c.init(Cipher.ENCRYPT_MODE, k);
    byte[] encryptedData = "".getBytes();
    encryptedData = c.doFinal(dataToSend);
    Base64.Encoder encoder = Base64.getEncoder();
    byte[] encryptedByteValue = encoder.encode(encryptedData);
    return new String(encryptedByteValue);
  } catch (Exception e) {
  }
  return ("");
}

//decrypt: Decrypt the message
public String decrypt(String data){
  try{
    Base64.Decoder decoder = Base64.getDecoder();
    byte[] encryptedData = decoder.decode(data);
    Cipher c = null;
    c = Cipher.getInstance(algorithm);
    SecretKeySpec k = new SecretKeySpec(key, algorithm);
    c.init(Cipher.DECRYPT_MODE, k);
    byte[] decrypted = null;
    decrypted = c.doFinal(encryptedData);
    return new String(decrypted);
  } catch (Exception e) {
  }
  return ("");
}

//check_mac: Checks if the message is sent from the client
public static boolean check_mac(String data){
  if (data.contains("!@#$!@#$%")){
    return true;
  }
  return false;
}

//Add_mac: Add a message authentication key
public static String Add_mac(String data){
  return "!@#$!@#$%"+data;
}

//Remove_mac: Remove the message authentication key
public static String Remove_mac(String data){
  return data.replace("!@#$!@#$%", "");
}

//Runnable
 public void run() {
    try {
        if (Thread.currentThread() == t1) {
            do {
                br1 = new BufferedReader(new InputStreamReader(System.in));
                pr1 = new PrintWriter(socket.getOutputStream(), true);
                in = br1.readLine();
                if(I == true && C == false){
                  pr1.println(Add_mac(in));
                }
                else if (C == true && I == false){
                  String data = encrypt(in);
                  pr1.println(data);
                }
                else if(C == true && I == true){
                  String str = encrypt(in);
                  pr1.println(Add_mac(str));
                }
                else if (C == false && I == false) {
                  pr1.println(in);
                }
            } while (!in.equals("END"));
        } else if(Thread.currentThread() == t2) {
            do {
                br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = br2.readLine();
                if (C == false && I == true){
                  if(Remove_mac(out).equals("END")){
                    socket.close();
                  }
                  if(Remove_mac(out) == null){
                    System.out.println("Client is disconnected");
                    reconnect();
                    break;
                  }
                  if(check_mac(out) == true){
                    String dataInt = out;
                    System.out.println("Client says : : : " + Remove_mac(dataInt));
                  } else {
                    System.out.println("Client says : : : " + "Integrity failed");
                  }
                }
                else if (C == true && I == false){
                  String data = decrypt(out);
                  if(data.equals("END")){
                    socket.close();
                  }
                  if(data == null){
                    System.out.println("Client is disconnected");
                    reconnect();
                    break;
                  }
                  System.out.println("Client says : : : " + data);
                }
                else if(C == true && I == true){

                  if(check_mac(out) == true){
                    String str2 = Remove_mac(out);
                    if(decrypt(str2).equals("END")){
                      socket.close();
                    }
                    if(decrypt(str2) == null){
                      System.out.println("Client is disconnected");
                      reconnect();
                      break;
                    }
                    if(decrypt(str2) == null){
                      System.out.println("Client is disconnected");
                      reconnect();
                      break;
                    }
                    System.out.println("Server says : : : " + decrypt(str2));
                  } else {
                    System.out.println("Server says : : : " + "Integrity failed");
                  }
                }
                else if (C == false && I == false) {
                  if(out.equals("END")){
                    socket.close();
                  }
                  if(out == null){
                    System.out.println("Client is disconnected");
                    reconnect();
                    break;
                  }
                  System.out.println("Client says : : : "  + out);
                }
            } while (!out.equals("END"));
        }
    } catch (Exception e) {
    }
}

//main
public static void main(String[] args)throws java.io.IOException {
    CIA();
    new server();
}
}
