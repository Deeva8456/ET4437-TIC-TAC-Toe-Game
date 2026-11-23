package com.mycompany.tiptactoeclient;

import com.tttws.TicTacToeWS;
import com.tttws.TicTacToeWebService;
import java.awt.*;
import javax.swing.*;

/**
 * ScoreBoard – shows this player's total wins, losses and draws.
 *
 * Open it from the main menu like this:
 *   new ScoreBoard(userId, username);
 *
 * username is needed because leagueTable() returns usernames, not user IDs.
 * Person 1 has the username after login/register.  //change/delete
 */
public class ScoreBoard extends JFrame {

    // web service connection
    private static TicTacToeWebService service;
    private static TicTacToeWS proxy;

    // ------------------------------------------------------------------ //

    public ScoreBoard(int userId, String username) {
        service = new TicTacToeWebService();
        proxy = service.getTicTacToeWSPort();

        setTitle("Score Board – " + username);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // only closes this window
        setResizable(false);

        buildUI(username);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Fetches the stats and builds the display
    private void buildUI(String username) {
        // count wins, losses and draws by scanning leagueTable
        int wins = 0, losses = 0, draws = 0;

        String data = proxy.leagueTable();

        if (!data.startsWith("ERROR")) {
            // each row: gameId, p1username, p2username, gamestate, timestamp
            for (String row : data.split("\n")) {
                String[] parts = row.trim().split(",");
                if (parts.length < 4) continue;

                String p1 = parts[1].trim();
                String p2 = parts[2].trim();
                int state;

                try {
                    state = Integer.parseInt(parts[3].trim());
                } catch (NumberFormatException e) {
                    continue; // skip rows with bad data
                }

                // only count finished games (state 1, 2 or 3)
                if (state < 1 || state > 3) continue;

                boolean isP1 = username.equals(p1);
                boolean isP2 = username.equals(p2);
                if (!isP1 && !isP2) continue; // this game doesn't involve us

                if (state == 3) {
                    draws++;
                } else if ((state == 1 && isP1) || (state == 2 && isP2)) {
                    wins++;
                } else {
                    losses++;
                }
            }
        }

        // --- layout ---
        setLayout(new BorderLayout(10, 10));

        // title at the top
        JLabel title = new JLabel("Your Score", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(14, 14, 4, 14));
        add(title, BorderLayout.NORTH);

        // three big coloured boxes for W / L / D
        JPanel boxes = new JPanel(new GridLayout(1, 3, 10, 0));
        boxes.setBorder(BorderFactory.createEmptyBorder(4, 14, 14, 14));
        boxes.add(makeBox("Wins",   wins,   new Color(60, 179, 113))); // green
        boxes.add(makeBox("Losses", losses, new Color(220, 80,  60))); // red
        boxes.add(makeBox("Draws",  draws,  new Color(100, 149, 237))); // blue
        add(boxes, BorderLayout.CENTER);
    }

    // Creates one coloured stat box with a label and a big number
    private JPanel makeBox(String label, int value, Color colour) {
        JPanel box = new JPanel(new BorderLayout(4, 4));
        box.setBackground(colour);
        box.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel numLabel = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
        numLabel.setForeground(Color.WHITE);

        JLabel txtLabel = new JLabel(label, SwingConstants.CENTER);
        txtLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtLabel.setForeground(Color.WHITE);

        box.add(numLabel, BorderLayout.CENTER);
        box.add(txtLabel, BorderLayout.SOUTH);
        return box;
    }
}
