package ui;

import dao.SupplierDAO;
import model.Supplier;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SupplierPanel extends JPanel {

    private final SupplierDAO supplierDAO;
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtContactPerson;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextArea txtAddress;
    private JTable tblSuppliers;
    private DefaultTableModel tableModel;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public SupplierPanel() {
        this.supplierDAO = new SupplierDAO();
        initComponents();
        loadSuppliersTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Left Form Panel ---
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBorder(BorderFactory.createTitledBorder("Supplier Details"));
        formContainer.setPreferredSize(new Dimension(360, 0));

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtName = new JTextField();
        txtContactPerson = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextArea(4, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane scrollAddress = new JScrollPane(txtAddress);

        // Field layouts
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        formFields.add(new JLabel("Supplier ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formFields.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formFields.add(new JLabel("Company Name:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formFields.add(new JLabel("Contact Person:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtContactPerson, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formFields.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtPhone, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formFields.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST;
        formFields.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        formFields.add(scrollAddress, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnAdd = new JButton("Add Supplier");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnClear = new JButton("Clear Form");

        btnAdd.setBackground(new Color(33, 115, 70));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        formContainer.add(formFields, BorderLayout.CENTER);
        formContainer.add(buttonPanel, BorderLayout.SOUTH);

        // --- Right Table Panel ---
        String[] columns = {"ID", "Company Name", "Contact Person", "Phone", "Email", "Address"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells directly in the table
            }
        };

        tblSuppliers = new JTable(tableModel);
        tblSuppliers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblSuppliers.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(tblSuppliers);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Registered Suppliers"));

        // Assemble Panels
        add(formContainer, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // --- Event Listeners ---
        btnAdd.addActionListener(e -> handleAddSupplier());
        btnUpdate.addActionListener(e -> handleUpdateSupplier());
        btnDelete.addActionListener(e -> handleDeleteSupplier());
        btnClear.addActionListener(e -> clearForm());

        tblSuppliers.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblSuppliers.getSelectedRow();
                if (selectedRow >= 0) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtContactPerson.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtPhone.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtEmail.setText(tableModel.getValueAt(selectedRow, 4).toString());
                    txtAddress.setText(tableModel.getValueAt(selectedRow, 5).toString());

                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }
        });
    }

    public void loadSuppliersTable() {
        tableModel.setRowCount(0);
        List<Supplier> list = supplierDAO.getAllSuppliers();
        for (Supplier s : list) {
            tableModel.addRow(new Object[]{
                s.getSupplierId(),
                s.getName(),
                s.getContactPerson(),
                s.getPhone(),
                s.getEmail(),
                s.getAddress()
            });
        }
    }

    private void handleAddSupplier() {
        if (!validateInputs()) return;

        Supplier supplier = new Supplier(
            txtName.getText().trim(),
            txtContactPerson.getText().trim(),
            txtPhone.getText().trim(),
            txtEmail.getText().trim(),
            txtAddress.getText().trim()
        );

        if (supplierDAO.addSupplier(supplier)) {
            JOptionPane.showMessageDialog(this, "Supplier added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSuppliersTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add supplier.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateSupplier() {
        if (txtId.getText().isEmpty() || !validateInputs()) return;

        Supplier supplier = new Supplier(
            Integer.parseInt(txtId.getText()),
            txtName.getText().trim(),
            txtContactPerson.getText().trim(),
            txtPhone.getText().trim(),
            txtEmail.getText().trim(),
            txtAddress.getText().trim()
        );

        if (supplierDAO.updateSupplier(supplier)) {
            JOptionPane.showMessageDialog(this, "Supplier updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadSuppliersTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update supplier.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteSupplier() {
        if (txtId.getText().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this supplier?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int supplierId = Integer.parseInt(txtId.getText());
            try {
                if (supplierDAO.deleteSupplier(supplierId)) {
                    JOptionPane.showMessageDialog(this, "Supplier deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSuppliersTable();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Supplier could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                // Catches Foreign Key constraint violations if medicines reference this supplier
                JOptionPane.showMessageDialog(this,
                    "Cannot delete this supplier because medicines are currently linked to it.\nDelete or reassign those medicines first.",
                    "Referential Integrity Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInputs() {
        if (txtName.getText().trim().isEmpty() ||
            txtContactPerson.getText().trim().isEmpty() ||
            txtPhone.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtContactPerson.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");
        tblSuppliers.clearSelection();

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}