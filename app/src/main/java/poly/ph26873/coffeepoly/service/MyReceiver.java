package poly.ph26873.coffeepoly.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.SplashActivity;
import poly.ph26873.coffeepoly.listData.ListData;

public class MyReceiver extends BroadcastReceiver {
    private int a = 0;
    public static boolean isConnected = true;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (isNetWorkAvailabe(context)==true) {
                isConnected = true;
                if (a != 0) {
                    Toast.makeText(context, "Internet connected", Toast.LENGTH_SHORT).show();
                    a = 0;
                }
//                kiemTraThongBao(context);
            } else {
                isConnected = false;
                a++;
                Toast.makeText(context, "Internet Disconnected", Toast.LENGTH_LONG).show();
            }
        }
        kiemTraEnable(context);

    }




    private void kiemTraEnable(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference readUserE = database.getReference("coffee-poly").child("user").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", "")).child("enable");
            readUserE.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListData.enable_user_current = snapshot.getValue(Integer.class);
                    if (ListData.enable_user_current == 1) {
                        Intent intent1 = new Intent(context, SplashActivity.class);
                        context.startActivity(intent1);
                        Toast.makeText(context, "Tài khoản của bạn đã bị vô hiệu hóa", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    public static void hienThongBao(Context context) {
        View view1 = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -850);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(view1);
        toast.show();
    }



    private boolean isNetWorkAvailabe(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
                NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                return wifi != null && wifi.isConnected() || (mobile != null && mobile.isConnected());
        }
    }
}