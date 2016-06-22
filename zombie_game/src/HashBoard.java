import java.util.*;



// Tracks the positions of an arbitrary 2D grid of Tiles. HashBoard
// uses an internal HashMap to store the tiles and their
// positions. This leads to O(T) memory consumption with O(R*C) shift
// operations.
public class HashBoard extends Board implements Cloneable {
	
  // Track the positions of tiles
  private static class TPos implements Cloneable {
    public final int row, col;
    public TPos(int r, int c){
      row=r; col=c;
    }
    public String toString(){
      return String.format("(%2d, %2d)",row,col);
    }
    public int hashCode(){
      return row*31 + col;
    }
    public boolean equals(Object o){
      if(o instanceof TPos){
        TPos p = (TPos) o;
        return this.row==p.row && this.col==p.col;
      }
      return false;
    }
  }

  private HashMap<TPos,Tile> tiles;     // Hash of tiles, also tracks number of tiles
  private int rows, cols;
  //private int tileCount;               // How many tiles are in the board
  private boolean lastShiftMovedTiles; // Did the last shift change anything?

  // Build a Board of the specified size that is empty of any tiles
  public HashBoard(int rows, int cols){
    if(rows <=0 || cols<=0){
      String msg = 
        String.format("Can't make a HashBoard of size %d %d",
                      rows,cols);
      throw new RuntimeException(msg);
    }
    this.rows = rows;
    this.cols = cols;
    this.tiles = new HashMap<TPos,Tile>();
    this.lastShiftMovedTiles = false;
    // throw new RuntimeException("Disabling HashBoard"); 
  }

  // Build a board that copies the 2D array of tiles provided Tiles
  // are immutable so can be referenced without copying.
  public HashBoard(Tile t[][]){
    this(t.length, t[0].length);
    for(int i=0; i<this.getRows(); i++){
      for(int j=0; j<this.getCols(); j++){
        if(t[i][j] != null){
          this.setTile(i,j,t[i][j]);
        }
      }
    }
    
    //thash = t.clone();
    // throw new RuntimeException("Disabling HashBoard"); 
  }

  // Private empty constructor for making copies
  private HashBoard(){}

  // Return the number of rows in the Board
  public int getRows(){ 
    return this.rows;
  }
  // Return the number of columns in the Board
  public int getCols(){
    return this.cols;
  }
  // Return how many tiles are present in the board (non-empty spaces)
  // TARGET COMPLEXITY: O(1)
  public int getTileCount(){
    return this.tiles.size();
  }
  // Return how many free spaces are in the board
  // TARGET COMPLEXITY: O(1)
  public int getFreeSpaceCount(){
    return getRows()*getCols() - getTileCount();
  }
  // Check if the position (i,j) is in bounds in the board
  private void boundsCheck(int i, int j){
    if(i < 0 || i >= this.getRows() ||
       j < 0 || j >= this.getCols()){
      String msg = 
        String.format("Index %d %d is out of bounds in HashBoard of size %d %d",
                      i,j,this.getRows(),this.getCols());
      throw new RuntimeException(msg);
    }
  }

  // Sets a tile, does bounds checking, no updates to tile count
  private void setTile(int i, int j, Tile tile){
    this.boundsCheck(i,j);
    if(tile==null){
      this.tiles.remove(new TPos(i,j));
    }
    else{ 
      this.tiles.put(new TPos(i,j), tile); 
    }
  }
    
  // Get the tile at a particular location.  If no tile exists at the
  // given location (free space) then null is returned. Throw a
  // runtime exception with a useful error message if an out of bounds
  // index is requested.
  public Tile tileAt(int i, int j) {
    this.boundsCheck(i,j);
    return this.tiles.get(new TPos(i,j));
  }

  // true if the last shift operation moved any tile; false otherwise
  // TARGET COMPLEXITY: O(1)
  public boolean lastShiftMovedTiles(){
    return this.lastShiftMovedTiles;
  }

  // Check to see if a shift in the direction dictated by the iterator
  // would merge any tiles.
  private boolean shiftWouldMerge(Iter2D iter){
    // Work over every row/column
    for(iter=iter; iter.outerInBounds(); iter.advanceOuterResetInner()){
      Tile lastTile = this.tileAt(iter.rowPos, iter.colPos);
      for(iter.advanceInner(); iter.innerInBounds(); iter.advanceInner()){
        Tile currTile = this.tileAt(iter.rowPos, iter.colPos);
        if(lastTile!=null && currTile!=null && lastTile.mergesWith(currTile)){
          return true;
        }
        if(currTile != null){
          lastTile = currTile;
        }
      }
    }
    return false;
  }

  // Return true if a shift left, right, up, or down would merge any
  // tiles. If no shift would cause any tiles to merge, return false.
  // The inability to merge anything is part of determining if the
  // game is over.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: number of rows
  // C: number of columns
  public boolean mergePossible(){
    return
      shiftWouldMerge(new LeftToRight()) ||
      shiftWouldMerge(new RightToLeft()) ||
      shiftWouldMerge(new UpToDown()) ||
      shiftWouldMerge(new DownToUp());
  }
      
  // Adds a tile to an empty spot (must be empty) and updates the tile
  // count
  // 
  // TARGET COMPLEXITY: O(1)
  private void addTile(int row, int col, Tile t){
    this.boundsCheck(row,col);
    if(this.tileAt(row,col) != null){
      String msg = String.format("Tile already present at %d %d",row,col);
      throw new RuntimeException(msg);
    }
    this.setTile(row,col,t);
  }

  // Add a the given tile to the board at the "freeL"th free space.
  // Free spaces are numbered 0,1,... from left to right accross the
  // columns of the zeroth row, then the first row, then the second
  // and so forth. For example the board with following configuration
  // 
  //    -    -    -    - 
  //    -    4    -    - 
  //   16    2    -    2 
  //    8    8    4    4 
  // 
  // has its 9 free spaces numbered as follows
  // 
  //    0    1    2    3 
  //    4    .    5    6 
  //    .    .    7    . 
  //    .    .    .    . 
  // 
  // where the dots (.) represent filled tiles on the board.
  // 
  // Calling addTileAtFreeSpace(6, new Tile(32) would leave the board in
  // the following state.
  // 
  //    -    -    -    - 
  //    -    4    -   32 
  //   16    2    -    2 
  //    8    8    4    4 
  // 
  // Throw a runtime exception with an informative error message if a
  // location that does not exist is requested.
  // 
  // TARGET COMPLEXITY: O(T + I)
  // T: the number of non-empty tiles in the board
  // I: the value of parameter freeL
  public void addTileAtFreeSpace(int freeL, Tile tile) {
    if(this.getFreeSpaceCount() <= freeL){
      String msg = String.format("Only %d free spaces, can't set %d to %s",
                                 this.getFreeSpaceCount(), freeL, tile);
      throw new RuntimeException(msg);
    }
    //    freeL=4;
    Iter2D iter = new LeftToRight();
    while(iter.inBounds()){
      Tile t = tileAt(iter.rowPos, iter.colPos);
      if(t == null && freeL == 0){ 
        this.addTile(iter.rowPos, iter.colPos, tile);
        return;
      }
      else if(t == null){ 
        freeL--; 
      }
      iter.advance();
    }
    throw new RuntimeException("Something is terribly wrong!");
    
  }

  // Pretty-printed version of the board. Use the format "%4s " to
  // print the String version of each tile in a grid.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: number of rows
  // C: number of columns
  public String toString(){
    StringBuilder sb = new StringBuilder();
    String fmt = "%4s ";
    for(int i=0; i<this.getRows(); i++){
      for(int j=0; j<this.getCols(); j++){
        String append = "-";
        Tile t = this.tileAt(i,j);
        if(t!=null){
          append = t.toString();
        }
        sb.append(String.format(fmt,append));
      }
      sb.append("\n");
    }
    return sb.toString();
  }

  public String debugString(){
    return this.tiles.toString(); // this.toString();
  }

  // Shifts iterators are opposite of their direction for efficiency
  // as the leading border tile can be established and new tiles can
  // be swapped towards the border tile repeatedly


  // Shift the tiles of Board in various directions.  Any tiles that
  // collide and should be merged should be changed internally in the
  // board.  Shifts only remove tiles, never add anything.  The shift
  // methods also set the state of the board internally so that a
  // subsequent call to lastShiftMovedTiles() will return true if any
  // Tile moved and false otherwise.  The methods return the score
  // that is generated from the shift which is the sum of the scores
  // all tiles merged during the shift. If no tiles are merged, the
  // return score is 0.
  // 
  // TARGET COMPLEXITY: O(R * C)
  // R: the number of rows of the board
  // C: the number of columns of the board
  public int shiftLeft(){ 
    return shift(new LeftToRight()); 
  }
  public int shiftRight(){ 
    return shift(new RightToLeft()); 
  }
  public int shiftUp(){ 
    return shift(new UpToDown()); 
  }
  public int shiftDown(){ 
    return shift(new DownToUp()); 
  }
  
  // Shift according to iteration in the given direction. Return the
  // score of whatever tiles are merged.
  private int shift(Iter2D iter){
    this.lastShiftMovedTiles = false;
    int score = 0;

    // Work over every row/column
    for(iter=iter; iter.outerInBounds(); iter.advanceOuterResetInner()){
      // Find the first non-empty tile and move it to the first position
      Iter2D borderPos = iter.copy();
      Tile borderTile = null;
      for(iter=iter; iter.innerInBounds(); iter.advanceInner()){
        if(this.tileAt(iter.rowPos,iter.colPos) != null){
          borderTile = this.tileAt(iter.rowPos,iter.colPos); 
          if(borderTile.isMovable()){
            // Shift tile over to the 0th position
            this.setTile(iter.rowPos,iter.colPos, null); 
            this.setTile(borderPos.rowPos,borderPos.colPos, borderTile);
            this.lastShiftMovedTiles |= !borderPos.equals(iter);
          }
          else{                 // Tile is not movable, change borderPos
            borderPos = iter.copy();
          }
          break;
        }
      }
      // Check for empty row
      if(borderTile == null){ 
        continue;
      }

      // Work through subsequent tiles in row/col; shift and merge if needed
      boolean previousMerge = false;
      for(iter.advanceInner(); iter.innerInBounds(); iter.advanceInner()){
        Tile movingTile = this.tileAt(iter.rowPos, iter.colPos);
        if(movingTile == null){
          continue;             // No tile to move
        }
        else if(!movingTile.isMovable()){
          borderTile = movingTile;
          borderPos = iter.copy();
          previousMerge = false;
        }        
        else if(!previousMerge && borderTile.mergesWith(movingTile)){ 
          // Merge tiles, border position remains the same
          this.setTile(iter.rowPos,iter.colPos, null);
          Tile merged = borderTile.merge(movingTile);
          borderTile = merged;
          this.setTile(borderPos.rowPos,borderPos.colPos, merged);
          previousMerge = true; // Don't merge again
          score += merged.getScore();
          this.lastShiftMovedTiles = true;
        }
        else{                   
          // Border changes, moving tile moves to new border position
          setTile(iter.rowPos,iter.colPos, null);
          borderPos.advanceInner();
          borderTile = movingTile;
          setTile(borderPos.rowPos, borderPos.colPos, borderTile);
          previousMerge = false; // Next time can merge
          if(!borderPos.equals(iter)){
            this.lastShiftMovedTiles = true;
          }
        }
      }
    }
    return score;
  }

  // Tracks a position in a 2D grid. Can move "forwards" through the
  // grid according to some order of progression.  Sets up a way to
  // move either by row or by column uniformly and either left to
  // right or right to left and likewise for up/down. Not static so
  // has access to getRows() and getCols() of the housing Board.
  abstract class Iter2D{
    int rowPos, colPos;               // Internal row/col position, fields for easy access
    abstract Iter2D copy();           // Make a copy of the iterator at the same position
    abstract boolean innerInBounds(); // Is the inner dimension in bounds?
    abstract boolean outerInBounds(); // Is the outer dimension in bounds?
    boolean inBounds(){               // Are both dimensions in bounds?
      return innerInBounds() || outerInBounds(); 
    }
    abstract void advanceInner();           // Move the inner coordinate forward
    abstract void advanceOuterResetInner(); // Move the outer coordinate forward, reset inner
    // Move forward adjusting inner or outer coordinate as needed
    void advance(){                         
      if(!this.inBounds()){
        throw new RuntimeException("Cannot advance");
      }
      this.advanceInner();
      if(!this.innerInBounds()){
        this.advanceOuterResetInner();
      }
    }
    // Are two Iter2Ds at the same position
    boolean equals(Iter2D o){ return rowPos==o.rowPos && colPos==o.colPos; }
  }
  // Move left to right accross rows, start in the upper left, move
  // down a row after reaching the final column, good for left shifts
  class LeftToRight extends Iter2D{
    LeftToRight(){ rowPos=0; colPos=0; }
    private LeftToRight(int r, int c){ rowPos=r; colPos = c; }
    Iter2D copy(){ return new LeftToRight(rowPos, colPos); }
    boolean innerInBounds(){ return colPos < getCols(); }
    boolean outerInBounds(){ return rowPos < getRows(); }
    void advanceInner(){ colPos++; }
    void advanceOuterResetInner(){ rowPos++; colPos=0; }
  }
  // Move right to left accross rows, start in the lower right, move
  // up a row on reaching the first column, good for right shifts
  class RightToLeft extends Iter2D{
    RightToLeft(){ rowPos=getRows()-1; colPos=getCols()-1; }
    private RightToLeft(int r, int c){ rowPos=r; colPos = c; }
    Iter2D copy(){ return new RightToLeft(rowPos, colPos); }
    boolean innerInBounds(){ return colPos >= 0; }
    boolean outerInBounds(){ return rowPos >= 0; }
    void advanceInner(){ colPos--; }
    void advanceOuterResetInner(){ rowPos--; colPos=getCols()-1; }
  }
  // Move from hight to low going down columns, start in the upper
  // left, return to the first row and move to the right a column
  // after reaching the bottom in the current column, good for up
  // shifts
  class UpToDown extends Iter2D{
    UpToDown(){ rowPos=0; colPos=0; }
    private UpToDown(int r, int c){ rowPos=r; colPos = c; }
    Iter2D copy(){ return new UpToDown(rowPos, colPos); }
    boolean innerInBounds(){ return rowPos < getRows(); }
    boolean outerInBounds(){ return colPos < getCols(); }
    void advanceInner(){ rowPos++; }
    void advanceOuterResetInner(){ rowPos=0; colPos++; }
  }
  // Move from low to high up each column, start in the lower right,
  // return to the bottom and move left a column on reaching the first
  // row, good for down shifts
  class DownToUp extends Iter2D{
    DownToUp(){ rowPos=getRows()-1; colPos=getCols()-1; }
    private DownToUp(int r, int c){ rowPos=r; colPos = c; }
    Iter2D copy(){ return new DownToUp(rowPos, colPos); }
    boolean innerInBounds(){ return rowPos >= 0; }
    boolean outerInBounds(){ return colPos >= 0; }
    void advanceInner(){ rowPos--; }
    void advanceOuterResetInner(){ rowPos=getRows()-1; colPos--; }
  }

  // Return an iterator that moves over all tiles
  public Iterator<Tile> tileIterator(){
    return tiles.values().iterator();
  }

  // REQUIRED: Create a distinct copy of the board including its
  // internal tile positions and any other state. Hash table provides
  // a shallow clone() method which makes this relatively easy as the
  // TPos and Tile classes are immutable.
  public Board copy(){
	  HashBoard copyboard = new HashBoard();  						//new hashboard
	    copyboard.rows = this.rows;									//transfer row info
	    copyboard.cols = this.cols;									//transfer col info
	    HashMap<TPos,Tile> copytiles = new HashMap<TPos,Tile>();	//new hashmap
	    copytiles = (HashMap<TPos, Tile>) this.tiles.clone();		//clone tiles
	    copyboard.tiles = copytiles;								//transfer info to copy

	    return copyboard;
  }

  // REQUIRED: Determine if this HashBoard equals another object. True
  // when the other object is another HashBoard, the boards have the
  // same size and have the same tiles in the same positions.  Whether
  // the last shift has moved tiles or not should NOT factor into
  // equality.
  public boolean equals(Object o){
	  if((this.hashCode() == o.hashCode()) && (o instanceof HashBoard)){
		  return true;			//same hash code AND is instance of
	  }
	  else{
		  return false;			//fails one of the conditions
	  }
  }  
  // REQUIRED: Compute a fair hash code for the board.  Use a
  // polynomial like code. Account for the size and contents of the
  // board.  DO NOT include whether the last shift moved the board or
  // not.
  public int hashCode(){
	//declare method variables
	int hash=0;
	int positionhashcode,tilecode,i,j;

	//cycle through the tile locations in the hashmap to calculate hashcode
	for(i=0; i<this.rows; i++){
	   for(j=0; j<this.cols; j++){
		   TPos find = new TPos(i,j);
		   if(tiles.containsKey(find)){
			   positionhashcode = 31* find.hashCode();	//hashcode for row/col position
			   tilecode = tiles.get(find).hashCode();	//hashcode for tile
			   hash+=positionhashcode + tilecode;			//aggregate
		   }
	   }
	}
	hash+=31*31*this.rows;									//board rows
   	hash+=31*this.cols;									//board cols
    return hash;
  }
}
