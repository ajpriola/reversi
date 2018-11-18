package strategy;

import java.util.List;
import reversi.Player;
import reversi.Board;
import reversi.Move;
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
public class Naive1Move implements Strategy {

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
        int maxScore = Integer.MIN_VALUE;
        Square maxMove = null;
        Board maxBoard = null;
        Player you = board.getCurrentPlayer();
        System.out.println(score(you, board)+":");
        System.out.println(board);
        for (Square s : board.getCurrentPossibleSquares()) {
            Board withNextMove = board.play(s);
            int nextScore = this.score(you, withNextMove);
            if (nextScore>maxScore) {
                System.out.println("Best so far: "+score(you, board));
                System.out.println(board);
                maxScore=nextScore;
                maxBoard = withNextMove;
                maxMove = s;
            }
        }
        System.out.println("Best: "+score(you, board));
        System.out.println(board);
        return maxMove;
    }
  

    private int score(Player player, Board board) {
        if (!board.isComplete()) {
            return board.getPlayerSquareCounts().get(player)-board.getPlayerSquareCounts().get(player.opponent());
        }
        if (board.getWinner()==player) {
            System.out.println("WINNER: "+board.getWinner().name()+" (you win)");
            System.out.println(board);
            return board.size()*board.size();
        }
        else {
            System.out.println("WINNER: "+board.getWinner().name()+" (you lose)");
            System.out.println(board);
            return -board.size()*board.size();
        }
    }
}
