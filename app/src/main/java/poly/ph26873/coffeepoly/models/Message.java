package poly.ph26873.coffeepoly.models;

public class Message {
    private String id_user;
    private String content;
    private String time;
    private int type;

    public Message() {
    }

    public Message(String id_user, String content, String time,int type) {
        this.id_user = id_user;
        this.content = content;
        this.time = time;
        this.type = type;

    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
