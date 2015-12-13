import java.util.ArrayList;
class SparseMatrix<anyType> implements Matrixable<anyType>{

   private int numRows;
   private int numCols;
   private int length;
   private ArrayList<anyType> items;
   private ArrayList<Integer> keys;
   private anyType defaultValue;

   public SparseMatrix(int numRows, int numCols){
      this.numRows = numRows;
      this.numCols = numCols;
      this.length = numRows * numCols;
      items = new ArrayList<anyType>();
      keys = new ArrayList<Integer>();
      this.defaultValue = null;
   }
   
   public SparseMatrix(int numRows, int numCols, anyType defaultValue){
      this.numRows = numRows;
      this.numCols = numCols;
      this.length = numRows * numCols;
      this.defaultValue = defaultValue;
      items = new ArrayList<anyType>();
      keys = new ArrayList<Integer>();
   }

   public boolean add(anyType x, int r, int c){
      items.add(x);
      keys.add((r*numCols) + c);
      return true;
   }
   
   public boolean isEmpty(){
      return items.size() == 0;
   }
   
   public boolean inBounds(int r, int c){
      return (r >= 0 && c >= 0 && r < numRows && c < numCols);
   }
   
   public anyType set(int r, int c, anyType x){
      int ind = keys.indexOf((r * numCols) + c);
      if (ind == -1){
         this.add(x, r, c);
      } 
      else {
         return items.set(ind, x);
      }
      return null;
   }
   
   public int size(){
      return items.size();
   }
   
   public int numRows(){
      return numRows;
   }
   
   public int numColumns(){
      return numCols;
   }
   
   public boolean contains(anyType x){
      return items.indexOf(x) > -1;
   }
   
   public boolean contains(int r, int c){
      return keys.indexOf((r * numCols) + c) == -1;
   }
   
   public int[] getLocation(anyType x){
      int ind = items.indexOf(x);
      if (ind == -1) 
         return null;
      int key = keys.get(ind);
      int[] retVal = {key / numCols, key % numCols};
      return retVal;
   }
   
   public Object[][] toArray(){
      Object[][] retVal = new Object[numRows][numCols];
      for (int i = 0; i < items.size(); i++){
         int key = keys.get(i);
         retVal[key / numCols][key % numCols] = items.get(i);
      }
      return retVal;
   }
   
   public void clear(){
      items.clear();
      keys.clear();
   }

   public anyType remove(int r, int c){
      int key = (r * numRows) + c;
      int index = keys.indexOf(key);
      if (index < 0) 
         return null;
      keys.remove(index);
      return items.remove(index);
   }

   public anyType get(int key){
      return keys.indexOf(key) == -1 ? defaultValue : items.get(keys.indexOf(key));
   }
   
   public anyType get(int r, int c){
      return this.get(this.getKey(r, c));
   }
   
   public int getKey(int r, int c){
      return (r * numCols) + c;
   }
}