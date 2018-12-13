import java.util.Date;

public class NegaScoutCheckersPlayer extends CheckersPlayer implements Minimax{
    public NegaScoutCheckersPlayer(String name) {
        super(name);
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

    @Override
    public Piece getMove(State var1, Date var2) {
        return null;
    }
}
