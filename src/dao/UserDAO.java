package dao;

import util.DBConnection;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Authenticates user using credentials from database
    public User authenticate(String username, String password) {
        String sql = "SELECT user_id, username, password, role, full_name FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name")
                    );
                }
            }
        } catch (SQLException ex) {
            System.err.println("Database authentication error: " + ex.getMessage());
        }
        return null;
    }
    
    // Retrieve all user accounts from database
    public List<User> getAllUsers(){
        List<User> userList = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, full_name FROM users ORDER BY user_id ASC";
        
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
        
            while(rs.next()){
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("full_name")
                );
                userList.add(user);
            }    
        }catch(SQLException ex){
            System.err.println("Error fetching user accounts: " + ex.getMessage());
        }
        return userList;
    }
    
    // Checks if username already exists
    public boolean isUsernameTaken(String username, int excludeUserId){
        String sql = "SELECT user_id FROM users WHERE username = ? AND user_id != ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
        
            stmt.setString(1, username);
            stmt.setInt(2, excludeUserId);
            
            try(ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }
        }catch(SQLException ex){
            System.err.println("Error checking username: " + ex.getMessage());
            return true;
        }
    }
    
    //Inserts new user
    public boolean addUser(User user){
        String sql = "INSERT INTO users (username, password, role, full_name) VALUES (?, ?, ?, ?)";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
        
        stmt.setString(1, user.getUsername());
        stmt.setString(2, user.getPassword());
        stmt.setString(3, user.getRole());
        stmt.setString(4, user.getFullName());
        
        return stmt.executeUpdate() > 0;
        }catch(SQLException ex){
            System.err.println("Error adding user: " + ex.getMessage());
            return false;
        }
    }
    

    //Updates existing user

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username = ?, password = ?, role = ?, full_name = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());
            stmt.setInt(5, user.getUserId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.err.println("Error updating user: " + ex.getMessage());
            return false;
        }
    }
    
    //Deletes user by user ID
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        }
    }
}