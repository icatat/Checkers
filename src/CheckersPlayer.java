import java.util.Date;

public abstract class CheckersPlayer {
    private String name;
    private Date deadline;

    CheckersPlayer(String name) {
        this.name = name;
        deadline = null;
    }

}
