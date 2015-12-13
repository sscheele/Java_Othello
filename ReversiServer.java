import java.io.*;
import java.net.*;

class ReversiServer extends Thread implements ReversiServerClient{
   private boolean hasConnection;
   private PrintWriter out;
   private BufferedReader in;
   private Thread t;
   
   
   public ReversiServer(){
      hasConnection = false;
   }
   
   public boolean hasConn(){
      return hasConnection;
   }
   
   public void run(){
      try{
         ServerSocket listener = new ServerSocket(9898); //listen on this port
         System.out.println("Opened listener!");
         Socket sock = listener.accept();
         hasConnection = true;
         out = new PrintWriter(sock.getOutputStream(), true);
         in = new BufferedReader(
                              new InputStreamReader(
                                 sock.getInputStream()
                              )
                           );
      } 
      catch (Exception e){
         System.out.println(e);
      }
   }
   
   public void start(){ //multithread program
      if (t == null){
         t = new Thread(this, "SrvThread");
         t.start();
      }
   }
   
   public void makeNetMove(int r, int c, int pNum){
      out.println("" + r);
      out.println("" + c);
      out.println("" + pNum);
   }
   
   public int[] getNetMove(){
      try{
         int[] moves = {
                     Integer.parseInt(in.readLine()),
                     Integer.parseInt(in.readLine()),
                     Integer.parseInt(in.readLine())
                     };
         return moves;  
      } 
      catch (Exception e){
         System.out.println(e);
      }
      return null;
   }
}
