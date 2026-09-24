package ui;

import dao.UserDAO;
import model.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class LoginFrame extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private final UserDAO userDAO;

    public LoginFrame() {
        this.userDAO = new UserDAO();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("HealthFirst Pharmacy - System Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 260);
        setResizable(false);
        setLocationRelativeTo(null);

        // Header Panel
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(33, 115, 70));
        JLabel lblTitle = new JLabel("HealthFirst Pharmacy Login");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        panelHeader.add(lblTitle);

        // Form Panel
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new java.awt.Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelForm.add(lblUsername, gbc);

        txtUsername = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panelForm.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelForm.add(lblPassword, gbc);

        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panelForm.add(txtPassword, gbc);

        // Button Panel
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnLogin = new JButton("Login");
        btnCancel = new JButton("Exit");

        btnLogin.setBackground(new Color(33, 115, 70));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);

        panelButtons.add(btnLogin);
        panelButtons.add(btnCancel);

        // Assemble Layout
        setLayout(new BorderLayout());
        add(panelHeader, BorderLayout.NORTH);
        add(panelForm, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);

        // Event Listeners
        btnLogin.addActionListener(this::handleLogin);
        btnCancel.addActionListener(e -> System.exit(0));
        
        // Enter key shortcut
        getRootPane().setDefaultButton(btnLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Login successful. Welcome, " + user.getFullName() + "!", 
                "Access Granted", 
                JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose(); // Close login window

            // Role-based redirection
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                new AdminDashboard(user).setVisible(true);
            } else if ("Cashier".equalsIgnoreCase(user.getRole())) {
                new CashierDashboard(user).setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password. Please try again.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    public static void main(String[] args) {
        // Set native Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}