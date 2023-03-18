package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private String image;
    private int price;
    private String content;
    private int status;
    private int quantitySold;
    private int type;

    //0 con
    // 1 het

    public Product() {
    }

    public Product(int id, String name, String image, int price, String content, int status, int quantitySold, int type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.content = content;
        this.status = status;
        this.quantitySold = quantitySold;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public int getPrice() {
        return price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
