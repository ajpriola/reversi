package strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
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
public class NaiveMinMax implements Strategy {

    protected Player you;
    private int maxDepth = 3;

    class ScoredMove {
        public final Square square;
        private final int score;
        private final Player player;
        private final boolean isFinal;
        private final boolean isPass;
        public int getScore(Player player) {
            if (player==this.player) {
                return score;
            }
            else {
                return -score;
            }
        }
        public ScoredMove(int score, Player player, Square square) {
            this.square = square;
            this.player = player;
            this.score = score;
            this.isFinal = false;
            this.isPass = false;
        }
        public ScoredMove(int score, Player player, boolean isFinal) {
            this.square = null;
            this.player = player;
            this.score = score;
            this.isPass = true;
            this.isFinal = isFinal;
        }
    }

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
        this.you = board.getCurrentPlayer();
        System.out.println("You are: "+you.name());
        //System.out.println(score(you, board)+":");
        //System.out.println(board);
        ScoredMove nextMove = this.bestMove(you, board, this.maxDepth);
        System.out.println("Best: "+nextMove.getScore(you));
        return nextMove.square;
    }
  
    protected ScoredMove bestMove(Player player, Board board, int maxDepth) {
        int maxScore = Integer.MIN_VALUE;
        Square maxSquare = null;
        if (board.isComplete()) {
            //System.out.println("Was "+this.score(you, board)+" (finished):");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return new ScoredMove(this.score(player, board), player, true); // game over
        }
        if (maxDepth<=0) {
            // if you already searched through all the possibilities you're going to,
            // return the first choice or stop
            //System.out.println("Reached max depth");
            Set<Square> possSquares = board.getCurrentPossibleSquares();
            if (possSquares.isEmpty()) {
                return new ScoredMove(this.score(player, board.pass()), player, false); // pass
            }
            else {
                Square nextSquare = new ArrayList<>(possSquares).get(0);
                Board withNextMove = board.play(nextSquare);
                int nextScore = this.score(player, withNextMove);
                return new ScoredMove(nextScore, player, nextSquare);
            }
        }
        //System.out.println("Was "+this.score(you, board)+":");
        //System.out.println(board.getMoves());
        //System.out.println(board);
        if (board.getCurrentPossibleSquares().isEmpty()) {
            Board withNextMove = board.pass();
            ScoredMove counterMove = this.bestMove(player.opponent(), withNextMove, maxDepth-1);
            return new ScoredMove(counterMove.getScore(player), player, false); // pass
        }
        for (Square s : board.getCurrentPossibleSquares()) {
            Board withNextMove = board.play(s);
            ScoredMove counterMove = this.bestMove(player.opponent(), withNextMove, maxDepth-1);
            int expectedScore = counterMove.getScore(player);
            if (expectedScore>maxScore) {
                //System.out.println("Best so far ("+player.name()+"): "+expectedScore);
                //System.out.println(board);
                maxScore=expectedScore;
                maxSquare = s;
            }
        }
        if (maxSquare==null) { throw new NullPointerException("max move should not be null");}
        return new ScoredMove(maxScore, player, maxSquare);
    }

    private int score(Player player, Board board) {
        if (!board.isComplete()) {
            return board.getPlayerSquareCounts().get(player)-board.getPlayerSquareCounts().get(player.opponent());
        }
        if (board.getWinner()==player) {
            //System.out.println(you==board.getWinner()?"You win!":"You lose.");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return board.size()*board.size();
        }
        else {
            //System.out.println(you==board.getWinner()?"You win!":"You lose.");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return -board.size()*board.size();
        }
    }
}
