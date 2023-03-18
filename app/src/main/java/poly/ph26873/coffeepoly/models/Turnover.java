package poly.ph26873.coffeepoly.models;

import java.io.Serializable;

public class Turnover implements Serializable {
    private String id;
    private int total;
    private String time;
    private String path;

    public Turnover() {
    }

    public Turnover(String id, int total,String time,String path) {
        this.id = id;
        this.total = total;
        this.time  = time;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
