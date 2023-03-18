package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class User implements Serializable {
    private String id;
    private String name;
    private int age;
    private String email;
    private String gender;
    private String address;
    private String numberPhone;
    private String image;
    private int enable;

    //type = 2 user
    //type = 1 nhanvien
    //type = 0 admin
    //enable = 0
    //disable =1


    public User() {
    }

    public User(String id, String name, int age, String email, String gender, String address, String numberPhone, String image, int enable) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.gender = gender;
        this.address = address;
        this.numberPhone = numberPhone;
        this.image = image;
        this.enable = enable;
    }


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
