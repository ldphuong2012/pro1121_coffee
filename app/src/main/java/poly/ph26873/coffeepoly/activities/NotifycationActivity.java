package poly.ph26873.coffeepoly.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.NotifyRCVAdapter;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Notify;

public class NotifycationActivity extends AppCompatActivity {

    private int count = 0;
    private RecyclerView recyclerView;
    private List<Notify> list;
    private static String em;
    private FirebaseDatabase database;
    private static boolean isFirts = true;
    private static final String TAG = "zzz";
    private NotifyRCVAdapter adapter;
    private int New = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation);
        list = new ArrayList<>();
        if (ListData.type_user_current == 2) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            assert user != null;
            em = Objects.requireNonNull(user.getEmail()).replace("@gmail.com", "");
        } else {
            em = "Staff_Ox3325";
        }
        back();
        showListNotifyCation();
        delete();
    }

    private void delete() {
        ImageView imageView = findViewById(R.id.imv_del_layout_notify);
        imageView.setOnClickListener(v -> {
            DatabaseReference reference = database.getReference("coffee-poly").child("notify").child(em);
            reference.removeValue();
        });
        ImageView imv_sort_layout_notify = findViewById(R.id.imv_sort_layout_notify);
        imv_sort_layout_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (New != 0) {
                    chuyenListdaXem();
                    showListNotifyCation();
                    New = 0;
                }
                Collections.reverse(list);
                adapter.setData(list);
            }
        });
    }


    private void showListNotifyCation() {
        recyclerView = findViewById(R.id.notiRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        adapter = new NotifyRCVAdapter(this);
        database = FirebaseDatabase.getInstance();
        if (ListData.type_user_current == 2) {
            DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child(em);
            layData(myRef1);
        } else {
            DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child("Staff_Ox3325");
            layData(myRef1);
        }
        adapter.setData(list);
        recyclerView.setAdapter(adapter);
        setAL();
    }

    private void layData(DatabaseReference myRef1) {
        list.clear();
        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null) {
                    list.add(notify);
                    if (notify.getStatus() == 0) {
                        Collections.sort(list, (o1, o2) -> o1.getStatus() - o2.getStatus());
                        New++;
                    }
                    adapter.setData(list);
                    recyclerView.setAdapter(adapter);
                    if (isFirts) {
                        setAL();
                        isFirts = false;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (notify.getTime().equals(list.get(i).getTime())) {
                            list.remove(i);
                            adapter.setData(list);
                            recyclerView.setAdapter(adapter);
                            setAL();
                            break;
                        }
                    }
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void back() {
        ImageView imageView = findViewById(R.id.imv_back_layout_notify);
        imageView.setOnClickListener(v -> {
            chuyenListdaXem();
            Mfinish();
        });
    }

    public void Mfinish() {
        finish();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            overridePendingTransition(R.anim.prev_enter, R.anim.prev_exit);
        }
    }

    @Override
    public void onBackPressed() {
        count++;
        if (count < 2) {
            Toast.makeText(getApplicationContext(), "Nhấn 2 lần để thoát", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            handler.postDelayed(() -> count = 0, 500);
        } else {
            finishAffinity();
            System.exit(0);
            super.onBackPressed();
        }
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        chuyenListdaXem();
    }

    private void chuyenListdaXem() {
        if (ListData.type_user_current == 2) {
            setSatus();
        } else {
            setSatus1();
        }
    }

    private void setSatus1() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() == 0) {
                DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child("Staff_Ox3325").child(list.get(i).getTime()).child("status");
                myRef1.setValue(1);
            }
        }
    }

    private void setSatus() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getStatus() == 0) {
                DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child(em).child(list.get(i).getTime()).child("status");
                myRef1.setValue(1);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
    }
}