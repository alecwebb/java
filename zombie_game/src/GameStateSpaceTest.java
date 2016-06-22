// Game Space State Public Tests : Sat Nov  1 19:47:08 EDT 2014 
// 
// UNIX Command line instructions
// 
// Compile
// lila [ckauffm2-hw3]% javac -cp .:junit-4.11.jar *.java
// 
// Run tests
// lila [ckauffm2-hw3]% java -cp .:junit-4.11.jar GameStateSpaceTest
// 
// WINDOWS Command line instructions: replace colon with semicolon
// 
// Compile
// lila [ckauffm2-hw3]% javac -cp .;junit-4.11.jar *.java
// 
// Run tests
// lila [ckauffm2-hw3]% java -cp .;junit-4.11.jar GameStateSpaceTest
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

@RunWith(Parameterized.class)
public class GameStateSpaceTest {
  /*Main method runs tests in this file*/ 
  public static void main(String args[]) {
    if(args.length>0){
      boardType = args[0];
    }
    System.out.println("Running with board type "+boardType);
    org.junit.runner.JUnitCore.main("GameStateSpaceTest");
  } 

  // Which kind of board will be used in the games
  static String boardType = "hash";
  // Controls whether all states of a game state are shown on errors
  // If set to true, will show every game state found during a failed
  // test which may result in VERY LONG output but can be helpful.
  boolean APPEND_ALL_STATES = false;

  static final String u = "u";
  static final String d = "d";
  static final String l = "l";
  static final String r = "r";

  public String name;
  public ZombieTrapGame initial;
  public int expectStateCount;
  public int expectBestScore;
  public List<String> expectBestMoves;
  public List<GameAndMoves> targetGameMoves;

  public GameStateSpaceTest(String name,
                            ZombieTrapGame initial,
                            int expectStateCount,
                            int expectBestScore,
                            String[] expectBestMoves,
                            List<GameAndMoves> targets)
  {
    this.name              = name;
    this.initial           = initial;
    this.expectStateCount  = expectStateCount;
    this.expectBestScore   = expectBestScore;
    this.expectBestMoves   = Arrays.asList(expectBestMoves);
    this.targetGameMoves   = targets;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Name: "+name+"\n");
    sb.append("Initial State:\n"+initial.toString());
    sb.append("Expect State Count: "+expectStateCount+"\n");
    sb.append("Expect Best Score:  "+expectBestScore+"\n");
    sb.append("Expect Best Moves:  "+expectBestMoves+"\n");
    return sb.toString();
  }

  public void appendAllStates(GameStateSpace states, StringBuilder sb){
    if(APPEND_ALL_STATES){
      sb.append("----------------------------\n");
      sb.append("States in the GameStateSpace\n");
      sb.append(""+states.stateCount()+" states\n");
      sb.append("----------------------------\n");
      for(ZombieTrapGame state : states){
        sb.append(String.format("Hash Code: %s\n%s\n",state.hashCode(),state));
      }
    }
  }    

  @Test(timeout=2000) public void testConstructor(){
    GameStateSpace states = new GameStateSpace(this.initial);
    // throw new RuntimeException("Fail!");
  }

  @Test(timeout=2000) public void testStateCount(){
    GameStateSpace states = new GameStateSpace(this.initial);
    int actualStateCount = states.stateCount();
    StringBuilder sb = new StringBuilder(this.name+"\n");
    sb.append("Actual and Expected stateCount do not match:\n");
    sb.append(String.format("Expect stateCount: %s\n",expectStateCount));
    sb.append(String.format("Actual stateCount: %s\n",actualStateCount));
    sb.append(this.toString());
    appendAllStates(states, sb);
    assertEquals(sb.toString(), expectStateCount, actualStateCount);
    // throw new RuntimeException("Fail!");
  }

  @Test(timeout=2000) public void testBestScore(){
    GameStateSpace states = new GameStateSpace(this.initial);
    int actualBestScore = states.bestScore();
    StringBuilder sb = new StringBuilder(this.name+"\n");
    sb.append("Actual and Expected bestScore do not match:\n");
    sb.append(String.format("Expect bestScore: %s\n",expectBestScore));
    sb.append(String.format("Actual bestScore: %s\n",actualBestScore));
    sb.append(this.toString());
    appendAllStates(states, sb);
    assertEquals(sb.toString(), expectBestScore, actualBestScore);
    // throw new RuntimeException("Fail!");
  }

  @Test(timeout=2000) public void testBestMoves(){
    StringBuilder sb = new StringBuilder(this.name+"\n");
    GameStateSpace states = new GameStateSpace(this.initial);
    List<String> actualBestMoves = states.bestMoves();
    // actualBestMoves.add("x");
    if(expectBestMoves.equals(actualBestMoves)){
      return;
    }
    else if(expectBestMoves.size() < actualBestMoves.size()){
      sb.append("Actual and Expected bestMoves do not match:\n");
      sb.append(String.format("Expect bestMoves: %s\n",expectBestMoves));
      sb.append(String.format("Actual bestMoves: %s\n",actualBestMoves));
      sb.append(this.toString());
      appendAllStates(states, sb);
      assertEquals(sb.toString(), expectBestMoves, actualBestMoves);
    }
    // Returned an equal length or shorter actual best moves. Check
    // whether the actual best moves actually produces the same score
    // as as the expected best moves.
    ZombieTrapGame game = this.initial.copy();
    for(String move : actualBestMoves){
      doShift(game,move);
    }
    // Check if an alternate equal length bestMoves was found
    if(game.getScore()==expectBestScore && 
       actualBestMoves.size() == expectBestMoves.size()){
      return;
    }
    // Shorter sequence to best score is a bug in tests
    else{                       
      sb.append("Actual bestMoveSequence is SHORTER than the expectedBestMove sequence\n");
      sb.append("YOU HAVE DISCOVERED A BUG IN THE TEST CASES: NOTIFY THE COURSE INSTRUCTOR\n");
      sb.append(String.format("Expect bestMoves: %s\n",expectBestMoves));
      sb.append(String.format("Actual bestMoves: %s\n",actualBestMoves));
      sb.append(this.toString());
      appendAllStates(states, sb);
      fail(sb.toString());
    }
  }

  @Test(timeout=2000) public void testStateReachable(){
    GameStateSpace states = new GameStateSpace(this.initial);
    for(int target=0; target<this.targetGameMoves.size(); target++){
      GameAndMoves gm = this.targetGameMoves.get(target);
      ZombieTrapGame targetGame = gm.game;
      List<String> targetMoves = gm.moves;
      boolean actualStateReachable = states.stateReachable(targetGame);
      boolean expectStateReachable = gm.moves!=null;
      // boolean expectStateReachable = gm.moves==null;
      StringBuilder sb = new StringBuilder(this.name+"\n");
      sb.append("Actual and Expected stateReachable do not match:\n");
      sb.append(String.format("Expect stateReachable: %s\n",expectStateReachable));
      sb.append(String.format("Actual stateReachable: %s\n",actualStateReachable));
      sb.append(String.format("Target Game:\n%s",targetGame));
      sb.append(String.format("Moves to Reach: %s\n",targetMoves));
      sb.append(this.toString());
      appendAllStates(states, sb);
      assertEquals(sb.toString(), expectStateReachable, actualStateReachable);
      // throw new RuntimeException("Fail!");
    }
  }
  // Check if the move sequence produced actuall reaches the target state
  @Test(timeout=2000) public void testMovesToReach(){
    GameStateSpace states = new GameStateSpace(this.initial);
    // For every target state based on the initial game look at a
    // target and calculate the moves to reach it
    for(int target=0; target<this.targetGameMoves.size(); target++){
      GameAndMoves gm = this.targetGameMoves.get(target);
      ZombieTrapGame targetGame = gm.game;
      List<String> expectMoves = gm.moves;
      List<String> actualMoves = states.movesToReach(targetGame);
      StringBuilder sb = new StringBuilder(this.name+"\n");
      sb.append("TARGET GAME NUMBER "+target+"\n");
      
      // Check if the moves are equal to the expectation
      if(expectMoves==null && actualMoves==null){
        continue;
      }
      else if(expectMoves !=null && expectMoves.equals(actualMoves)){
        continue;
      }
      else if(expectMoves!=null && actualMoves==null){
        sb.append("State misidentified as NOT reachable\n");
        sb.append(String.format("Expect sequence: %s\n",expectMoves));
        sb.append(String.format("Actual sequence: %s\n",actualMoves));
        sb.append(String.format("Target Game:\n%s",targetGame));
        sb.append(this.toString());
        appendAllStates(states, sb);
        fail(sb.toString());
      }

      // ALREADY THERE
      // If equal length do they produce the correct board: could be
      // alternate paths to the same state
      ZombieTrapGame game = this.initial.copy();
      for(String move : actualMoves){
        doShift(game,move);
      }

      // ADD THIS CODE to fix a bug in the original test cases
      if(expectMoves==null && actualMoves!=null){
        sb.append("State misidentified as IS reachable\n");
        sb.append(String.format("Expect sequence: %s\n",expectMoves));
        sb.append(String.format("Actual sequence: %s\n",actualMoves));
        sb.append(String.format("Target Game:\n%s",targetGame));
        sb.append(String.format("Actual End Game:\n%s",game));
        sb.append(this.toString());
        appendAllStates(states, sb);
        fail(sb.toString());
      }

      // ALREADY THERE
      if(!game.equals(targetGame)){ // Moves don't lead to target state
        sb.append("Actual move sequence does not lead to target state\n");
        sb.append(String.format("Expect sequence (length %s): %s\n",expectMoves.size(),expectMoves));
        sb.append(String.format("Target Game:\n%s",targetGame));
        sb.append(String.format("Actual sequence (length %s): %s\n",actualMoves.size(),actualMoves));
        sb.append(String.format("Actual End Game:\n%s",game));
      }                  
      // Different path but same length, not an error
      else if(actualMoves.size() == expectMoves.size()){
        continue;
      }
      // Check if the moves are longer than the expected
      else if(actualMoves.size() > expectMoves.size()){
        sb.append("Actual move sequence is longer than the expected move sequence\n");
        sb.append(String.format("Expect sequence (length %s): %s\n",expectMoves.size(),expectMoves));
        sb.append(String.format("Actual sequence (length %s): %s\n",actualMoves.size(),actualMoves));
        sb.append(String.format("Target Game:\n%s",targetGame));
      }
      // A shorter path!?
      else {
        sb.append("Actual move sequence is SHORTER than the expected move sequence\n");
        sb.append("YOU HAVE DISCOVERED A BUG IN THE TEST CASES: NOTIFY THE COURSE INSTRUCTOR\n");
        sb.append(String.format("Expect sequence (length %s): %s\n",expectMoves.size(),expectMoves));
        sb.append(String.format("Actual sequence (length %s): %s\n",actualMoves.size(),actualMoves));
        sb.append(String.format("Target Game:\n%s",targetGame));
      }
      sb.append(this.toString());
      appendAllStates(states, sb);
      fail(sb.toString());
    }
  }

  // Simple class to hold a pair of a target game and the moves to
  // reach it
  public static class GameAndMoves{
    public ZombieTrapGame game;
    public List<String> moves;
    public GameAndMoves(ZombieTrapGame g, List<String> m){
      this.game=g; this.moves=m;
    }
    public GameAndMoves(ZombieTrapGame g, String[] m){
      this.game=g;
      this.moves= (m==null) ? null : Arrays.asList(m);
    }
  }

  // Utility method to create tiles from a Scanner which usually reads
  // from a file
  static Tile [][] tilesFromScanner(Scanner input){
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


  // Utility to create a board form a string
  public static ZombieTrapGame makeGame(String initialBoard, int initialScore){
    Tile [][] tiles = tilesFromScanner(new Scanner(initialBoard));
    return new ZombieTrapGame(tiles,initialScore,0, boardType);
  }

  // Utility to do a shift on a game based a string argument for the
  // direction
  public static void doShift(ZombieTrapGame game, String shift){
    switch(shift){ 
      case "u": game.shiftUp();     break;   // case "u": 
      case "d": game.shiftDown();   break;   // case "d": 
      case "l": game.shiftLeft();   break;   // case "l": 
      case "r": game.shiftRight();  break;   // case "r": 
      default: throw new RuntimeException("Something is terribly wrong.");
    }
  }


  // @Parameters(name= "Test Game {index}") // name parameter is incompatible with DrJava

  // The method below constructs a set of test data which is run on
  // the above @test methods. To add boards that will be tested,
  // construct an array of objects and add it to the targetGameMoves
  // variable below.  The arrray of objects correpsond to the fields
  // GameStateSpaceTest:
  // 
  // {name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves}
  @Parameters
  public static Iterable<Object[]> makeTestData() {
    String name;
    ZombieTrapGame initial;
    int expectStateCount;
    int expectBestScore;
    String[] expectBestMoves;
    ZombieTrapGame targetGame;
    String[] targetMoves;
    List<GameAndMoves> targetGameMoves;
    List<ZombieTrapGame> unreachableGames;

    ArrayList<Object[]> testData = new ArrayList<Object[]>();

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 0";
    initial = makeGame(
                       "   - BRCK \n"+
                       "   -  PIT \n"+
                       "   -    - \n"+
                       "   - Zomb \n"+
                       "", 0);
    expectStateCount = 4;
    expectBestScore = 1;
    expectBestMoves = new String[]{u};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                          "   - BRCK \n"+
                          "   -  PIT \n"+
                          "   -    - \n"+
                          "Zomb    - \n"+
                          "", 0);
    targetMoves = new String[]{l};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1
    targetGame = makeGame(
                          "Zomb BRCK \n"+
                          "   -  PIT \n"+
                          "   -    - \n"+
                          "   -    - \n"+
                          "", 0);
    targetMoves = new String[]{l,u};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 2 Unreachable
    targetGame = makeGame(
                          "   - BRCK \n"+
                          "   -  PIT \n"+
                          "   - Zomb \n"+
                          "   -    - \n"+
                          "", 0);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 1";
    initial = makeGame(
                       "   - BRCK    -    -    -    - \n"+
                       "   - BRCK    -  PIT    -    - \n"+
                       "Zomb Zomb    - Zomb    -    - \n"+
                       "",0);
    expectStateCount = 42;
    expectBestScore = 2;
    expectBestMoves = new String[]{r,u,l,d};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                       "   - BRCK    -    -    -    - \n"+
                       "   - BRCK    -  PIT    -    - \n"+
                       "   -    -    -    - Zomb Zomb \n"+
                       "",1);
    targetMoves = new String[]{u,d,r};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1
    targetGame = makeGame(
                       "Zomb BRCK    -    -    -    - \n"+
                       "Zomb BRCK    -  PIT    -    - \n"+
                       "Zomb    -    -    -    -    - \n"+
                       "",0);
    targetMoves = new String[]{l,u,l,d,l,u,l};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 2 Unreachable
    targetGame = makeGame(
                       "   - BRCK    -    -    -    - \n"+
                       "   - BRCK    -  PIT    -    - \n"+
                       "   -    -    -    -    -    - \n"+
                       "",3);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 2";
    initial = makeGame(
                       "   - Zomb    - \n"+
                       "   -  PIT    - \n"+
                       "   - Zomb    - \n"+
                       "",0);
    expectStateCount = 14;
    expectBestScore = 2;
    expectBestMoves = new String[]{d,u};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                       "   -    -    - \n"+
                       "   -  PIT Zomb \n"+
                       "   -    - Zomb \n"+
                       "",0);
    targetMoves = new String[]{r,d};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1
    targetGame = makeGame(
                       "   -    -    - \n"+
                       "   -  PIT    - \n"+
                       "Zomb    -    - \n"+
                       "",1);
    targetMoves = new String[]{d,l};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 2 Unreachable
    targetGame = makeGame(
                       "   -    -    - \n"+
                       "Zomb  PIT    - \n"+
                       "   -    -    - \n"+
                       "",1);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 3";
    initial = makeGame(
                       "Zomb    -    - \n"+
                       "   -    -    - \n"+
                       "   -    - BRCK \n"+
                       "   -  PIT    - \n"+
                       "",0);
    expectStateCount = 6;
    expectBestScore = 1;
    expectBestMoves = new String[]{d,r};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                       "   -    -    - \n"+
                       "Zomb    -    - \n"+
                       "   -    - BRCK \n"+
                       "   -  PIT    - \n"+
                       "",0);
    targetMoves = new String[]{r,d,l};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1 Unreachable
    targetGame = makeGame(
                       "   -    -    - \n"+
                       "   -    -    - \n"+
                       "   -    - BRCK \n"+
                       "   -  PIT Zomb \n"+
                       "",0);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 4";
    initial = makeGame(
                       " Zomb  BRCK    -    - \n"+
                       "  PIT     -    -    - \n"+
                       "    -     -    - Zomb \n"+
                       "",0);
    expectStateCount = 11;
    expectBestScore = 2;
    expectBestMoves = new String[]{l,u,d};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                       "    -  BRCK    -    - \n"+
                       "  PIT     -    -    - \n"+
                       "    -     - Zomb    - \n"+
                       "",1);
    targetMoves = new String[]{u,l,d};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 5";
    initial = makeGame(
                       "   -    -    -    -    -    - Zomb \n"+
                       " PIT    -    - BRCK    -    -    - \n"+
                       "   -    -  PIT Zomb    -    -    - \n"+
                       "   -    -    - Zomb    -    -    - \n"+
                       "",0);
    expectStateCount = 37;
    expectBestScore = 3;
    expectBestMoves = new String[]{l,u,d};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                       "   -    -    -    -    -    -    - \n"+
                       " PIT    -    - BRCK    -    -    - \n"+
                       "   -    -  PIT    -    -    -    - \n"+
                       "   - Zomb    -    - Zomb    -    - \n"+
                       "",1);
    targetMoves = new String[]{d,r,u,l,d};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1 Unreachable
    targetGame = makeGame(
                       "   -    -    -    -    -    -    - \n"+
                       " PIT    -    - BRCK    -    -    - \n"+
                       "   -    -  PIT    -    -    -    - \n"+
                       "   - Zomb    -    - Zomb    -    - \n"+
                       "",0);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 6";
    initial = makeGame(
                       "   - Zomb    - Zomb    - \n"+
                       "BRCK BRCK BRCK Zomb Zomb \n"+
                       "BRCK BRCK  PIT  PIT Zomb \n"+
                       "Zomb Zomb Zomb Zomb Zomb \n"+
                       "BRCK    - Zomb BRCK BRCK \n"+
                       "",0);
    expectStateCount = 476;
    expectBestScore = 10;
    expectBestMoves = new String[]{l, u, d, l, r, u, u, d, l};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                          "   -    - Zomb    - Zomb \n"+
                          "BRCK BRCK BRCK    - Zomb \n"+
                          "BRCK BRCK  PIT  PIT    - \n"+
                          "   -    -    -    -    - \n"+
                          "BRCK    -    - BRCK BRCK \n"+
                          "",8);
    targetMoves = new String[]{u, l, u, d, r, d, u};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1 Unreachable
    targetGame = makeGame(
                          "   -    - Zomb    -    - \n"+
                          "BRCK BRCK BRCK    -    - \n"+
                          "BRCK BRCK  PIT  PIT    - \n"+
                          "Zomb    -    -    -    - \n"+
                          "BRCK Zomb    - BRCK BRCK \n"+
                          "",8);
    targetMoves = null;
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});


    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 7";
    initial = makeGame(
                       "BRCK    - BRCK    - \n"+
                       "   - BRCK Zomb Zomb \n"+
                       "   -    - Zomb BRCK \n"+
                       "   - BRCK BRCK BRCK \n"+
                       "Zomb Zomb Zomb    - \n"+
                       "Zomb BRCK BRCK    - \n"+
                       "Zomb BRCK  PIT Zomb \n"+
                       "",0);
    expectStateCount = 1294;
    expectBestScore = 7;
    expectBestMoves = new String[]{l, r, d, l, r, d, l, r, d, l, d, r, d, l, d, r, d, l, d, r, d, l};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // Target 0
    targetGame = makeGame(
                          "BRCK    - BRCK Zomb \n"+
                          "Zomb BRCK Zomb    - \n"+
                          "Zomb Zomb    - BRCK \n"+
                          "   - BRCK BRCK BRCK \n"+
                          "   -    -    - Zomb \n"+
                          "   - BRCK BRCK Zomb \n"+
                          "   - BRCK  PIT Zomb \n"+
                          "",1);
    targetMoves = new String[]{r, u, r, d, l, d, r, d, r, u};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Target 1 Unreachable
    targetGame = makeGame(
                          "BRCK    - BRCK Zomb \n"+
                          "Zomb BRCK Zomb Zomb \n"+
                          "Zomb    -    - BRCK \n"+
                          "   - BRCK BRCK BRCK \n"+
                          "   - Zomb    -    - \n"+
                          "   - BRCK BRCK    - \n"+
                          "   - BRCK  PIT    - \n"+
                          "",3);
    targetMoves = new String[]{l, r, d, l, d, r, d, l, r, u, r, u, l, u};
    targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});

    ////////////////////////////////////////////////////////////////////////////////
    name = "Test Game 8 Brutal";
    initial = makeGame(
                       "   -    - BRCK Zomb BRCK BRCK Zomb    - \n"+
                       "Zomb Zomb    - BRCK Zomb    -    - Zomb \n"+
                       "BRCK BRCK BRCK Zomb BRCK BRCK    - Zomb \n"+
                       "   - BRCK BRCK BRCK BRCK Zomb BRCK    - \n"+
                       "   -    - BRCK BRCK BRCK BRCK    - BRCK \n"+
                       "Zomb BRCK BRCK Zomb  PIT BRCK Zomb Zomb \n"+
                       "Zomb BRCK BRCK  PIT Zomb BRCK    - BRCK \n"+
                       "   - BRCK Zomb BRCK BRCK Zomb    - BRCK \n"+
                       "Zomb Zomb    - Zomb    -    -    -  PIT \n"+
                       "",0);
    expectStateCount = 4593;
    expectBestScore = 11;
    expectBestMoves = new String[]{r, r, r, d, r, r, r, l, d, r, r, r};
    targetGameMoves = new ArrayList<GameAndMoves>();
    // // Target 0
    // targetGame = makeGame(
    //                    "   -    -    -    -    -    -    - \n"+
    //                    " PIT    -    - BRCK    -    -    - \n"+
    //                    "   -    -  PIT    -    -    -    - \n"+
    //                    "   - Zomb    -    - Zomb    -    - \n"+
    //                    "",1);
    // targetMoves = new String[]{d,r,u,l,d};
    // targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // // Target 1 Unreachable
    // targetGame = makeGame(
    //                    "   -    -    -    -    -    -    - \n"+
    //                    " PIT    -    - BRCK    -    -    - \n"+
    //                    "   -    -  PIT    -    -    -    - \n"+
    //                    "   - Zomb    -    - Zomb    -    - \n"+
    //                    "",0);
    // targetMoves = null;
    // targetGameMoves.add(new GameAndMoves(targetGame,targetMoves));
    // // Add to test examples
    testData.add(new Object[]{name, initial, expectStateCount, expectBestScore, expectBestMoves, targetGameMoves});


    // Return all the test data which will be used
    return testData;
  }



}      
 
