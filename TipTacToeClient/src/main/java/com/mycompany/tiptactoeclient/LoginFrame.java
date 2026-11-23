
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    // Text fields for the user's login details
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {

        // Set up the login window
        setTitle("Tic Tac Toe - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Create heading
        JLabel title = new JLabel(
            "Login to Tic Tac Toe",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Create form panel
        JPanel formPanel = new JPanel(
            new GridLayout(3, 2, 10, 10)
        );

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Add components to form
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);

        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        formPanel.add(loginButton);
        formPanel.add(backButton);

        // Add everything to main panel
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);

        // Login button action
        loginButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            // Check if any fields are empty
            if (username.isEmpty() || password.length == 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter your username and password."
                );

            } else {

                // Temporary message until SOAP is connected
                JOptionPane.showMessageDialog(
                    this,
                    "Login screen works! SOAP connection is next."
                );
            }

            java.util.Arrays.fill(password, '\0');
        });

        // Back button returns to Welcome Screen
        backButton.addActionListener(e -> {
            new WelcomeFrame().setVisible(true);
            dispose();
        });
    }
}
