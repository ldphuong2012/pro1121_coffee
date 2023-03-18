package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class History implements Serializable {
    private String id;
    private int status;
    // 0 chưa xóa
    //1 đã xóa

    public History() {
    }

    public History(String id, int status) {
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
