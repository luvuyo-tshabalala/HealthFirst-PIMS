package model;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int medicineId;
    private int quantitySold;
    private double priceAtSale;
    private String medicineName;

    public SaleItem(){}

    public SaleItem(int saleItemId, int saleId, int medicineId, int quantitySold, double priceAtSale){
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.medicineId = medicineId;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    public SaleItem(int medicineId, String medicineName, int quantitySold, double priceAtSale){
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.quantitySold = quantitySold;
        this.priceAtSale = priceAtSale;
    }

    public int getSaleItemId(){
        return saleItemId;
    }
    
    public void setSaleItemId(int saleItemId){
        this.saleItemId = saleItemId;
    }

    public int getSaleId(){
        return saleId;
    }
    
    public void setSaleId(int saleId){
        this.saleId = saleId;
    }

    public int getMedicineId(){
        return medicineId;
    }
    
    public void setMedicineId(int medicineId){
        this.medicineId = medicineId;
    }

    public int getQuantitySold(){
        return quantitySold;
    }
    public void setQuantitySold(int quantitySold){
        this.quantitySold = quantitySold;
    }

    public double getPriceAtSale(){
        return priceAtSale;
    }
    
    public void setPriceAtSale(double priceAtSale){
        this.priceAtSale = priceAtSale;
    }

    public String getMedicineName(){
        return medicineName;
    }
    
    public void setMedicineName(String medicineName){
        this.medicineName = medicineName;
    }

    public double getSubtotal(){
        return this.quantitySold * this.priceAtSale;
    }
}