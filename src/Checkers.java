
// consider 8X8 checkers, 10X10 and 12X12
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Checkers {
    private CheckersPlayer player1;
    private CheckersPlayer player2;
    private long p1timeUsed;
    private long p2timeUsed;
    private int turnDuration;

    private GameState2 state;

    private UserInterface ui;

    /**
     * The release version of this code.
     */
    public static final String VERSION = "1.1";
    /**
     * The release date of this code.
     */
    public static final String REV_DATE = "2006-11-05";


    public Checkers(CheckersPlayer player1, CheckersPlayer player2, UserInterface ui) {
        this.player1 = player1;
        this.player2 = player2;
        this.p1timeUsed = 0;
        this.p2timeUsed = 0;
        this.turnDuration = 5;
        this.state = new GameState2();
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
        System.out.println(state);
        while (state.getStatus() == GameState2.GameStatus.PLAYING && (state.getPreviousState() == null || state.getCurrentPlayer() != state.getPreviousState().getCurrentPlayer())) {
            if (state.getPreviousState() != null
                    && state.getPreviousState().getCurrentPlayer() == state.getCurrentPlayer())

                // We can change the log.
                log((state.getCurrentPlayer() == GameState2.Player.PLAYER1 ? player1.getName()
                        : player2.getName())
                        + " gets to go again!");

            ui.handleStateUpdate(state);

            CheckersPlayer player = (state.getCurrentPlayer() == GameState2.Player.PLAYER1 ? player1
                    : player2);

            boolean validMove;

            do {
                validMove = true;

                Move move;

                if (turnDuration <= 0 || player instanceof HumanCheckersPlayer) {
                    ui.updateTimeRemaining(player, -1);

                    Date start = new Date();
                    move = player.getMoveInternal(state, null);
                    Date end = new Date();
                    ui.updateTimeRemaining(player, -1);

                    if (state.getCurrentPlayer() == GameState2.Player.PLAYER1) {
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

                        Move moves[] = state.getValidMoves().toArray(new Move[0]);

                        int next = state.getRandom().nextInt(moves.length);

                        // We can change the log
                        log("Randomly moving " + player.getName() + " to " + moves[next].to.toString()
                                + "...");

                        move = moves[next];
                    }

                    if (state.getCurrentPlayer() == GameState2.Player.PLAYER1) {
                        p1timeUsed += playerTimerThread.getElapsedMillis();
                        ui.updateTimeUsed(player, p1timeUsed);
                    } else {
                        p2timeUsed += playerTimerThread.getElapsedMillis();
                        ui.updateTimeUsed(player, p2timeUsed);
                    }
                }
                try {
                    GameState2 original = (GameState2) state.clone();
                    state = state.applyMove(move);
                    System.out.println(state.getNumKings(state.getCurrentPlayer()));

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

    public GameState2 getState() {
        return state;
    }

    private class PlayerTimerThread implements Runnable {
        Thread thread;
        CheckersPlayer player;
        Date deadline;
        Move move;
        GameState2 state;
        Date startTime;
        Date endTime;

        public PlayerTimerThread(CheckersPlayer player, GameState2 state) {
            this.player = player;
            this.state = state;
            move = null;
            thread = new Thread(this);
            startTime = null;
            endTime = null;
        }

        public Move getMove(int timeLimitSeconds) throws TimeoutException {
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

    static String getSimplifiedClassName(String className) {
        int lastPeriod = className.lastIndexOf(".");
        if (lastPeriod < 0)
            return className;
        else
            return className.substring(lastPeriod + 1);
    }

    /**
     * To run Checkers with GUI: java checker <Player1> <Player2> <timeLimit>
     * @param args takes in player1, player 2 and timeLimit
     */
    public static void main(String[] args) {
        UserInterface ui = null;
        String[] sarg = new String[4];
        int sargs = 0;
        boolean printUse = false;
        long seed = 0;
        boolean seedSet = false;
        int turnDuration = -1;

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("-")) {
                /**
                 * This is the class name of an agent
                 */
                if (sargs < 4)
                    sarg[sargs++] = args[i];
                else {
                    System.err.println("Warning: unexpected argument \"" + args[i] + "\"!");
                    printUse = true;
                }
            }
            else if (args[i].equals("-s")) {
                /**
                 * Set the seed to the random number generator
                 */
                if (i == args.length - 1) {
                    System.err
                            .println("Error: -s requires an argument (the number with which to seed the random number generator)");
                    printUse = true;
                }
                else {
                    seed = Long.parseLong(args[++i]);
                    seedSet = true;
                }
            }
            else if (args[i].equals("-d")) {
                /**
                 * Set the maximum turn duration
                 */
                if (i == args.length - 1) {
                    System.err
                            .println("Error: -d requires an argument (the maximum turn duration in seconds)");
                    printUse = true;
                }
                else {
                    turnDuration = Integer.parseInt(args[++i]);
                }
            }
            else {
                System.err.println("Warning: unexpected argument \"" + args[i] + "\"!");
                printUse = true;
            }
        }

        if (ui == null)
            ui = new GraphicalUserInterface();

        CheckersPlayer players[];

        if (sargs < 2) {
            players = ui.getPlayers();
        }
        else {
            players = new CheckersPlayer[2];
            String player1class = sarg[0];
            String player1name = (sargs > 2 ? sarg[1] : getSimplifiedClassName(player1class));
            if (player1name.equals(""))
                player1name = "Player 1";
            String player2class = (sargs > 2 ? sarg[2] : sarg[1]);
            String player2name = (sargs > 3 ? sarg[3] : getSimplifiedClassName(player2class));
            if (player2name.equals(player1name))
                player2name = player2name + "2";
            else if (player2name.equals(""))
                player2name = "Player 2";
            try {
                players[0] = instantiatePlayer(player1class, player1name);
            }
            catch (NoSuchMethodException nsme1) {
                System.err
                        .println("Error Instantiating Agent: Make sure the agent class for player 1 ("
                                + player1class
                                + ")\nhas a constructor that accepts a single string as an argument!");
                printUse = true;
            }
            catch (Exception e1) {
                System.err.println("Error Instantiating Agent: " + e1.toString());
                printUse = true;
            }
            try {
                players[1] = instantiatePlayer(player2class, player2name);
            }
            catch (NoSuchMethodException nsme2) {
                System.err
                        .println("Error Instantiating Agent: Make sure the agent class for player 2 ("
                                + player2class
                                + ")\nhas a constructor that accepts a single string as an argument!");
                printUse = true;
            }
            catch (Exception e2) {
                System.err.println("Error Instantiating Agent: " + e2.toString());
                printUse = true;
            }
        }

        if (printUse) {
            printUsage();
            System.exit(1);
        }

        ui.setPlayers(players[0], players[1]);
        if (ui instanceof Logger) {
            players[0].setLogger((Logger) ui);
            players[1].setLogger((Logger) ui);
        }
        Checkers checkers = new Checkers(players[0], players[1], ui);
        checkers.turnDuration = turnDuration;
        if (ui instanceof Logger)
            ((Logger) ui).log(getVersionInfo(), null);
        else
            System.out.println(getVersionInfo());
        CheckersPlayer winner = checkers.play();
        if (winner == null)
            checkers.log("It was a tie!");
        else
            checkers.log("The winner was " + winner + "!");

        for (CheckersPlayer op : players) {
            if (op instanceof Minimax) {
                Minimax mm = (Minimax) op;
                checkers.log(op.getName() + " Stats:");
                checkers.log("          Nodes: " + mm.getNodesGenerated());
                checkers.log("    Evaluations: " + mm.getStaticEvaluations());
                checkers.log("  Ave Branching: " + mm.getAveBranchingFactor());
                checkers.log("  Eff Branching: " + mm.getEffectiveBranchingFactor());
            }
        }
    }

    static String getVersionInfo() {
        return "Othello Version " + VERSION + " " + REV_DATE + "\n"
                + "Copyright 2006--2007, Evan A. Sultanik" + "\n" + "http://www.sultanik.com/"
                + "\n" + "\n";
    }

    /**
     * Prints command line usage information.
     */
    public static void printUsage() {
        System.err.println(getVersionInfo());
        System.err
                .println("Usage: othello [options] [player1class [player1name] player2class [player2name]]");
        System.err.println();
        System.err.println("  player1class      Class name of the agent for player1");
        System.err
                .println("                    (i.e. \"org.drexel.edu.cs.ai.othello.RandomOthelloPlayer\")");
        System.err.println("  player1name       The name for player1 (i.e. \"Evan's Agent\")");
        System.err.println("  player2class      Class name of the agent for player2");
        System.err.println("  player2name       The name for player2");
        System.err.println();
        System.err.println("OPTIONS:");
        System.err.println("         -s  number Seed for the simulator's random number generator.");
        System.err.println("                    If omitted, time since the epoch is used.");
        System.err.println("         -nw        Run in console mode (a GUI is used by default)");
        System.err
                .println("         -d  number Sets the amount of time (in seconds) an agent has to make");
        System.err.println("                    its decision each turn (i.e. the deadline).");
        System.err
                .println("                    A value <= 0 will result in an infinite deadline (this is");
        System.err.println("                    the default).");
    }
}
