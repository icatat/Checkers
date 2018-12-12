
public class Piece {
    int row;
    int col;
    static final String[] colnames = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
    boolean isKing;
    State.Player owner;

    public Piece(int newRow, int newCol, State.Player player) {
        this.row = newRow;
        this.col = newCol;
        this.isKing = false;
        this.owner = player;

    }

    public State.Player getOwner() {
        return owner;
    }

    public void setPlayer(State.Player player) {
        this.owner = player;
    }
    public void setKing(int newX, int newY) {
        this.isKing = true;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public String toString() {
        return colnames[this.col] + Integer.toString(this.row);
    }

}
