import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

public class Checkers {
    private CheckersPlayer player1;
    private CheckersPlayer player2;
    private long p1timeUsed;
    private long p2timeUsed;
    private int turnDuration;

    private State state;

    private UserInterface ui;

    public Checkers(CheckersPlayer player1, CheckersPlayer player2, UserInterface ui) {
        this.player1 = player1;
        this.player2 = player2;
        this.p1timeUsed = 0;
        this.p2timeUsed = 0;
        this.turnDuration = 5;
        this.state = new State();
        this.ui = ui;

    }

    public static CheckersPlayer instantiatePlayer(String className, String playerName)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, ClassNotFoundException,
            ClassCastException {
        Class<?> c = Class.forName(className);
        Constructor<?> constr = c.getDeclaredConstructor(String.class);
        Object o = constr.newInstance(playerName);
        return (CheckersPlayer) o;
    }

    public CheckersPlayer play() {
        while (state.getStatus() == State.GameStatus.PLAYING) {
            if (state.getPreviousState() != null
                    && state.getPreviousState().getCurrentPlayer() == state.getCurrentPlayer())

                // We can change the log.
                log((state.getCurrentPlayer() == State.Player.PLAYER1 ? player1.getName()
                        : player2.getName())
                        + " gets to go again!");

            ui.handleStateUpdate(state);

            CheckersPlayer player = (state.getCurrentPlayer() == State.Player.PLAYER1 ? player1
                    : player2);
            boolean validMove;
            do {
                validMove = true;

                Piece move;

                if (turnDuration <= 0) {
                    ui.updateTimeRemaining(player, -1);

                    Date start = new Date();
                    move = player.getMoveInternal(state, null);
                    Date end = new Date();
                    ui.updateTimeRemaining(player, -1);

                    if (state.getCurrentPlayer() == State.Player.PLAYER1) {
                        p1timeUsed += end.getTime() - start.getTime();
                        ui.updateTimeUsed(player, p1timeUsed);
                    }
                    else {
                        p2timeUsed += end.getTime() - start.getTime();
                        ui.updateTimeUsed(player, p2timeUsed);
                    }
                } else {
                    PlayerTimerThread playerTimerThread = new PlayerTimerThread(player, state);
                    try {
                        move = playerTimerThread.getMove(turnDuration);
                    } catch (TimeoutException te) {

                        log(te.getMessage());

                        Piece moves[] = state.getValidMoves().toArray(new Piece[0]);

                        int next = state.getRandom().nextInt(moves.length);

                        // We can change the log
                        log("Randomly moving " + player.getName() + " to " + moves[next].toString()
                                + "...");

                        move = moves[next];
                    }

                    if (state.getCurrentPlayer() == State.Player.PLAYER1) {
                        p1timeUsed += playerTimerThread.getElapsedMillis();
                        ui.updateTimeUsed(player, p1timeUsed);
                    } else {
                        p2timeUsed += playerTimerThread.getElapsedMillis();
                        ui.updateTimeUsed(player, p2timeUsed);
                    }
                }
                try {
                    state = state.applyMove(move);
                } catch (RuntimeException rte) {
                    // This can be changed to InvalidMoveException if we want to make a separate class for that
                    log(rte.getMessage());
                    ui.handleStateUpdate(state);
                    validMove = false;
                }
            } while (!validMove);
        }

        ui.handleStateUpdate(state);
        switch (state.getStatus()) {
            case PLAYER1WON:
                return player1;
            case PLAYER2WON:
                return player2;
            default:
                return null;
        }
    }

    /**
     * Logs a message to the user interface.
     */
    public void log(String message) {
        if (ui instanceof Logger) {
            ((Logger) ui).log(message, this);
        }else{
            System.err.println(message);
        }
    }

    public State getState() {
        return state;
    }

    private class PlayerTimerThread implements Runnable {
        Thread thread;
        CheckersPlayer player;
        Date deadline;
        Piece move;
        State state;
        Date startTime;
        Date endTime;

        public PlayerTimerThread(CheckersPlayer player, State state) {
            this.player = player;
            this.state = state;
            move = null;
            thread = new Thread(this);
            startTime = null;
            endTime = null;
        }

        public Piece getMove(int timeLimitSeconds) throws TimeoutException {
            long sleepInterval = ((long) timeLimitSeconds * 1000) / 60;
            startTime = new Date();
            deadline = new Date(startTime.getTime() + (long) timeLimitSeconds * 1000);
            thread.start();
            while (move == null && (new Date()).before(deadline)) {
                try {
                    Thread.yield();
                    Thread.sleep(sleepInterval);
                } catch (Exception e) {
                }
                ui.updateTimeRemaining(player, (new Long((deadline.getTime() - (new Date())
                        .getTime()) / 1000)).intValue());
            }
            thread = null;
            if (move == null)
                throw new TimeoutException(player.getName() + " took to long to move!");
            if (endTime == null) {
                Date currTime = new Date();
                if (currTime.getTime() - startTime.getTime() > (long) timeLimitSeconds * 1000)
                    endTime = new Date(startTime.getTime() + (long) timeLimitSeconds * 1000);
                else
                    endTime = currTime;
            }
            return move;
        }

        public long getElapsedMillis() {
            if (endTime != null && startTime != null)
                return endTime.getTime() - startTime.getTime();
            else if (startTime != null)
                return (new Date()).getTime() - startTime.getTime();
            else
                return 0;
        }

        public void run() {
            move = player.getMoveInternal(state, deadline);
            if (endTime == null)
                endTime = new Date();
            thread = null;
        }

    }

    /**
     * To run Checkers with GUI: java checker <Player1> <Player2> <timeLimit>
     * @param args takes in player1, player 2 and timeLimit
     */
    public static void main(String[] args) {
        if(args.length > 3 || args.length < 2){
            System.err.println("Warning: not correct arguments");
        }

        UserInterface ui = new GraphicalUserInterface();;

        int turnDuration = -1;
        CheckersPlayer[] players = new CheckersPlayer[2];

        for(int i=0; i<args.length; i++){
            if(i==0){
                try {
                    checkersPlayers[0] = instantiatePlayer(args[0], "Player 1: " + args[0]);
                }catch (Exception e1){
                    System.err.println("Error Instantiating Agent for Player 1");
                }
            }else if(i==1){
                try {
                    checkersPlayers[1] = instantiatePlayer(args[1], "Player 2: " + args[1]);
                }catch (Exception e1){
                    System.err.println("Error Instantiating Agent for Player 2");
                }
            }else{
                turnDuration = Integer.parseInt(args[2]);
            }
        }

        ui.setPlayers(players[0], players[1]);

        Checkers checkers = new Checkers(players[0], players[1], ui);
        checkers.turnDuration = turnDuration;

        CheckersPlayer winner = checkers.play();

        // To print winner in the ui
        if (winner == null) {
            checkers.log("It was a tie!");
        }else {
            checkers.log("The winner was " + winner + "!");
        }

        for (CheckersPlayer op : players) {
            MiniMax mm = (MiniMax) op;
            checkers.log("Stats for " + op.getName() + ":");
            checkers.log("          Nodes: " + mm.getNodesGenerated());
            checkers.log("    Evaluations: " + mm.getStaticEvaluations());
            checkers.log("  Ave Branching: " + mm.getAveBranchingFactor());
            checkers.log("  Eff Branching: " + mm.getEffectiveBranchingFactor());
        }
    }
}
