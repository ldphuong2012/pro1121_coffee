package poly.ph26873.coffeepoly.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.service.MyReceiver;
import poly.ph26873.coffeepoly.service.MyService;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "zzz";
    private MyReceiver myReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.d(TAG, "SplashActivity: ---------------------------");
        TextView tv_coofee_poly = findViewById(R.id.tv_coofee_poly);
        ObjectAnimator ob1 = ObjectAnimator.ofFloat(tv_coofee_poly, "rotation", 0f, 360f);
        ob1.setDuration(2000);
        ObjectAnimator ob2 = ObjectAnimator.ofFloat(tv_coofee_poly, "alpha", 0.3f, 1.1f);
        ob2.setDuration(2000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(ob1).with(ob2);
        animatorSet.start();
        Log.d(TAG, "--------------SplashActivity--------------");

        if (MyReceiver.isConnected == true) {
            serviceConnection();
            new Handler().postDelayed(this::nextActivity, 3000);
        } else {
            Toast.makeText(SplashActivity.this, "Hãy kiểm tra kết nối mạng", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
            builder.setTitle("Không có kết nối mạng");
            builder.setCancelable(false);
            builder.setPositiveButton("Thoát", (dialog, which) -> System.exit(0));
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void serviceConnection() {
        Intent intent = new Intent(SplashActivity.this, MyService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }else {
            startService(intent);
        }
    }




    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (ListData.type_user_current == -1 || ListData.enable_user_current != 0) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                return;
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String em = Objects.requireNonNull(user.getEmail()).replaceAll("@gmail.com", "");
            DatabaseReference myRef1 = database.getReference("coffee-poly/bill_current/" + em);
            myRef1.removeValue();
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "user: " + user.getEmail());
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myReceiver, intentFilter);
    }
}