import java.util.*;



// Encapsulates all the possible game states of a game of Zombie Trap.
public class GameStateSpace implements Iterable<ZombieTrapGame>{
	//declare class variables
	private List<String> movedir = new ArrayList<String>();
	private HashMap<ZombieTrapGame,List<String>> visited;
	private Queue<ZombieTrapGame> q;
	private ZombieTrapGame game, bestgame, nextgame;
	private int bestscore = 0;
	
  // Create a new game state space based on the initial game given.
  // Initialize any internal data needed then build up the state space.
  public GameStateSpace(ZombieTrapGame initial){
	  buildStateSpace(initial);		//call buildstatespace method
  }

  // Remove any current states. The build up a state space based on
  // the initial game that is provided.
  public void buildStateSpace(ZombieTrapGame initial){
	  
	  this.q = new LinkedList<ZombieTrapGame>();					//create the queue
	  this.visited = new HashMap<ZombieTrapGame,List<String>>();	//create the hashmap
	  visited.put(initial, movedir);								//add initial to visited HASHMAP
	  q.add(initial);												//add initial to the queue
	  while(q.isEmpty()==false){
		  this.game = q.remove();									//dequeue the game
		  if(game.getScore() > bestscore){
			  bestgame = game.copy();								//set bestgame if conditions are met
			  bestscore = game.getScore();							//update bestscore
		  }//endif
			  nextgame = game.copy(); 								//copy game
		      nextgame.shiftUp();									//perform shift
			  if(visited.containsKey(nextgame)==false){  			//confirm state is not in hashmap
				  List<String> moves = new ArrayList<String>();
				  moves.add("u");									//add direction to list
				  visited.put(nextgame,moves);						//add state to hashmap
				  q.add(nextgame);									//add to queue		
			  }//endif
			  
		      nextgame = game.copy();								//copy game
		      nextgame.shiftDown();		 							//perform shift
			  if(visited.containsKey(nextgame)==false){  			//confirm state is not in hashmap
			      List<String> moves2 = new ArrayList<String>();
				  moves2.add("d");									//add direction to list
				  visited.put(nextgame,moves2);						//add state to hashmap
				  q.add(nextgame);									//add to queue
			  }//endif
			  
		      nextgame = game.copy();								//copy game
		      nextgame.shiftLeft();									//perform shift
			  if(visited.containsKey(nextgame)==false){  			//confirm state is not in hashmap
			      List<String> moves3 = new ArrayList<String>();
				  moves3.add("l");									//add direction to list
				  visited.put(nextgame,moves3);						//add state to hashmap
				  q.add(nextgame);				  					//add to queue
			  }//endif
			  
		      nextgame = game.copy();								//copy game
		      nextgame.shiftRight();								//perform shift
			  if(visited.containsKey(nextgame)==false){  			//confirm state is not in hashmap
			      List<String> moves4 = new ArrayList<String>();
				  moves4 = (visited.get(game));
				  moves4.add("r");									//add direction to list
				  visited.put(nextgame,moves4);						//add state to hashmap
				  q.add(nextgame);				  					//add to queue
			  }//endif
	  }//endwhile
  }//endbfs

  // Return an iterator that enumerates all of reachable states
  // including the initial state.  The order of enumeration is not
  // important.
  public Iterator<ZombieTrapGame> iterator(){
	  Iterator<ZombieTrapGame> it = visited.keySet().iterator();	//iterator of keys
	  return it;
  }

  // Return true if the game given is reachable through some move
  // sequence from the initial state given.
  // 
  // TARGET COMPLEXITY: O(1)
  public boolean stateReachable(ZombieTrapGame state){
	  if(visited.containsKey(state)){	//search hashmap for given state
		  return true;
	  }
	  else{
		  return false;
	  }
  }

  // Return the shortest move sequence that will move from the initial
  // game stat to the given state.  Moves are single letter strings
  // with "u" for up, "l" for left, "d" for down, and "r" for right
  // and are returned in a list.  If several equal length move
  // sequences would reach the state, any of the shortest sequence may.
  // be returned
  // 
  // TARGET COMPLEXITY: O(P)
  // P: the shortest path from the GameStateSpace's inital
  // configuration to the given target state
  public List<String> movesToReach(ZombieTrapGame targetState){
		  return visited.get(targetState);		//get targetstate movelist from hashmap
  }

  // Return the best score that is achievable based on the initial
  // game used in the creation of the GameStateSpace
  // 
  // TARGET COMPLEXITY: O(1)
  public int bestScore(){
	  return bestscore;		//bestscore is a global, calculated from buildstatespace
  }

  // Return how many states are in the state space.
  // 
  // TARGET COMPLEXITY: O(1)
  public int stateCount(){
	  int statecount;
	  statecount = visited.size();	//size of hashmap is size of states
	  return statecount;
  }

  // Return a move sequence as in movesToReach() which will go from
  // the inital state to the best scoring game state.  If more than
  // one move sequence will result in a best score, return the
  // shortest set of moves.  If there is a tie for both score and
  // shortness of move sequence, any optimal sequence of moves can be
  // returned.
  public List<String> bestMoves(){
	  return this.movesToReach(bestgame);	//movestoreach method on bestgame
  }
}
