import java.util.Date;

public class QuiesenceCheckersPlayer extends CheckersPlayer implements Minimax{

    public QuiesenceCheckersPlayer(String name) {
        super(name);
    }

    @Override
    public Piece getMove(State var1, Date var2) {
        return null;
    }

    @Override
    public int staticEvaluator(State state) {
        return 0;
    }

    @Override
    public int getNodesGenerated() {
        return 0;
    }

    @Override
    public int getStaticEvaluations() {
        return 0;
    }

    @Override
    public double getAveBranchingFactor() {
        return 0;
    }

    @Override
    public double getEffectiveBranchingFactor() {
        return 0;
    }
}
