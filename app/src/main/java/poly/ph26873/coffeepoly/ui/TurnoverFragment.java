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
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.TurnoverRCVAdapter;
import poly.ph26873.coffeepoly.models.Turnover;

public class TurnoverFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_turnover, container, false);
    }

    private EditText edt_thang, edt_nam;
    private ImageButton ib_giam_thang, ib_tang_thang, ib_giam_nam, ib_tang_nam;
    private static final String TAG = "zzz";
    private RecyclerView recyclerView;
    private TurnoverRCVAdapter adapter;
    private TextView tv_doanh_thu_tkb;
    private boolean isFirst = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
        getListThongKeSanPham();
    }


    private void initUi(View view) {
        edt_thang = view.findViewById(R.id.edt_thang1);
        edt_nam = view.findViewById(R.id.edt_nam1);
        ib_giam_thang = view.findViewById(R.id.ib_giam_thang1);
        ib_tang_thang = view.findViewById(R.id.ib_tang_thang1);
        ib_giam_nam = view.findViewById(R.id.ib_giam_nam1);
        ib_tang_nam = view.findViewById(R.id.ib_tang_nam1);
        recyclerView = view.findViewById(R.id.tkbRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setHasFixedSize(true);
        tv_doanh_thu_tkb = view.findViewById(R.id.tv_doanh_thu_tkb);
        adapter = new TurnoverRCVAdapter(getContext());
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
        List<Turnover> list = new ArrayList<>();
        String month11 = edt_thang.getText().toString().trim().replace(".", "");
        String year11 = edt_nam.getText().toString().trim().replace(".", "");
        if(month11.length()==1){
            month11="0"+month11;
        }
        String key = month11 + "_" + year11;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("turnover_bill").child(key);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Turnover turnover = snapshot.getValue(Turnover.class);
                if (turnover != null) {
                    list.add(turnover);
                }
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
                Turnover turnover = snapshot.getValue(Turnover.class);
                if (turnover == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (turnover.getId() == list.get(i).getId()) {
                        list.set(i, turnover);
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

    private void showTurnover(List<Turnover> list) {
        if (list.size() == 0) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    0, 0);
            tv_doanh_thu_tkb.setLayoutParams(lp);
        } else {
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv_doanh_thu_tkb.setLayoutParams(lp1);
            int dt = 0;
            for (Turnover turnover : list) {
                dt += turnover.getTotal();
            }
            tv_doanh_thu_tkb.setText("Doanh thu: " + dt + "K");
        }
    }

    private void setAL(){
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }
}