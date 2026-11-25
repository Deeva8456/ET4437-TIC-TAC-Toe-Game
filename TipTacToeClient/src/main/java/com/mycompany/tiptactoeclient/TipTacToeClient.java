
package com.mycompany.tiptactoeclient;

import javax.swing.SwingUtilities;

public class TipTacToeClient {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new WelcomeFrame().setVisible(true);
        });
    }
}
