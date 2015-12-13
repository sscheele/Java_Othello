import java.io.*;
import java.net.*;

class Multicaster{ //extends Thread?
   private boolean hasConnection;
   private ReversiServer srv;
   
   public Multicaster(ReversiServer srv){
      hasConnection = false;
      this.srv = srv;
   }
   
   public String getMyIP(){ //gets your own local IP
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
   
   /*
   public void run(){
   
   }
   */
   
   public void createNetwork(){
      try {
         DatagramSocket socket = new DatagramSocket();;
         String msg = "" + getMyIP();
         byte[] outBuf = msg.getBytes();
         final int PORT = 8888;
         InetAddress address = InetAddress.getByName("224.2.2.3");
         DatagramPacket outPacket = new DatagramPacket(outBuf, outBuf.length, address, PORT);
         
         while (!srv.hasConn()) {
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
      catch (Exception ioe) {
         System.out.println(ioe);
      }   
   }
}
