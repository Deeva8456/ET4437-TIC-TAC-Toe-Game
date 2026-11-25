package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ScoreFrame extends JFrame {

    private JLabel winsLabel;
    private JLabel lossesLabel;
    private JLabel drawsLabel;

    public ScoreFrame(String username) {

        setTitle("Tic Tac Toe - My Scores");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel(
            "My Scoreboard",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel playerLabel = new JLabel(
            "Player: " + username,
            SwingConstants.CENTER
        );

        winsLabel = new JLabel("Wins: 0");
        lossesLabel = new JLabel("Losses: 0");
        drawsLabel = new JLabel("Draws: 0");

        JPanel statsPanel = new JPanel(
            new GridLayout(4, 1, 10, 10)
        );

        statsPanel.add(playerLabel);
        statsPanel.add(winsLabel);
        statsPanel.add(lossesLabel);
        statsPanel.add(drawsLabel);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        panel.add(title, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        add(panel);

        loadScores(username);
    }

    private void loadScores(String username) {

        try {
            WebServiceClient client = new WebServiceClient();
            String result = client.leagueTable();

            int wins = 0;
            int losses = 0;
            int draws = 0;

            if (result == null || result.startsWith("ERROR")) {
                if ("ERROR-NOGAMES".equals(result)) {
                    setScores(0, 0, 0);
                    return;
                }

                winsLabel.setText("Wins: unavailable");
                lossesLabel.setText("Losses: unavailable");
                drawsLabel.setText("Draws: unavailable");
                return;
            }

            String[] rows = result.split("\n");

            for (String row : rows) {

                String[] fields = row.split(",");

                if (fields.length < 5) {
                    continue;
                }

                String player1 = fields[1].trim();
                String player2 = fields[2].trim();
                int gameState = Integer.parseInt(fields[3].trim());

                if (gameState == 1) {

                    if (player1.equalsIgnoreCase(username)) {
                        wins++;
                    }

                    if (player2.equalsIgnoreCase(username)) {
                        losses++;
                    }

                } else if (gameState == 2) {

                    if (player2.equalsIgnoreCase(username)) {
                        wins++;
                    }

                    if (player1.equalsIgnoreCase(username)) {
                        losses++;
                    }

                } else if (gameState == 3) {

                    if (player1.equalsIgnoreCase(username)
                            || player2.equalsIgnoreCase(username)) {
                        draws++;
                    }
                }
            }

            setScores(wins, losses, draws);

        } catch (Exception e) {

            winsLabel.setText("Wins: unavailable");
            lossesLabel.setText("Losses: unavailable");
            drawsLabel.setText("Draws: unavailable");
        }
    }

    public void setScores(int wins, int losses, int draws) {

        winsLabel.setText("Wins: " + wins);
        lossesLabel.setText("Losses: " + losses);
        drawsLabel.setText("Draws: " + draws);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ScoreFrame frame = new ScoreFrame("TestUser");
            frame.setVisible(true);
        });
    }
}
