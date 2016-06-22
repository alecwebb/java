// HashBoard Public Tests
// 
// UNIX Command line instructions
// 
// Compile
// lila [ckauffm2-hw3]% javac -cp .:junit-4.11.jar *.java
// 
// Run tests
// lila [ckauffm2-hw3]% java -cp .:junit-4.11.jar HashBoardTest
// 
// WINDOWS Command line instructions: replace colon with semicolon
// 
// Compile
// lila [ckauffm2-hw3]% javac -cp .;junit-4.11.jar *.java
// 
// Run tests
// lila [ckauffm2-hw3]% java -cp .;junit-4.11.jar HashBoardTest
// 
// For IDEs, consult documentation to see how to run junit tests

// org.junit.Assert.assertEquals - statically imported
// assertEquals(message, expected, actual);
import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import java.util.*;

//@RunWith(Parameterized.class)
public class HashBoardTest {
  public static String boardType = "hash";

  /*Main method runs tests in this file*/ 
  public static void main(String args[]) {
    if(args.length>0){
      boardType = args[0];
    }
    System.out.println("Running with board type "+boardType);
    org.junit.runner.JUnitCore.main("HashBoardTest");
  } 

  public static Board makeBoard(Tile tiles[][]){
         if(boardType.equals("hash"))  { return new HashBoard(tiles); }
   // else if(boardType.equals("dense")) { return new DenseBoard(tiles);  }
   // else if(boardType.equals("sparse")){ return new SparseBoard(tiles);  }
    else { throw new RuntimeException("Unsupported board type: "+boardType); }
  }
  public static ZombieTrapGame makeGame(Tile tiles[][], int initialScore){
    return new ZombieTrapGame(tiles, initialScore, 0, boardType);
  }

  static class DummyTile extends Tile{
    public boolean equals(Object o){
      return o instanceof DummyTile;
    }
    public int hashCode(){ return 99; }
    public String toString(){ return "Dumb"; }
    public int getScore(){ return -99; }
    public boolean mergesWith(Tile t){ return false; }
    public Tile merge(Tile t){ return null; }
  }
  public static final Tile Zomb = new Zombie();
  public static final Tile BRCK = new Brick();
  public static final Tile PIT  = new Pit();
  public static final Tile Dumb = new DummyTile();

  // Utility to compare two boards
  public static 
    void compareBoards(Board b1, Board b2, 
                       boolean expectEqual, boolean expectHCEqual){
    String msg;
    String base_msg = 
      String.format("Board 1: hashCode = %s\n%sBoard 2: hashCode = %s\n%s",
                    b1.hashCode(),b1,b2.hashCode(),b2);
    boolean actualEqual = b1.equals(b2);
    msg = String.format("\nExpect equals: %s\nActual equals: %s\n%s",
                        expectEqual, actualEqual, base_msg);
    assertEquals(msg,expectEqual,actualEqual);
    boolean actualHCEqual = b1.hashCode()==b2.hashCode();
    msg = String.format("\nExpect hash codes equal: %s\nActual hash codes equal: %s\n%s",
                        expectHCEqual, actualHCEqual, base_msg);
    assertEquals(msg,expectHCEqual,actualHCEqual);
  }    

  // Utility to compare two games
  public static 
    void compareGames(ZombieTrapGame g1, ZombieTrapGame g2, 
                      boolean expectEqual, boolean expectHCEqual){
    String msg;
    String base_msg = 
      String.format("Game 1: hashCode = %s\n%sGame 2: hashCode = %s\n%s",
                    g1.hashCode(),g1,g2.hashCode(),g2);
    boolean actualEqual = g1.equals(g2);
    msg = String.format("\nExpect board equals: %s\nActual board equals: %s\n%s",
                        expectEqual, actualEqual, base_msg);
    assertEquals(msg,expectEqual,actualEqual);
    boolean actualHCEqual = g1.hashCode()==g2.hashCode();
    msg = String.format("\nExpect hash codes equal: %s\nActual hash codes equal: %s\n%s",
                        expectHCEqual, actualHCEqual, base_msg);
    assertEquals(msg,expectHCEqual,actualHCEqual);
  }    


  ////////////////////////////////////////////////////////////////////////////////
  // HashBoard tests
  @Test public void board_equal1(){
    Tile [][] tiles = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    Board b1 = makeBoard(tiles);
    Board b2 = makeBoard(tiles);
    compareBoards(b1,b2,true,true);
  }
  @Test public void board_equal2(){
    Tile [][] tiles = {
      {null, null, null},
      {null, null, null},
      {null, null, null},
    };
    Board b1 = makeBoard(tiles);
    Board b2 = makeBoard(tiles);
    compareBoards(b1,b2,true,true);
  }
  @Test public void board_equal3(){
    Tile [][] tiles = {
      {null, Dumb},
      {Dumb, null},
      {null, Dumb},
      {Dumb, null},
      {null, Dumb},
    };
    Board b1 = makeBoard(tiles);
    Board b2 = makeBoard(tiles);
    compareBoards(b1,b2,true,true);
  }

  @Test public void board_not_equal1(){
    Tile [][] tiles1 = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    Tile [][] tiles2 = {
      {Zomb, BRCK, null,  PIT},
      {Zomb, null, null,  PIT},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal2(){
    Tile [][] tiles1 = {
      {Zomb},
      {null},
    };
    Tile [][] tiles2 = {
      {null},
      {Zomb},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal4(){
    Tile [][] tiles1 = {
      {null},
      {null},
    };
    Tile [][] tiles2 = {
      {null},
      {null},
      {null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal5(){
    Tile [][] tiles1 = {
      {null,null,null},
    };
    Tile [][] tiles2 = {
      {null,null,null,null,null,null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal6(){
    Tile [][] tiles1 = {
      {Zomb, null},
      {null, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {null, Zomb},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal7(){
    Tile [][] tiles1 = {
      {null, null, BRCK, null},
      {Zomb,  PIT, null, null},
      {null, null, null, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {BRCK, null},
      {null, Zomb},
      { PIT, null},
      {null, null},
      {null, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal8(){
    Tile [][] tiles1 = {
      {null, null, null, null},
      {null, null, null, null},
      {null, null, null, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {null, null},
      {null, null},
      {null, null},
      {null, null},
      {null, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_not_equal9(){
    Tile [][] tiles1 = {
      {null, null, null, null},
      {Dumb, null, Dumb, null},
      {null, null, null, Dumb},
    };
    Tile [][] tiles2 = {
      {null, null, null, null},
      {Dumb, null, Dumb, null},
      {null, null, null, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    compareBoards(b1,b2,false,false);
  }

  @Test public void board_equal_shift1(){
    Tile [][] tiles1 = {
      {Zomb, null},
      {null, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {null, Zomb},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    b1.shiftDown(); b1.shiftRight();
    compareBoards(b1,b2,true,true);
  }
  @Test public void board_equal_shift2(){
    Tile [][] tiles1 = {
      {Zomb, null},
      {null, null},
      { PIT, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {null, null},
      { PIT, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = makeBoard(tiles2);
    b1.shiftDown(); 
    compareBoards(b1,b2,true,true);
  }

  @Test public void board_copy1(){
    Tile [][] tiles1 = {
      {null, null, null, null},
      {null, null, null, null},
      {null, null, null, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = b1.copy();
    compareBoards(b1,b2,true,true);
    b1.addTileAtFreeSpace(3,Zomb);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_copy2(){
    Tile [][] tiles1 = {
      {null, null, null, null},
      {Zomb, Dumb, BRCK, PIT},
      {null, null, null, null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = b1.copy();
    compareBoards(b1,b2,true,true);
    b1.addTileAtFreeSpace(5,Zomb);
    compareBoards(b1,b2,false,false);
  }
  @Test public void board_copy3(){
    Tile [][] tiles1 = {
      {null},
      {null},
      { PIT},
      {null},
      {Zomb},
      {null},
    };
    Board b1 = makeBoard(tiles1);
    Board b2 = b1.copy();
    compareBoards(b1,b2,true,true);
    b1.addTileAtFreeSpace(0,Zomb);
    b1.addTileAtFreeSpace(0,BRCK);
    compareBoards(b1,b2,false,false);
  }
  
  ////////////////////////////////////////////////////////////////////////////////
  // ZombieTrapGame Tests

  @Test public void game_equal1(){
    Tile [][] tiles = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    ZombieTrapGame b1 = makeGame(tiles,0);
    ZombieTrapGame b2 = makeGame(tiles,0);
    compareGames(b1,b2,true,true);
  }
  @Test public void game_equal2(){
    Tile [][] tiles = {
      {Zomb, BRCK, null,  PIT},
      {Zomb, null, null,  PIT},
    };
    ZombieTrapGame b1 = makeGame(tiles,5);
    ZombieTrapGame b2 = makeGame(tiles,5);
    compareGames(b1,b2,true,true);
  }
  @Test public void game_equal3(){
    Tile [][] tiles = {
      {null, null, null},
      {null, null, null},
    };
    ZombieTrapGame b1 = makeGame(tiles,5);
    ZombieTrapGame b2 = makeGame(tiles,5);
    compareGames(b1,b2,true,true);
  }

  @Test public void game_not_equal1(){
    Tile [][] tiles1 = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    Tile [][] tiles2 = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,10);
    compareGames(b1,b2,false,false);
  }
  @Test public void game_not_equal2(){
    Tile [][] tiles1 = {
      {Zomb, BRCK, null,  PIT},
      {Zomb, null, null,  PIT},
    };
    Tile [][] tiles2 = {
      {Zomb, BRCK, null,  PIT},
      {null, Zomb, null,  PIT},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,0);
    compareGames(b1,b2,false,false);
  }
  @Test public void game_not_equal3(){
    Tile [][] tiles1 = {
      {null, null, null, null},
      {null, null, null, null},
      {null, null, null, null},
    };
    Tile [][] tiles2 = {
      {null, null},
      {null, null},
      {null, null},
      {null, null},
      {null, null},
      {null, null},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,0);
    compareGames(b1,b2,false,false);
  }
  @Test public void game_not_equal4(){
    Tile [][] tiles1 = {
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
    };
    Tile [][] tiles2 = {
      {BRCK, BRCK, BRCK, BRCK},
      {BRCK, BRCK, BRCK, BRCK},
      {BRCK, BRCK, BRCK, BRCK},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,0);
    compareGames(b1,b2,false,false);
  }
  @Test public void game_not_equal5(){
    Tile [][] tiles1 = {
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
    };
    Tile [][] tiles2 = {
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,1);
    compareGames(b1,b2,false,false);
  }

  @Test public void game_equal_shift1(){
    Tile [][] tiles1 = {
      {null, Zomb, null, null},
      {Zomb, null, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
    };
    Tile [][] tiles2 = {
      {null, null, null, null},
      {Zomb, Zomb, Zomb, Zomb},
      {Zomb, Zomb, Zomb, Zomb},
    };
    ZombieTrapGame b1 = makeGame(tiles1,2);
    ZombieTrapGame b2 = makeGame(tiles2,2);
    b1.shiftDown();
    compareGames(b1,b2,true,true);
  }
  @Test public void game_equal_shift2(){
    Tile [][] tiles1 = {
      {null, Zomb, null, null},
      {Zomb, null, BRCK, null},
      {Zomb, Zomb, Zomb,  PIT},
    };
    Tile [][] tiles2 = {
      {null, null, null, Zomb},
      {null, Zomb, BRCK, null},
      {null, Zomb, Zomb,  PIT},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = makeGame(tiles2,1);
    b1.shiftRight();
    compareGames(b1,b2,true,true);
  }

  @Test public void game_copy1(){
    Tile [][] tiles1 = {
      {null},
      {null},
      { PIT},
      {null},
      {Zomb},
      {null},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = b1.copy();
    compareGames(b1,b2,true,true);
    b1.addRandomTile("zombie");
    compareGames(b1,b2,false,false);
  }
  @Test public void game_copy2(){
    Tile [][] tiles1 = {
      {null},
      {Zomb},
      { PIT},
      {null},
      {Zomb},
      {null},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    ZombieTrapGame b2 = b1.copy();
    compareGames(b1,b2,true,true);
    b1.shiftUp();
    compareGames(b1,b2,false,false);
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Check for shortcuts in hash code computations
  @Test public void game_shortcut1(){
    Tile [][] tiles1 = {
      {null},
      {Zomb},
      { PIT},
      {null},
      {Zomb},
      {null},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    int stringHC = b1.toString().hashCode();
    int gameHC = b1.hashCode();
    String msg = "\nGames should not use toString() for hash code computations";
    // assertNotEquals(msg,stringHC,gameHC);
    assertFalse(msg,gameHC==stringHC);
  }
  @Test public void game_shortcut2(){
    Tile [][] tiles1 = {
      {null, null, null},
      {null, Dumb, null},
      {null, null, Zomb},
    };
    ZombieTrapGame b1 = makeGame(tiles1,0);
    int stringHC = b1.toString().hashCode();
    int gameHC = b1.hashCode();
    String msg = "\nGames should not use toString() for hash code computations";
    // assertNotEquals(msg,stringHC,gameHC);
    assertFalse(msg,gameHC==stringHC);
  }
  @Test public void board_shortcut1(){
    Tile [][] tiles1 = {
      {null},
      {Zomb},
      { PIT},
      {null},
      {Zomb},
      {null},
    };
    Board b1 = makeBoard(tiles1);
    int stringHC = b1.toString().hashCode();
    int boardHC = b1.hashCode();
    String msg = "\nBoards should not use toString() for hash code computations";
    // assertNotEquals(msg,stringHC,gameHC);
    assertFalse(msg,boardHC==stringHC);
  }
  @Test public void board_shortcut2(){
    Tile [][] tiles1 = {
      {null, null, null},
      {null, Dumb, null},
      {null, null, Zomb},
    };
    Board b1 = makeBoard(tiles1);
    int stringHC = b1.toString().hashCode();
    int boardHC = b1.hashCode();
    String msg = "\nBoards should not use toString() for hash code computations";
    // assertNotEquals(msg,stringHC,gameHC);
    assertFalse(msg,boardHC==stringHC);
  }
  @Test public void board_shortcut3(){
    Tile [][] tiles1 = {
      {null},
    };
    Board b1 = makeBoard(tiles1);
    int stringHC = b1.toString().hashCode();
    int boardHC = b1.hashCode();
    String msg = "\nBoards should not use toString() for hash code computations";
    // assertNotEquals(msg,stringHC,gameHC);
    assertFalse(msg,boardHC==stringHC);
  }
  
}      
