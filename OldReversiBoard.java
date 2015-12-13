import javax.swing.JPanel;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Math;


public class OldReversiBoard extends JPanel implements MouseListener, MouseMotionListener
{
   private ImageIcon white = new ImageIcon("white.jpg");
   private ImageIcon black = new ImageIcon("black.jpg");
   private ImageIcon blank = new ImageIcon("empty.jpg");
   private ImageIcon gray = new ImageIcon("gray.jpg");
      
   private static final int SIZE=75;	//size of cell being drawn
 
   //This array will be represented graphically on the screen
   private static int[][] board;

 	//A moveable smiley face will start in the center of the field
   private static int playerR;			//start row for the player
   private static int playerC;			//start col for the player
     
   protected static int mouseX;			//locations for the mouse pointer
   protected static int mouseY;
   
   private int playerColor = 1; //player is white
   private Integer[] currPos = {-1, -1};


   public OldReversiBoard()
   {
      addMouseListener(this);
      addMouseMotionListener(this);
      mouseX = 0;
      mouseY = 0;
   
      int numRows = 8;		//an 8x8 board
      int numColumns = 8;
      board = new int[numRows][numColumns];
      for(int r=0;r<board.length;r++)					//board is empty
         for(int c=0;c<board[0].length;c++)
            board[r][c] = 0;
      board[3][3] = 1; //add initial pieces
      board[4][4] = 1;
      board[3][4] = 2;
      board[4][3] = 2; 
   }


	//post:  shows different pictures on the screen in grid format depending on the values stored in the array board
	//			0-blank, 1-white, 2-black and gives priority to drawing the player
   public void showBoard(Graphics g)	
   {
      int x =0, y=0;		//upper left corner location of where image will be drawn
      for(int r=0;r<board.length;r++)
      {
         x = 0;						//reset the row distance
         for(int c=0;c<board[0].length;c++)
         {
            if(board[r][c]==0)
               g.drawImage(blank.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if(board[r][c]==1)
               g.drawImage(white.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if(board[r][c]==2)
               g.drawImage(black.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if (board[r][c]==3)
               g.drawImage(gray.getImage(), x, y, SIZE, SIZE, null);
         
            x+=SIZE;
         }
         y+=SIZE;
      }
   }
   
   public boolean isOnBoard(int r, int c){
      return (r >= 0 && c >= 0 && r < board.length && c < board[0].length);
   }
   
   public void getValidMoves(int pNum){
      int enemyNum = (pNum % 2) + 1;
      for (int r = 0; r < board.length; r++){
         for (int c = 0; c < board[0].length; c++){
            if (board[r][c] == pNum){
               for (int a = -1; a <= 1; a++){
                  for (int b = -1; b <= 1; b++){
                     if (isOnBoard(r + a, c + b) && board[r + a][c + b] == enemyNum){ //search for enemy pieces
                        outerloop:
                        for (int i = 1; i < board.length; i++){ //extend current line
                           if (isOnBoard(r + (i * a), c + (i * b)) && board[r + (i * a)][c + (i * b)] == 0){
                              board[r + (i * a)][c + (i * b)] = 3;
                              break outerloop;
                           } 
                           else if (isOnBoard(r + (i * a), c + (i * b)) && (board[r + (i * a)][c + (i * b)] == pNum || board[r + (i * a)][c + (i * b)] == 3)){
                              break outerloop;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
   
   public void getAIMove(){
      int AIColor = (playerColor % 2) + 1;
      getValidMoves(AIColor);
      int maxMovePoints = 0;
      int myR = -1;
      int myC = -1;
      for (int r = 0; r < board.length; r++){
         for (int c = 0; c < board[0].length; c++){
            if (board[r][c] == 3){
               if ((r == 0 && (c == 0 || c == board[0].length)) || (r == board.length && (c == 0 && c == board[0].length))){
                  makeMove(r, c, AIColor);
               } 
               else {
                  int posPts = getPoints(r, c, AIColor);
                  if (posPts > maxMovePoints){
                     maxMovePoints = posPts;
                     myR = r;
                     myC = c;
                  }   
               }
            }
         }
      }
      try {
         Thread.sleep(1000);                 //1000 milliseconds is one second.
      } 
      catch(InterruptedException ex) {
         Thread.currentThread().interrupt();
      }
      makeMove(myR, myC, AIColor);
      getValidMoves(playerColor);
   }
   
   public int getPoints(int r, int c, int pNum){
      int enemyNum = (pNum % 2) + 1;
      int sum = 0;
      for (int a = -1; a <= 1; a++){
         for (int b = -1; b <= 1; b++){
            if (isOnBoard(r + a, c + b) && board[r + a][c + b] == enemyNum){ //search for enemy pieces
               outerloop:
               for (int i = 1; i < board.length; i++){ //extend current line
                  if (isOnBoard(r + (i * a), c + (i * b)) && board[r + (i * a)][c + (i * b)] == pNum){
                     for (int x = 0; x <= i; x++){
                        sum++;
                     } 
                     break outerloop;
                  }
                  else if (isOnBoard(r + (i * a), c + (i * b)) && board[r + (i * a)][c + (i * b)] == 0){
                     break outerloop;
                  }
               }
            }
         }
      }
      return sum;
   }
  
   //pre: r, c is a valid move
   public void makeMove(int r, int c, int pNum){
      int enemyNum = (pNum % 2) + 1;
      for (int a = -1; a <= 1; a++){
         for (int b = -1; b <= 1; b++){
            if (isOnBoard(r + a, c + b) && board[r + a][c + b] == enemyNum){ //search for enemy pieces
               outerloop:
               for (int i = 1; i < board.length; i++){ //extend current line
                  if (isOnBoard(r + (i * a), c + (i * b)) && board[r + (i * a)][c + (i * b)] == pNum){
                     for (int x = 0; x <= i; x++){
                        board[r + (x * a)][c + (x * b)] = pNum;
                     } 
                     break outerloop;
                  }
                  else if (isOnBoard(r + (i * a), c + (i * b)) && board[r + (i * a)][c + (i * b)] == 0){
                     break outerloop;
                  }
               }
            }
         }
      }
      clearPossibleMoves();
   }
   
   public void clearPossibleMoves(){
      for (int r = 0; r < board.length; r++){
         for (int c = 0; c < board[0].length; c++){
            if (board[r][c] == 3) board[r][c] = 0;
         }
      }
   }
   
   

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g); 
      g.setColor(Color.blue);		//draw a blue boarder around the board
      g.fillRect(0, 0, (board[0].length*SIZE), (board.length*SIZE));
      showBoard(g);					//draw the contents of the array board on the screen
   }
   
	 //***BEGIN MOUSE STUFF***
   private class Listener implements ActionListener
   {
      public void actionPerformed(ActionEvent e)	//this is called for each timer iteration - make the enemy move randomly
      {
         repaint();
      }
   }

   public void mouseClicked( MouseEvent e )
   {
   
   }

   public void mousePressed( MouseEvent e )
   {
      mouseX = e.getX();
      mouseY = e.getY();
   
      int button = e.getButton();
      if(button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3)
      {
         int mouseR = (mouseY/SIZE);
         int mouseC = (mouseX/SIZE);
      
         if(mouseR >=0 && mouseC >= 0 && mouseR < board.length && mouseC < board[0].length)
         {
            if (board[mouseR][mouseC] == 3){
               makeMove(mouseR, mouseC, playerColor);
            }
         }
      
      } 
      repaint();
   }

   public void mouseReleased( MouseEvent e )
   {
      mouseX = e.getX();
      mouseY = e.getY();
   
      int button = e.getButton();
      if(button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3)
      {
         int mouseR = (mouseY/SIZE);
         int mouseC = (mouseX/SIZE);
      
         if(mouseR >=0 && mouseC >= 0 && mouseR < board.length && mouseC < board[0].length)
         {
            if (board[mouseR][mouseC] == playerColor){
               getValidMoves((playerColor % 2) + 1);
               getAIMove();
               getValidMoves(playerColor);
            }
         }
      
      } 
      repaint();
   }

   public void mouseEntered( MouseEvent e )
   {}

   public void mouseMoved( MouseEvent e)
   {
   }

   public void mouseDragged( MouseEvent e)
   {}

   public void mouseExited( MouseEvent e )
   {}

}
