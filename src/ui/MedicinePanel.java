package ui;

import dao.MedicineDAO;
import dao.SupplierDAO;
import model.Medicine;
import model.Supplier;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class MedicinePanel extends JPanel {

    private final MedicineDAO medicineDAO;
    private final SupplierDAO supplierDAO;

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCompany;
    private JComboBox<String> cmbType;
    private JTextField txtPrice;
    private JTextField txtQuantity;
    private JTextField txtReorderLevel;
    private JTextField txtExpiryDate; // Format: yyyy-MM-dd
    private JComboBox<Supplier> cmbSupplier;

    private JTable tblMedicines;
    private DefaultTableModel tableModel;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;

    public MedicinePanel() {
        this.medicineDAO = new MedicineDAO();
        this.supplierDAO = new SupplierDAO();
        initComponents();
        loadSuppliersDropdown();
        loadMedicinesTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Left Form Panel ---
        JPanel formContainer = new JPanel(new BorderLayout(10, 10));
        formContainer.setBorder(BorderFactory.createTitledBorder("Medicine Details"));
        formContainer.setPreferredSize(new Dimension(380, 0));

        JPanel formFields = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtName = new JTextField();
        txtCompany = new JTextField();

        String[] types = {"Tablet", "Capsule", "Syrup", "Injection", "Cream", "Drops", "Inhaler"};
        cmbType = new JComboBox<>(types);

        txtPrice = new JTextField();
        txtQuantity = new JTextField();
        txtReorderLevel = new JTextField();
        txtExpiryDate = new JTextField();
        txtExpiryDate.setToolTipText("Format: YYYY-MM-DD");

        cmbSupplier = new JComboBox<>();

        // Add fields to GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formFields.add(new JLabel("Medicine ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formFields.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formFields.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formFields.add(new JLabel("Company:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtCompany, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formFields.add(new JLabel("Type:*"), gbc);
        gbc.gridx = 1;
        formFields.add(cmbType, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formFields.add(new JLabel("Price (ZAR):*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtPrice, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formFields.add(new JLabel("Stock Quantity:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtQuantity, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formFields.add(new JLabel("Reorder Level:*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtReorderLevel, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        formFields.add(new JLabel("Expiry (YYYY-MM-DD):*"), gbc);
        gbc.gridx = 1;
        formFields.add(txtExpiryDate, gbc);

        gbc.gridx = 0; gbc.gridy = 8;
        formFields.add(new JLabel("Supplier:*"), gbc);
        gbc.gridx = 1;
        formFields.add(cmbSupplier, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnAdd = new JButton("Add Medicine");
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
        String[] columns = {"ID", "Name", "Company", "Type", "Price", "Stock", "Reorder", "Expiry", "Supplier ID"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblMedicines = new JTable(tableModel);
        tblMedicines.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblMedicines.setRowHeight(24);
        JScrollPane tableScroll = new JScrollPane(tblMedicines);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Medicine Inventory"));

        // Assemble Panels
        add(formContainer, BorderLayout.WEST);
        add(tableScroll, BorderLayout.CENTER);

        // --- Event Listeners ---
        btnAdd.addActionListener(e -> handleAddMedicine());
        btnUpdate.addActionListener(e -> handleUpdateMedicine());
        btnDelete.addActionListener(e -> handleDeleteMedicine());
        btnClear.addActionListener(e -> clearForm());

tblMedicines.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int viewRow = tblMedicines.getSelectedRow();
                if (viewRow >= 0) {
                    // Convert view row to model row to safeguard against table sorting/filtering
                    int row = tblMedicines.convertRowIndexToModel(viewRow);

                    txtId.setText(getCellValue(row, 0));
                    txtName.setText(getCellValue(row, 1));
                    txtCompany.setText(getCellValue(row, 2));
                    
                    String typeVal = getCellValue(row, 3);
                    if (!typeVal.isEmpty()) {
                        cmbType.setSelectedItem(typeVal);
                    }
                    
                    txtPrice.setText(getCellValue(row, 4));
                    txtQuantity.setText(getCellValue(row, 5));
                    txtReorderLevel.setText(getCellValue(row, 6));
                    txtExpiryDate.setText(getCellValue(row, 7));

                    String supplierIdStr = getCellValue(row, 8);
                    if (!supplierIdStr.isEmpty()) {
                        try {
                            int supplierId = Integer.parseInt(supplierIdStr);
                            selectSupplierInDropdown(supplierId);
                        } catch (NumberFormatException ex) {
                            System.err.println("Invalid supplier ID in row: " + supplierIdStr);
                        }
                    }

                    btnAdd.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                }
            }

            /*
         Safely retrieves table model values without throwing NullPointerException.
     */
            private String getCellValue(int row, int col) {
                Object val = tableModel.getValueAt(row, col);
                return (val != null) ? val.toString().trim() : "";
            }
        });
    }

    public void loadSuppliersDropdown() {
        cmbSupplier.removeAllItems();
        List<Supplier> suppliers = supplierDAO.getAllSuppliers();
        for (Supplier s : suppliers) {
            cmbSupplier.addItem(s);
        }
    }

    public void loadMedicinesTable() {
        tableModel.setRowCount(0);
        List<Medicine> list = medicineDAO.getAllMedicines();
        System.out.println("Medicines fetched from database: " + list.size());
        
        for (Medicine m : list) {
            tableModel.addRow(new Object[]{
                m.getMedicineId(),
                (m.getName() != null ? m.getName() : ""),
                (m.getCompany() != null ? m.getCompany() : ""),
                (m.getMedicineType() != null ? m.getMedicineType() : ""),
                String.format(java.util.Locale.US, "%.2f", m.getPrice()),
                m.getQuantityInStock(),
                m.getReorderLevel(),
                (m.getExpiryDate() != null ? m.getExpiryDate().toString() : ""),
                m.getSupplierId()
            });
        }
    }

    private void selectSupplierInDropdown(int supplierId) {
        for (int i = 0; i < cmbSupplier.getItemCount(); i++) {
            Supplier s = cmbSupplier.getItemAt(i);
            if (s.getSupplierId() == supplierId) {
                cmbSupplier.setSelectedIndex(i);
                break;
            }
        }
    }

    private void handleAddMedicine() {
        Medicine med = parseFormFields(0);
        if (med == null) return;

        if (medicineDAO.addMedicine(med)) {
            JOptionPane.showMessageDialog(this, "Medicine added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMedicinesTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save medicine.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdateMedicine() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        Medicine med = parseFormFields(id);
        if (med == null) return;

        if (medicineDAO.updateMedicine(med)) {
            JOptionPane.showMessageDialog(this, "Medicine updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMedicinesTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update medicine.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteMedicine() {
        if (txtId.getText().isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this medicine record?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int medId = Integer.parseInt(txtId.getText());
            try {
                if (medicineDAO.deleteMedicine(medId)) {
                    JOptionPane.showMessageDialog(this, "Medicine deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadMedicinesTable();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this, "Medicine not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Cannot delete this medicine because it is referenced in past sales records.",
                    "Referential Integrity Alert",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Medicine parseFormFields(int id) {
        String name = txtName.getText().trim();
        String company = txtCompany.getText().trim();
        String type = (String) cmbType.getSelectedItem();
        String priceStr = txtPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        String reorderStr = txtReorderLevel.getText().trim();
        String expiryStr = txtExpiryDate.getText().trim();
        Supplier selectedSupplier = (Supplier) cmbSupplier.getSelectedItem();

        if (name.isEmpty() || company.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty() ||
            reorderStr.isEmpty() || expiryStr.isEmpty() || selectedSupplier == null) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int qty = Integer.parseInt(qtyStr);
            int reorder = Integer.parseInt(reorderStr);
            Date expiryDate = Date.valueOf(LocalDate.parse(expiryStr));

            if (price < 0 || qty < 0 || reorder < 0) {
                JOptionPane.showMessageDialog(this, "Price, Quantity, and Reorder level cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            return new Medicine(id, name, company, type, price, qty, reorder, expiryDate, selectedSupplier.getSupplierId());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a valid number, and Quantity / Reorder level must be integers.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Expiry date must be in standard format: YYYY-MM-DD (e.g., 2027-12-31).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCompany.setText("");
        cmbType.setSelectedIndex(0);
        txtPrice.setText("");
        txtQuantity.setText("");
        txtReorderLevel.setText("");
        txtExpiryDate.setText("");
        if (cmbSupplier.getItemCount() > 0) cmbSupplier.setSelectedIndex(0);
        tblMedicines.clearSelection();

        btnAdd.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}