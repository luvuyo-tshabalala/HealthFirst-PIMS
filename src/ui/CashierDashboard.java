package ui;

import model.User;
import javax.swing.*;

public class CashierDashboard extends JFrame {
    private final User currentUser;
    
    public CashierDashboard(User user){
        this.currentUser = user;
        setTitle("Pharmacy Inventory Management System - Cashier POS");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel lblWelcome = new JLabel("Welcome, Cashier: " + currentUser.getFullName(), SwingConstants.CENTER);
        add(lblWelcome);
    }
}
