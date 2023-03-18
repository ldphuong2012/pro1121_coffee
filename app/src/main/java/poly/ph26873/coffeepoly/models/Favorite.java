package poly.ph26873.coffeepoly.models;

import java.util.List;

public class Favorite {
    private List<Integer> list_id_product;

    public Favorite() {
    }

    public Favorite(List<Integer> list_id_product) {
        this.list_id_product = list_id_product;
    }

    public List<Integer> getList_id_product() {
        return list_id_product;
    }

    public void setList_id_product(List<Integer> list_id_product) {
        this.list_id_product = list_id_product;
    }
}
