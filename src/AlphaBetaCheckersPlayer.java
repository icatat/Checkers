import java.util.AbstractSet;
import java.util.Date;

public class AlphaBetaCheckersPlayer extends CheckersPlayer implements Minimax{

    // Based on the experiment result depthLimit is set to 4.
    private int depthLimit = 3;
    private static int staticEvaluations = 0;
    private static int totalSuccessors = 0;
    private static int exploredSuccessors = 0;
    private static int totalParents = 0;

    private static GameState2.Player curOriginalPlayer = null;

    public AlphaBetaCheckersPlayer(String name) {
        super(name);
    }

    @Override
    public Move getMove(GameState2 curState, Date var2) {
        AbstractSet<GameState2> successors = curState.getSuccessors(true);

        GameState2 optimalState = null;

        int evaluation = Integer.MIN_VALUE;

        curOriginalPlayer = curState.getCurrentPlayer();

        // Minimax with alpha beta pruning
        for (GameState2 state : successors) {
            int curEval = minValue(state, Integer.MIN_VALUE, Integer.MAX_VALUE, 1);

            if (curEval > evaluation) {
                evaluation = curEval;
                optimalState = state;
            }
        }

        if (optimalState == null) return null;

        return optimalState.getPreviousMove();
    }

    @Override
    public int staticEvaluator(GameState2 state) {
        if (state == null) return 0;
        staticEvaluations++;

        return state.getScore(curOriginalPlayer);
    }

    @Override
    public int getNodesGenerated() {
        return exploredSuccessors;
    }

    @Override
    public int getStaticEvaluations() {
        return staticEvaluations;
    }

    @Override
    public double getAveBranchingFactor() {
        return (double)totalSuccessors/(double)totalParents;
    }

    @Override
    public double getEffectiveBranchingFactor() {
        return (double)exploredSuccessors/(double)totalParents;
    }

    /**
     * Check if the state is terminal, or if the depth limit has been exceeded
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return true if it terminal state, else return false;
     */
    private boolean isTerminalState(GameState2 state, int depth) {

        if (state == null || depthLimit != -1 && depth >= depthLimit) return true;

        if(state.getStatus() != GameState2.GameStatus.PLAYING) {
            return true;
        }

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
    public int maxValue(GameState2 state, int a, int b, int depth) {
        if (isTerminalState(state, depth)) {
            return staticEvaluator(state);
        }

        int v = Integer.MIN_VALUE;
        AbstractSet<GameState2> successors = state.getSuccessors(true);
        totalSuccessors += successors.size();
        totalParents++;
        depth++;

        for (GameState2 s : successors) {
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

    public int minValue(GameState2 state, int a, int b, int depth) {
        if (isTerminalState(state, depth)) {
            return staticEvaluator(state);
        }

        int v = Integer.MAX_VALUE;
        AbstractSet<GameState2> successors = state.getSuccessors(true);
        totalSuccessors+= successors.size();
        totalParents++;
        depth++;
        for (GameState2 s : successors) {
            if (s == null) continue;
            exploredSuccessors++;
            v = Math.min(v, (maxValue(s, a, b, depth)));
            if (v <= a) return v;
            b = Math.min(v, b);
        }
        return v;

    }
}