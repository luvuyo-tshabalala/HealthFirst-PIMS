package ui;

import dao.ReportDAO;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ReportPanel extends JPanel {

    private final ReportDAO reportDAO;

    // Report Tables and Models
    private DefaultTableModel salesModel;
    private JTable tblSales;
    private JLabel lblTotalRevenue;

    private DefaultTableModel itemWiseModel;
    private JTable tblItemWise;

    private DefaultTableModel lowStockModel;
    private JTable tblLowStock;

    private DefaultTableModel expiryModel;
    private JTable tblExpiry;

    public ReportPanel() {
        this.reportDAO = new ReportDAO();
        initComponents();
        loadAllReports();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top Control Header
        JPanel topBar = new JPanel(new BorderLayout());
        JLabel lblHeader = new JLabel("Pharmacy Analytical & Inventory Reports");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JButton btnRefresh = new JButton("Refresh Reports");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(33, 115, 70));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(e -> loadAllReports());

        topBar.add(lblHeader, BorderLayout.WEST);
        topBar.add(btnRefresh, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Sub-tabs for the 4 distinct report views
        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        reportTabs.addTab("1. Sales Report", createSalesReportPanel());
        reportTabs.addTab("2. Item-Wise Sales", createItemWiseReportPanel());
        reportTabs.addTab("3. Low Stock Alert", createLowStockReportPanel());
        reportTabs.addTab("4. Expiry Alert (Next 1 Month)", createExpiryReportPanel());

        add(reportTabs, BorderLayout.CENTER);
    }

    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Sale ID", "Date / Time", "Cashier", "Total Amount (ZAR)"};
        salesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblSales = new JTable(salesModel);
        styleTable(tblSales);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalRevenue = new JLabel("Total Cumulative Revenue: R 0.00");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalRevenue.setForeground(new Color(33, 115, 70));
        footerPanel.add(lblTotalRevenue);

        panel.add(new JScrollPane(tblSales), BorderLayout.CENTER);
        panel.add(footerPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createItemWiseReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Medicine ID", "Medicine Name", "Type", "Units Sold", "Total Revenue (ZAR)"};
        itemWiseModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblItemWise = new JTable(itemWiseModel);
        styleTable(tblItemWise);

        panel.add(new JScrollPane(tblItemWise), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLowStockReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Medicine ID", "Medicine Name", "Stock On Hand", "Reorder Level", "Supplier", "Contact Phone"};
        lowStockModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblLowStock = new JTable(lowStockModel);
        styleTable(tblLowStock);

        panel.add(new JScrollPane(tblLowStock), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createExpiryReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Medicine ID", "Medicine Name", "Type", "Stock Available", "Expiry Date", "Supplier"};
        expiryModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblExpiry = new JTable(expiryModel);
        styleTable(tblExpiry);

        panel.add(new JScrollPane(tblExpiry), BorderLayout.CENTER);
        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public void loadAllReports() {
        loadSalesReport();
        loadItemWiseReport();
        loadLowStockReport();
        loadExpiryReport();
    }

    private void loadSalesReport() {
        salesModel.setRowCount(0);
        List<Object[]> rows = reportDAO.getSalesReport();
        double totalRev = 0.0;

        for (Object[] row : rows) {
            salesModel.addRow(row);
            String amountStr = row[3].toString().replace(',', '.');
            totalRev += Double.parseDouble(row[3].toString());
        }
        lblTotalRevenue.setText(String.format("Total Cumulative Revenue: R %.2f", totalRev));
    }

    private void loadItemWiseReport() {
        itemWiseModel.setRowCount(0);
        List<Object[]> rows = reportDAO.getItemWiseSalesReport();
        for (Object[] row : rows) {
            itemWiseModel.addRow(row);
        }
    }

    private void loadLowStockReport() {
        lowStockModel.setRowCount(0);
        List<Object[]> rows = reportDAO.getLowStockReport();
        for (Object[] row : rows) {
            lowStockModel.addRow(row);
        }
    }

    private void loadExpiryReport() {
        expiryModel.setRowCount(0);
        List<Object[]> rows = reportDAO.getExpiryReport();
        for (Object[] row : rows) {
            expiryModel.addRow(row);
        }
    }
}