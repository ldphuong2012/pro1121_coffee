package poly.ph26873.coffeepoly.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;
import poly.ph26873.coffeepoly.models.User;

public class MyService extends Service {
    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        All();
        return START_STICKY;
    }

    private void All() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM_yyyy");
        String month = simpleDateFormat.format(calendar.getTime());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        capNhatListProduct(database);
        capNhatListQuanProduct(database, month);
        layLoaiTaiKhoan(database);
        laydanhsachUser(database);
    }


    private void laydanhsachUser(FirebaseDatabase database) {
        DatabaseReference reference = database.getReference("coffee-poly").child("user");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    ListData.listUser.add(user);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void layLoaiTaiKhoan(FirebaseDatabase database) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String chilgPath = user.getEmail().replaceAll("@gmail.com", "");
            Log.d("zzz", "layLoaiTaiKhoan: " + chilgPath);
            DatabaseReference readUser = database.getReference("coffee-poly").child("type_user").child(chilgPath);
            readUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(Integer.class) != null) {
                        ListData.type_user_current = snapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            DatabaseReference readUserE = database.getReference("coffee-poly").child("user").child(chilgPath).child("enable");
            readUserE.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListData.enable_user_current = snapshot.getValue(Integer.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    //ham update
    private void capNhatListQuanProduct(FirebaseDatabase database, String month) {
        DatabaseReference refQuanPrd = database.getReference("coffee-poly").child("turnover_product").child(month);
        refQuanPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listQuanPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listQuanPrd.add(dataSnapshot.getValue(QuantitySoldInMonth.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void capNhatListProduct(FirebaseDatabase database) {
        DatabaseReference refPrd = database.getReference("coffee-poly").child("product");
        refPrd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListData.listPrd.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData.listPrd.add(dataSnapshot.getValue(Product.class));
                }
                Log.d("zzz", "ListData.listPrd: = " + ListData.listPrd.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        } else {
            stopSelf();
        }
    }
}