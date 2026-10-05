package model;

import java.sql.Date;

public class Medicine {
    private int medicineId;
    private String name;
    private String company;
    private String medicineType;
    private double price;
    private int quantityInStock;
    private int reorderLevel;
    private Date expiryDate;
    private int supplierId;

    public Medicine() {}

    // Complete constructor used by MedicineDAO
    public Medicine(int medicineId, String name, String company, String medicineType, 
                    double price, int quantityInStock, int reorderLevel, Date expiryDate, int supplierId) {
        this.medicineId = medicineId;
        this.name = name;
        this.company = company;
        this.medicineType = medicineType;
        this.price = price;
        this.quantityInStock = quantityInStock;
        this.reorderLevel = reorderLevel;
        this.expiryDate = expiryDate;
        this.supplierId = supplierId;
    }

    // Getters
    public int getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public String getCompany() { return company; }
    public String getMedicineType() { return medicineType; }
    public double getPrice() { return price; }
    public int getQuantityInStock() { return quantityInStock; }
    public int getReorderLevel() { return reorderLevel; }
    public Date getExpiryDate() { return expiryDate; }
    public int getSupplierId() { return supplierId; }

    // Setters
    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }
    public void setName(String name) { this.name = name; }
    public void setCompany(String company) { this.company = company; }
    public void setMedicineType(String medicineType) { this.medicineType = medicineType; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantityInStock(int quantityInStock) { this.quantityInStock = quantityInStock; }
    public void setReorderLevel(int reorderLevel) { this.reorderLevel = reorderLevel; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
}