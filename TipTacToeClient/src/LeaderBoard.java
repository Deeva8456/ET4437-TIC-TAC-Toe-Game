package com.mycompany.tiptactoeclient;

import com.tttws.TicTacToeWS;
import com.tttws.TicTacToeWebService;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;

/**
 * LeaderBoard – shows wins, losses and draws for every player,
 * sorted by wins so the best player is at the top.
 *
 * Open it from the main menu like this: //delete after
 *   new LeaderBoard();
 */
public class LeaderBoard extends JFrame {

    // web service connection
    private static TicTacToeWebService service;
    private static TicTacToeWS proxy;

    // ------------------------------------------------------------------ //

    public LeaderBoard() {
        service = new TicTacToeWebService();
        proxy = service.getTicTacToeWSPort();

        setTitle("Leader Board");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        buildUI();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Fetches all game data and builds the table
    private void buildUI() {
        setLayout(new BorderLayout(10, 10));

        // title at the top
        JLabel title = new JLabel("Leader Board", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(14, 14, 4, 14));
        add(title, BorderLayout.NORTH);

        // build the stats map from leagueTable data
        // key = username, value = int[]{wins, losses, draws}
        Map<String, int[]> stats = new LinkedHashMap<>();

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

                // make sure both players have a row in the map
                stats.putIfAbsent(p1, new int[]{0, 0, 0});
                stats.putIfAbsent(p2, new int[]{0, 0, 0});

                if (state == 3) {
                    // draw – both players get a draw
                    stats.get(p1)[2]++;
                    stats.get(p2)[2]++;
                } else if (state == 1) {
                    // P1 won
                    stats.get(p1)[0]++;
                    stats.get(p2)[1]++;
                } else {
                    // P2 won
                    stats.get(p2)[0]++;
                    stats.get(p1)[1]++;
                }
            }
        }

        // sort players by wins (highest first)
        List<Map.Entry<String, int[]>> sorted = new ArrayList<>(stats.entrySet());
        sorted.sort((a, b) -> b.getValue()[0] - a.getValue()[0]);

        // fill the table model
        String[] columns = {"Player", "Wins", "Losses", "Draws"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // read-only table
            }
        };

        for (Map.Entry<String, int[]> entry : sorted) {
            int[] s = entry.getValue();
            model.addRow(new Object[]{entry.getKey(), s[0], s[1], s[2]});
        }

        // show a message if there are no finished games yet
        if (sorted.isEmpty()) {
            model.addRow(new Object[]{"No finished games yet", "-", "-", "-"});
        }

        // create and style the JTable
        JTable table = new JTable(model);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // right-align the number columns
        DefaultTableCellRenderer centre = new DefaultTableCellRenderer();
        centre.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 1; i <= 3; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centre);
            table.getColumnModel().getColumn(i).setPreferredWidth(70);
        }
        table.getColumnModel().getColumn(0).setPreferredWidth(150); // player name column

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        scroll.setPreferredSize(new Dimension(380, 220));
        add(scroll, BorderLayout.CENTER);
    }
}
