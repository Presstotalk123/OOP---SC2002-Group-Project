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
        this.stockLevel = quantity;
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "Name='" + medicationName + '\'' +
                ", Stock=" + stockLevel +
                ", Low Stock Level=" + lowStockAlertLevel +
                '}';
    }
}