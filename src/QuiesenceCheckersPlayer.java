import java.util.Date;
import java.util.AbstractSet;
public class QuiesenceCheckersPlayer extends CheckersPlayer implements Minimax{


    // Based on the experiment result depthLimit is set to 4.
    private int depthLimit = 4;
    private static int staticEvaluations = 0;
    private static int totalSuccessors = 0;
    private static int exploredSuccessors = 0;
    private static int totalParents = 0;

    public QuiesenceCheckersPlayer (String name) {
        super(name);
    }

    /**
     * Constructor 2
     *
     * @param name the name of the player
     * @param depthLimit maximum depth that can be explored
     */
    public QuiesenceCheckersPlayer (String name, int depthLimit) {
        super(name);
        this.depthLimit = depthLimit;
    }

    /**
     * This method uses minimax algorithm to find the best move for MaxPlayer
     *
     * @param currentState current state of the game
     * @param deadline maximum amount of time the operation can take
     * @return return the best move for MaxPlayer
     */
    public Piece getMove(State currentState, Date deadline) {
        AbstractSet<State> successors = currentState.getSuccessors(true);

        State optimalState = null;
        State.Player currentPlayer = currentState.getCurrentPlayer();

        int evaluation = Integer.MAX_VALUE;

        // Minimax with alpha beta pruning
        for (State state : successors) {
            int curEval = minValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);

            if (curEval < evaluation) {
                evaluation = curEval;
                optimalState = state;
            }
        }

        if (optimalState == null) return null;

        return optimalState.getPreviousMove();

    }


    /**
     * Check if the state is terminal, or if the depth limit has been exceeded
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return true if it terminal state, else return false;
     */
    private boolean isTerminalState(State state, int depth) {

        if (depthLimit != -1 && depth >= depthLimit) return true;

        if(state.getStatus() != State.GameStatus.PLAYING) return true;

        return false;

    }

    /**
     * It maximizes the value of the evaluation function.
     *
     * @param state the state to be evaluated
     * @param a value of the best alternative for max
     * @param b value of the best alternative for min
     * @param depth current depth of the state
     * @return the maximum value of the evaluation function
     */
    public int maxValue(State state, int a, int b, int depth) {
        if (isTerminalState(state, depth)) {
            return quiesenceSearch(a, b, state, depth);
        }

        int v = Integer.MIN_VALUE;
        AbstractSet<State> successors = state.getSuccessors(true);
        totalSuccessors += successors.size();
        totalParents++;
        depth++;

        for (State s : successors) {
            if ( s == null) continue;

            exploredSuccessors++;
            v = Math.max(v, (minValue(s,a, b, depth)));

            if (v >= b) return v;

            a = Math.max(v, a);
        }

        return v;

    }

    /**
     * It minimizes the value of the evaluation function.
     *
     * @param state the state to be evaluated
     * @param a value of the best alternative for max
     * @param b value of the best alternative for min
     * @param depth current depth of the state
     * @return the minimum value of the evaluation function
     */

    public int minValue(State state, int a, int b, int depth) {
        if (isTerminalState(state, depth)) {
            return quiesenceSearch(a, b, state, depth);
        }

        int v = Integer.MAX_VALUE;
        AbstractSet<State> successors = state.getSuccessors(true);
        totalSuccessors+= successors.size();
        totalParents++;
        depth++;
        for (State s : successors) {
            if (s == null) continue;
            exploredSuccessors++;
            v = Math.min(v, (maxValue(s, a, b, depth)));
            if (v <= a) return v;
            b = Math.min(v, b);
        }
        return v;

    }

    /**
     * quiesce(int alpha, int beta) {
     *         int score = eval();
     *         if (score >= beta) return score;
     *         for (each capturing move m) {
     *             make move m;
     *             score = -quiesce(-beta,-alpha);
     *             unmake move m;
     *             if (score >= alpha) {
     *                 alpha = score;
     *                 if (score >= beta) break;
     *             }
     *         }
     *         return score;
     *     }
     */
    private int quiesenceSearch(int alpha, int beta, State state, int depth) {
        int score = staticEvaluator(state);
        if (score >= beta) return score;
        AbstractSet<Piece> successors = state.getValidMoves();
        depth++;
        System.out.println(depth);
        for (Piece p : successors) {
            state.applyMove(p);
            score = -quiesenceSearch(-alpha, -beta, state, depth);
            state = state.getPreviousState();

            if (score >= alpha) {
                alpha = score;
                if (score >= beta) break;
            }

        }
        return score;
    }


    /**
     * Compute the value of the simple static evaluation function
     *
     * @state the state to be evaluated
     * @return the value of the simple static evaluation function
     */
    @Override
    public int staticEvaluator(State state) {
        staticEvaluations++;
        return state.getScore(state.getCurrentPlayer());
    }

    /**
     * Get the number of nodes generated
     *
     * @return the number of nodes generated.
     */
    @Override
    public int getNodesGenerated() {
        return exploredSuccessors;
    }

    /**
     * Get the number of static evaluations
     *
     * @return the number of static evaluations performed.
     */
    @Override
    public int getStaticEvaluations() {
        return staticEvaluations;
    }

    /**
     * Get the average branching factor of the nodes that
     * were expanded during the search.
     *
     * @return the average branching factor.
     */
    @Override
    public double getAveBranchingFactor() {
        return (double)totalSuccessors/(double)totalParents;
    }

    /**
     * Get the effective branching factor of the nodes that
     * were expanded during the search.
     *
     * @return the effective branching factor.
     */
    @Override
    public double getEffectiveBranchingFactor() {
        return (double)exploredSuccessors/(double)totalParents;
    }
}
