package ui;

import model.User;
import javax.swing.*;

public class AdminDashboard extends JFrame {
    private final User currentUser;
    
    public AdminDashboard(User user){
        this.currentUser = user;
        setTitle("Pharmacy Inventory Management System - Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel lblWelcome = new JLabel("Welcome, Admin: " + currentUser.getFullName(), SwingConstants.CENTER);
        add(lblWelcome);
    }
}
