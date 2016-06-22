// Concrete implementation of a Tile. Bricks do not merge with
// anything and do not move.
public class Brick extends Tile {
  // Create a Brick
  public Brick(){  }

  // Should always return false
  public boolean mergesWith(Tile moving){
    return false;
  }

  // Should throw an informative runtime exception as bricks never
  // merge with anything
  public Tile merge(Tile moving){
    String msg = 
      String.format("Can't merge tiles |%s| and |%s|",
                    this.toString(), moving.toString());
    throw new RuntimeException(msg);
  }

  // Always 0
  public int getScore(){
    return 0;
  }

  // Always false
  public boolean isMovable(){
    return false;
  }

  // Return the string "BRCK"
  public String toString(){
    return "BRCK";
  }

  // true if the other is a Brick and false otherwise
  public boolean equals(Object other){
    return other instanceof Brick;
  }

  // Hash code of all bricks is 19
  public int hashCode(){
    return 19;
  }
}
