package ui;

import dao.MedicineDAO;
import model.Medicine;
import model.SaleItem;
import model.User;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class CashierDashboard extends JFrame {

    private final User currentUser;
    private final MedicineDAO medicineDAO;
    private final List<SaleItem> cartItems;

    //Inventory Table Components
    private DefaultTableModel inventoryTableModel;
    private JTable inventoryTable;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JTextField txtSearch;
    private JSpinner spinQuantity;
    private JButton btnAddToCart;

    // Cart Components
    private DefaultTableModel cartTableModel;
    private JTable cartTable;
    private JLabel lblTotalAmount;
    private JButton btnRemoveItem;
    private JButton btnClearCart;
    private JButton btnCheckout;

    public CashierDashboard(User user) {
        this.currentUser = user;
        this.medicineDAO = new MedicineDAO();
        this.cartItems = new ArrayList<>();
        initComponents();
        loadInventoryData();
    }

    private void initComponents() {
        setTitle("HealthFirst Pharmacy - Cashier Point of Sale (POS)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        //Header Bar
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(33, 115, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("HealthFirst Pharmacy | Point of Sale (POS)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        String cashierName = (currentUser != null) ? currentUser.getFullName() : "Cashier";
        JLabel lblUser = new JLabel("Cashier: " + cashierName);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setBackground(new Color(220, 53, 69));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> handleLogout());

        userPanel.add(lblUser);
        userPanel.add(btnLogout);
        headerPanel.add(userPanel, BorderLayout.EAST);

        //Main Content Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.55); // 55% inventory search, 45% cart
        splitPane.setContinuousLayout(true);

        // LEFT: Inventory & Stock Check Panel
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 5),
            BorderFactory.createTitledBorder("Medicine Stock Check & Selection")
        ));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 8, 5));
        JLabel lblSearch = new JLabel("Search Medicine:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchPanel.add(lblSearch, BorderLayout.WEST);
        searchPanel.add(txtSearch, BorderLayout.CENTER);

        // Inventory Table
        String[] invCols = {"ID", "Name", "Type", "Price (ZAR)", "Stock Available"};
        inventoryTableModel = new DefaultTableModel(invCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(inventoryTableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.setRowHeight(24);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        tableSorter = new TableRowSorter<>(inventoryTableModel);
        inventoryTable.setRowSorter(tableSorter);

        JScrollPane scrollInventory = new JScrollPane(inventoryTable);

        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JLabel lblQty = new JLabel("Quantity:");
        lblQty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinQuantity.setPreferredSize(new Dimension(65, 26));

        btnAddToCart = new JButton("Add to Cart");
        btnAddToCart.setBackground(new Color(33, 115, 70));
        btnAddToCart.setForeground(Color.WHITE);
        btnAddToCart.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAddToCart.setFocusPainted(false);

        addToCartPanel.add(lblQty);
        addToCartPanel.add(spinQuantity);
        addToCartPanel.add(btnAddToCart);

        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(scrollInventory, BorderLayout.CENTER);
        leftPanel.add(addToCartPanel, BorderLayout.SOUTH);

        
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 5, 10, 10),
            BorderFactory.createTitledBorder("Active Sales Cart")
        ));

        // Cart Table
        String[] cartCols = {"Item ID", "Medicine Name", "Unit Price", "Qty", "Subtotal (ZAR)"};
        cartTableModel = new DefaultTableModel(cartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cartTable.setRowHeight(24);
        cartTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollCart = new JScrollPane(cartTable);

        // Cart Bottom Controls & Total Display
        JPanel cartBottomPanel = new JPanel(new BorderLayout(10, 10));
        cartBottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));

        // Total and item action buttons
        JPanel actionsAndTotalPanel = new JPanel(new BorderLayout(5, 5));
        
        lblTotalAmount = new JLabel("Total: R 0.00", SwingConstants.RIGHT);
        lblTotalAmount.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotalAmount.setForeground(new Color(33, 115, 70));
        lblTotalAmount.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRemoveItem = new JButton("Remove Selected");
        btnRemoveItem.setFocusPainted(false);
        btnClearCart = new JButton("Clear Cart");
        btnClearCart.setFocusPainted(false);
        cartActions.add(btnRemoveItem);
        cartActions.add(btnClearCart);

        actionsAndTotalPanel.add(cartActions, BorderLayout.WEST);
        actionsAndTotalPanel.add(lblTotalAmount, BorderLayout.EAST);

        // Checkout Button
        btnCheckout = new JButton("Proceed to Checkout & Bill");
        btnCheckout.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCheckout.setBackground(new Color(0, 122, 204));
        btnCheckout.setForeground(Color.WHITE);
        btnCheckout.setPreferredSize(new Dimension(0, 38));
        btnCheckout.setFocusPainted(false);

        cartBottomPanel.add(actionsAndTotalPanel, BorderLayout.CENTER);
        cartBottomPanel.add(btnCheckout, BorderLayout.SOUTH);

        rightPanel.add(scrollCart, BorderLayout.CENTER);
        rightPanel.add(cartBottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        btnAddToCart.addActionListener(e -> handleAddToCart());
        btnRemoveItem.addActionListener(e -> handleRemoveSelectedItem());
        btnClearCart.addActionListener(e -> handleClearCart());
        btnCheckout.addActionListener(e -> handleCheckoutStub());

        // Instant Live Search Filter Listener
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterInventory(); }
            @Override public void removeUpdate(DocumentEvent e) { filterInventory(); }
            @Override public void changedUpdate(DocumentEvent e) { filterInventory(); }
        });
    }

    public void loadInventoryData() {
        inventoryTableModel.setRowCount(0);
        List<Medicine> medicines = medicineDAO.getAllMedicines();
        for (Medicine m : medicines) {
            inventoryTableModel.addRow(new Object[]{
                m.getMedicineId(),
                m.getName(),
                m.getMedicineType(),
                String.format(java.util.Locale.US,"%.2f", m.getPrice()),
                m.getQuantityInStock()
            });
        }
    }

    private void filterInventory() {
        String query = txtSearch.getText().trim();
        if (query.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            // Case-insensitive filter
            tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + query));
        }
    }

    private void handleAddToCart() {
        int viewRow = inventoryTable.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine from the inventory list.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = inventoryTable.convertRowIndexToModel(viewRow);
        int medicineId = Integer.parseInt(inventoryTableModel.getValueAt(modelRow, 0).toString());
        String medicineName = inventoryTableModel.getValueAt(modelRow, 1).toString();
        String priceStr = inventoryTableModel.getValueAt(modelRow, 3).toString().replace(',', '.');
        double price = Double.parseDouble(priceStr);
        int currentStock = Integer.parseInt(inventoryTableModel.getValueAt(modelRow, 4).toString());
        int requestedQty = (Integer) spinQuantity.getValue();

        // Calculate carted quantity of item
        int existingCartQty = 0;
        int existingIndex = -1;
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).getMedicineId() == medicineId) {
                existingCartQty = cartItems.get(i).getQuantitySold();
                existingIndex = i;
                break;
            }
        }

        if (existingCartQty + requestedQty > currentStock) {
            JOptionPane.showMessageDialog(this,
                "Insufficient stock! Available: " + currentStock + " | Already in cart: " + existingCartQty,
                "Stock Limit Exceeded",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (existingIndex != -1) {
            // Update existing cart item
            SaleItem item = cartItems.get(existingIndex);
            item.setQuantitySold(item.getQuantitySold() + requestedQty);
        } else {
            // Add new line item
            SaleItem newItem = new SaleItem(medicineId, medicineName, requestedQty, price);
            cartItems.add(newItem);
        }

        refreshCartTable();
        spinQuantity.setValue(1);
    }

    private void handleRemoveSelectedItem() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        cartItems.remove(selectedRow);
        refreshCartTable();
    }

    private void handleClearCart() {
        if (cartItems.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all items from the cart?",
            "Clear Cart",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            cartItems.clear();
            refreshCartTable();
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0.0;

        for (SaleItem item : cartItems) {
            double subtotal = item.getSubtotal();
            total += subtotal;

            cartTableModel.addRow(new Object[]{
                item.getMedicineId(),
                item.getMedicineName(),
                String.format(java.util.Locale.US,"%.2f", item.getPriceAtSale()),
                item.getQuantitySold(),
                String.format(java.util.Locale.US,"%.2f", subtotal)
            });
        }

        lblTotalAmount.setText(String.format(java.util.Locale.US,"Total: R %.2f", total));
    }

    private void handleCheckoutStub() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "The cart is empty. Add items before proceeding to checkout.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Cart is ready for checkout (" + cartItems.size() + " items).\nProceeding to Commit 9 for database transaction & bill generation.",
            "Checkout Verification",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to log out of the POS system?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // Getters for checkout processing
    public List<SaleItem> getCartItems() { return cartItems; }
    public User getCurrentUser() { return currentUser; }
    public JButton getBtnCheckout() { return btnCheckout; }
}