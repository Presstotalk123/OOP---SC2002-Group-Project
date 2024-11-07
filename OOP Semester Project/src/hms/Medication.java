package hms;

public class Medication {
    private String medicationName;
    private int stockLevel;
    private int lowStockAlertLevel;

    public Medication(String medicationName, int stockLevel, int lowStockAlertLevel) {
        this.medicationName = medicationName;
        this.stockLevel = stockLevel;
        this.lowStockAlertLevel = lowStockAlertLevel;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public int getLowStockAlertLevel() {
        return lowStockAlertLevel;
    }

    public void updateStockLevel(int quantity) {
        if (quantity >= 0) {
            this.stockLevel = quantity;
        } else {
            System.out.println("Stock level cannot be negative.");
        }
    }

    public void setLowStockAlertLevel(int lowStockAlertLevel) {
        if (lowStockAlertLevel >= 0) {
            this.lowStockAlertLevel = lowStockAlertLevel;
        } else {
            System.out.println("Low stock alert level cannot be negative.");
        }
    }
}