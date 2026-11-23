
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import java.awt.*;

public class WelcomeFrame extends JFrame {

    public WelcomeFrame() {

        // Set up the window
        setTitle("Tic Tac Toe");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        // Create title
        JLabel title = new JLabel(
            "Tic Tac Toe", SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 26));

        // Welcome message
        JLabel welcome = new JLabel(
            "Welcome to Tic Tac Toe!",
            SwingConstants.CENTER
        );

        // Create buttons
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

       // Open the Login window
        loginButton.addActionListener(e -> {
        new LoginFrame().setVisible(true);
        dispose();
    });

        // Temporary Register action
        registerButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(
                this, "Register button works!"
            );
        });

        // Add components
        panel.add(title);
        panel.add(welcome);
        panel.add(loginButton);
        panel.add(registerButton);

        // Show panel
        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeFrame().setVisible(true);
        });
    }
}
