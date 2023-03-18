package poly.ph26873.coffeepoly.listData;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;
import poly.ph26873.coffeepoly.models.User;

public class ListData {
    public static List<Product> listPrd = new ArrayList<>();
    public static List<QuantitySoldInMonth> listQuanPrd = new ArrayList<>();
    public static int type_user_current = -1;
    public static int enable_user_current = -1;
    public static List<User> listUser = new ArrayList<>();
}
