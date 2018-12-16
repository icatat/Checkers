
import java.util.*;
import java.math.BigInteger;

public class State implements Cloneable {
    private Player player;
    private State previous;
    private Piece move;
    private HashSet<Piece> validMoves1;
    private HashSet<Piece> validMoves2;
    private int p1score;
    private int p2score;
    private HashSet<State> successors;
    private BigInteger hash;
    private Piece[][] board;

    /**
     * An enumeration of the possible owners of a square in the game board.
     *
     * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
     *         Sultanik</a>
     */
    public enum Player {
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
    public State() {
        init();
    }

    private void init() {
        board = new Piece[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Piece(i, j, Player.EMPTY);
            }
        }

        player = Player.PLAYER1;

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
        State gs = new State();

        gs.board = new Piece[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                gs.board[i][j] = board[i][j];
        gs.player = player;
        gs.previous = previous;
        gs.move = move;
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

    public Player getOwner(Piece square) {
        return getPieceOwner(square.row, square.col);
    }

    Piece wouldJumpAndRemove(Piece move, Player player, Direction direction) {
        int row = move.row;
        int col = move.col;

        switch (direction) {
            case UPLEFT:
                if(player == Player.PLAYER2 && !move.isKing) {
                    return null;
                }
                if (row - 2 >= 0 && col - 2 >= 0 && board[row - 2][col - 2].getOwner() == getCurrentPlayer() && board[row - 1][col - 1].getOwner() == getOpponent(player)) {
                    return new Piece(row, col,player);
                }
                return null;
            case UPLEFT2:
                if(player == Player.PLAYER2 && !move.isKing) {
                    return null;
                }
                if (row - 1 >= 0 && col - 1 >= 0 && board[row - 1][col - 1].getOwner() == getCurrentPlayer()) {
                    return new Piece(row, col,player);
                }
                return null;
            case UPRIGHT:
                if(player == Player.PLAYER2 && !move.isKing) {
                    return null;
                }
                if (row - 2 >= 0 && col + 2 < board[row - 2].length && board[row - 2][col + 2].getOwner() == getCurrentPlayer() && board[row - 1][col + 1].getOwner() == getOpponent(player)) {
                    return new Piece(row, col,player);
                }
                return null;
            case UPRIGHT2:
                if(player == Player.PLAYER2 && !move.isKing) {
                    return null;
                }
                if (row - 1 >= 0 && col + 1 < board[row - 1].length && board[row - 1][col + 1].getOwner() == getCurrentPlayer()) {
                    return new Piece(row, col,player);
                }
                return null;

            case DOWNLEFT:
                if(player == Player.PLAYER1 && !move.isKing) {
                    return null;
                }
                if (row + 2 < board.length && col - 2 >= 0 && board[row + 2][col - 2].getOwner() == getCurrentPlayer() && board[row + 1][col - 1].getOwner() == getOpponent(player)) {
                    return new Piece(row, col, player);
                }
                return null;
            case DOWNLEFT2:
                if(player == Player.PLAYER1 && !move.isKing) {
                    return null;
                }
                if (row + 1 < board.length && col - 1 >= 0 && board[row + 1][col - 1].getOwner() == getCurrentPlayer()) {
                    return new Piece(row, col,player);
                }
                return null;

            case DOWNRIGHT:
                if(player == Player.PLAYER1 && !move.isKing) {
                    return null;
                }
                if (row + 2 < board.length && col + 2 < board[row + 2].length && board[row + 2][col + 2].getOwner() == getCurrentPlayer() && board[row + 1][col + 1].getOwner() == getOpponent(player)) {
                    return new Piece(row, col, player);
                }
                return null;
            case DOWNRIGHT2:
                if(player == Player.PLAYER1 && !move.isKing) {
                    return null;
                }
                if (row + 1 < board.length && col + 1  < board[0].length && board[row + 1][col + 1].getOwner() == getCurrentPlayer()) {
                    return new Piece(row, col,player);
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
    public boolean isLegalMove(Piece piece, Player player) {
        int row = piece.row;
        int col = piece.col;

        if ( board[row][col].getOwner() != Player.EMPTY)
            return false;
        for (Direction d : Direction.values()) {
            if (wouldJumpAndRemove(piece, player, d) != null)
                return true;
        }
        return false;
    }

    /**
     * Returns all valid Moves that may be taken from this state.
     */
    public AbstractSet<Piece> getValidMoves() {
        return getValidMoves(getCurrentPlayer());
    }

    /**
     * Returns all valid Moves that may be taken by <code>player</code> from
     * this state.
     */
    public AbstractSet<Piece> getValidMoves(Player player) {
        HashSet<Piece> moves = (player == Player.PLAYER1 ? validMoves1 : validMoves2);
        if (moves != null)
            return moves;
        moves = new HashSet<Piece>();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece m = new Piece(i, j, getPieceOwner(i, j));
                if (isLegalMove(m, player)) {
                    moves.add(m);
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

        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j].getOwner() == player && board[i][j].isKing) {
                    count += 10; //because king means there are 2 pieces stack on each other
                } else if (board[i][j].getOwner() == player) {
                    count++;
                }
            }
        }
        if (player == Player.PLAYER1)
            p1score = count;
        else if (player == Player.PLAYER2)
            p2score = count;
        return count;
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
    /**
     * Returns the current status of the game.
     */
//    public GameStatus getStatus() {
//        if (getNumPieces(getCurrentPlayer()) + getNumPieces(getOpponent(getCurrentPlayer())) == 0 ||
//                getNumPieces(getCurrentPlayer()) == getNumPieces(getOpponent(getCurrentPlayer())) &&
//                        getNumPieces(getOpponent(getCurrentPlayer())) != 12 ||
//                ((getValidMoves(Player.PLAYER1).size() <= 0 || getValidMoves(Player.PLAYER2).size() <= 0)) && getPreviousState() != null) {
//            int p1score = getScore(Player.PLAYER1);
//            int p2score = getScore(Player.PLAYER2);
//            if (p1score > p2score)
//                return GameStatus.PLAYER1WON;
//            else if (p1score < p2score)
//                return GameStatus.PLAYER2WON;
//            else
//                return GameStatus.TIE;
//        }
//        else {
//            return GameStatus.PLAYING;
//        }
//    }

    public GameStatus getStatus() {
        if (getNumPieces(Player.PLAYER1) == 0 || (getValidMoves(Player.PLAYER1).size() <= 0 && getPreviousState() != null)) {
                return GameStatus.PLAYER2WON;
        } else if (getNumPieces(Player.PLAYER2) == 0 || (getValidMoves(Player.PLAYER2).size() <= 0 && getPreviousState() != null)) {
                return GameStatus.PLAYER1WON;
        } else if (getNumPieces(Player.PLAYER2) == getNumPieces(Player.PLAYER1)  && getNumPieces(Player.PLAYER2) != 12) {
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
    public AbstractSet<State> getSuccessors() {
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
     * @see #applyMove(Piece, boolean)
     */
    public AbstractSet<State> getSuccessors(boolean includePreviousStateReference) {
        if (successors != null)
            return successors;
        AbstractSet<Piece> movesSet = getValidMoves();
        Piece moves[] = new Piece[movesSet.size()];
        moves = movesSet.toArray(moves);

        successors = new HashSet<State>(moves.length);
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] == null) continue;
            try {
                State temp = (State)this.clone();
                State newTemp = (State)applyMove(moves[i], includePreviousStateReference).clone();

                while (!temp.equals(newTemp) && temp.getNumPieces(getOpponent(getCurrentPlayer())) - newTemp.getNumPieces(getOpponent(getCurrentPlayer())) == 1 ) {
                    temp = (State)newTemp.clone();
                    newTemp = (State)temp.applyMove(moves[i], includePreviousStateReference).clone();
                    System.out.println(temp);
                }
                temp.player = getOpponent(player);
                successors.add(temp);
            }
            catch (InvalidMoveException ime) {
                /* This should not happen! */
//                System.err.println(ime.toString());
            }
        }

        return successors;
    }

    /**
     * Equivalent to {@link #applyMove(Piece,boolean) applyMove(move, true)}.
     *
     * @see #applyMove(Piece,boolean)
     */
    public State applyMove(Piece move) throws InvalidMoveException {
        return applyMove(move, true);
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
    public State applyMove(Piece move, boolean includePreviousStateReference) {
        Piece bracket;
        boolean found_good_direction = false;
        int row;
        int col;
        State newState = (State) clone();
        Player player = getCurrentPlayer();
        newState.previous = (includePreviousStateReference ? this : null);
        newState.move = move;

        if (move == null) {
            return newState;
//            throw new InvalidMoveException(move, getCurrentPlayer(),
//                    "The move sent to GameState.applyMove() was null!");
        }

        if (board[move.row][move.col].getOwner() != Player.EMPTY)
            throw new InvalidMoveException(move, getCurrentPlayer(), "The space is not empty!");


        bracket = wouldJumpAndRemove(move, player, Direction.UPLEFT);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row - 1][col - 1].owner = Player.EMPTY;
            newState.board[row - 2][col - 2].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPLEFT2);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;
            newState.board[row][col].owner = player;
            newState.board[row - 1][col - 1].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPRIGHT);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row - 1][col + 1].owner = Player.EMPTY;
            newState.board[row - 2][col + 2].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.UPRIGHT2);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row - 1][col + 1].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNLEFT);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row + 1][col - 1].owner = Player.EMPTY;
            newState.board[row + 2][col - 2].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNLEFT2);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row + 1][col - 1].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNRIGHT);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row + 1][col + 1].owner = Player.EMPTY;
            newState.board[row + 2][col + 2].owner = Player.EMPTY;
        }

        bracket = wouldJumpAndRemove(move, player, Direction.DOWNRIGHT);
        if (bracket != null) {
            found_good_direction = true;
            row = move.row;
            col = move.col;

            newState.board[row][col].owner = player;
            newState.board[row + 1][col + 1].owner = Player.EMPTY;
        }
        if (!found_good_direction) {
            return this;
        }
            newState.player = player;

        return newState;
    }

    /**
     * Returns the previous state (or <code>null</code> if this is the initial
     * state). This function may also return <code>null</code> if
     * <code>this</code> was created without a back-reference to the previous
     * state (to save memory).
     *
     * @see #applyMove(Piece, boolean)
     */
    public State getPreviousState() {
        return previous;
    }

    /**
     * Returns the previous move that was used to get to this state (or
     * <code>null</code> if this is the initial state).
     */
    public Piece getPreviousMove() {
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
        if (!(o instanceof State))
            return false;
        State gs = (State) o;
        if (gs.player != player)
            return false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] != gs.board[i][j])
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
        State gs = new State();
        System.out.println(gs);
        System.out.println("Successors:");
        State succ[] = gs.getSuccessors().toArray(new State[0]);
        for (int i = 0; i < succ.length; i++)
            System.out.println(succ[i]);
    }
}
