import java.util.AbstractSet;
import java.util.Date;

public class NegaScoutCheckersPlayer extends CheckersPlayer implements Minimax{

    private int depthLimit = 3;
    private static int staticEvaluations = 0;
    private static int totalSuccessors = 0;
    private static int exploredSuccessors = 0;
    private static int totalParents = 0;
    private GameState2.Player originalPlayer = null;

    public NegaScoutCheckersPlayer(String name) {
        super(name);
    }

    //constructor with 2 parameters
    public NegaScoutCheckersPlayer(String name, int depthLimit) {
        super(name);
        this.depthLimit = depthLimit;
    }

    public int getDepthLimit() {
        return depthLimit;
    }

    @Override
    public int staticEvaluator(GameState2 state) {
        if (state == null) return 0;
        staticEvaluations++;

        return state.getScore(originalPlayer);
    }

    @Override
    public int getNodesGenerated() {
        return totalSuccessors;
    }

    @Override
    public int getStaticEvaluations() {
        return staticEvaluations;
    }

    @Override
    public double getAveBranchingFactor() {
        return (double)totalSuccessors/(double) totalParents;
    }

    @Override
    public double getEffectiveBranchingFactor() {
        return (double)exploredSuccessors/(double) totalParents;
    }

    @Override
    public Move getMove(GameState2 currentState, Date deadline) {
        AbstractSet<GameState2> successors = currentState.getSuccessors(true);

        GameState2 optimalState = null;
        // State.Player currentPlayer = currentState.getCurrentPlayer();
        // substitute the above line with below
        originalPlayer = currentState.getCurrentPlayer();

        // fixes the 2's complement reversal bug
        int evaluation = Integer.MIN_VALUE/2;

        // Choosing max from NegaScout algorithm
        for (GameState2 state : successors) {
            int curEval = NegaScout(state, 1, Integer.MIN_VALUE/2, Integer.MAX_VALUE, System.currentTimeMillis(), deadline);

            if (curEval > evaluation) {
                evaluation = curEval;
                optimalState = state;
            }
        }

        if (optimalState == null) return null;
        return optimalState.getPreviousMove();
    }

    /**
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @param alpha value of the best alternative for max
     * @param beta value of the best alternative for min
     * @param startTime the starting time of the algorithm
     * @param deadline maximum amount of time the operation can take
     * @return
     */
    public int NegaScout (GameState2 state, int depth, int alpha, int beta, long startTime, Date deadline) {

    	if (depth > depthLimit || isTerminalState(state, depth, startTime, deadline)) {
            return staticEvaluator(state);
        }
        int score = alpha;
        int n = beta;
        AbstractSet<GameState2>successors = state.getSuccessors(true);
        totalSuccessors += successors.size();
        totalParents++;
        for (GameState2 succ : successors) {
            if (succ == null) continue;
            exploredSuccessors++;
            int cur = -NegaScout(succ, depth + 1, -n, -alpha, startTime, deadline);
            if (cur > score) {
                if (n == beta || depth <= 2) {
                    score = cur;
                } else {
                    score = -NegaScout(succ, depth + 1, -beta, -cur, startTime, deadline);
                }
            }

            if (score > alpha) {
                alpha = score;
            }
            if (alpha >= beta) return alpha;
            n = alpha + 1;
        }

        return score;
    }

    /**
     * Check if the state is terminal, or if the depth limit has been exceeded
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return true if it terminal state, else return false;
     */
    private boolean isTerminalState(GameState2 state, int depth, long startTime,  Date deadline) {

        if (depthLimit != -1 && depth >= depthLimit) return true;
        if(state.getStatus() != GameState2.GameStatus.PLAYING) return true;
        if (deadline != null &&  System.currentTimeMillis() - startTime >= deadline.getTime()) return true;

        return false;

    }
}