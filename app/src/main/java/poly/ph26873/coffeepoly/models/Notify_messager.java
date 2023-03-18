package poly.ph26873.coffeepoly.models;

public class Notify_messager {
    private String id_user;
    private int status;
    //0 co tin nhan moi tu user
    //2 co tin nhan moi tu nhan vien
    // 1



    public Notify_messager() {
    }

    public Notify_messager(String id_user, int status) {
        this.id_user = id_user;
        this.status = status;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
