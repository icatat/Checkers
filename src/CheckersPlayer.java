import java.util.Date;

public abstract class CheckersPlayer {
    private String name;
    private Date deadline;
    private Logger logger;

    public CheckersPlayer(String name) {
        this.name = name;
        this.deadline = null;
        this.logger = null;
    }

    void setLogger(Logger logger) {
        this.logger = logger;
    }

    public abstract Move getMove(GameState2 var1, Date var2);

    public String getName() {
        return this.name;
    }

    public Move getMoveInternal(GameState2 state, Date deadline) {
        this.deadline = deadline;
        Move var3 = this.getMove(state, deadline);
        this.deadline = null;
        return var3;
    }

    protected long getMillisUntilDeadline() {
        return this.deadline == null ? 0L : this.deadline.getTime() - (new Date()).getTime();
    }

    protected void log(String var1) {
        if (this.logger == null) {
            System.out.println(this.name + ": " + var1);
        } else {
            logger.log(var1, this);
        }
    }

    public String toString() {
        return this.name;
    }

}
