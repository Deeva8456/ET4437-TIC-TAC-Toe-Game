package com.mycompany.tiptactoeclient;

import com.tttws.TicTacToeWS;
import com.tttws.TicTacToeWebService;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * GameWindow – shows the 3x3 Tic Tac Toe board and handles the full game.
 * Implements Runnable so the polling thread fits into the class cleanly.
 *
 * Person 2 opens this after newGame()  -> new GameWindow(gameId, userId, 1) //delete after
 * Person 2 opens this after joinGame() -> new GameWindow(gameId, userId, 2)
 */
public class GameWindow extends JFrame implements Runnable {

    // Web service connection – same pattern shown in the spec
    private static TicTacToeWebService service;
    private static TicTacToeWS proxy;

    // Basic game info set in the constructor
    private int gameId;
    private int userId;   // this player's id
    private int playerNum; // 1 = X (moves first), 2 = O

    // The 9 board buttons stored as [row][col]
    private JButton[][] buttons = new JButton[3][3];
    private JLabel statusLabel;

    // volatile because the polling thread and the EDT both read/write these
    private volatile boolean myTurn = false;
    private volatile boolean gameOver = false;
    private volatile int moveCount = 0; // total moves on the board so far

    private Thread pollThread; // the background thread that waits for the opponent

    // ------------------------------------------------------------------ //

    public GameWindow(int gameId, int userId, int playerNum) {
        this.gameId = gameId;
        this.userId = userId;
        this.playerNum = playerNum;

        // set up web service connection
        service = new TicTacToeWebService();
        proxy = service.getTicTacToeWSPort();

        // window title tells the player which symbol they are
        String symbol = (playerNum == 1) ? "X" : "O";
        setTitle("Tic Tac Toe  –  Game " + gameId + "  –  You are " + symbol);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        buildUI();

        // stop the polling thread when the player closes the window
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (pollThread != null) pollThread.interrupt();
                dispose();
            }
        });

        pack();
        setLocationRelativeTo(null); // centre on screen
        setVisible(true);

        // load any moves that are already on the server, then decide whose turn it is
        startGame();
    }

    // ------------------------------------------------------------------ //

    // Builds the status label and the 3x3 grid of buttons
    private void buildUI() {
        setLayout(new BorderLayout());

        // status bar at the top tells the player what is happening
        statusLabel = new JLabel("Loading...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // 3x3 grid with dark lines between squares
        JPanel grid = new JPanel(new GridLayout(3, 3, 4, 4));
        grid.setBackground(Color.DARK_GRAY);
        grid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("SansSerif", Font.BOLD, 48));
                btn.setPreferredSize(new Dimension(110, 110));
                btn.setBackground(Color.WHITE);
                btn.setFocusPainted(false);
                btn.setEnabled(false); // disabled until it is our turn

                // x = column, y = row (matches Appendix A coordinates)
                final int col = x, row = y;
                btn.addActionListener(e -> squareClicked(col, row));

                buttons[y][x] = btn;
                grid.add(btn);
            }
        }
        add(grid, BorderLayout.CENTER);
    }

    // Called once after the window opens – loads the board and decides whose turn it is
    private void startGame() {
        String board = proxy.getBoard(gameId);

        if (!board.startsWith("ERROR")) {
            // some moves already exist (e.g. P1 placed X before P2 joined)
            String[] rows = board.split("\n");
            drawBoard(rows);
            moveCount = rows.length;
        }

        // after an even number of moves it is P1's turn; after an odd number it is P2's
        myTurn = (moveCount % 2 == 0) ? (playerNum == 1) : (playerNum == 2);

        if (myTurn) {
            statusLabel.setText("Your turn!");
            enableEmptySquares(true);
        } else {
            statusLabel.setText("Waiting for opponent...");
            startPolling(); // kick off the background thread
        }

        // only P1 (the creator) starts the auto-delete countdown
        if (playerNum == 1) {
            startAutoDelete();
        }
    }

    // If nobody joins within 15 minutes, delete the game and close this window
    private void startAutoDelete() {
        Thread timer = new Thread(() -> {
            try {
                Thread.sleep(15 * 60 * 1000); // wait 15 minutes
            } catch (InterruptedException e) {
                return; // game started normally, timer was interrupted
            }

            // check if the game is still waiting (state -1 means no P2 yet)
            String state = proxy.getGameState(gameId);
            if ("-1".equals(state)) {
                proxy.deleteGame(gameId, userId); // remove it from the server
                SwingUtilities.invokeLater(() -> {
                    gameOver = true;
                    if (pollThread != null) pollThread.interrupt();
                    JOptionPane.showMessageDialog(GameWindow.this,
                        "No one joined after 15 minutes.\nThe game has been deleted.",
                        "Game Expired", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                });
            }
        });
        timer.setDaemon(true); // won't stop the app from closing
        timer.start();
    }

    // ------------------------------------------------------------------ //

    // Handles a click on square (x, y) – sends the move to the server
    private void squareClicked(int x, int y) {
        if (!myTurn || gameOver) return;

        // lock the board while we talk to the server
        myTurn = false;
        enableEmptySquares(false);
        statusLabel.setText("Sending move...");

        // try to claim this square
        String result = proxy.takeSquare(x, y, gameId, userId);

        if (!"1".equals(result)) {
            // server rejected the move – let the player try again
            myTurn = true;
            enableEmptySquares(true);
            String msg = "ERROR-TAKEN".equals(result) ? "That square is taken!" : "Move failed, try again.";
            statusLabel.setText(msg);
            return;
        }

        // move accepted – show our symbol on the board
        placeSymbol(x, y, playerNum);
        moveCount++;

        // check if this move won or drew the game
        String win = proxy.checkWin(gameId);
        if (isGameOver(win)) {
            proxy.setGameState(gameId, Integer.parseInt(win)); // save the final state
            showResult(win);
        } else {
            statusLabel.setText("Waiting for opponent...");
            startPolling(); // wait for the other player's move
        }
    }

    // ------------------------------------------------------------------ //

    // Draws all moves from getBoard onto the buttons
    // Each row from the server is "pId,x,y"
    private void drawBoard(String[] rows) {
        for (String row : rows) {
            String[] parts = row.trim().split(",");
            if (parts.length < 3) continue; // skip any bad data

            int pid = Integer.parseInt(parts[0].trim());
            int x   = Integer.parseInt(parts[1].trim()); // column
            int y   = Integer.parseInt(parts[2].trim()); // row

            // work out whether this move belongs to us or the opponent
            int owner = (pid == userId) ? playerNum : (playerNum == 1 ? 2 : 1);
            placeSymbol(x, y, owner);
        }
    }

    // Puts X or O on a button and locks it so it can't be clicked again
    private void placeSymbol(int x, int y, int player) {
        buttons[y][x].setText(player == 1 ? "X" : "O");
        buttons[y][x].setForeground(player == 1 ? Color.BLUE : Color.RED);
        buttons[y][x].setEnabled(false);
    }

    // Enables or disables only the empty squares
    private void enableEmptySquares(boolean enabled) {
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 3; x++)
                if (buttons[y][x].getText().isEmpty())
                    buttons[y][x].setEnabled(enabled);
    }

    // ------------------------------------------------------------------ //

    // Shows the final result message and locks the whole board
    private void showResult(String state) {
        gameOver = true;
        enableEmptySquares(false); // lock everything

        String msg;
        if ("3".equals(state)) {
            msg = "It's a draw!";
        } else if (Integer.parseInt(state) == playerNum) {
            msg = "You won! Well done!";
        } else {
            msg = "You lost. Better luck next time!";
        }

        statusLabel.setText("Game over – " + msg);
        JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    // Returns true if the win code means the game is finished
    private boolean isGameOver(String win) {
        return "1".equals(win) || "2".equals(win) || "3".equals(win);
    }

    // ------------------------------------------------------------------ //

    // Starts the background polling thread
    private void startPolling() {
        pollThread = new Thread(this);
        pollThread.setDaemon(true); // thread stops automatically when the app exits
        pollThread.start();
    }

    /**
     * Polling thread – checks the server every 2 seconds for the opponent's move.
     * Runs until a new move arrives, the game ends, or the window is closed.
     */
    @Override
    public void run() {
        while (!gameOver) {
            try {
                Thread.sleep(2000); // wait 2 seconds between each check
            } catch (InterruptedException e) {
                return; // window was closed, exit the thread
            }

            // see if the server has marked the game as finished already
            String state = proxy.getGameState(gameId);
            if (isGameOver(state)) {
                SwingUtilities.invokeLater(() -> showResult(state));
                return;
            }

            // check whether a new move has appeared on the board
            String board = proxy.getBoard(gameId);
            if (board.startsWith("ERROR")) continue; // server error, try again

            String[] rows = board.split("\n");
            if (rows.length <= moveCount) continue; // no new move yet

            // opponent has moved – check for a win or draw
            String win = proxy.checkWin(gameId);

            // update the UI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                drawBoard(rows);
                moveCount = rows.length;

                if (isGameOver(win)) {
                    showResult(win);
                } else {
                    // it is our turn now
                    myTurn = true;
                    enableEmptySquares(true);
                    statusLabel.setText("Your turn!");
                }
            });

            return; // stop polling; it restarts after the player's next move
        }
    }
}
