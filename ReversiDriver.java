import javax.swing.JFrame;
import javax.swing.JOptionPane;

class ReversiDriver								//Driver Program
{
   public static ReversiBoard screen;					//Game window
   public boolean hasConnection;
   
   public static void main(String[]args)
   {
      String ttl = "Reversi ";
      int m;
      JFrame frm = new JFrame();
      String[] playTypeOptions = {"AI", "Local", "Network"};
      int n = JOptionPane.showOptionDialog(
                            frm, 
                            "What kind of game would you like to play?",
                            "Game type",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            playTypeOptions,
                            playTypeOptions[0]
                            );
      if (n == 2){
         m = JOptionPane.showConfirmDialog(
                            frm, 
                            "Host game?",
                            "Game type",
                            JOptionPane.YES_NO_OPTION
                            );
         screen = new ReversiBoard(n, frm, (m == JOptionPane.YES_OPTION));
         ttl += (m == JOptionPane.YES_OPTION) ? "Server" : ""; //so we know which is the server
      }  
      else {
         screen = new ReversiBoard(n, frm, false);
         m = 0;
      }    
      JFrame frame = new JFrame(ttl);	//window title
      frame.setSize(800, 800);					//Size of game window
      frame.setLocation(100, 50);				//location of game window on the screen
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setContentPane(screen);		
      frame.setVisible(true);
      if (n != 2){
         screen.getValidMoves(1);
      } 
      else {
         screen.getValidMoves(m == JOptionPane.YES_OPTION ? 1 : 2); //get valid moves for your own color
      }
      String[] Options = {"Yes", "No"};
   }   
}

