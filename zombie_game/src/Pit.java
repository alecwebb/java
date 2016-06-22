// Concrete implementation of a Tile. Pits do not move but merge with
// Zombies to produce scores
public class Pit extends Tile {
  private int score = 0;
  
  public Pit(){  }

  // Should always return false
  public boolean mergesWith(Tile moving){
    return moving instanceof Zombie;
  }

  // Return the current pit if merging with zombie
  public Tile merge(Tile moving){
    if(moving instanceof Zombie){
      this.score = 1;
      return this;
    }
    else{
      String msg = 
        String.format("Can't merge tiles |%s| and |%s|",
                      this.toString(), moving.toString());
      throw new RuntimeException(msg);
    }
    
  }

  // 0 initially, 1 whenever a zombie falls into the pit
  public int getScore(){
    return this.score;
  }

  // Always false
  public boolean isMovable(){
    return false;
  }

  // Return the string "PIT"
  public String toString(){
    return "PIT";
  }

  // true if the other is a Pit and false otherwise
  public boolean equals(Object other){
    return other instanceof Pit;
  }

  // Hash code of all Pits is 29
  public int hashCode(){
    return 29;
  }
}
