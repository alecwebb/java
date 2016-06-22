// Concrete implementation of a Tile. 
public class Zombie extends Tile {
  public Zombie(){  }

  // Should always return false
  public boolean mergesWith(Tile moving){
    return false;
  }

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

  public boolean isMovable(){
    return true;
  }

  // Return the string "BRCK"
  public String toString(){
    return "Zomb";
  }

  // true if the other is a zombie and false otherwise
  public boolean equals(Object other){
    return other instanceof Zombie;
  }

  // Hash code of all zombies is 666
  public int hashCode(){
    return 666;
  }
}
