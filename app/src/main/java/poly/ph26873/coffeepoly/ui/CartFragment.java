package poly.ph26873.coffeepoly.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.CartRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.History;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Notify;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class CartFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    private RecyclerView cartRecyclerView;
    private CartRCVAdapter cartRCVAdapter;
    private List<Item_Bill> list;
    private TextView tv_cart_thong_ke, tv_cart_tong_tien, tv_cart_mess;
    private ImageButton btn_cart_order;
    private String email;
    private FirebaseDatabase database;
    private String thong_ke = "";
    private int tong_tien = 0;
    private List<Item_Bill> list1 = new ArrayList<>();
    private static final String TAG = "zzz";
    private List<String> listSPdel;
    private LinearLayout ln_bill;
    private boolean isFirst = true;
    private AlertDialog alertDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView);
        tv_cart_thong_ke = view.findViewById(R.id.tv_cart_thong_ke);
        tv_cart_tong_tien = view.findViewById(R.id.tv_cart_tong_tien);
        tv_cart_mess = view.findViewById(R.id.tv_cart_mess);
        ln_bill = view.findViewById(R.id.ln_bill);
        btn_cart_order = view.findViewById(R.id.btn_cart_order);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        cartRecyclerView.setLayoutManager(manager);
        cartRecyclerView.setHasFixedSize(true);
        cartRCVAdapter = new CartRCVAdapter(getContext());
        list = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        email = user.getEmail().replaceAll("@gmail.com", "");
        database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/cart/" + email);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item_Bill item_bill = snapshot.getValue(Item_Bill.class);
                if (item_bill != null) {
                    list.add(item_bill);
                    cartRCVAdapter.setData(list);
                    if (isFirst == true) {
                        setAL();
                        isFirst = false;
                    }
                    cartRecyclerView.setAdapter(cartRCVAdapter);
                    layDanhSachTinhTien();
                }
                if (list.size() == 0) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv_cart_mess.setText("Giỏ hàng của bạn hiện không có sản phẩm nào");
                    tv_cart_mess.setLayoutParams(lp);
                    ln_bill.setVisibility(View.INVISIBLE);
                } else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                           0, 0);
                    tv_cart_mess.setLayoutParams(lp);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Item_Bill item_bill = snapshot.getValue(Item_Bill.class);
                if (list.isEmpty() || item_bill == null) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getTime() == item_bill.getTime()) {
                        list.set(i, item_bill);
                        cartRCVAdapter.setData(list);
                        layDanhSachTinhTien();
                        break;
                    }
                }
                if (list.size() == 0) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv_cart_mess.setText("Giỏ hàng của bạn hiện không có sản phẩm nào");
                    tv_cart_mess.setLayoutParams(lp);
                    ln_bill.setVisibility(View.INVISIBLE);
                } else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0,0);
                    tv_cart_mess.setLayoutParams(lp);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Item_Bill item_bill = snapshot.getValue(Item_Bill.class);
                if (item_bill == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getTime() == item_bill.getTime()) {
                        list.remove(list.get(i));
                        break;
                    }
                }
                if (list.size()==0) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tv_cart_mess.setText("Giỏ hàng của bạn hiện không có sản phẩm nào");
                    tv_cart_mess.setLayoutParams(lp);
                    ln_bill.setVisibility(View.INVISIBLE);
                } else {
                    tv_cart_mess.setText("");
                }
                cartRCVAdapter.setData(list);
                layDanhSachTinhTien();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }

        });
        if (list.isEmpty()) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_cart_mess.setText("Giỏ hàng của bạn hiện không có sản phẩm nào");
            tv_cart_mess.setLayoutParams(lp);
            ln_bill.setVisibility(View.INVISIBLE);
        } else {
            tv_cart_mess.setText("");
        }
        btn_cart_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(getContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tong_tien == 0) {
                    return;
                }
                ProgressDialog progressDialog = new ProgressDialog(getContext());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Đang tiến hàng đặt hàng....");
                progressDialog.show();
                DatabaseReference AddressRef = database.getReference("coffee-poly/user/" + email);
                AddressRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                        if (tong_tien > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setCancelable(false);
                            View view1 = LayoutInflater.from(builder.getContext()).inflate(R.layout.layout_thong_tin_dat_hang, null);
                            TextView tv_xac_nhan_time = view1.findViewById(R.id.tv_xac_nhan_time);
                            TextView tv_xac_nhan_total = view1.findViewById(R.id.tv_xac_nhan_total);
                            TextView tv_xac_nhan_note = view1.findViewById(R.id.tv_xac_nhan_note);
                            EditText edt_xac_nhan_name = view1.findViewById(R.id.edt_xac_nhan_name);
                            EditText edt_xac_nhan_address = view1.findViewById(R.id.edt_xac_nhan_address);
                            EditText edt_xac_nhan_nb = view1.findViewById(R.id.edt_xac_nhan_nb);
                            EditText edt_xac_nhan_mess = view1.findViewById(R.id.edt_xac_nhan_mess);
                            TextInputLayout til_xac_nhan_name = view1.findViewById(R.id.til_xac_nhan_name);
                            TextInputLayout til_xac_nhan_address = view1.findViewById(R.id.til_xac_nhan_address);
                            TextInputLayout til_xac_nhan_nb = view1.findViewById(R.id.til_xac_nhan_nb);
                            Button btn_xac_nhan_dat_hang = view1.findViewById(R.id.btn_xac_nhan_dat_hang);
                            ImageView btn_xac_nhan_huy = view1.findViewById(R.id.btn_xac_nhan_huy);
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy HH:mm:ss");
                            String time = simpleDateFormat.format(calendar.getTime());
                            tv_xac_nhan_time.setText("Thời gian: " + time);
                            edt_xac_nhan_name.setText(user1.getName());
                            edt_xac_nhan_address.setText(user1.getAddress());
                            edt_xac_nhan_nb.setText(user1.getNumberPhone());
                            tv_xac_nhan_total.setText("Tổng tiền: " + tong_tien + "K");
                            tv_xac_nhan_note.setText("Chi tiết: \n" + thong_ke);
                            edt_xac_nhan_mess.setText("Trống");
                            btn_xac_nhan_dat_hang.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (edt_xac_nhan_name.getText().toString().trim().isEmpty()) {
                                        til_xac_nhan_name.setError("Không được để trống trường này!");
                                        edt_xac_nhan_name.requestFocus();
                                        return;
                                    }
                                    til_xac_nhan_name.setError("");
                                    if (edt_xac_nhan_address.getText().toString().trim().isEmpty()) {
                                        til_xac_nhan_address.setError("Không được để trống trường này!");
                                        edt_xac_nhan_address.requestFocus();
                                        return;
                                    }
                                    til_xac_nhan_address.setError("");

                                    if (!android.util.Patterns.PHONE.matcher(edt_xac_nhan_nb.getText().toString().trim()).matches()) {
                                        til_xac_nhan_nb.setError("Kiểm tra lại số điện thoại");
                                        edt_xac_nhan_nb.requestFocus();
                                        edt_xac_nhan_nb.setSelection(edt_xac_nhan_nb.length());
                                        return;
                                    }
                                    til_xac_nhan_nb.setError("");
                                    if (edt_xac_nhan_mess.getText().toString().isEmpty()) {
                                        edt_xac_nhan_mess.setText("Trống");
                                    }
                                    Bill bill = new Bill();
                                    bill.setId(time);
                                    bill.setName(edt_xac_nhan_name.getText().toString().trim());
                                    bill.setList(list1);
                                    bill.setTotal(tong_tien);
                                    bill.setAddress(edt_xac_nhan_address.getText().toString().trim());
                                    bill.setNumberPhone(edt_xac_nhan_nb.getText().toString().trim());
                                    bill.setNote(thong_ke);
                                    bill.setId_user(email);
                                    bill.setStatus(1);
                                    bill.setMess(edt_xac_nhan_mess.getText().toString().trim());
                                    DatabaseReference myBill = database.getReference("coffee-poly/bill/" + email + "/" + time);
                                    myBill.setValue(bill, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            Log.d(TAG, "Đã cập nhật đơn hàng lên bill");
                                            Toast.makeText(getContext(), "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                            DatabaseReference delBillcurrent = database.getReference("coffee-poly/bill_current/" + email);
                                            delBillcurrent.removeValue(new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    Log.d(TAG, "Đã xóa bill hiện thời");
                                                    capNhatLichSuDatHang(time);
                                                    thongbao(time);
                                                    thongbaonhanvien(time);
                                                    alertDialog.dismiss();
                                                    tong_tien = 0;
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            btn_xac_nhan_huy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    DatabaseReference myBill = database.getReference("coffee-poly/bill_current/" + email);
                                    myBill.removeValue();
                                }
                            });
                            builder.setView(view1);
                            alertDialog = builder.create();
                            alertDialog.show();
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void thongbaonhanvien(String time) {
        Notify notify = new Notify();
        notify.setTime(time);
        notify.setContent("Có đơn mới : " + time);
        notify.setStatus(0);
        DatabaseReference reference = database.getReference("coffee-poly").child("notify").child("Staff_Ox3325").child(time);
        reference.setValue(notify);
    }

    private void thongbao(String time) {
        Notify notify = new Notify();
        notify.setTime(time);
        notify.setContent("Đơn hàng " + time + " đã đặt hàng thành công.");
        notify.setStatus(0);
        DatabaseReference reference = database.getReference("coffee-poly").child("notify").child(email).child(time);
        reference.setValue(notify);
    }

    private void capNhatLichSuDatHang(String time) {
        DatabaseReference reference = database.getReference("coffee-poly/history/" + email + "/" + time);
        History history = new History(time, 0);
        reference.setValue(history, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.d(TAG, "Cập nhật lịch sử thành công");
                xoaSanPhamTrongGioHang();
            }
        });
    }

    private void xoaSanPhamTrongGioHang() {
        for (int i = 0; i < listSPdel.size(); i++) {
            int pp = i;
            DatabaseReference delBillcurrent = database.getReference("coffee-poly/cart/" + email + "/" + listSPdel.get(i));
            delBillcurrent.removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    Log.d(TAG, "Đã xóa sản phẩm trong giỏ hàng " + listSPdel.get(pp));
                }
            });
        }

    }


    public void layDanhSachTinhTien() {
        DatabaseReference reference1 = database.getReference("coffee-poly/bill_current/" + email);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list1.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list1.add(dataSnapshot.getValue(Item_Bill.class));
                }
                if (list1.size() == 0) {
                    tv_cart_thong_ke.setText("");
                    tv_cart_tong_tien.setText("");
                    //-------------------------------------------
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_bill.setLayoutParams(lp);
                    return;
                }
                layDanhSachSanPham(list1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        0, 0);
                ln_bill.setLayoutParams(lp);
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                0, 0);
        ln_bill.setLayoutParams(lp);
    }

    private void layDanhSachSanPham(List<Item_Bill> list1) {
        List<Product> list2 = new ArrayList<>();
        DatabaseReference reference = database.getReference("coffee-poly/product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list2.add(dataSnapshot.getValue(Product.class));
                }
                soSanh(list1, list2);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void soSanh(List<Item_Bill> list1, List<Product> list2) {
        listSPdel = new ArrayList<>();
        listSPdel.clear();
        List<Product> list3 = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            for (int j = 0; j < list2.size(); j++) {
                if (list1.get(i).getId_product() == list2.get(j).getId()) {
                    list3.add(list2.get(j));
                    listSPdel.add(list1.get(i).getTime());
                }
            }
        }
        tong_tien = 0;
        thong_ke = "";
        for (int i = 0; i < list3.size(); i++) {
            tong_tien += list1.get(i).getQuantity() * list3.get(i).getPrice();
            thong_ke += list3.get(i).getName().replaceAll("Cà phê", "") + "  x" + list1.get(i).getQuantity() + "  *" + list3.get(i).getPrice() + "K  = " + list1.get(i).getQuantity() * list3.get(i).getPrice() + "K\n";
        }
        ln_bill.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ln_bill.setLayoutParams(lp);
        tv_cart_thong_ke.setText(thong_ke);
        tv_cart_tong_tien.setText("Thanh toán: " + tong_tien + "K");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                cartRCVAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cartRCVAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        cartRecyclerView.setLayoutAnimation(layoutAnimationController);
    }

}