package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderboardFrame extends JFrame {

    private JTable leaderboardTable;
    private DefaultTableModel tableModel;

    public LeaderboardFrame() {

        setTitle("Tic Tac Toe - Leaderboard");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel(
            "Player Leaderboard",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 24));

        String[] columns = {
            "Username",
            "Wins",
            "Losses",
            "Draws"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setRowHeight(30);
        leaderboardTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
            new JScrollPane(leaderboardTable);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeButton, BorderLayout.SOUTH);

        add(panel);

        loadLeaderboard();
    }

    private void loadLeaderboard() {

        try {

            WebServiceClient client = new WebServiceClient();
            String result = client.leagueTable();

            clearLeaderboard();

            if (result == null || result.startsWith("ERROR")) {
                return;
            }

            Map<String, int[]> playerStats = new LinkedHashMap<>();

            String[] rows = result.split("\n");

            for (String row : rows) {

                String[] fields = row.split(",");

                if (fields.length < 5) {
                    continue;
                }

                String player1 = fields[1].trim();
                String player2 = fields[2].trim();
                int gameState = Integer.parseInt(fields[3].trim());

                // Ignore games that are not finished
                if (gameState != 1 && gameState != 2
                        && gameState != 3) {
                    continue;
                }

                playerStats.putIfAbsent(player1, new int[3]);
                playerStats.putIfAbsent(player2, new int[3]);

                int[] stats1 = playerStats.get(player1);
                int[] stats2 = playerStats.get(player2);

                if (gameState == 1) {
                    stats1[0]++;
                    stats2[1]++;
                }

                else if (gameState == 2) {
                    stats2[0]++;
                    stats1[1]++;
                }

                else if (gameState == 3) {
                    stats1[2]++;
                    stats2[2]++;
                }
            }

            for (Map.Entry<String, int[]> entry
                    : playerStats.entrySet()) {

                String username = entry.getKey();
                int[] stats = entry.getValue();

                addPlayerStats(
                    username,
                    stats[0],
                    stats[1],
                    stats[2]
                );
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Could not load the leaderboard.",
                "Connection Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void addPlayerStats(
            String username,
            int wins,
            int losses,
            int draws) {

        tableModel.addRow(new Object[]{
            username, wins, losses, draws
        });
    }

    public void clearLeaderboard() {
        tableModel.setRowCount(0);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            LeaderboardFrame frame =
                new LeaderboardFrame();

            frame.setVisible(true);
        });
    }
}
