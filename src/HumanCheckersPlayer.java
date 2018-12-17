// package edu.drexel.cs.ai.othello;

import java.util.Date;
//import java.util.ArrayList;
//import java.security.SecureRandom;

/**
 * An interface for having a human play othello through the
 * {@link UserInterface user interface}.
 *
 * @author <a href="http://www.sultanik.com" target="_blank">Evan A.
 *         Sultanik</a>
 */
public final class HumanCheckersPlayer extends CheckersPlayer {
    Move nextMove;
    Move prev;

    /**
     * Creates a new agent that plays according to human input.
     */
    public HumanCheckersPlayer(String name) {
        super(name);
        nextMove = null;
        prev = null;
    }

    /**
     * Callback function for receiving the next move from the UI.
     */
    public void handleUIInput(Move from) {
        nextMove = from;
    }

    /**
     * Returns the next move as input by the human from the UI. Note that this
     * function will block until the UI makes a call to
     * {@link #handleUIInput(Move)} with the next move. Also, the
     * HumanOthelloPlayer agent will always have an infinite deadline.
     */
    public Move getMove(GameState2 currentState, Date deadline) {
        while (nextMove == null) {
            /* wait for the UI to send us the next move */
            try {
                Thread.yield();
                Thread.sleep(10);
            }
            catch (Exception e) {
            }
        }
        Move next = nextMove;
        nextMove = null;
        return next;
    }
}
