import java.util.AbstractSet;
import java.util.Date;

public class MiniMaxCheckersPlayer extends CheckersPlayer implements Minimax{

    private int depthLimit = 4;
    private static int staticEvaluations = 0;
    private static int totalSuccessors = 0;
    private static int exploredSuccessors = 0;
    private static int totalParents = 0;
    private static GameState2.Player curOriginalPlayer = null;

    public MiniMaxCheckersPlayer(String name) {
        super(name);
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

    @Override
    public Move getMove(GameState2 curState, Date var2) {
        AbstractSet<GameState2> successors = curState.getSuccessors(true);

        GameState2 optimalState = null;

        int evaluation = Integer.MIN_VALUE;
        curOriginalPlayer = curState.getCurrentPlayer();

        // Minimax algorithm
        for (GameState2 state : successors) {

            int curEval = minValue(state, depthLimit);

            if (curEval > evaluation) {
                evaluation = curEval;
                optimalState = (GameState2)state.clone();
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
    public int minValue(GameState2 state, int depth) {
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
    public int maxValue(GameState2 state, int depth) {
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
    private boolean isTerminalState(GameState2 state, int depth) {

        if(state.getNumPieces(state.getCurrentPlayer()) == 0) return true;
        if(state.getNumPieces(state.getOpponent(state.getCurrentPlayer())) == 0) return true;

        if (state == null || depthLimit != -1 && depth >= depthLimit) {
            return true;
        }

        if(state.getStatus() != GameState2.GameStatus.PLAYING) {
            return true;
        }

        return false;

    }
}
