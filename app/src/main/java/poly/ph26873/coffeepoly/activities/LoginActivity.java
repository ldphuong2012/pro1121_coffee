package poly.ph26873.coffeepoly.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtPass;
    private Button btnLogin;
    private TextView tv_reset_password;
    private TextInputLayout til_email, til_pass;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@gmail.com$", Pattern.CASE_INSENSITIVE);

    private int count = 0;
    private LinearLayout ln_internet_lg;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "---------LoginActivity------------- ");
        initUi();
        initAccount();
        checkUser();
        resetPassWord();
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_lg.setVisibility(View.INVISIBLE);
                } else {
                    ln_internet_lg.setVisibility(View.VISIBLE);
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }

    private void resetPassWord() {
        tv_reset_password.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ResetPassWordActivity.class);
            String email_rs = edtEmail.getText().toString().trim();
            if (!email_rs.isEmpty()) {
                intent.putExtra("email_rs", email_rs);
            }
            startActivity(intent);
        });
    }

    private void initAccount() {
        Intent intent = getIntent();
        String email1 = intent.getStringExtra("email");
        String pass1 = intent.getStringExtra("pass");
        if (email1 != null) {
            edtEmail.setText(email1);
        }
        if (pass1 != null) {
            edtPass.setText(pass1);
        }
    }

    private void checkUser() {
        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim().toLowerCase();
            String password = edtPass.getText().toString().trim();
            if (email.length() == 0) {
                til_email.setError("Email không được để trống");
                edtEmail.requestFocus();
                return;
            }
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                til_email.setError("Email không đúng định dạng");
                edtEmail.requestFocus();
                edtEmail.setSelection(email.length());
                return;
            }
            til_email.setError("");
            edtEmail.clearFocus();
            if (password.length() == 0) {
                til_pass.setError("Mật khẩu không được để trống");
                edtPass.requestFocus();
                return;
            }
            til_pass.setError("");
            edtPass.clearFocus();
            progressDialog.show();
            if (MyReceiver.isConnected==false) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            } else {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef1 = database.getReference("coffee-poly/bill_current/" + email.replaceAll("@gmail.com", ""));
                myRef1.removeValue();
                String chilgPath = email.replaceAll("@gmail.com", "");
                DatabaseReference readUserE = database.getReference("coffee-poly").child("user").child(chilgPath).child("enable");
                readUserE.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ListData.enable_user_current = snapshot.getValue(Integer.class);
                        if (ListData.enable_user_current == 1) {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Tài khoản của bạn đã bị vô hiệu hóa", Toast.LENGTH_SHORT).show();
                            ListData.type_user_current = -1;
                        } else {
                            mAuth = FirebaseAuth.getInstance();
                            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                                if (task.isSuccessful()) {
                                    putMK(email, password);
                                    DatabaseReference readUser = database.getReference("coffee-poly").child("type_user").child(chilgPath);
                                    readUser.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ListData.type_user_current = snapshot.getValue(Integer.class);
                                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finishAffinity();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

            }
        });

    }

    private void putMK(String email, String password) {
        String email1 = email.replaceAll("@gmail.com","");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("pw_user").child(email1);
        reference.setValue(password);
    }


    private void initUi() {
        ln_internet_lg = findViewById(R.id.ln_internet_lg);
        edtEmail = findViewById(R.id.edt_email);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.btn_login);
        TextView tvSignUp = findViewById(R.id.tv_signup);
        til_email = findViewById(R.id.til_email);
        til_pass = findViewById(R.id.til_pass);
        tv_reset_password = findViewById(R.id.tv_reset_password);
        tvSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("Đang đăng nhập");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Vui lòng đợi trong giây lát...");
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String us = edtEmail.getText().toString().trim();
        String ps = edtPass.getText().toString().trim();
        outState.putString("us", us);
        outState.putString("ps", ps);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            edtEmail.setText(savedInstanceState.getString("us"));
            edtPass.setText(savedInstanceState.getString("ps"));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        progressDialog.dismiss();
    }

}