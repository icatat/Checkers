
public class Move {
    Square from;
    Square to;
    GameState2.Player player;

    public Move(Square from, Square to, GameState2.Player player) {
        this.from = from;
        this.to = to;
        this.player = player;

    }

    public Move(Square from, Square to) {
        this.from = from;
        this.to = to;
        this.player = null;
    }

    public GameState2.Player getPlayer() {
        return player;
    }


    public void setKingFrom(boolean val) {
        this.from.setKing(val);
    }
    public void setKingTo(boolean val) {
        this.to.setKing(val);
    }
    public void setPlayer(GameState2.Player player) {
        this.player = player;
        from.setPlayer(player);
        to.setPlayer(player);
    }

    public Square getFrom() {
        return this.from;
    }

    public Square getTo() {
        return this.to;
    }

    public int colDist() {
        return to.col - from.col;
    }

    public int rowDist() {
        return to.row - from.row;
    }

    public Object clone() {

        Square newFrom = new Square(from.getRow(), from.getCol(), player);
        Square newTo = new Square(to.getRow(), to.getCol(), player);
        Move m = new Move(newFrom, newTo, player);

        return m;
    }


}
