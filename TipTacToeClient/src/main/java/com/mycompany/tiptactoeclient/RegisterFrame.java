
package com.mycompany.tiptactoeclient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

public class RegisterFrame extends JFrame {

    // Fields for user information
    private JTextField firstNameField;
    private JTextField surnameField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterFrame() {

        // Set up the window
        setTitle("Tic Tac Toe - Register");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the main panel
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Create heading
        JLabel title = new JLabel(
            "Create an Account",
            SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 22));

        // Create the registration form
        JPanel formPanel = new JPanel(
            new GridLayout(5, 2, 10, 10)
        );

        // Create input fields
        firstNameField = new JTextField();
        surnameField = new JTextField();
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        // Create buttons
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        // Add labels and fields
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);

        formPanel.add(new JLabel("Surname:"));
        formPanel.add(surnameField);

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        // Add buttons
        formPanel.add(registerButton);
        formPanel.add(backButton);

        // Add components to main panel
        panel.add(title, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);

        add(panel);

        // Register button action
        registerButton.addActionListener(e -> {

            String firstName = firstNameField.getText().trim();
            String surname = surnameField.getText().trim();
            String username = usernameField.getText().trim();
            char[] password = passwordField.getPassword();

            // Check for empty fields
            if (firstName.isEmpty() ||
                surname.isEmpty() ||
                username.isEmpty() ||
                password.length == 0) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields."
                );

           
} else {

    try {
        // Connect to the web service
        WebServiceClient client = new WebServiceClient();

        // Register the new user
        String result = client.register(
            username,
            new String(password),
            firstName,
            surname
        );

        if ("ERROR-REPEAT".equals(result)) {

            JOptionPane.showMessageDialog(
                this,
                "Username already exists. Please choose another."
            );

        } else {

            try {
                int userId = Integer.parseInt(result);

                if (userId > 0) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Registration successful! Welcome " + username
                    );

                    // Main menu will open here later

                } else {
                    JOptionPane.showMessageDialog(
                        this,
                        "Registration failed."
                    );
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Registration failed: " + result
                );
            }
        }

    } catch (Exception ex) {

        JOptionPane.showMessageDialog(
            this,
            "Could not connect to the web service."
        );
    }
}

            Arrays.fill(password, '\0');
        });

        // Back button returns to Welcome Screen
        backButton.addActionListener(e -> {
            new WelcomeFrame().setVisible(true);
            dispose();
        });
    }
}
