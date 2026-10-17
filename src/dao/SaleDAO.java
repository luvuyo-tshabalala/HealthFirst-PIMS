package dao;

import util.DBConnection;
import model.Sale;
import model.SaleItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SaleDAO {

    /**
     * Executes an atomic sale transaction across the sales, sale_items,
     * and medicines tables.
     * Returns the generated sale_id upon success, or -1 if the transaction fails.
     */
    public int processSale(Sale sale, List<SaleItem> items) throws SQLException {
        Connection conn = null;
        PreparedStatement stmtSale = null;
        PreparedStatement stmtItem = null;
        PreparedStatement stmtUpdateStock = null;
        ResultSet generatedKeys = null;
        int generatedSaleId = -1;

        String insertSaleSql = "INSERT INTO sales (total_amount, user_id) VALUES (?, ?)";
        String insertItemSql = "INSERT INTO sale_items (sale_id, medicine_id, quantity_sold, price_at_sale) VALUES (?, ?, ?, ?)";
        String updateStockSql = "UPDATE medicines SET quantity_in_stock = quantity_in_stock - ? WHERE medicine_id = ?";

        try {
            conn = DBConnection.getConnection();
            // Begin atomic transaction
            conn.setAutoCommit(false);

            // 1. Insert transaction header into sales
            stmtSale = conn.prepareStatement(insertSaleSql, Statement.RETURN_GENERATED_KEYS);
            stmtSale.setDouble(1, sale.getTotalAmount());
            stmtSale.setInt(2, sale.getUserId());
            stmtSale.executeUpdate();

            generatedKeys = stmtSale.getGeneratedKeys();
            if (generatedKeys.next()) {
                generatedSaleId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating sale record failed; no ID obtained.");
            }

            // 2. Prepare batch statements for line items and stock reduction
            stmtItem = conn.prepareStatement(insertItemSql);
            stmtUpdateStock = conn.prepareStatement(updateStockSql);

            for (SaleItem item : items) {
                // Insert into sale_items
                stmtItem.setInt(1, generatedSaleId);
                stmtItem.setInt(2, item.getMedicineId());
                stmtItem.setInt(3, item.getQuantitySold());
                stmtItem.setDouble(4, item.getPriceAtSale());
                stmtItem.addBatch();

                // Deduct inventory stock
                stmtUpdateStock.setInt(1, item.getQuantitySold());
                stmtUpdateStock.setInt(2, item.getMedicineId());
                stmtUpdateStock.addBatch();
            }

            // Execute batches
            stmtItem.executeBatch();
            stmtUpdateStock.executeBatch();

            // Commit atomic transaction
            conn.commit();
            return generatedSaleId;

        } catch (SQLException ex) {
            // Roll back all changes if any part of the checkout fails
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw ex;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (stmtSale != null) stmtSale.close();
            if (stmtItem != null) stmtItem.close();
            if (stmtUpdateStock != null) stmtUpdateStock.close();
            if (conn != null) conn.setAutoCommit(true);
        }
    }
}