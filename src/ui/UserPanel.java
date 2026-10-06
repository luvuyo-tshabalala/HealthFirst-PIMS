package ui;

import dao.UserDAO;
import model.User;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserPanel extends JPanel {

    private final UserDAO userDAO;
    private DefaultTableModel tableModel;
    private JTable usersTable;

    private JTextField txtUserId;
    private JTextField txtFullName;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public UserPanel() {
        this.userDAO = new UserDAO();
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Left Form Panel ---
        JPanel formContainer = new JPanel(new BorderLayout(5, 5));
        formContainer.setPreferredSize(new Dimension(360, 0));
        formContainer.setBorder(BorderFactory.createTitledBorder("User Credentials & Role"));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // User ID (Read-only)
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        inputPanel.add(new JLabel("User ID:"), gbc);
        txtUserId = new JTextField();
        txtUserId.setEditable(false);
        txtUserId.setBackground(new Color(235, 235, 235));
        gbc.gridx = 1; gbc.weightx = 0.65;
        inputPanel.add(txtUserId, gbc);

        // Full Name
        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Full Name:*"), gbc);
        txtFullName = new JTextField();
        gbc.gridx = 1;
        inputPanel.add(txtFullName, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Username:*"), gbc);
        txtUsername = new JTextField();
        gbc.gridx = 1;
        inputPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Password:*"), gbc);
        txtPassword = new JPasswordField();
        gbc.gridx = 1;
        inputPanel.add(txtPassword, gbc);

        // Role Dropdown
        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Role:*"), gbc);
        cmbRole = new JComboBox<>(new String[]{"Cashier", "Admin"});
        gbc.gridx = 1;
        inputPanel.add(cmbRole, gbc);

        // --- Action Buttons ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        btnAdd = new JButton("Add User");
        btnAdd.setBackground(new Color(33, 115, 70));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);

        btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(0, 122, 204));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);

        btnDelete = new JButton("Delete");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);

        btnClear = new JButton("Clear Form");
        btnClear.setFocusPainted(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formContainer.add(inputPanel, BorderLayout.CENTER);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        // --- Right Data Table Panel ---
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createTitledBorder("System Accounts"));

        String[] columnNames = {"User ID", "Full Name", "Username", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setRowHeight(24);
        usersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollTable = new JScrollPane(usersTable);
        tableContainer.add(scrollTable, BorderLayout.CENTER);

        // Layout Assembly
        add(formContainer, BorderLayout.WEST);
        add(tableContainer, BorderLayout.CENTER);

        // --- Event Listeners ---
        btnAdd.addActionListener(e -> handleAdd());
        btnUpdate.addActionListener(e -> handleUpdate());
        btnDelete.addActionListener(e -> handleDelete());
        btnClear.addActionListener(e -> clearForm());

        // Row Selection Binding
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersTable.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });
    }

    public void loadUserData() {
        tableModel.setRowCount(0);
        List<User> users = userDAO.getAllUsers();
        for (User u : users) {
            tableModel.addRow(new Object[]{
                u.getUserId(),
                u.getFullName(),
                u.getUsername(),
                u.getRole()
            });
        }
    }

    private void populateFormFromSelectedRow() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow >= 0) {
            int userId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
            txtUserId.setText(String.valueOf(userId));
            txtFullName.setText(tableModel.getValueAt(selectedRow, 1).toString());
            txtUsername.setText(tableModel.getValueAt(selectedRow, 2).toString());
            cmbRole.setSelectedItem(tableModel.getValueAt(selectedRow, 3).toString());
            
            // Password is left blank on selection for security; entering a new value updates it
            txtPassword.setText("");
        }
    }

    private void handleAdd() {
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.isUsernameTaken(username, 0)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Duplicate Username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(username, password, role, fullName);
        if (userDAO.addUser(newUser)) {
            JOptionPane.showMessageDialog(this, "User account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create user account.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an account from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(txtUserId.getText().trim());
        String fullName = txtFullName.getText().trim();
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cmbRole.getSelectedItem();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields (enter existing or new password).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (userDAO.isUsernameTaken(username, userId)) {
            JOptionPane.showMessageDialog(this, "Username already exists for another account.", "Duplicate Username", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User updatedUser = new User(userId, username, password, role, fullName);
        if (userDAO.updateUser(updatedUser)) {
            JOptionPane.showMessageDialog(this, "User details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadUserData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user account.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select an account from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = Integer.parseInt(txtUserId.getText().trim());
        String username = txtUsername.getText().trim();

        // Prevent accidental lockout by disallowing deletion of the primary admin (ID 1)
        if (userId == 1 || "admin".equalsIgnoreCase(username)) {
            JOptionPane.showMessageDialog(this, "The primary administrator account cannot be deleted.", "Action Forbidden", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete account: " + username + "?",
            "Confirm Account Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUser(userId)) {
                    JOptionPane.showMessageDialog(this, "User account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUserData();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "User could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Catches FK restrict if user processed sales transactions
                JOptionPane.showMessageDialog(
                    this,
                    "Cannot delete this user because they have recorded sales transactions in the system.",
                    "Integrity Constraint",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void clearForm() {
        txtUserId.setText("");
        txtFullName.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        usersTable.clearSelection();
    }
}