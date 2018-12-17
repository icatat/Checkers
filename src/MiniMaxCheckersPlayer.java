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
            System.out.println("Succ: " + state);
            int curEval = minValue(state, depthLimit);

            if (curEval > evaluation) {
                evaluation = curEval;
                optimalState = (GameState2)state.clone();
            }
        }

        if (optimalState == null) return null;

        return optimalState.getPreviousMove();


//        curOriginalPlayer = curState.getCurrentPlayer();
//
//        Move moves[] = curState.getValidMoves().toArray(new Move[0]); // the array of all available moves
//        if (moves.length == 0) return null;
//        Move bestMove = (Move)moves[0].clone();
//        int max = -1;
//        // System.out.println(currentState.toString());
//
//        // Minimax
//        for (Move m : moves) {
//            GameState2 newState = (GameState2) curState.clone();
//            int score = minValue(newState.applyMove(m), 1);
//
//            // System.out.println("Considering move " + m.toString() + ", " + "score would be " + score);
//            if (score > max) {
//                max = score;
//                bestMove = (Move)m.clone();
//            }
//        }
//         System.out.println("bestMove " + bestMove.from.toString() + bestMove.to.toString());
//        return bestMove;
    }

    /**
     * It minimizes the value of the evaluation function.
     *
     * @param state the state to be evaluated
     * @param depth current depth of the state
     * @return the minimum value of the evaluation function
     */
    public int minValue(GameState2 state, int depth) {
        System.out.println("MIN: " + state);
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
        System.out.println("OUT: " + state);
        if (isTerminalState(state, depth)) {
            return staticEvaluator(state);
        }


        int v = Integer.MIN_VALUE;
        AbstractSet<GameState2> successors = state.getSuccessors(true);
        totalSuccessors += successors.size();
        totalParents++;
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
        System.out.println("Terminal? " + state);
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
