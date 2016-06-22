import java.util.*;
import java.io.*;

// Class to play a single game Zombie Trap
public class PlayZombieTrap {

  // Types of boards allowed
  static ArrayList<String> boardTypes = 
    new ArrayList<String>(Arrays.asList(new String[]{"dense","sparse","hash"}));
    

  // Play a game of Zombie Trap of the given size. Allows one to
  // specify the a number of random bricks, whether the type of board
  // implementation to use and a random seed.
  // 
  // usage: java PlayZombieTrap rows cols zombies pits bricks {dense|sparse|hash} [random-seed]
  //   rows/cols: the size of the board [int]
  //   zombies pits bricks: the number of various objects
  //   {dense|sparse|hash}: type of board implementation to use
  //   random-seed: used to initialize the random number generator [int]
  public static void main(String args[]) throws Exception{
    if(args.length!=1 && args.length < 4){
      System.out.println("usage: java PlayZombieTrap boardFile");
      System.out.println("  boardFile: a file that contains a pre-established board such as no-win.txt");
      System.out.println("usage: java PlayZombieTrap rows cols zombies pits bricks {dense|sparse|hash} [random-seed]");
      System.out.println("  rows/cols: the size of the board [int]");
      System.out.println("  zombies pits bricks: the number of various objects");
      System.out.println("  {dense|sparse|hash}: type of board to use, one of "+boardTypes);
      System.out.println("  random-seed: used to initialize the random number generator [int]");
      return;
    }

    ZombieTrapGame game = null;
    if(args.length == 1){
      game = new ZombieTrapGame(new Scanner(new File(args[0])),0);
    }
    else{
      int rows        = Integer.parseInt(args[0]);
      int cols        = Integer.parseInt(args[1]);
      int zombieCount = Integer.parseInt(args[2]);
      int pitCount    = Integer.parseInt(args[3]);
      int brickCount  = Integer.parseInt(args[4]);

      // Optional args
      int seed = 13579;           // Default random number
      if(args.length > 5){
        seed = Integer.parseInt(args[5]);
      }
      String boardType = "hash";
      if(args.length > 6){
        boardType = args[5];
      }
      if(!boardTypes.contains(boardType)){
        String msg = String.format("3rd arg '%s' must be one of %s",boardTypes);
        throw new RuntimeException(msg);
      }

      int initialScore = 0;
      game = new ZombieTrapGame(rows,cols,initialScore,seed,boardType);
      for(int i=0; i<zombieCount; i++){
        game.addRandomTile("zombie");
      }
      for(int i=0; i<pitCount; i++){
        game.addRandomTile("pit");
      }
      for(int i=0; i<brickCount; i++){
        game.addRandomTile("brick");
      }
    }

    System.out.println("Instructions");
    System.out.println("------------");
    System.out.println("Enter moves as l r u d q for");
    System.out.println("l: shift left");
    System.out.println("r: shift right");
    System.out.println("u: shift up");
    System.out.println("l: shift down");
    System.out.println("q: quit game");
    System.out.println();

    Scanner stdin = new Scanner(System.in);
    while(!game.isGameOver()){
      System.out.printf("Score: %d\n",game.getScore());
      System.out.println(game.boardString());
      System.out.printf("Move: ");
      String input = stdin.next();

      if(input.equals("q")){ 
        break; 
      }
      else if(input.equals("l")){
        game.shiftLeft();
      }
      else if(input.equals("r")){
        game.shiftRight();
      }
      else if(input.equals("u")){
        game.shiftUp();
      }
      else if(input.equals("d")){
        game.shiftDown();
      }

      System.out.println(input);

    }
    System.out.println(game);
    System.out.printf("Game Over! Final Score: %d\n",game.getScore());
  }
}
