package poly.ph26873.coffeepoly.models;

public class QuantitySoldInMonth {
    private int id_Product;
    private int quantitySold;

    public QuantitySoldInMonth() {
    }

    public QuantitySoldInMonth(int id_Product, int quantitySold) {
        this.id_Product = id_Product;
        this.quantitySold = quantitySold;
    }

    public int getId_Product() {
        return id_Product;
    }

    public void setId_Product(int id_Product) {
        this.id_Product = id_Product;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

}
