
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author 
 */
public class GraphicalUserInterface extends JFrame implements UserInterface, Logger {
    
    private static final long serialVersionUID = 1;
    
    GameState2 state;
    //TODO check if panel is needed
    CheckersPanel panel;
    private CheckersPlayer player1, player2;
    private JTextPane logArea;
    private LogDocument logDocument;
    private JLabel name1, name2;
    private JLabel score1, score2;
    private JLabel timeUsed1, timeUsed2;
    private JLabel timeRemaining1, timeRemaining2;
    private JLabel arrow1, arrow2;
    private LinkedList<MessageSourcePair> logQueue;

    private class MessageSourcePair {
        public String message;
        public Object source;

        public MessageSourcePair(String m, Object s) {
            message = m;
            source = s;
        }
    }

    /**
     * Constructs a new <code>GraphicalUserInterface</code>.
     */
    public GraphicalUserInterface() {
        super("Checkers Game");
        player1 = null;
        player2 = null;
        panel = null;
        setPreferredSize(new Dimension(800, 510));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        logQueue = new LinkedList<MessageSourcePair>();
    }

    public void setPlayers(CheckersPlayer player1, CheckersPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    private class GameSelectorDialog extends Dialog implements ActionListener, WindowListener {
        
        private static final long serialVersionUID = 1;
        
        JComboBox<String> p1selector, p2selector;
        JTextField p1name, p2name;
        JButton okayButton;
        public CheckersPlayer player1, player2;

        public GameSelectorDialog(Frame parent) {
            super(parent, "Select the Players", true);
            this.player1 = null;
            this.player2 = null;
            Vector<String> possiblePlayers = new Vector<String>();
            //TODO check if ClassCreator is necessary
            for (Iterator<Class<? extends CheckersPlayer>> i = ClassCreator.getClassesOfType(
                    CheckersPlayer.class).iterator(); i.hasNext();) {
                Class<?> c = i.next();
                if (!c.getName().equals("CheckersPlayer")) {
                    possiblePlayers.add(c.getName());
                    // 		    try {
                    // 			possiblePlayers.addElement((OthelloPlayer)c.newInstance());
                    // 		    } catch(Exception e) {
                    // 			e.printStackTrace();
                    // 		    }
                }
            }
            p1selector = new JComboBox<String>(possiblePlayers);
            p2selector = new JComboBox<String>(possiblePlayers);

            JPanel selectorPanel = new JPanel();
            selectorPanel.setLayout(new BoxLayout(selectorPanel, BoxLayout.Y_AXIS));
            JPanel playerFields = new JPanel();

            JPanel player1 = new JPanel();
            player1.setBorder(new BevelBorder(BevelBorder.RAISED));
            player1.setLayout(new BoxLayout(player1, BoxLayout.Y_AXIS));
            player1.add(new JLabel("Player 1"));

            JPanel player2 = new JPanel();
            player2.setBorder(new BevelBorder(BevelBorder.RAISED));
            player2.setLayout(new BoxLayout(player2, BoxLayout.Y_AXIS));
            player2.add(new JLabel("Player 2"));

            JPanel p1form = new JPanel(new SpringLayout());
            p1form.add(new JLabel("Agent:", JLabel.TRAILING));
            p1form.add(p1selector);
            p1form.add(new JLabel("Name:", JLabel.TRAILING));
            p1name = new JTextField("Player 1");
            p1form.add(p1name);
            SpringUtilities.makeCompactGrid(p1form, 2, 2, 6, 6, 6, 6);
            player1.add(p1form);

            JPanel p2form = new JPanel(new SpringLayout());
            p2form.add(new JLabel("Agent:", JLabel.TRAILING));
            p2form.add(p2selector);
            p2form.add(new JLabel("Name:", JLabel.TRAILING));
            p2name = new JTextField("Player 2");
            p2form.add(p2name);
            SpringUtilities.makeCompactGrid(p2form, 2, 2, 6, 6, 6, 6);
            player2.add(p2form);

            okayButton = new JButton("Start");
            okayButton.addActionListener(this);

            addWindowListener(this);

            playerFields.add(player1);
            playerFields.add(player2);
            selectorPanel.add(playerFields);
            selectorPanel.add(okayButton);

            setLayout(new BorderLayout());
            add(new JScrollPane(selectorPanel), BorderLayout.CENTER);
            setSize(new Dimension(700, 150));
            setVisible(true);
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }

        public void windowDeactivated(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowOpened(WindowEvent e) {
        }

        public void actionPerformed(ActionEvent ae) {
            try {
                player1 = Checkers.instantiatePlayer((String) p1selector.getSelectedItem(), p1name
                        .getText());
            }
            catch (NoSuchMethodException nsme1) {
                JOptionPane.showMessageDialog(this, "Make sure the agent class for player 1 ("
                        + p1selector.getSelectedItem()
                        + ")\nhas a constructor that accepts a single string as an argument!",
                        "Error Instantiating Agent", JOptionPane.ERROR_MESSAGE);
                return;
            }
            catch (Exception e1) {
                JOptionPane.showMessageDialog(this, e1.toString(), "Error Instantiating Agent",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                player2 = Checkers.instantiatePlayer((String) p2selector.getSelectedItem(), p2name
                        .getText());
            }
            catch (NoSuchMethodException nsme2) {
                JOptionPane.showMessageDialog(this, "Make sure the agent class for player 2 ("
                        + p2selector.getSelectedItem()
                        + ")\nhas a constructor that accepts a single string as an argument!",
                        "Error Instantiating Agent", JOptionPane.ERROR_MESSAGE);
                return;
            }
            catch (Exception e2) {
                JOptionPane.showMessageDialog(this, e2.toString(), "Error Instantiating Agent",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            setVisible(false);
        }
    }

    public CheckersPlayer[] getPlayers() {
        //OthelloPlayer players[] = new OthelloPlayer[2];
        GameSelectorDialog gsd = new GameSelectorDialog(this);
        return new CheckersPlayer[] { gsd.player1, gsd.player2 };
    }

    private class LogDocument extends DefaultStyledDocument {
        
        private static final long serialVersionUID = 1;
        
        public LogDocument() {
            super();
        }

        public void log(String message, Object source) {
            final MutableAttributeSet normal = new SimpleAttributeSet();
            StyleConstants.setForeground(normal, Color.BLACK);
            StyleConstants.setBold(normal, true);
            final MutableAttributeSet p1 = new SimpleAttributeSet();
            StyleConstants.setForeground(p1, Color.BLUE);
            StyleConstants.setBold(p1, false);
            final MutableAttributeSet p2 = new SimpleAttributeSet();
            StyleConstants.setForeground(p2, Color.GREEN);
            StyleConstants.setBold(p2, false);
            final MutableAttributeSet err = new SimpleAttributeSet();
            StyleConstants.setForeground(err, Color.RED);
            StyleConstants.setBold(err, true);
            AttributeSet attributes = normal;
            if (source == player1)
                attributes = p1;
            else if (source == player2)
                attributes = p2;
            else if (source instanceof Exception)
                attributes = err;
            try {
                insertString(logArea.getText().length(), message, attributes);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void handleStateUpdate(GameState2 newState) {
        state = newState;
        if (player1 == null || player2 == null)
            return; /*
                     * the players haven't yet been assigned (this shouldn't
                     * ever happen)
                     */
        boolean panelWasNull = false;
        JSplitPane horiz_split = null;
        if (panel == null) {
            panelWasNull = true;
            panel = new CheckersPanel(state, player1, player2);
            panel.setBorder(new BevelBorder(BevelBorder.RAISED));

            JPanel stats = new JPanel();
            stats.setLayout(new SpringLayout());
            Font arrowFont = new Font("Times", Font.BOLD, 24);
            arrow1 = new JLabel("\u2193", JLabel.CENTER);
            arrow1.setFont(arrowFont);
            arrow2 = new JLabel("\u2193", JLabel.CENTER);
            arrow2.setFont(arrowFont);
            stats.add(arrow1);
            stats.add(arrow2);
            arrow1.setVisible(false);
            arrow2.setVisible(false);
            name1 = new JLabel(player1.getName(), JLabel.CENTER);
            name1.setForeground(Color.BLUE);
            name2 = new JLabel(player2.getName(), JLabel.CENTER);
            name2.setForeground(Color.GREEN);
            JPanel s1 = new JPanel();
            score1 = new JLabel();
            score1.setForeground(Color.BLUE);
            s1.add(new JLabel("Score:"));
            s1.add(score1);
            JPanel s2 = new JPanel();
            score2 = new JLabel();
            score2.setForeground(Color.GREEN);
            s2.add(new JLabel("Score:"));
            s2.add(score2);
            stats.add(name1);
            stats.add(name2);
            stats.add(s1);
            stats.add(s2);
            JPanel time1 = new JPanel();
            time1.add(new JLabel("Time used:"));
            timeUsed1 = new JLabel("0s");
            time1.add(timeUsed1);
            JPanel time2 = new JPanel();
            time2.add(new JLabel("Time used:"));
            timeUsed2 = new JLabel("0s");
            time2.add(timeUsed2);
            stats.add(time1);
            stats.add(time2);
            JPanel rem1 = new JPanel();
            rem1.add(new JLabel("Remaining:"));
            timeRemaining1 = new JLabel("-");
            rem1.add(timeRemaining1);
            JPanel rem2 = new JPanel();
            rem2.add(new JLabel("Remaining:"));
            timeRemaining2 = new JLabel("-");
            rem2.add(timeRemaining2);
            stats.add(rem1);
            stats.add(rem2);
            SpringUtilities.makeCompactGrid(stats, 5, 2, 6, 6, 6, 6);

            horiz_split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, stats);
            horiz_split.setContinuousLayout(true);
            horiz_split.setResizeWeight(1.0);
            horiz_split.setDividerLocation(0.5);

            logDocument = new LogDocument();
            logArea = new JTextPane(logDocument);
            //logArea.setLineWrap(false);
            logArea.setEditable(false);
            logArea.setFont(new Font("Courier", Font.PLAIN, 12));
            JScrollPane textScroller = new JScrollPane(logArea);
            textScroller.setPreferredSize(new Dimension(getWidth(), 300));
            JSplitPane vert_split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, horiz_split,
                    textScroller);
            vert_split.setContinuousLayout(true);
            vert_split.setResizeWeight(1);
            vert_split.setDividerLocation(300);
            getContentPane().add(vert_split);

            pack();
            setVisible(true);

            while (!logQueue.isEmpty()) {
                MessageSourcePair msp = logQueue.removeFirst();
                log(msp.message, msp.source);
            }
        }
        else {
            panel.updateState(state);
        }
        if (state.getCurrentPlayer() == GameState2.Player.PLAYER1) {
            arrow1.setVisible(true);
            arrow2.setVisible(false);
        }
        else {
            arrow1.setVisible(false);
            arrow2.setVisible(true);
        }
        updateScores();
        if (panelWasNull)
            horiz_split.resetToPreferredSizes();
    }

    void updateScores() {
        score1.setText(Integer.toString(state.getScore(GameState2.Player.PLAYER1)));
        score2.setText(Integer.toString(state.getScore(GameState2.Player.PLAYER2)));
    }

    /**
     * Logs a message with a source equal to <code>this</code>.
     */
    public void log(String message) {
        log(message, null);
    }

    public void log(String message, Object source) {
        if (logDocument == null) {
            logQueue.add(new MessageSourcePair(message, source));
        }
        else {
            logDocument.log(message + "\n", source);
            Rectangle bounds = logArea.getAccessibleContext().getAccessibleComponent().getBounds();
            try {
                logArea.scrollRectToVisible(new Rectangle(0, (int) bounds.getHeight(), 1, 1));
            }
            catch (Exception e) {
            } /*
               * this will sometimes throw an array index out of bounds error
               * due to a race condition
               */
        }
    }

    public void updateTimeRemaining(CheckersPlayer player, int secondsRemaining) {
        String sr;
        //TODO check humanCheckersPlayer
//        if (player instanceof HumanCheckersPlayer)
//            sr = "\u221e"; /* <-- \221e == the infinity symbol */
        if (secondsRemaining >= 0)
            sr = Integer.toString(secondsRemaining) + "s";
        else
            sr = "-";
        if (player == player1) {
            timeRemaining1.setText(sr);
            timeRemaining2.setText("-");
        }
        else {
            timeRemaining1.setText("-");
            timeRemaining2.setText(sr);
        }
    }

    public void updateTimeUsed(CheckersPlayer player, long millisUsed) {
        String secondsUsed = Double.toString(millisUsed / 1000.0);
        if (player == player1)
            timeUsed1.setText(secondsUsed);
        else
            timeUsed2.setText(secondsUsed);
    }
}
