import java.util.AbstractSet;
import java.util.Date;

public class MiniMaxCheckersPlayer extends CheckersPlayer implements Minimax{

    private int depthLimit = 4;
    private static int staticEvaluations = 0;
    private static int totalSuccessors = 0;
    private static int exploredSuccessors = 0;
    private static int totalParents = 0;

    public MiniMaxCheckersPlayer(String name) {
        super(name);
    }

    @Override
    public int staticEvaluator(State state) {
        staticEvaluations++;
        return state.getScore(state.getCurrentPlayer());
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

    @Override
    public Piece getMove(State curState, Date var2) {
        AbstractSet<State> successors = curState.getSuccessors(true);

        State optimalState = null;

        int evaluation = Integer.MAX_VALUE;

        // Minimax algorithm
        for (State state : successors) {
            int curEval = minValue(state, 1);

            if (curEval < evaluation) {
                evaluation = curEval;
                optimalState = state;
            }
        }

        if (optimalState == null) return null;
        return optimalState.getPreviousMove();
    }

    /**
     * It minimizes the value of the evaluation function.
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return the minimum value of the evaluation function
     */
    public int minValue(State state, int depth) {
        if (isTerminalState(state, depth)) {
            return staticEvaluator(state);
        }

        int v = Integer.MAX_VALUE;
        AbstractSet<State> successors = state.getSuccessors(true);
        totalSuccessors+= successors.size();
        totalParents++;
        depth++;
        System.out.println(depth);
        for (State s : successors) {
            if (s == null) continue;
            exploredSuccessors++;
            v = Math.min(v, (maxValue(s, depth)));
        }
        return v;

    }

    /**
     * It maximizes the value of the evaluation function.
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return the maximum value of the evaluation function
     */
    public int maxValue(State state, int depth) {
        if (isTerminalState(state, depth)) {
            return staticEvaluator(state);
        }

        int v = Integer.MIN_VALUE;
        AbstractSet<State> successors = state.getSuccessors(true);
        totalSuccessors += successors.size();
        totalParents++;
        depth++;
        System.out.println(depth);
        for (State s : successors) {
            if ( s == null) continue;
            exploredSuccessors++;
            v = Math.max(v, (minValue(s, depth)));
        }

        return v;

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

        if(state.getStatus() != State.GameStatus.PLAYING) {
            return true;
        }

        return false;

    }
}
