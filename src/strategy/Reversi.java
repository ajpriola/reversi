package strategy;

import reversi.Board;
import reversi.Square;
import reversi.Strategy;
import java.util.Random;

/**
 * An AI based strategy for choosing a square based
 * on the current state of the board.
 *
 * @author AJ Priola
 * @version 0.0.1
 * @since 2018-10-25
 */
public class Reversi implements Strategy {

  private final long TIME = 1000;
  private long start;
  private int nodeEnd, epthEnd;

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

    start = System.currentTimeMillis();

    Board initial;
    int depth = 3;
    int nodes = 20;
    int alpha = 999, beta = 999;
    boolean max = false;
    Square optimal;

    if (!board.getCurrentPossibleSquares().isEmpty())
      initial = board;
    else
      initial = board.pass();

    Square[] possible = (Square[])board.getCurrentPossibleSquares().toArray();

    optimal = possible[new Random().nextInt(possible.length)];

    int score = prune(board.play(optimal), alpha, beta, depth, nodes--, !max);

    for (int i = 0; i < possible.length; i++) {
      if (alpha >= beta || board.isComplete()) break;

      if (TIME - (System.currentTimeMillis() - start) < TIME - 100)
        return optimal;

      if (max)
        alpha = Math.max(alpha, score);
      else
        beta = Math.min(beta, score);

      int pre = prune(board.play(possible[i]), alpha, beta, depth, nodes--, !max);

      if (max) {
        if (pre > score) {
          score = pre;
          optimal = possible[i];
        }
        alpha = Math.max(alpha, score);
      } else {
        if (pre < score) {
          score = pre;
          optimal = possible[i];
        }
        beta = Math.min(beta, score);
      }
    }

    return optimal;
  }

  private int prune(Board board, int alpha, int beta, int depth, int nodes, boolean max) {

    if (board.isComplete())
      return Math.abs(board.getPlayerSquareCounts().get(board.getCurrentPlayer())) - board.getPlayerSquareCounts().get(board.getCurrentPlayer().opponent());

    if (nodes <= 0) {
      nodeEnd++;
      return Math.abs(board.getPlayerSquareCounts().get(board.getCurrentPlayer()) - board.getPlayerSquareCounts().get(board.getCurrentPlayer().opponent()));
    }

    depth--;

    Square[] possible = (Square[])board.getCurrentPossibleSquares().toArray();

    if (possible.length == 0)
      return prune(board.pass(), alpha, beta, depth, nodes--, !max);

    int opt = max ? -999 : 999;

    for (int i = 0; i < possible.length; i++) {
      if (alpha >= beta) break;

      int pre = prune(board.play(possible[i]), alpha, beta, depth, nodes--, !max);

      if (max) {
        opt = Math.max(opt, pre);
        alpha = Math.max(alpha, pre);
      } else {
        opt = Math.min(opt, pre);
        beta = Math.min(beta, pre);
      }
    }

    return opt;
  }
}
