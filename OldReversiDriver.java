   import javax.swing.JFrame;

   public class OldReversiDriver								//Driver Program
   {
      public static OldReversiBoard screen;					//Game window
   
      public static void main(String[]args)
      {
         screen = new OldReversiBoard();
         JFrame frame = new JFrame("Reversi");	//window title
         frame.setSize(800, 800);					//Size of game window
         frame.setLocation(100, 50);				//location of game window on the screen
         frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         frame.setContentPane(screen);		
         frame.setVisible(true);
         screen.getValidMoves(1);
      }
   }
