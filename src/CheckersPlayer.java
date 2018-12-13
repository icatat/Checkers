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

    public abstract Piece getMove(State var1, Date var2);

    public String getName() {
        return this.name;
    }

    public Piece getMoveInternal(State state, Date deadline) {
        this.deadline = deadline;
        Piece var3 = this.getMove(state, deadline);
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
