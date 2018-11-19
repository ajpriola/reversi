package strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class WithOpeningBook implements Strategy {

    protected Player you;
    protected Player opponent;
    private int maxDepth = 4; // how many steps to look ahead
    GameBook gamebook;
    
    class GameBook {
        protected Map<List<Move>, Integer> expectedValues = new HashMap<>();
        public int getScore(List<Move> moves, Player player) {
            int score = this.expectedValues.get(moves);
            // scores are relative to the last person that played
            if (player==moves.get(moves.size()-1).getPlayer()) {
                return score;
            }
            else {
                return -score;
            }
        }
        public boolean contains(List<Move> moves) {
            return this.expectedValues.containsKey(moves);
        }
        protected List<List<Move>> otherSymmetries(List<Move> moves) {
            ArrayList<List<Move>> res = new ArrayList<>();
            ArrayList<Move> right = new ArrayList<>();
            for (Move m : moves) {
                Square s = m.getSquare();
                // 1 4 5    3 2 1
                // 2     ->     4
                // 3            5
                Square s1 = new Square(s.getColumn(), 8-s.getRow());
                right.add(new Move(s1, m.getPlayer()));
            }
            res.add(right);
            ArrayList<Move> upsideDownAndBackwards = new ArrayList<>();
            for (Move m : moves) {
                Square s = m.getSquare();
                // 1 4 5        3
                // 2     ->     2
                // 3        5 4 1
                Square s1 = new Square(8-s.getRow(), 8-s.getColumn());
                upsideDownAndBackwards.add(new Move(s1, m.getPlayer()));
            }
            res.add(upsideDownAndBackwards);
            ArrayList<Move> left = new ArrayList<>();
            for (Move m : moves) {
                Square s = m.getSquare();
                // 1 4 5    5
                // 2     -> 4
                // 3        1 2 3
                Square s1 = new Square(8-s.getColumn(), s.getRow());
                left.add(new Move(s1, m.getPlayer()));
            }
            res.add(left);
            return res;
        }
        protected List<Move> parse(String moves) {
            // moves are in the form A5 (black), b4 (white), etc.
            if (moves.length()%2!=0) {
                throw new IllegalArgumentException("move list should have an even number of characters");

            }
            ArrayList<Move> res = new ArrayList<>();
            for (int i=0; i<moves.length(); i+=2) {
                char col = moves.charAt(i);
                char row = moves.charAt(i+1);
                int r,c;
                Player player;
                if (Character.isUpperCase(col)) {
                    player = Player.BLACK;
                    if ('A'<=col && col<='H') {
                        c = col-'A';
                    }
                    else {
                        throw new IllegalArgumentException("column for black should be between A and H");
                    }
                }
                else {
                    player = Player.WHITE;
                    if ('a'<=col && col<='h') {
                        c = col-'a';
                    }
                    else {
                        throw new IllegalArgumentException("column for white should be between a and h");
                    }
                }
                
                if ('1'<=row && row<='8') {
                    r = row-'1';
                }
                else {
                    throw new IllegalArgumentException("row should be between 1 and 8");
                }
                
                res.add(new Move(new Square(r, c), player));
            }
            return res;
        } 
        public void add(List<Move> moves, int score) {
            this.expectedValues.put(moves, score);
            // add the 3 other versions of this pattern, too
            for (List<Move> sym : this.otherSymmetries(moves)) {
                this.expectedValues.put(sym, score);
            }
        }
        public void add(String moves, int score) {
            this.expectedValues.put(this.parse(moves), score);
        }
        public void add(List<Move> base, List<Move> nexts, List<Integer> scores) {
            // add variations on one base game
            if (nexts.size() != scores.size()) {
                throw new IllegalArgumentException("should have the same number of next squares and scores");
            }
            for (int i=0; i<nexts.size(); i++) {
                Move m = nexts.get(i);
                int score = scores.get(i);
                List<Move> moves = new ArrayList<>(base);
                moves.add(m);
                this.add(moves, score);
            }
        }
        public void add(String base, String nexts, List<Integer> scores) {
            this.add(this.parse(base), this.parse(nexts), scores);
        }
    }
    class ScoredMove {
        // return this instead of an integer so that you can include the square also
        // should have everything needed to recreate a move (or lack of one)
        public final Square square;
        private final int score;
        private final Player player;
        private final boolean isFinal;
        private final boolean isPass;
        
        // score is relative to the current player (which was more important
        //  when there used to be just one bestMove function
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
        // hard-code players for alpha-beta
        this.you = board.getCurrentPlayer();
        this.opponent = this.you.opponent();
        System.out.println("You are: "+you.name());
        //System.out.println(score(you, board)+":");
        //System.out.println(board);
        // alpha = -inf, beta = +inf
        ScoredMove nextMove = this.bestMove(board, this.maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        System.out.println("Best: "+nextMove.getScore(you));
        return nextMove.square;
    }
  

    protected ScoredMove worstMove(Board board, int maxDepth, int alpha, int beta) {
        // maxScore is from opponent's perspective
        int maxScore = Integer.MIN_VALUE;
        Square maxSquare = null;
        if (board.isComplete()) {
            //System.out.println("Was "+this.score(opponent, board)+" (finished):");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return new ScoredMove(this.score(opponent, board), opponent, true); // game over
        }
        if (maxDepth<=0) {
            // if opponent already searched through all the possibilities opponent're going to,
            // return the first choice or stop
            //System.out.println("Reached max depth");
            Set<Square> possSquares = board.getCurrentPossibleSquares();
            if (possSquares.isEmpty()) {
                return new ScoredMove(this.score(opponent, board.pass()), opponent, false); // pass
            }
            else {
                Square nextSquare = new ArrayList<>(possSquares).get(0);
                Board withNextMove = board.play(nextSquare);
                int nextScore = this.score(opponent, withNextMove);
                return new ScoredMove(nextScore, opponent, nextSquare);
            }
        }
        //System.out.println("Was "+this.score(opponent, board)+":");
        //System.out.println(board.getMoves());
        //System.out.println(board);
        if (board.getCurrentPossibleSquares().isEmpty()) {
            Board withNextMove = board.pass();
            ScoredMove counterMove = this.bestMove(withNextMove, maxDepth-1, alpha, beta);
            return new ScoredMove(counterMove.getScore(opponent), opponent, false); // pass
        }
        for (Square s : board.getCurrentPossibleSquares()) {
            // only need to worry about alpha/beta here, because
            //  this is the only case when you might have siblings to prune
            Board withNextMove = board.play(s);
            int expectedScore;
            List<Move> nextMoves = withNextMove.getMoves();
            if (this.gamebook.contains(nextMoves)) {
                expectedScore = this.gamebook.getScore(nextMoves, opponent);
            }
            else {
                ScoredMove counterMove = this.bestMove(withNextMove, maxDepth-1, alpha, beta);
                expectedScore = counterMove.getScore(opponent);
                
            }
            // these three if statements correspond to the three main statements
            //  in min-value in the alpha-beta pseudocode
            if (expectedScore>maxScore) {
                //System.out.println("Worst so far ("+opponent.name()+"): "+expectedScore);
                //System.out.println(board);
                maxScore=expectedScore;
                maxSquare = s;
            }
            if (-maxScore<=alpha) { // alpha/beta are from you's perspective
                return new ScoredMove(maxScore, opponent, maxSquare);
            }
            if (-maxScore<=beta) {
                beta = -maxScore;
            }
        }
        if (maxSquare==null) { throw new NullPointerException("max move should not be null");}
        return new ScoredMove(maxScore, opponent, maxSquare);
    }
    protected ScoredMove bestMove(Board board, int maxDepth, int alpha, int beta) {
        // mostly identical to worstMove
        int maxScore = Integer.MIN_VALUE;
        Square maxSquare = null;
        if (board.isComplete()) {
            //System.out.println("Was "+this.score(you, board)+" (finished):");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return new ScoredMove(this.score(you, board), you, true); // game over
        }
        if (maxDepth<=0) {
            // if you already searched through all the possibilities you're going to,
            // return the first choice or stop
            //System.out.println("Reached max depth");
            Set<Square> possSquares = board.getCurrentPossibleSquares();
            if (possSquares.isEmpty()) {
                return new ScoredMove(this.score(you, board.pass()), you, false); // pass
            }
            else {
                Square nextSquare = new ArrayList<>(possSquares).get(0);
                Board withNextMove = board.play(nextSquare);
                int nextScore = this.score(you, withNextMove);
                return new ScoredMove(nextScore, you, nextSquare);
            }
        }
        //System.out.println("Was "+this.score(you, board)+":");
        //System.out.println(board.getMoves());
        //System.out.println(board);
        if (board.getCurrentPossibleSquares().isEmpty()) {
            Board withNextMove = board.pass();
            ScoredMove counterMove = this.worstMove(withNextMove, maxDepth-1, alpha, beta);
            return new ScoredMove(counterMove.getScore(you), you, false); // pass
        }
        for (Square s : board.getCurrentPossibleSquares()) {
            Board withNextMove = board.play(s);
            int expectedScore;
            List<Move> nextMoves = withNextMove.getMoves();
            if (this.gamebook.contains(nextMoves)) {
                expectedScore = this.gamebook.getScore(nextMoves, you);
            }
            else {
                ScoredMove counterMove = this.bestMove(withNextMove, maxDepth-1, alpha, beta);
                expectedScore = counterMove.getScore(you);
                
            }
            if (expectedScore>maxScore) {
                //System.out.println("Best so far ("+you.name()+"): "+expectedScore);
                //System.out.println(board);
                maxScore=expectedScore;
                maxSquare = s;
            }
            if (maxScore>=beta) { // no negative b/c alpha/beta are from you's perspective
                return new ScoredMove(maxScore, you, maxSquare);
            }
            if (maxScore>=alpha) { // alpha instead of beta (these are the only two differences)
                alpha = maxScore;
            }
        }
        if (maxSquare==null) { throw new NullPointerException("max move should not be null");}
        return new ScoredMove(maxScore, you, maxSquare);
    }

    private int score(Player player, Board board) {
        // score of player.opponent() is just -score
        if (!board.isComplete()) {
            // how many more squares you hold than your opponent
            return board.getPlayerSquareCounts().get(player)-board.getPlayerSquareCounts().get(player.opponent());
        }
        if (board.getWinner()==player) {
            //System.out.println(you==board.getWinner()?"You win!":"You lose.");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return board.size()*board.size(); // winning is equivalent to holding all of the squares
        }
        else {
            //System.out.println(you==board.getWinner()?"You win!":"You lose.");
            //System.out.println(board.getMoves());
            //System.out.println(board);
            return -board.size()*board.size();
        }
    }
    public WithOpeningBook() {
        this.gamebook = new GameBook();
        // these are from http://samsoft.org.uk/reversi/openings.htm
        gamebook.add("C4c3", "C2D3E6F5", new ArrayList<>(Arrays.asList(-11, 0, -6, -7)));
        gamebook.add("C4c3D3", "c5e3", new ArrayList<>(Arrays.asList(0,0)));
        gamebook.add("C4c3D3c5", "B2B3B4B5B6C6D6E6F6", new ArrayList<>(Arrays.asList(-16,-4,0,-4,-4,-4,0,-5,-4)));
        gamebook.add("C4c3D3c5B4", "a3a5b3d2e3f3", new ArrayList<>(Arrays.asList(-5,-8,-4,0,-3,-5)));
        gamebook.add("C4c3D3c5B4d2", "C2C6D6E1E2E6", new ArrayList<>(Arrays.asList(0,-4,0,-5,-4,-7)));
        gamebook.add("C4c3D3c5B4d2D6", "a4a5b3b5b6c6c7d7e3f3f4f5", new ArrayList<>(Arrays.asList(-10,-9,0,-4,-5,-3,-9,-12,-4,-5,-4,-5)));
        gamebook.add("C4c3D3c5B4e3", "C2C6D2D6E2F4", new ArrayList<>(Arrays.asList(0,0,0,0,3,0)));
        gamebook.add("C4c3D3c5B4d2E2", "a3a4a5b3e3f2f3f4", new ArrayList<>(Arrays.asList(-4,-10,-5,4,-3,-8,-9,-8)));

    }
}
