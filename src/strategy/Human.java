package strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import reversi.Board;
import reversi.ReversiTournament;
import reversi.Square;
import reversi.Strategy;

/**
 * A strategy to familiarize yourself with the game. 
 * Update the timeout in the {@code ReversiTournament} to give yourself enough to choose a square.
 */
public class Human implements Strategy {

  @Override
  public Square chooseSquare(Board board) {
    //Display the current status of the boad
    System.out.println(board);
    return chooseOne(board.getCurrentPossibleSquares());
  }

  /**
   * A simple method that displays the possible moves and asks the user
   * to pick a move.
   * 
   * @param itemSet
   *          The set of items from which to select.
   * @return The user selected item from the set.
   */
  public static <T> T chooseOne(Set<T> itemSet) {
	  List<T> itemList = new ArrayList<>(itemSet);
	  int picked = 0;
	  
	    Scanner in = new Scanner(System.in);
	
	    int i = 0;
	    for (T item : itemList) {
	      System.out.println(i + " " + item);
	      i++;
	    }
	
	    picked = in.nextInt();
	  
    return itemList.get(picked);
  }
}
