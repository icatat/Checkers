public interface Minimax {

    public int staticEvaluator(State state);

    public int getNodesGenerated();

    public int getStaticEvaluations();

    public double getAveBranchingFactor();

    public double getEffectiveBranchingFactor();
}
