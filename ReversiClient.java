import java.io.*;
import java.net.*;

class ReversiClient implements ReversiServerClient{
   private String serverIP;
   private Socket sock;
   private BufferedReader in;
   private PrintWriter out;

   public ReversiClient(){ 
      try{
         byte[] inBuf = new byte[256];
         MulticastSocket socket = new MulticastSocket(8888);
         DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
         InetAddress address = InetAddress.getByName("224.2.2.3");
         socket.joinGroup(address); //join multicast network
         socket.receive(inPacket); //get server IP
         serverIP = new String(inBuf, 0, inPacket.getLength()); //parse server IP
         socket.close();
         
         sock = new Socket(serverIP, 9898);
         in = new BufferedReader(
               new InputStreamReader(
                  sock.getInputStream()
               )
             );
         out = new PrintWriter(sock.getOutputStream(), true);
      } 
      catch (Exception e){
         System.out.println(e);
      }
   }
   
   public int[] getNetMove(){ //recieve 3 numbers for r, c, and pNum
      try{
         int[] moves = {
                     Integer.parseInt(in.readLine()),
                     Integer.parseInt(in.readLine()),
                     Integer.parseInt(in.readLine())
                     };
         for (int i : moves){
         System.out.println("" + i);
         }
         return moves;  
      } 
      catch (Exception e){
         System.out.println(e);
      }
      return null;
   }
   
   public void makeNetMove(int r, int c, int pNum){ //sends 3 numbers for r, c, and pNum
      out.println("" + r);
      out.println("" + c);
      out.println("" + pNum);
   }
}
