import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.Math;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.net.*;
import java.io.*;


public class ReversiBoard extends JPanel implements MouseListener, MouseMotionListener
{
   private ImageIcon white = new ImageIcon("white.jpg");
   private ImageIcon black = new ImageIcon("black.jpg");
   private ImageIcon blank = new ImageIcon("empty.jpg");
   private ImageIcon gray = new ImageIcon("gray.jpg");
      
   private static final int SIZE=75;	//size of cell being drawn
 
   //This array will be represented graphically on the screen
   private static int[][] board;
   
   public static SparseMatrix<Integer> brd;
 	//A moveable smiley face will start in the center of the field
   private static int playerR;			//start row for the player
   private static int playerC;			//start col for the player
     
   protected static int mouseX;			//locations for the mouse pointer
   protected static int mouseY;
   
   private int playerColor = 1; //player is white
   private Integer[] currPos = {-1, -1};
   
   private boolean useAI;
   private boolean networkPlay;
   private JFrame frm;
   int numMoves = 0;
   private boolean isServer;
   
   private ReversiServer srv; //accepts connections
   private ReversiClient cli; //makes connections
   private ReversiServerClient moveMaker; //makes the moves


   public ReversiBoard(int gameType, JFrame frm, boolean isServer)
   {
      addMouseListener(this);
      addMouseMotionListener(this);
      mouseX = 0;
      mouseY = 0;
   
      int numRows = 8;		//an 8x8 board
      int numColumns = 8;
   
      brd = new SparseMatrix(8, 8, 0);
      brd.add(1, 3, 3);
      brd.add(1, 4, 4);
      brd.add(2, 3, 4);
      brd.add(2, 4, 3);
      this.useAI = (gameType == 0);
      this.networkPlay = (gameType == 2);
      this.isServer = isServer;
      this.frm = frm;
      
      if (networkPlay){
         if (isServer){    //set up a multicast network and broadcast server IP until we get a connection
            srv = new ReversiServer();
            srv.start();
            Multicaster m = new Multicaster(srv);
            m.createNetwork();
            moveMaker = srv;
         }
         else {      //connect to multicast network, get server IP, and establish connection
            cli = new ReversiClient();
            this.playerColor = 2;
            System.out.println("Player color is: " + playerColor);
            moveMaker = cli;
            clearPossibleMoves();
            int[] netMove = moveMaker.getNetMove();
            makeMove(netMove[0], netMove[1], netMove[2]);
         }
      }
   }


	//post:  shows different pictures on the screen in grid format depending on the values stored in the array board
	//			0-blank, 1-white, 2-black and gives priority to drawing the player
   public void showBoard(Graphics g)	
   {
      int x =0, y=0;		//upper left corner location of where image will be drawn
      for(int r=0;r<brd.numRows();r++)
      {
         x = 0;						//reset the row distance
         for(int c=0;c<brd.numColumns();c++)
         {
            if(brd.get(r, c) == 0)
               g.drawImage(blank.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if(brd.get(r, c) == 1)
               g.drawImage(white.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if(brd.get(r, c) == 2)
               g.drawImage(black.getImage(), x, y, SIZE, SIZE, null);  //scaled image
            else if (brd.get(r, c) == 3)
               g.drawImage(gray.getImage(), x, y, SIZE, SIZE, null);
         
            x+=SIZE;
         }
         y+=SIZE;
      }
      int wPoints = 0;
      int bPoints = 0;
      for (int r = 0; r < brd.numRows(); r++){ //determine # points for both sides
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == 1) wPoints++;
            else if (brd.get(r, c) == 2) bPoints++;
         }
      }
      g.drawString("White: " + wPoints, x + (SIZE / 2), SIZE / 2); //display points on right side
      g.drawString("Black: " + bPoints, x + (SIZE / 2), SIZE);
   }
   

   
   
   //sets all valid moves for pNum to 3
   public void getValidMoves(int pNum){
      int enemyNum = (pNum % 2) + 1;
      for (int r = 0; r < brd.numRows(); r++){
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == pNum){
               for (int a = -1; a <= 1; a++){
                  for (int b = -1; b <= 1; b++){
                     if (brd.inBounds(r + a, c + b) && brd.get(r + a, c + b) == enemyNum){ //search for enemy pieces
                        outerloop:
                           for (int i = 1; i < brd.numRows(); i++){ //extend current line
                           if (brd.inBounds(r + (i * a), c + (i * b)) && brd.get(r + (i * a), c + (i * b)) == 0){
                              brd.add(3, r + (i * a), c + (i * b));
                              break outerloop;
                           } 
                           else if (brd.inBounds(r + (i * a), c + (i * b)) && (brd.get(r + (i * a), c + (i * b)) == pNum || brd.get(r + (i * a), c + (i * b)) == 3)){
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
   
   
   //AI goes for a corner if possible, otherwise makes move that gets most points
   public void getAIMove(){
      int AIColor = (playerColor % 2) + 1;
      getValidMoves(AIColor);
      int maxMovePoints = 0;
      int myR = -1;
      int myC = -1;
      for (int r = 0; r < brd.numRows(); r++){
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == 3){
               if ((r == 0 && (c == 0 || c == brd.numColumns())) || (r == brd.numRows() && (c == 0 && c == brd.numColumns()))){
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
   
   //returns number of points scored by a move to (r, c)
   public int getPoints(int r, int c, int pNum){
      int enemyNum = (pNum % 2) + 1;
      int sum = 0;
      for (int a = -1; a <= 1; a++){
         for (int b = -1; b <= 1; b++){
            if (brd.inBounds(r + a, c + b) && brd.get(r + a, c + b) == enemyNum){ //search for enemy pieces
               outerloop:
               for (int i = 1; i < brd.numRows(); i++){ //extend current line
                  if (brd.inBounds(r + (i * a), c + (i * b)) && brd.get(r + (i * a), c + (i * b)) == pNum){
                     for (int x = 0; x <= i; x++){
                        sum++;
                     } 
                     break outerloop;
                  }
                  else if (brd.inBounds(r + (i * a), c + (i * b)) && brd.get(r + (i * a), c + (i * b)) == 0){
                     break outerloop;
                  }
               }
            }
         }
      }
      return sum;
   }
  
   //pre: r, c is a valid move
   //post: makes move to (r, c) and scores appropriate points
   public void makeMove(int r, int c, int pNum){
      int enemyNum = (pNum % 2) + 1;
      for (int a = -1; a <= 1; a++){
         for (int b = -1; b <= 1; b++){
            if (brd.inBounds(r + a, c + b) && brd.get(r + a, c + b) == enemyNum){ //search for enemy pieces
               outerloop:
               for (int i = 1; i < brd.numRows(); i++){ //extend current line
                  if (brd.inBounds(r + (i * a), c + (i * b)) && brd.get(r + (i * a), c + (i * b)) == pNum){
                     for (int x = 0; x <= i; x++){
                        int row = r + (x * a);
                        int col = c + (x * b);
                        brd.set(row, col, pNum);
                     } 
                     break outerloop;
                  }
                  else if (brd.inBounds(r + (i * a), c + (i * b)) && brd.get(r + (i * a), c + (i * b)) == 0){
                     break outerloop;
                  }
               }
            }
         }
      }
      if (!networkPlay){
         int iW = isOver(enemyNum);
         if (iW > 0) infoBox("white");
         else if (iW < 0) infoBox("black");
      }
      clearPossibleMoves();
      repaint();
   }
   
   
   //display a dialog saying who won and then check to see if they want to play again
   public void infoBox(String color){
      JOptionPane.showMessageDialog(null, color + " has won!!!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
      int n = JOptionPane.showConfirmDialog(
                            frm, "Would you like to play again?",
                            "Play again?",
                            JOptionPane.YES_NO_OPTION);
      if (n == JOptionPane.NO_OPTION){
         System.exit(1);
      } 
      else {
         brd.clear();
         brd.add(1, 3, 3);
         brd.add(1, 4, 4);
         brd.add(2, 3, 4);
         brd.add(2, 4, 3);
      }
   }
   
   public int isOver(int pNum){ //returns a negative number if black wins, positive if white wins
      getValidMoves(pNum);
      int total = 0;
      for (int r = 0; r < brd.numRows(); r++){
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == 3) 
               return 0;
            if (brd.get(r, c) == 2) total--;
            if (brd.get(r, c) == 1) total++;
         }
      }
      return total;
      
      /*
      int black = 0;
      int white = 0;
      for (int r = 0; r < brd.numRows(); r++){
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == 1){
               white++;
            }
            else if (brd.get(r, c) == 2){
               black++;
            }
         }
      }
      if (black + white < 64){
         return 0;
      } 
      else{
         return white - black;
      }
      */
   
   }
   
   
   //sets all "3"'s on board to "0"'s
   public void clearPossibleMoves(){
      for (int r = 0; r < brd.numRows(); r++){
         for (int c = 0; c < brd.numColumns(); c++){
            if (brd.get(r, c) == 3) brd.remove(r, c);
         }
      }
   }   

   public void paintComponent(Graphics g)
   {
      super.paintComponent(g); 
      g.setColor(Color.blue);		//draw a blue boarder around the board
      g.fillRect(0, 0, (brd.numColumns()*SIZE), (brd.numRows()*SIZE));
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
      
         if(mouseR >=0 && mouseC >= 0 && mouseR < brd.numRows() && mouseC < brd.numColumns())
         {
            if (brd.get(mouseR, mouseC) == 3){
               if (!networkPlay){
                  makeMove(mouseR, mouseC, playerColor);
                  
                  int iW = isOver((playerColor % 2) + 1);
                  if (iW > 0) infoBox("white");
                  else if (iW < 0) infoBox("black");
               } 
               else {
                  moveMaker.makeNetMove(mouseR, mouseC, playerColor); //make your move
                  makeMove(mouseR, mouseC, playerColor);
                  
                  int iW = isOver((playerColor % 2) + 1);
                  if (iW > 0) infoBox("white");
                  else if (iW < 0) infoBox("black");
                                    
                  getValidMoves(playerColor);
               }
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
      
         if(mouseR >=0 && mouseC >= 0 && mouseR < brd.numRows() && mouseC < brd.numColumns())
         {
            if (brd.get(mouseR, mouseC) == playerColor){
               if (useAI){
                  getValidMoves((playerColor % 2) + 1);
                  getAIMove();
               } 
               else if (!networkPlay){
                  playerColor = (playerColor % 2) + 1; //switch player colors (for local play)
               } 
               else {
                  int[] netMove = moveMaker.getNetMove(); //get opponent's move
                  makeMove(netMove[0], netMove[1], netMove[2]);
                  
                  int iW = isOver(playerColor);
                  if (iW > 0) infoBox("white");
                  else if (iW < 0) infoBox("black");
               }
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
