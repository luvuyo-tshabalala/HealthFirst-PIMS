package ui;

import model.User;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class AdminDashboard extends JFrame {

    private final User currentUser;
    private JTabbedPane tabbedPane;

    // Modular tab panels to be populated in Commits 5, 6, 7, and 10
    private JPanel medicinesPanel;
    private JPanel suppliersPanel;
    private JPanel usersPanel;
    private JPanel reportsPanel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("HealthFirst Pharmacy - Administrator Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);

        // Top Navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 115, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // System & Screen Title
        JLabel lblTitle = new JLabel("HealthFirst Pharmacy | Admin Dashboard");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        // User Info & Logout Button
        JPanel userActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userActionPanel.setOpaque(false);

        String displayName = (currentUser != null) ? currentUser.getFullName() : "Administrator";
        JLabel lblUser = new JLabel("Logged in as: " + displayName);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69)); // Crimson red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> handleLogout());

        userActionPanel.add(lblUser);
        userActionPanel.add(btnLogout);
        headerPanel.add(userActionPanel, BorderLayout.EAST);

        // Tabbed Pane Construction
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        medicinesPanel = new MedicinePanel();
        suppliersPanel = new SupplierPanel();
        usersPanel = new UserPanel();
        reportsPanel = new ReportPanel();

        // The 4 mandatory tabs
        tabbedPane.addTab("Manage Medicines", medicinesPanel);
        tabbedPane.addTab("Manage Suppliers", suppliersPanel);
        tabbedPane.addTab("Manage Users", usersPanel);
        tabbedPane.addTab("Reports", reportsPanel);

        // Assemble Layout
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPlaceholderPanel(String moduleName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        
        JLabel lblPlaceholder = new JLabel(moduleName, SwingConstants.CENTER);
        lblPlaceholder.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        lblPlaceholder.setForeground(new Color(120, 120, 120));
        
        panel.add(lblPlaceholder, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out of the Administrator Portal?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            // Re-open login screen
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // Getters allowing subsequent commits to access tab panels directly
    public JPanel getMedicinesPanel() { return medicinesPanel; }
    public JPanel getSuppliersPanel() { return suppliersPanel; }
    public JPanel getUsersPanel() { return usersPanel; }
    public JPanel getReportsPanel() { return reportsPanel; }
    public JTabbedPane getTabbedPane() { return tabbedPane; }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Test run with fallback admin user
        SwingUtilities.invokeLater(() -> {
            User testAdmin = new User(1, "admin", "admin123", "Admin", "System Administrator");
            new AdminDashboard(testAdmin).setVisible(true);
        });
    }
}