==============================================================================
 HEALTHFIRST PHARMACY INVENTORY MANAGEMENT SYSTEM (PIMS)
 User Manual / README
==============================================================================

 1. PROJECT OVERVIEW
------------------------------------------------------------------------------
 PIMS is a desktop application built for "HealthFirst" pharmacy to replace
 manual record-keeping. It lets the pharmacy:

   - Manage stock (medicines, quantities, expiry dates, suppliers)
   - Process sales at a Point of Sale (POS) screen and generate bills
   - View business reports (sales, item-wise, low stock, expiry)
   - Control access through role-based logins (Admin and Cashier)

 Technologies used:
   - Java (Swing/AWT) for the graphical user interface
   - JDBC with MySQL Connector/J for database connectivity
   - MySQL for the relational database
   - jBCrypt for password hashing
   - NetBeans IDE (development) and MySQL Workbench (database design/testing)


 2. DEFAULT LOGIN CREDENTIALS
------------------------------------------------------------------------------
   Role          Username      Password
   -----------   -----------   -----------
   Administrator admin         admin123
   Cashier       cashier       cash123

 Passwords are stored as BCrypt hashes in the database, not as plain text.


 3. SUBMISSION CONTENTS
------------------------------------------------------------------------------
   /NetBeansProjects/HealthFirst_PIMS
    /dist/
    luvuyo_pims.exe     The location of the executable application
                        
   /src                 All Java source files (.java)
   database.sql         Script to create the database, tables and sample data
   /screenshots         Screenshots of the required screens
   README.txt           This file
   Github Repository    https://github.com/luvuyo-tshabalala/HealthFirst-PIMS


 4. SYSTEM REQUIREMENTS
------------------------------------------------------------------------------
   - Windows 10 or later
   - Java Runtime Environment (JRE) [version, e.g. 17 or later], unless a
     Java runtime is bundled with the executable
   - MySQL Server [version, e.g. 8.0 or later] running on localhost, port 3306


 5. INSTALLATION AND SETUP
------------------------------------------------------------------------------

 OPTION A: Using the bundled MySQL database (if included in the zip)
 --------------------------------------------------------------------
   1. Extract the submitted zip file to a folder
   2. Run start.bat (if provided). It starts the bundled MySQL server and
      imports database.sql on first run.
   3. Double-click luvuyo_pims.exe to launch the application.
   4. Log in using the credentials in Section 2.

 OPTION B: Using your own MySQL installation
 --------------------------------------------------------------------
   1. Make sure MySQL Server is installed and running.
   2. Open MySQL Workbench and connect to your local server.
   3. Open database.sql (File > Open SQL Script) and run the entire script
      (lightning bolt icon). This will:
        - CREATE the database "healthfirst_pims"
        - CREATE all five tables (users, suppliers, medicines, sales,
          sale_items)
        - INSERT sample data (users, suppliers and medicines)
   4. Check that the connection details match your MySQL setup. The defaults
      used by the application are:
          Host     : localhost
          Port     : 3306
          Database : healthfirst_pims
          Username : [your MySQL username, e.g. root]
          Password : [your MySQL password]
      If yours differ, update the constants at the top of
      src/util/DBConnection.java and rebuild the project.
   5. Double-click yourname_pims.exe to launch the application.

 RUNNING FROM SOURCE (NetBeans)
 --------------------------------------------------------------------
   1. Open NetBeans and choose File > Open Project, then select the project
      folder.
   2. Confirm the libraries are attached: MySQL Connector/J and jBCrypt
      (Project Properties > Libraries).
   3. Run database.sql as described in Option B.
   4. Right-click the project and choose Run (the main class is the Login
      form).


 6. USER GUIDE
------------------------------------------------------------------------------

 6.1 LOGGING IN
 --------------------------------------------------------------------
   - Enter your username and password and click Login.
   - Administrators are taken to the Admin Dashboard.
   - Cashiers are taken to the Cashier Dashboard.
   - Incorrect details display an error message and the user remains on the
     login screen.

 6.2 ADMINISTRATOR MODULE
 --------------------------------------------------------------------
   The Admin Dashboard has the following tabs:

   MANAGE MEDICINES
     - View all medicines in the table.
     - ADD: fill in the form (name, company, type, price, quantity, reorder
       level, expiry date, supplier) and click Add.
     - UPDATE: select a row, edit the fields and click Update.
     - DELETE: select a row and click Delete, then confirm.
     - Price and quantity must be valid non-negative numbers; the expiry date
       must be a valid date.

   MANAGE SUPPLIERS
     - Add, update and delete supplier records (name, contact person, phone,
       email, address).
     - A supplier that still has medicines linked to it cannot be deleted.

   MANAGE USERS
     - Create new Cashier accounts (username, full name, password).
     - Delete existing Cashier accounts.
     - Only Cashier accounts are listed for management.

   REPORTS
     - Sales Report    : Sales made, with dates, totals and the cashier who
                         processed them.
     - Item-Wise Report: Quantity sold and revenue per medicine.
     - Low Stock Report: Medicines at or below their reorder level.
     - Expiry Report   : Medicines expiring within the next month.

 6.3 CASHIER MODULE
 --------------------------------------------------------------------
   POINT OF SALE (simulation only)
     1. Search for or select a medicine.
     2. Enter the quantity and click Add to Cart.
     3. Repeat for each item. The cart shows each line total and the grand
        total.
     4. Click Checkout to complete the sale. The sale is saved, stock levels
        are reduced, and a Bill window opens.
     5. From the Bill window the bill can be printed or saved.
     6. Click Clear Cart to remove all items without completing a sale.

   STOCK CHECK
     - Look up a medicine to see its price and availability without making a
       sale.
     - Cashiers cannot add, edit or delete medicines.


 7. DATABASE STRUCTURE
------------------------------------------------------------------------------
   users        (user_id, username, password, role, full_name)
   suppliers    (supplier_id, name, contact_person, phone, email, address)
   medicines    (medicine_id, name, company, medicine_type, price,
                 quantity_in_stock, reorder_level, expiry_date, supplier_id)
   sales        (sale_id, sale_date, total_amount, user_id)
   sale_items   (sale_item_id, sale_id, medicine_id, quantity_sold,
                 price_at_sale)

   Relationships:
     medicines.supplier_id  -> suppliers.supplier_id
     sales.user_id          -> users.user_id
     sale_items.sale_id     -> sales.sale_id
     sale_items.medicine_id -> medicines.medicine_id


 8. TROUBLESHOOTING
------------------------------------------------------------------------------
   "Communications link failure"
       MySQL Server is not running. Start it (via MySQL Workbench, Windows
       Services, or start.bat) and try again.

   "Access denied for user"
       The MySQL username or password in DBConnection.java does not match
       your MySQL account. Update the constants and rebuild.

   "Unknown database 'healthfirst_pims'"
       database.sql has not been run yet. Run the full script first.

   Application does not start when double-clicking the .exe
       Java may not be installed. Install a JRE of the required version
       (see Section 4) or run the project from NetBeans.

   Login fails with the default credentials
       Re-run database.sql to restore the sample users.

   No data in the Expiry Report
       The report only lists medicines expiring within the next month.
       Add or edit a medicine with a nearby expiry date to see results.


 9. REFERENCES
------------------------------------------------------------------------------
   Oracle (n.d.) The Java Tutorials: Creating a GUI With Swing. Available at:
       https://docs.oracle.com/javase/tutorial/uiswing/TOC.html

   Oracle (n.d.) The Java Tutorials: JDBC Database Access. Available at:
       https://docs.oracle.com/javase/tutorial/jdbc/basics/gettingstarted.html

   MySQL (n.d.) MySQL Workbench Manual. Available at:
       https://dev.mysql.com/doc/workbench/en/

   Apache NetBeans (n.d.) Designing a Swing GUI in NetBeans IDE. Available at:
       https://netbeans.apache.org/tutorial/main/kb/docs/java/quickstart-gui/

   GitHub (n.d.) Quickstart for repositories. Available at:
       https://docs.github.com/en/repositories/creating-and-managing-repositories/quickstart-for-repositories

