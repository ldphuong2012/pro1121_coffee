package poly.ph26873.coffeepoly.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.DetailTurnoverRCVAdapter;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;


public class ThongKeSanPhamFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_thong_ke_san_pham, container, false);
    }


    private EditText edt_thang, edt_nam;
    private ImageButton ib_giam_thang, ib_tang_thang, ib_giam_nam, ib_tang_nam;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";
    private RecyclerView recyclerView;
    private DetailTurnoverRCVAdapter adapter;
    private TextView tv_doanh_thu_tksp;
    private boolean isFirst = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        database = FirebaseDatabase.getInstance();
        initUi(view);
        getListThongKeSanPham();
    }


    private void initUi(View view) {
        recyclerView = view.findViewById(R.id.tkspRecyclerView);
        tv_doanh_thu_tksp = view.findViewById(R.id.tv_doanh_thu_tksp);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);
        adapter = new DetailTurnoverRCVAdapter(getContext());
        edt_thang = view.findViewById(R.id.edt_thang);
        ib_giam_thang = view.findViewById(R.id.ib_giam_thang);
        edt_nam = view.findViewById(R.id.edt_nam);
        ib_tang_thang = view.findViewById(R.id.ib_tang_thang);
        ib_giam_nam = view.findViewById(R.id.ib_giam_nam);
        ib_tang_nam = view.findViewById(R.id.ib_tang_nam);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
        String time = simpleDateFormat.format(calendar.getTime());
        String month = time.substring(0, 2);
        String year = time.substring(3, 7);
        edt_thang.setText(month);
        edt_nam.setText(year);
        edt_thang.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edt_thang.getText().toString().trim().isEmpty()) {
                    int thang;
                    try {
                        thang = Integer.parseInt(String.valueOf(s));
                        if (thang > 12) {
                            edt_thang.setTextColor(Color.RED);
                        } else {
                            edt_thang.setTextColor(Color.BLACK);
                        }
                    } catch (Exception e) {
                        edt_thang.setTextColor(Color.RED);
                    }
                } else {
                    edt_thang.setText(month);
                    edt_thang.setTextColor(Color.RED);
                }
                getListThongKeSanPham();
            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_thang.clearFocus();
            }
        });
        ib_giam_thang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_thang.getText().toString().trim().isEmpty()) {
                    int thang = Integer.parseInt(edt_thang.getText().toString().trim());
                    if (thang > 1) {
                        thang--;
                        edt_thang.setText(thang + "");
                    } else {
                        edt_thang.setText("12");
                    }
                } else {
                    edt_thang.setText("12");
                }
                getListThongKeSanPham();
            }
        });
        ib_tang_thang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_thang.getText().toString().trim().isEmpty()) {
                    int thang = Integer.parseInt(edt_thang.getText().toString().trim());
                    if (thang < 12) {
                        thang++;
                        edt_thang.setText(thang + "");
                    } else {
                        edt_thang.setText("1");
                    }
                } else {
                    edt_thang.setText("1");
                }
            }

        });
        edt_nam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edt_nam.getText().toString().trim().isEmpty()) {
                    int nam;
                    try {
                        nam = Integer.parseInt(String.valueOf(s));
                        if (nam < 1000) {
                            edt_thang.setTextColor(Color.RED);
                        } else {
                            edt_thang.setTextColor(Color.BLACK);
                        }

                    } catch (Exception e) {
                        edt_nam.setTextColor(Color.RED);
                    }

                } else {
                    edt_nam.setTextColor(Color.RED);
                }
                getListThongKeSanPham();
            }

            @Override
            public void afterTextChanged(Editable s) {
                edt_nam.clearFocus();
            }
        });
        ib_giam_nam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_nam.getText().toString().trim().isEmpty()) {
                    int nam = Integer.parseInt(edt_nam.getText().toString().trim());
                    if (nam > 1) {
                        nam--;
                        edt_nam.setText(nam + "");
                        edt_nam.setTextColor(Color.BLACK);
                    } else {
                        edt_nam.setText(year);
                    }
                } else {
                    edt_nam.setText(year);
                }
                getListThongKeSanPham();
            }
        });
        ib_tang_nam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_nam.getText().toString().trim().isEmpty()) {
                    int nam = Integer.parseInt(edt_nam.getText().toString().trim());
                    nam++;
                    edt_nam.setText(nam + "");
                    edt_nam.setTextColor(Color.BLACK);
                } else {
                    edt_nam.setText(year);
                }
                getListThongKeSanPham();
            }
        });
        edt_thang.clearFocus();
    }

    private void getListThongKeSanPham() {
        List<QuantitySoldInMonth> list = new ArrayList<>();
        String month11 = edt_thang.getText().toString().trim().replace(".", "");
        String year11 = edt_nam.getText().toString().trim().replace(".", "");
        if(month11.length()==1){
            month11="0"+month11;
        }
        String key = month11 + "_" + year11;
        Log.d(TAG, "key: " + key);
        DatabaseReference reference = database.getReference("coffee-poly").child("turnover_product").child(key);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildAdded: ");
                QuantitySoldInMonth sold = snapshot.getValue(QuantitySoldInMonth.class);
                if (sold != null) {
                    list.add(sold);
                }
                Collections.sort(list, new Comparator<QuantitySoldInMonth>() {
                    @Override
                    public int compare(QuantitySoldInMonth o1, QuantitySoldInMonth o2) {
                        return o2.getQuantitySold() - o1.getQuantitySold();
                    }
                });
                adapter.setData(list);
                if (isFirst == true) {
                    setAL();
                    isFirst = false;
                }
                recyclerView.setAdapter(adapter);
                showTurnover(list);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildChanged: ");
                QuantitySoldInMonth sold = snapshot.getValue(QuantitySoldInMonth.class);
                if (sold == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (sold.getId_Product() == list.get(i).getId_Product()) {
                        list.set(i, sold);
                        Collections.sort(list, new Comparator<QuantitySoldInMonth>() {
                            @Override
                            public int compare(QuantitySoldInMonth o1, QuantitySoldInMonth o2) {
                                return o2.getQuantitySold() - o1.getQuantitySold();
                            }
                        });
                        adapter.setData(list);
                        break;
                    }
                }
                showTurnover(list);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "onChildRemoved: ");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG, "onChildMoved: ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: ");
            }
        });
        adapter.setData(list);
        showTurnover(list);
    }

    private void showTurnover(List<QuantitySoldInMonth> list) {
        if (list.size() == 0) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, 0);
            tv_doanh_thu_tksp.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_doanh_thu_tksp.setLayoutParams(lp1);
            int dt = 0;
            for (QuantitySoldInMonth sold : list) {
                for (int i = 0; i < ListData.listPrd.size(); i++) {
                    if (sold.getId_Product() == ListData.listPrd.get(i).getId()) {
                        dt += sold.getQuantitySold() * ListData.listPrd.get(i).getPrice();
                    }
                }
            }
            tv_doanh_thu_tksp.setText("Doanh thu: " + dt + "K");
        }
    }
    private void setAL(){
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }
}