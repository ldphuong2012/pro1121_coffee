package poly.ph26873.coffeepoly.models;

public class Notify {
    private String time;
    private String content;
    private int status;

    public Notify() {
    }

    public Notify(String time, String content, int status) {
        this.time = time;
        this.content = content;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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
}
