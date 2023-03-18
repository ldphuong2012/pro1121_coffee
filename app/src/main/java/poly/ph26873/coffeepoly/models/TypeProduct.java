package poly.ph26873.coffeepoly.models;

public class TypeProduct {
    private int id;
    private String country;

    public TypeProduct() {
    }

    public TypeProduct(int id, String country) {
        this.id = id;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
