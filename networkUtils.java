import java.io.*;
import java.net.*;

class networkUtils{
   public static String getMyIP(){
      try{
         InetAddress IP = InetAddress.getLocalHost();
         String retVal = IP.getHostAddress();
         return retVal;
      } 
      catch (Exception e){
         System.out.println(e);
      }
      return null;
   }

   public static void createNetwork(){
      DatagramSocket socket = null;
      DatagramPacket outPacket = null;
      byte[] outBuf;
      final int PORT = 8888;
   
      try {
         socket = new DatagramSocket();
         String msg;
         msg = "Reversi server @ " + getMyIP();
         outBuf = msg.getBytes();
         InetAddress address = InetAddress.getByName("224.2.2.3");
         outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
         
         while (true) {
         //Send to multicast IP address and port
            socket.send(outPacket);
         
            //System.out.println("Server sends : " + msg);
            try {
               Thread.sleep(500);
            } 
            catch (InterruptedException ie) {
            }
         }
      } 
      catch (IOException ioe) {
         System.out.println(ioe);
      }   
   }
}