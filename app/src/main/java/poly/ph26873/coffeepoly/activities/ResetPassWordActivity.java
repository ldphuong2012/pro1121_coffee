package poly.ph26873.coffeepoly.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class ResetPassWordActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private TextInputLayout til_rs_email;
    private EditText edt_rs_email;
    private Button btn_send_code;
    private TextView tv_rs_mess;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    private LinearLayout ln_internet_rs;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass_word);
        Log.d(TAG, "------------ResetPassWordActivity----------------");
        initUi();
        SendCode();
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_rs.setVisibility(View.INVISIBLE);
                } else {
                    ln_internet_rs.setVisibility(View.VISIBLE);
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }


    @SuppressLint("SetTextI18n")
    private void SendCode() {
        btn_send_code.setOnClickListener(v -> {
            String email = edt_rs_email.getText().toString().trim();
            if (email.isEmpty()) {
                til_rs_email.setError("Không được để trống trường này!");
                edt_rs_email.requestFocus();
                return;
            }
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                til_rs_email.setError("Email không đúng định dạng!");
                edt_rs_email.requestFocus();
                edt_rs_email.setSelection(email.length());
                return;
            }
            if (ListData.listUser.isEmpty()) {
                Toast.makeText(this, "Hãy thử lại sau", Toast.LENGTH_SHORT).show();
                return;
            }
            int a = 0;
            for (int i = 0; i < ListData.listUser.size(); i++) {
                if (edt_rs_email.getText().toString().trim().toLowerCase().equals(ListData.listUser.get(i).getEmail())) {
                    a++;
                    break;
                }
            }
            if (a == 0) {
                Toast.makeText(this, "Email không tồn tại trên hệ thống", Toast.LENGTH_SHORT).show();
                return;
            }
            til_rs_email.setError("");
            edt_rs_email.clearFocus();
            if (MyReceiver.isConnected == false) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                return;
            }
            progressDialog.show();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    tv_rs_mess.setVisibility(View.VISIBLE);
                    tv_rs_mess.setTextColor(Color.BLACK);
                    tv_rs_mess.setText("Chúng tôi đã gửi yêu cầu thiết lập lại mật khẩu \n qua email " + edt_rs_email.getText().toString());
                        } else {
                            tv_rs_mess.setVisibility(View.VISIBLE);
                            tv_rs_mess.setText("Đã có lỗi xảy ra khi gửi mã thiết lập lại mật khẩu.\nHãy kiểm tra lại email " + edt_rs_email.getText().toString());
                            tv_rs_mess.setTextColor(Color.RED);
                            til_rs_email.setError("Kiểm tra email!");
                            edt_rs_email.requestFocus();
                            edt_rs_email.setSelection(email.length());
                        }
                    });

        });
    }

    private void initUi() {
        ln_internet_rs = findViewById(R.id.ln_internet_rs);
        til_rs_email = findViewById(R.id.til_rs_email);
        edt_rs_email = findViewById(R.id.edt_rs_email);
        tv_rs_mess = findViewById(R.id.tv_rs_mess);
        btn_send_code = findViewById(R.id.btn_send_code);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Đang kiểm tra...");
        Intent intent = getIntent();
        String email_rs = intent.getStringExtra("email_rs");
        if (email_rs != null) {
            if (VALID_EMAIL_ADDRESS_REGEX.matcher(email_rs).find()) {
                edt_rs_email.setText(email_rs);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("em", edt_rs_email.getText().toString().trim());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            edt_rs_email.setText(savedInstanceState.getString("em"));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}