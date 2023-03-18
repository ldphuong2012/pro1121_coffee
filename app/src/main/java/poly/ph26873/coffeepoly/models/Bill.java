package poly.ph26873.coffeepoly.models;

import java.io.Serializable;
import java.util.List;

public class Bill {
    private String id;
    private String name;
    private List<Item_Bill> list;
    private int total;
    private String address;
    private String numberPhone;
    private String note;
    private int status;
    private String id_user;
    private String mess;
    // status = 0 - đã xac nhan
    // status = 1 - đang xac nhan
    // status = 2 - đã hủy
    // status = 3 - dang giao
    // status = 4 - giao thanh cong,==5 boom hang
    //id chính là thời gian đặt hàng định dạng: dd_MM_yyyy kk:mm:ss


    public Bill() {
    }

    public Bill(String id, String name, List<Item_Bill> list, int total, String address, String numberPhone, String note, int status, String id_user, String mess) {
        this.id = id;
        this.list = list;
        this.total = total;
        this.address = address;
        this.note = note;
        this.status = status;
        this.name = name;
        this.numberPhone = numberPhone;
        this.id_user = id_user;
        this.mess = mess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberPhone() {
        return numberPhone;
    }

    public void setNumberPhone(String numberPhone) {
        this.numberPhone = numberPhone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Item_Bill> getList() {
        return list;
    }

    public void setList(List<Item_Bill> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}
