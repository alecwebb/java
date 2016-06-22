import java.util.*;

// Represents the internal state of a game of Zombie Trap and allows
// various operations of game moves as methods.  Default is to use
// HashBoard as the internal Board but may make minor alterations to
// allow DenseBoard or SparseBoard to be used as well.
public class ZombieTrapGame implements Cloneable{
  private Board board;          // The internal board
  private Board copyboard;          // The internal board
  private int score;            // The current score of the game
  private Random random;        // The random number generator
  private int hash;	

  // Utility method to create tiles from a Scanner which usually reads
  // from a file
  private static Tile [][] tilesFromScanner(Scanner input){
    // Read the contents of the Scanner into a 2D ArrayList
    ArrayList<ArrayList<Tile>> rows = new ArrayList<ArrayList<Tile>>();
    while(input.hasNextLine()){
      Scanner line = new Scanner(input.nextLine());
      ArrayList<Tile> row = new ArrayList<Tile>();
      rows.add(row);
      while(line.hasNext()){
        String s = line.next();
        Tile t = null;
        if(s.equals("-")){ t=null; }
        else if(s.equals("BRCK")){ t=new Brick(); }
        else if(s.equals("PIT")){ t=new Pit(); }
        else if(s.equals("Zomb")){ t=new Zombie(); }
        else{ throw new RuntimeException("What is a "+s); }
        row.add(t);
      }
    }
    int m = rows.size();
    int n = rows.get(0).size();
    Tile [][] tiles = new Tile[m][n];
    // Prepare a 2D array of tiles for use in constructors
    for(int i=0; i<m; i++){
      for(int j=0; j<n; j++){
        tiles[i][j] = rows.get(i).get(j);
      }
    }
    return tiles;
  }

  // Create a ZombieTrapGame from a Scanner which usually reads from a
  // file for file loading
  public ZombieTrapGame(Scanner input, int seed){
    this(tilesFromScanner(input), 0, seed);
  }

  // Create a game with a HashBoard with the given number of rows and
  // columns. Initialize the game's internal random number generator
  // to the given seed.
  public ZombieTrapGame(int rows, int cols, int score, int seed) {
    this(rows,cols,seed,score,"hash");
  }

  // Create a game with a HashBoard which has the given arrangement
  // of tiles. Initialize the game's internal random number generator
  // to the given seed.
  public ZombieTrapGame(Tile tiles[][], int score, int seed ) {
    this(tiles,seed,score,"hash");
  }

  // Create a game with the given board type with the given number of
  // rows and columns.  Initial score is the value given.  Initialize
  // the game's internal random number generator to the given seed.
  public ZombieTrapGame(int rows, int cols, int score, int seed,  String boardType) {
    this.score = score;
    this.random = new Random(seed);
         if(boardType.equals("hash"))  { this.board = new HashBoard(rows,cols);  }
    // else if(boardType.equals("dense")) { this.board = new DenseBoard(rows,cols);  }
    // else if(boardType.equals("sparse")){ this.board = new SparseBoard(rows,cols); }
    else { throw new RuntimeException("Unsupported board type: "+boardType); }
  }

  // Create a game with the given board type which has the given
  // arrangement of tiles. Initial score is the value
  // given. Initialize the game's internal random number generator to
  // the given seed.
  public ZombieTrapGame(Tile tiles[][], int score, int seed,  String boardType) {
    this.score = score;
    this.random = new Random(seed);
         if(boardType.equals("hash"))  { this.board = new HashBoard(tiles);  }
    // else if(boardType.equals("dense")) { this.board = new DenseBoard(tiles);  }
    // else if(boardType.equals("sparse")){ this.board = new SparseBoard(tiles); }
    else { throw new RuntimeException("Unsupported board type: "+boardType); }
  }

  // Return the number of rows in the Game
  public int getRows(){
    return this.board.getRows();
  }

  // Return the number of columns in the Game
  public int getCols(){
    return this.board.getCols();
  }

  // Return the current score of the game.
  public int getScore(){
    return this.score;
  }

  // Return a string representation of the board; useful for text UIs
  // like PlayZombieTrap
  public String boardString(){
    return board.toString();
  }

  // Return the tile at a given position in the grid; throws an
  // exception if the request is out of bounds. Potentially useful for
  // more complex UIs which want to lay out tiles individually.
  public Tile tileAt(int i, int j){
    return board.tileAt(i,j);
  }

  // Shift tiles left and update the score
  public void shiftLeft(){
    this.score += board.shiftLeft();
  }
  // Shift tiles right and update the score
  public void shiftRight(){
    this.score += board.shiftRight();
  }
  // Shift tiles up and update the score
  public void shiftUp(){
    this.score += board.shiftUp();
  }
  // Shift tiles down and update the score
  public void shiftDown(){
    this.score += board.shiftDown();
  }

  // If the game board has F>0 free spaces, return a random integer
  // between 0 and F-1.  If there are no free spaces, throw an
  // exception.
  public int randomFreeLocation(){
    int freeSpaces = this.board.getFreeSpaceCount();
    return this.random.nextInt(freeSpaces);
  }

  // Add a random tile to a random free position. The type of the tile
  // may be one of zombie, pit, or brick
  public void addRandomTile(String tileType){
    int freeSpaces = this.board.getFreeSpaceCount();
    if(freeSpaces == 0){
      return;
    }
    int location = randomFreeLocation();
    Tile tile = null;
         if(tileType.equals("zombie")) { tile = new Zombie(); }
    else if(tileType.equals("pit"))    { tile = new Pit();    }
    else if(tileType.equals("brick"))  { tile = new Brick();  }
    else { throw new RuntimeException("Unsupported tile type: "+tileType); }
    board.addTileAtFreeSpace(location,tile);
  }

  // Returns true if any zombies remain on the board and false otherwise
  public boolean isGameOver(){
    Iterator<Tile> iter = this.board.tileIterator();
    while(iter.hasNext()){
      Tile t = iter.next();
      if(t instanceof Zombie){
        return false;
      }
    }
    return true;
  }

  // Pretty print some representation of the game. No specific format
  // is required, used mainly for debugging purposes.
  public String toString(){
    return String.format("Score: %d\n%s",
                         getScore(), board);
  }

  private ZombieTrapGame(){ }

  // REQUIRED: Make a copy of a game. The copy should have identical
  // internal state (board/score) EXCEPT for the any random number
  // generators which may be shared between the original and they
  // copy.
  public ZombieTrapGame copy(){
	  ZombieTrapGame zcopy = new ZombieTrapGame();	//new zombietrap game
	  zcopy.score = this.score;						//set score
	  zcopy.board = this.board.copy();				//copy board
	
	  return zcopy;
  }

  // REQUIRED: True when o is another ZombieTrapGame and the score and
  // state of o match this game. False otherwise.
  public boolean equals(Object o){
	  if(this.hashCode() == o.hashCode()&& (o instanceof ZombieTrapGame)){
		  return true;			//same code, and type
	  }
	  else{
		  return false;
	  }
  }

  // REQUIRED: Compute a hash code for the game.  The hash code should
  // be based on the state the board and the current score of the
  // game.  Two games with the same same tiles but a different score
  // should have different hash codes.
  public int hashCode(){
		int hash;
		hash = (31 * score) + this.board.hashCode();  //factore in score to hashcode call to board
		
	    return hash;
  }
}

