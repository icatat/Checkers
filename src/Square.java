
public class Square {
    int row;
    int col;
    static final String[] colnames = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
    boolean isKing;
    GameState2.Player owner;

    public Square(int newRow, int newCol, GameState2.Player player) {
        this.row = newRow;
        this.col = newCol;
        this.isKing = false;
        this.owner = player;

    }

    public GameState2.Player getOwner() {
        return owner;
    }

    public void setPlayer(GameState2.Player player) {
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
