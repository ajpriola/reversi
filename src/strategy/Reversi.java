package strategy;

import reversi.Board;
import reversi.Square;
import reversi.Strategy;

/**
 * An AI based strategy for choosing a square based
 * on the current state of the board.
 *
 * @author AJ Priola
 * @version 0.0.1
 * @since 2018-10-25
 */
public class Reversi implements Strategy {

  /**
   * Determines where the current player should play their next piece. Some
   * methods that may be useful for defining such a strategy:
   * <ul>
   * <li>{@link Board#getCurrentPossibleSquares()}</li>
   * <li>{@link Board#getCurrentPlayer()}</li>
   * <li>{@link Board#getSquareOwners()}</li>
   * <li>{@link Board#getPlayerSquareCounts()}</li>
   * </ul>
   *
   * @param board The current state of the Reversi board.
   * @return The square where the current player should play their next piece.
   */
  @Override
  public Square chooseSquare(Board board) {
    return null;
  }
}
