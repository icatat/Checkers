public interface Minimax {

    public int staticEvaluator(GameState2 state);

    public int getNodesGenerated();

    public int getStaticEvaluations();

    public double getAveBranchingFactor();

    public double getEffectiveBranchingFactor();
}
