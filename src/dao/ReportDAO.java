package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportDAO {

     // Report 1: Sales Transaction Report
    public List<Object[]> getSalesReport() {
        List<Object[]> reportData = new ArrayList<>();
        String sql = "SELECT s.sale_id, s.sale_date, u.full_name AS cashier_name, s.total_amount "
                   + "FROM sales s "
                   + "JOIN users u ON s.user_id = u.user_id "
                   + "ORDER BY s.sale_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reportData.add(new Object[]{
                    rs.getInt("sale_id"),
                    rs.getTimestamp("sale_date"),
                    rs.getString("cashier_name"),
                    String.format(Locale.US, "%.2f", rs.getDouble("total_amount"))
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching sales report: " + ex.getMessage());
        }
        return reportData;
    }


     // Report 2: Item-Wise Sales Performance Report
    public List<Object[]> getItemWiseSalesReport() {
        List<Object[]> reportData = new ArrayList<>();
        String sql = "SELECT m.medicine_id, m.name AS medicine_name, m.medicine_type, "
                   + "COALESCE(SUM(si.quantity_sold), 0) AS total_units_sold, "
                   + "COALESCE(SUM(si.quantity_sold * si.price_at_sale), 0.00) AS total_revenue "
                   + "FROM medicines m "
                   + "JOIN sale_items si ON m.medicine_id = si.medicine_id "
                   + "GROUP BY m.medicine_id, m.name, m.medicine_type "
                   + "ORDER BY total_revenue DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reportData.add(new Object[]{
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    rs.getString("medicine_type"),
                    rs.getInt("total_units_sold"),
                    String.format(Locale.US, "%.2f", rs.getDouble("total_revenue"))
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching item-wise sales report: " + ex.getMessage());
        }
        return reportData;
    }

    
      // Report 3: Low Stock Alert Report
    public List<Object[]> getLowStockReport() {
        List<Object[]> reportData = new ArrayList<>();
        String sql = "SELECT m.medicine_id, m.name, m.quantity_in_stock, m.reorder_level, "
                   + "sup.name AS supplier_name, sup.phone AS supplier_phone "
                   + "FROM medicines m "
                   + "JOIN suppliers sup ON m.supplier_id = sup.supplier_id "
                   + "WHERE m.quantity_in_stock <= m.reorder_level "
                   + "ORDER BY m.quantity_in_stock ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reportData.add(new Object[]{
                    rs.getInt("medicine_id"),
                    rs.getString("name"),
                    rs.getInt("quantity_in_stock"),
                    rs.getInt("reorder_level"),
                    rs.getString("supplier_name"),
                    rs.getString("supplier_phone")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching low stock report: " + ex.getMessage());
        }
        return reportData;
    }

     // Report 4: Expiry Alert Report
    public List<Object[]> getExpiryReport() {
        List<Object[]> reportData = new ArrayList<>();
        String sql = "SELECT m.medicine_id, m.name, m.medicine_type, m.quantity_in_stock, "
                   + "m.expiry_date, sup.name AS supplier_name "
                   + "FROM medicines m "
                   + "JOIN suppliers sup ON m.supplier_id = sup.supplier_id "
                   + "WHERE m.expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 MONTH) "
                   + "ORDER BY m.expiry_date ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reportData.add(new Object[]{
                    rs.getInt("medicine_id"),
                    rs.getString("name"),
                    rs.getString("medicine_type"),
                    rs.getInt("quantity_in_stock"),
                    rs.getDate("expiry_date").toString(),
                    rs.getString("supplier_name")
                });
            }
        } catch (SQLException ex) {
            System.err.println("Error fetching expiry report: " + ex.getMessage());
        }
        return reportData;
    }
}
