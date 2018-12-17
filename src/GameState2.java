
import java.util.*;
import java.math.BigInteger;

public class GameState2 implements Cloneable {
    private Player player;
    private GameState2 previous;
    private Move move;
    private HashSet<Move> validMoves1;
    private HashSet<Move> validMoves2;
    private int p1score;
    private int p2score;
    private HashSet<GameState2> successors;

    private BigInteger hash;
    private Square[][] board;
    private Random random;

    /**
     * An enumeration of the possible owners of a square in the game board.
     *
     * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
     *         Sultanik</a>
     */
    enum Player {
        PLAYER1,
        PLAYER2,
        EMPTY
    }

    enum Direction {
        UPLEFT, UPRIGHT, DOWNLEFT, DOWNRIGHT, UPLEFT2, UPRIGHT2, DOWNLEFT2, DOWNRIGHT2
    }

    /**
     * An enumeration of the possible states of the game.
     *
     * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
     *         Sultanik</a>
     */
    public enum GameStatus {
        PLAYER1WON,
        PLAYER2WON,
        TIE,
        PLAYING
    }
    /**
     * Constructs a new <code>GameState</code> with the initial board
     * configuration, a random initial player, and the random number generator
     * seeded to a random value.
     */
    public GameState2() {
        random = new Random();
        init();
    }

    private void init() {
        board = new Square[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Square(i, j, Player.EMPTY);
            }
        }

        player = (random.nextInt(2) == 0 ? Player.PLAYER1 : Player.PLAYER2);

        //Player 1

        board[0][1].setPlayer(Player.PLAYER1);
        board[0][3].setPlayer(Player.PLAYER1);
        board[0][5].setPlayer(Player.PLAYER1);
        board[0][7].setPlayer(Player.PLAYER1);

        board[1][0].setPlayer(Player.PLAYER1);
        board[1][2].setPlayer(Player.PLAYER1);
        board[1][4].setPlayer(Player.PLAYER1);
        board[1][6].setPlayer(Player.PLAYER1);

        board[2][1].setPlayer(Player.PLAYER1);
        board[2][3].setPlayer(Player.PLAYER1);
        board[2][5].setPlayer(Player.PLAYER1);
        board[2][7].setPlayer(Player.PLAYER1);

        //Player 2
        board[5][0].setPlayer(Player.PLAYER2);
        board[5][2].setPlayer(Player.PLAYER2);
        board[5][4].setPlayer(Player.PLAYER2);
        board[5][6].setPlayer(Player.PLAYER2);

        board[6][7].setPlayer(Player.PLAYER2);
        board[6][5].setPlayer(Player.PLAYER2);
        board[6][3].setPlayer(Player.PLAYER2);
        board[6][1].setPlayer(Player.PLAYER2);

        board[7][0].setPlayer(Player.PLAYER2);
        board[7][2].setPlayer(Player.PLAYER2);
        board[7][4].setPlayer(Player.PLAYER2);
        board[7][6].setPlayer(Player.PLAYER2);


        previous = null;
        move = null;
        validMoves1 = null;
        validMoves2 = null;
        p1score = -1;
        p2score = -1;
        successors = null;
        hash = null;
    }

    /**
     * Returns a copy of this GameState. Note that this is a shallow copy;
     * preceding states are not cloned.
     */
    public Object clone() {
        GameState2 gs = new GameState2();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gs.board[i][j].setPlayer(board[i][j].getOwner());

        gs.player = player;
        if (previous != null) {
            gs.previous = (GameState2) previous.clone();
        }
        if (move != null) {
            gs.move = (Move) move.clone();
        }
        gs.validMoves1 = null;
        gs.validMoves2 = null;
        gs.p1score = -1; /* force a recount of the scores */
        gs.p2score = -1;
        gs.successors = null;
        gs.hash = hash;
        return gs;
    }

    /**
     * Returns the player whose turn it is to make a move.
     */
    public Player getCurrentPlayer() {
        return player;
    }

    /**
     * Returns the opponent of a player.
     */
    public Player getOpponent(Player player) {
        if (player == Player.PLAYER1)
            return Player.PLAYER2;
        else if (player == Player.PLAYER2)
            return Player.PLAYER1;
        else
            return player;
    }

    /**
     * Returns the player that currently owns the given square.
     */
    public Player getPieceOwner(int row, int col) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8)
            return null;
        else
            return board[row][col].getOwner();
    }

    public Player getOwner(Square square) {
        return getPieceOwner(square.row, square.col);
    }

    Move wouldJumpAndRemove(Move move, Player player, Direction direction) {


        Square from = move.from;
        Square to = move.to;

        int fromRow = from.row;
        int fromCol = from.col;


        if (player == GameState2.Player.PLAYER1 && fromRow == 7 || player == GameState2.Player.PLAYER2 && fromRow == 0) {
            from.setKing(true);
        }
        if (board[fromRow][fromCol].isKing) {
            from.setKing(true);
        }

        int toRow = to.row;
        int toCol = to.col;

        int colDist = move.colDist();
        int rowDist = move.rowDist();

        switch (direction) {
            case UPLEFT:
                if(player == Player.PLAYER1 && !from.isKing) {
                    return null;
                }
                if (rowDist == -2 && colDist == -2 && board[toRow + 1][toCol + 1].getOwner() == getOpponent(player)) {
                    return move;
                }
                return null;
            case UPLEFT2:
                if(player == Player.PLAYER1 && !from.isKing) {
                    return null;
                }
                if (rowDist == -1 && colDist == -1 ) {
                    return move;
                }
                return null;
            case UPRIGHT:
                if(player == Player.PLAYER1 && !from.isKing) {
                    return null;
                }
                if (rowDist == -2 && colDist == 2 && board[toRow + 1][toCol - 1].getOwner() == getOpponent(player)) {
                    return move;
                }
                return null;
            case UPRIGHT2:
                if(player == Player.PLAYER1 && !from.isKing) {
                    return null;
                }
                if (rowDist == -1 && colDist == 1) {
                    return move;
                }
                return null;

            case DOWNLEFT:
                if(player == Player.PLAYER2 && !from.isKing) {
                    return null;
                }
                if (rowDist == 2 && colDist == -2 && board[toRow - 1][toCol + 1].getOwner() == getOpponent(player)) {
                    return move;
                }
                return null;
            case DOWNLEFT2:
                if(player == Player.PLAYER2 && !from.isKing) {
                    return null;
                }
                if (rowDist == 1 && colDist == -1 ) {
                    return move;
                }
                return null;

            case DOWNRIGHT:
                if(player == Player.PLAYER2 && !from.isKing) {
                    return null;
                }
                if (rowDist == 2 && colDist == 2 && board[toRow - 1][toCol - 1].getOwner() == getOpponent(player)) {
                    return move;
                }
                return null;
            case DOWNRIGHT2:
                if(player == Player.PLAYER2 && !from.isKing) {
                    return null;
                }
                if (rowDist == 1 && colDist == 1 ) {
                    return move;
                }
                return null;

            default:
                return null;
        }
    }

    /**
     * Returns <code>true</code> if and only if <code>move</code> is legal for
     * <code>player</code>.
     */
    public boolean isLegalMove(Move move, Player player) {
        Square from = move.from;
        Square to = move.to;

        if ( board[from.row][from.col].getOwner() != player)
            return false;

        if ( board[to.row][to.col].getOwner() != Player.EMPTY)
            return false;


        for (Direction d : Direction.values()) {
            if (wouldJumpAndRemove(move, player, d) != null)
                return true;
        }
        return false;
    }

    /**
     * Returns all valid Moves that may be taken from this state.
     */
    public AbstractSet<Move> getValidMoves() {
        return getValidMoves(getCurrentPlayer());
    }

    /**
     * Returns all valid Moves that may be taken by <code>player</code> from
     * this state.
     */
    public AbstractSet<Move> getValidMoves(Player player) {
        HashSet<Move> moves = (player == Player.PLAYER1 ? validMoves1 : validMoves2);
        if (moves != null)
            return moves;
        moves = new HashSet<Move>();

        for (int yfrom = 0; yfrom < 8; yfrom++) {
            for (int xfrom = 0; xfrom  < 8; xfrom ++) {

                for (int yto = 0; yto < 8; yto++) {
                    for (int xto = 0; xto < 8; xto++) {


                        Square from = new Square(yfrom, xfrom, getPieceOwner(yfrom, xfrom));
                        Square to = new Square(yto, xto, getPieceOwner(yto, xto));

                        Move m = new Move(from, to);

                        if (isLegalMove(m, player)) {
                            moves.add(m);
                        }
                    }
                }
            }
        }
        if (player == Player.PLAYER1)
            validMoves1 = moves;
        else
            validMoves2 = moves;


        return moves;
    }

    /**
     * Returns the number of pieces the player has on the board
     */
    public int getScore(Player player) {

        GameState2.Player opponent = getOpponent(player);
        int validMovesOpponent = getValidMoves(opponent).size();
        int validMovesCurrent = getValidMoves(player).size();
        int numKingsCurrent = getNumKings(player);
        int numKingsOpponent = getNumKings(opponent);
        int numPiecesCurrent = getNumPieces(player);
        int numPiecesOpponent = getNumPieces(opponent);

        int totalScore = 0;

        if (validMovesOpponent == 0) totalScore += 1000;

        totalScore += validMovesCurrent - validMovesOpponent;
        totalScore += (numKingsCurrent - numKingsOpponent) * 10;
        totalScore += numPiecesCurrent - numPiecesOpponent;


        if (player == Player.PLAYER1)
            p1score = totalScore;
        else if (player == Player.PLAYER2)
            p2score = totalScore;

        return totalScore;
    }

    /**
     * Returns the winner of the game or <code>null</code> if the game was
     * either a tie or the game has not yet finished.
     */
    public Player getWinner() {
        switch (getStatus()) {
            case PLAYER1WON:
                return Player.PLAYER1;
            case PLAYER2WON:
                return Player.PLAYER2;
            default:
                return null;
        }
    }

    public int getNumKings(Player p) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getOwner() == p && board[i][j].isKing) {
                    count++;
                }
            }
        }
        return count;
    }

    public boolean isKing(int row, int col) {
        return board[row][col].isKing;
    }
    public int getNumPieces(Player p) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getOwner() == p) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countMove(GameState2 newState) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (newState.getPieceOwner(i,j) == getCurrentPlayer() && this.getPieceOwner(i, j) != getCurrentPlayer()) {
                    count++;
                }
            }
        }
        return count;
    }
    /**
     * Returns the current status of the game.
     */

    public GameStatus getStatus() {

        if (getNumPieces(getCurrentPlayer()) == getNumKings(getCurrentPlayer()) && getNumKings(getOpponent(getCurrentPlayer())) == getNumPieces(getOpponent(getCurrentPlayer()))) {
            return GameStatus.TIE;
        }
        if (getPreviousState() != null && getPreviousState().getCurrentPlayer().equals(getCurrentPlayer())) {
            if (getCurrentPlayer() == Player.PLAYER1) return GameStatus.PLAYER2WON;
            return GameStatus.PLAYER1WON;
        }
        if (getNumPieces(Player.PLAYER1) == 0 || (getValidMoves(Player.PLAYER1).size() <= 0 && getPreviousState() != null)) {
            return GameStatus.PLAYER2WON;
        } else if (getNumPieces(Player.PLAYER2) == 0 || (getValidMoves(Player.PLAYER2).size() <= 0 && getPreviousState() != null)) {
            return GameStatus.PLAYER1WON;
        } else if (getValidMoves(Player.PLAYER1).size() <= 0 && getValidMoves(Player.PLAYER2).size() <= 0) {
            return GameStatus.TIE;
        }
        else {
            return GameStatus.PLAYING;
        }
    }
    /**
     * Equivalent to {@link #getSuccessors(boolean) getSuccessors(true)}.
     *
     * @see #getSuccessors(boolean)
     */
    public AbstractSet<GameState2> getSuccessors() {
        return getSuccessors(true);
    }

    /**
     * Returns all valid GameStates that may succeed this state.
     *
     * <p>
     * Note that if <code>includePreviousStateReference</code> is
     * <code>false</code>, the returned states will return <code>null</code>
     * when {@link #getPreviousState()} is called. This is useful to reduce
     * memory if the back-references are not required.
     * </p>
     *
     * @param includePreviousStateReference whether or not the returned states
     *            should have back-references to <code>this</code>.
     * @see #applyMove(Move, boolean)
     */
    public AbstractSet<GameState2> getSuccessors(boolean includePreviousStateReference) {

        if (successors != null)
            return successors;
        AbstractSet<Move> movesSet = getValidMoves();
        Move moves[] = new Move[movesSet.size()];
        moves = movesSet.toArray(moves);
        successors = new HashSet<>();

        for (int i = 0; i < moves.length; i++) {

            if (moves[i] == null) continue;
            try {

                successors.add(applyMove(moves[i]));
            }
            catch (InvalidMoveException ime) {
                /* This should not happen! */
                System.err.println(ime.toString());
            }
        }

        return successors;
    }

    /**
     * Equivalent to {@link #applyMove(Move,boolean) applyMove(move, true)}.
     *
     * @see #applyMove(Move,boolean)
     */
    public GameState2 applyMove(Move move) throws InvalidMoveException {
        GameState2 original = (GameState2)clone();
        GameState2 newState = applyMoveRecurse(original, move);
        newState.previous = original;
        newState.switchPlayer();

        return newState;
    }

    public GameState2 applyMoveRecurse(GameState2 state, Move move) {

        if (state.equals(state.getPreviousState())) {
            return state;
        }
        GameState2 newState = (GameState2) state.clone();
        try {
            newState = newState.applyMove(move, true);

        } catch (Exception e) {
            return newState;
        }

        if (Math.abs(move.colDist()) <= 1 || Math.abs(move.rowDist()) <= 1) return newState;

        AbstractSet<Move> validMoves = newState.getValidMoves();

        for (Move m: validMoves) {

            if (m.from.col != move.to.col || m.from.row != move.to.row) continue;
            if (Math.abs(m.colDist()) <= 1 || Math.abs(m.rowDist()) <= 1) continue;

            GameState2 original = (GameState2) newState.clone();
            original.applyMove(m);
        }

        return newState;
    }
    /**
     * Returns the GameState resulting from applying the given move to this
     * state.
     *
     * <p>
     * <code>applyMove</code> <b>does not</b> apply the given move to the
     * current state; it does not in any way alter <code>this</code>. Instead,
     * <code>applyMove</code> <em>returns</em> the state that <em>results</em>
     * from making the given move in the current state.
     * </p>
     *
     * <p>
     * Note that if <code>includePreviousStateReference</code> is
     * <code>false</code>, the returned state will return <code>null</code> when
     * {@link #getPreviousState()} is called. This is useful to save memory if
     * the back-references to previous states are not required (<i>i.e.</i> the
     * previous states may be garbage collected).
     * </p>
     *
     * @param includePreviousStateReference whether or not the returned state
     *            should have a back-reference to <code>this</code>.
     * @throws InvalidMoveException if <code>move</code> is not a valid move
     *             from this state.
     */
    public GameState2 applyMove(Move move, boolean includePreviousStateReference) {
        Move bracket;

        GameState2 newState = (GameState2) clone();
        Player player = getCurrentPlayer();

        newState.previous = (includePreviousStateReference ? this : null);
        newState.move = (Move)move.clone();

        Square from = move.from;
        Square to = move.to;

        if (to == null) {
            throw new InvalidMoveException(move, getCurrentPlayer(),
                    "The move sent to GameState.applyMove() was null!");
        }
        if (from == null) {
            throw new InvalidMoveException(move, getCurrentPlayer(),
                    "The move sent to GameState.applyMove() was null!");
        }

        if (board[to.row][to.col].getOwner() != Player.EMPTY) {
            throw new InvalidMoveException(move, getCurrentPlayer(), "The space is not empty!");
        }

        if (board[from.row][from.col].getOwner() != getCurrentPlayer()) {
            throw new InvalidMoveException(move, getCurrentPlayer(), "This piece is  not yours!");
        }

        int fromRow = from.row;
        int fromCol = from.col;

        int toRow = to.row;
        int toCol = to.col;

        bracket = wouldJumpAndRemove(move, player, Direction.UPLEFT);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[toRow + 1][toCol + 1].setPlayer(Player.EMPTY);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPLEFT2);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPRIGHT);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[toRow + 1][toCol - 1].setPlayer(Player.EMPTY);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPRIGHT2);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNLEFT);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[toRow - 1][toCol + 1].setPlayer(Player.EMPTY);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNLEFT2);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNRIGHT);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[toRow - 1][toCol - 1].setPlayer(Player.EMPTY);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNRIGHT2);
        if (bracket != null) {
            newState.board[toRow][toCol].setPlayer(player);
            newState.board[fromRow][fromCol].setPlayer(Player.EMPTY);

        }

        if (bracket != null) {

            if (bracket.from.isKing) {

                newState.board[toRow][toCol].setKing(true);
            }
            newState.board[fromRow][fromCol].setKing(false);
        }

        return newState;
    }

    public void switchPlayer() {
        player = getOpponent(getCurrentPlayer());
    }
    /**
     * Returns the previous state (or <code>null</code> if this is the initial
     * state). This function may also return <code>null</code> if
     * <code>this</code> was created without a back-reference to the previous
     * state (to save memory).
     *
     * @see #applyMove(Move, boolean)
     */
    public GameState2 getPreviousState() {
        return previous;
    }

    /**
     * Returns the previous move that was used to get to this state (or
     * <code>null</code> if this is the initial state).
     */
    public Move getPreviousMove() {
        return move;
    }

    /**
     * Returns a string representation of the game board.
     * <p>
     * Example:
     *
     * <pre>
     * a b c d e f g h   [@=2 O=5]
     *   0 . . . . . . . .
     *   1 . . . . . . . .
     *   2 . . . O . . . .
     *   3 . . . O O . . .
     *   4 . . @ O @ . . .
     *   5 . . O . . . . .
     *   6 . . . . . . . .
     *   7 . . . . . . . .
     * </pre>
     *
     * </p>
     */
    public String toString() {
        String s = "    a b c d e f g h   [@=" + getScore(Player.PLAYER1) + " O="
                + getScore(Player.PLAYER2) + "]\n";
        for (int i = 0; i < 8; i++) {
            s += "  " + i;
            for (int j = 0; j < 8; j++) {
                Player p = getPieceOwner(i, j);
                s += " " + (p == Player.PLAYER1 ? "@" : (p == Player.PLAYER2 ? "O" : "."));
            }
            if (i < 7)
                s += "\n";
        }
        return s;
    }

    /**
     * Returns whether or not <code>o</code> is equivalent to this GameState.
     *
     * @see #uniqueHashCode()
     * @see #hashCode()
     */
    public boolean equals(Object o) {
        if (!(o instanceof GameState2))
            return false;
        GameState2 gs = (GameState2) o;
        if (gs.player != player)
            return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (getPieceOwner(i, j) != gs.getPieceOwner(i, j))
                    return false;
            }
        }
        return true;
    }

    private static BigInteger multiplier[] = null;
    private static BigInteger two = new BigInteger("2");

    /**
     * Returns a unique number identifying this GameState.
     *
     * <p>
     * <table>
     * <tr>
     * <td colspan="4" align="left">The following are always true:</td>
     * </tr>
     * <tr>
     * <td><ul type="disc">
     * <li></li></ul></td>
     * <td align="right"><code>x.uniqueHashCode() == y.uniqueHashCode()</code></td>
     * <td>&rArr;</td>
     * <td><code>x.equals(y)</code></td>
     * </tr>
     * <tr>
     * <td><ul type="disc">
     * <li></li></ul></td>
     * <td align="right"><code>x.equals(y)</code></td>
     * <td>&rArr;</td>
     * <td><code>x.uniqueHashCode() == y.uniqueHashCode()</code></td>
     * </tr>
     * <tr>
     * <td><ul type="disc">
     * <li></li></ul></td>
     * <td align="right"><code>x.equals(y)</code></td>
     * <td>&rArr;</td>
     * <td><code>x.hashCode() == y.hashCode()</code></td>
     * </tr>
     * <tr>
     * <td colspan="4" align="left"><em>However</em>, the following are
     * <em>not necessarily</em> true:</td>
     * </tr>
     * <tr>
     * <td><ul type="disc">
     * <li></li></ul></td>
     * <td align="right"><code>x.hashCode() == y.hashCode()</code></td>
     * <td>&rArr;</td>
     * <td><code>x.equals(y)</code></td>
     * </tr>
     * </table>
     * </p>
     *
     * @see #equals(Object)
     * @see #hashCode()
     */
    public BigInteger uniqueHashCode() {
        if (hash == null) {
            hash = (player == Player.PLAYER1 ? BigInteger.ZERO : BigInteger.ONE);
            int i, j, idx = 0;
            if (multiplier == null) {
                BigInteger three = new BigInteger("3");
                multiplier = new BigInteger[64];
                multiplier[0] = three;
                for (i = 1; i < 64; i++)
                    multiplier[i] = multiplier[i - 1].multiply(three);
            }
            for (i = 0; i < 8; i++) {
                for (j = 0; j < 8; j++) {
                    Player p = getPieceOwner(i, j);
                    if (p == Player.EMPTY)
                        idx++;
                    else
                        hash.add(multiplier[idx++].multiply(p == Player.PLAYER1 ? BigInteger.ONE
                                : two));
                }
            }
        }
        return hash;
    }

    /**
     * Equivalent to calling {@link Object#hashCode() hashCode()} on
     * the result of {@link #uniqueHashCode() uniqueHashCode()}.
     *
     * @see #equals(Object)
     * @see #hashCode()
     */
    public int hashCode() {
        return uniqueHashCode().hashCode();
    }

    public static void main(String[] args) {
        GameState2 gs = new GameState2();
        GameState2 succ[] = gs.getSuccessors().toArray(new GameState2[0]);
        for (int i = 0; i < succ.length; i++)
            System.out.println(succ[i]);
    }

    /**
     * Returns the random number generator for this game.
     */
    public Random getRandom() {
        return random;
    }
}
