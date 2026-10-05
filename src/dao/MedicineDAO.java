package dao;

import util.DBConnection;
import model.Medicine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {

    public List<Medicine> getAllMedicines() {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT medicine_id, name, company, medicine_type, price, "
                   + "quantity_in_stock, reorder_level, expiry_date, supplier_id "
                   + "FROM medicines ORDER BY medicine_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Medicine m = new Medicine(
                    rs.getInt("medicine_id"),
                    rs.getString("name"),
                    rs.getString("company"),
                    rs.getString("medicine_type"),
                    rs.getDouble("price"),
                    rs.getInt("quantity_in_stock"),
                    rs.getInt("reorder_level"),
                    rs.getDate("expiry_date"),
                    rs.getInt("supplier_id")
                );
                list.add(m);
            }
        } catch (SQLException ex) {
            System.err.println("Database Error in getAllMedicines(): " + ex.getMessage());
            ex.printStackTrace(); // Prints the exact SQL/connection issue to the NetBeans console
        }
        return list;
    }

    // Add medicine method
    public boolean addMedicine(Medicine med) {
        String sql = "INSERT INTO medicines (name, company, medicine_type, price, quantity_in_stock, reorder_level, expiry_date, supplier_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, med.getName());
            stmt.setString(2, med.getCompany());
            stmt.setString(3, med.getMedicineType());
            stmt.setDouble(4, med.getPrice());
            stmt.setInt(5, med.getQuantityInStock());
            stmt.setInt(6, med.getReorderLevel());
            stmt.setDate(7, med.getExpiryDate());
            stmt.setInt(8, med.getSupplierId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Update medicine method
    public boolean updateMedicine(Medicine med) {
        String sql = "UPDATE medicines SET name=?, company=?, medicine_type=?, price=?, quantity_in_stock=?, reorder_level=?, expiry_date=?, supplier_id=? "
                   + "WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, med.getName());
            stmt.setString(2, med.getCompany());
            stmt.setString(3, med.getMedicineType());
            stmt.setDouble(4, med.getPrice());
            stmt.setInt(5, med.getQuantityInStock());
            stmt.setInt(6, med.getReorderLevel());
            stmt.setDate(7, med.getExpiryDate());
            stmt.setInt(8, med.getSupplierId());
            stmt.setInt(9, med.getMedicineId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Delete medicine method
    public boolean deleteMedicine(int medicineId) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, medicineId);
            return stmt.executeUpdate() > 0;
        }
    }
}