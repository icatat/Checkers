
/**
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
 *         Sultanik</a>
 */
public class InvalidMoveException extends RuntimeException {

    private static final long serialVersionUID = 1;

    Move move;
    GameState2.Player player;

    /**
     * Constructs an <code>InvalidMoveException</code> with the offending move,
     * the player that attempted the invalid move, and a message explaining why
     * the move was invalid.
     */
    public InvalidMoveException(Move move, GameState2.Player player, String message) {
        super(message);
        this.move = move;
        this.player = player;
    }

    /**
     * Returns the offending move.
     */
    public Move getMove() {
        return move;
    }

    /**
     * Returns the offending player.
     */
    public GameState2.Player getPlayer() {
        return player;
    }

    /**
     * Returns a string representation of this exception.
     */
    public String toString() {
        return (move != null ? move.toString() + ": " : "") + super.toString();
    }
}
