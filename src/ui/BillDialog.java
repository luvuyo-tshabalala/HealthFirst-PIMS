package ui;

import model.SaleItem;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.*;

public class BillDialog extends JDialog {

    private final int saleId;
    private final String cashierName;
    private final List<SaleItem> items;
    private final double totalAmount;
    private final double amountPaid;
    private final double changeDue;
    private final String receiptDate;

    private JTextArea txtReceipt;
    private JButton btnPrintSave;
    private JButton btnClose;

    public BillDialog(Frame parent, int saleId, String cashierName, List<SaleItem> items, double totalAmount, double amountPaid) {
        super(parent, "Customer Bill / Receipt - Sale #" + saleId, true);
        this.saleId = saleId;
        this.cashierName = cashierName;
        this.items = items;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.changeDue = Math.max(0.0, amountPaid - totalAmount);
        this.receiptDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        initComponents();
        generateReceiptText();
    }

    private void initComponents() {
        setSize(460, 580);
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(10, 10));

        // Receipt Display Area
        txtReceipt = new JTextArea();
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtReceipt.setMargin(new Insets(10, 15, 10, 15));
        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        add(scrollPane, BorderLayout.CENTER);

        // Control Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        btnPrintSave = new JButton("Print / Save Receipt");
        btnPrintSave.setBackground(new Color(33, 115, 70));
        btnPrintSave.setForeground(Color.WHITE);
        btnPrintSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrintSave.setFocusPainted(false);

        btnClose = new JButton("Close");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnClose.setFocusPainted(false);

        buttonPanel.add(btnPrintSave);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Handlers
        btnPrintSave.addActionListener(e -> handleSaveReceipt());
        btnClose.addActionListener(e -> dispose());
    }

    private void generateReceiptText() {
        StringBuilder sb = new StringBuilder();
        sb.append("==============================================\n");
        sb.append("            HEALTHFIRST PHARMACY              \n");
        sb.append("          12 Medical Way, Pretoria            \n");
        sb.append("          Tel: +27 (012) 555-0199             \n");
        sb.append("==============================================\n");
        sb.append(String.format("Receipt No : #%06d\n", saleId));
        sb.append(String.format("Date / Time: %s\n", receiptDate));
        sb.append(String.format("Cashier    : %s\n", cashierName));
        sb.append("----------------------------------------------\n");
        sb.append(String.format("%-20s %5s %8s %9s\n", "Item", "Qty", "Price", "Subtotal"));
        sb.append("----------------------------------------------\n");

        for (SaleItem item : items) {
            String name = item.getMedicineName();
            if (name.length() > 19) {
                name = name.substring(0, 16) + "...";
            }
            sb.append(String.format("%-20s %5d %8.2f %9.2f\n",
                    name,
                    item.getQuantitySold(),
                    item.getPriceAtSale(),
                    item.getSubtotal()));
        }

        sb.append("----------------------------------------------\n");
        sb.append(String.format("TOTAL AMOUNT DUE:               R %10.2f\n", totalAmount));
        sb.append(String.format("CASH TENDERED:                  R %10.2f\n", amountPaid));
        sb.append(String.format("CHANGE RETURNED:                R %10.2f\n", changeDue));
        sb.append("==============================================\n");
        sb.append("        Thank you for your patronage!         \n");
        sb.append("       Medicines sold cannot be returned      \n");
        sb.append("==============================================\n");

        txtReceipt.setText(sb.toString());
        txtReceipt.setCaretPosition(0);
    }

    private void handleSaveReceipt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("Receipt_Sale_" + saleId + ".txt"));
        int userChoice = fileChooser.showSaveDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(txtReceipt.getText());
                JOptionPane.showMessageDialog(this,
                    "Receipt successfully saved to:\n" + fileToSave.getAbsolutePath(),
                    "Receipt Exported",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving receipt file: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}