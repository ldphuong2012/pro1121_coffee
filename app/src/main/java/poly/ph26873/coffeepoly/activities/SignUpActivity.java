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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "zzz";
    private EditText edtEmail, edtPass;
    private Button btnSignUp;
    private TextView tvSignIn;
    private TextInputLayout til_email1, til_pass1;
    private FirebaseAuth mAuth;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@gmail.com$", Pattern.CASE_INSENSITIVE);
    private ProgressDialog progressDialog;
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_USER = "user";
    private LinearLayout ln_internet_su;
    private BroadcastReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Log.d(TAG, "---------SignUpActivity------------- ");
        initUi();
        signInAccount();
        changeToSignUp();
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_su.setVisibility(View.INVISIBLE);
                } else {
                    ln_internet_su.setVisibility(View.VISIBLE);
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }

    private void changeToSignUp() {
        tvSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void signInAccount() {
        btnSignUp.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim().toLowerCase();
            String password = edtPass.getText().toString().trim();
            if (email.length() == 0) {
                til_email1.setError("Không được để trống trường này!");
                edtEmail.requestFocus();
                return;
            }
            if (!VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
                til_email1.setError("Email sai định dạng!");
                edtEmail.requestFocus();
                edtEmail.setSelection(email.length());
                return;
            }
            til_email1.setError("");
            edtEmail.clearFocus();
            if (password.length() < 6) {
                til_pass1.setError("Mật khẩu phải nhiều hơn 5 kí tự!");
                edtPass.requestFocus();
                return;
            }
            til_pass1.setError("");
            edtPass.clearFocus();
            if (MyReceiver.isConnected==false) {
                Toast.makeText(this, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                return;
            }
            progressDialog.setTitle("Đang tiến hành tạo tài khoản...");
            progressDialog.show();
            mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            String email1 = email.replaceAll("@gmail.com", "");
                            User user = new User(email1, email1, 18, email, "Nam", "Trống", "Trống", "https://firebasestorage.googleapis.com/v0/b/coffepoly-f7e3b.appspot.com/o/avatar.jpg?alt=media&token=131ad1fb-e9c5-49e6-a2b8-429955b12588", 0);
                            CreateFrofileUser(user, email1);
                            putPassWord(email1, password);
                            Toast.makeText(SignUpActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("pass", password);
                                startActivity(intent);
                                finishAffinity();
                            }, 1000);
                        } else {
                            Log.d(TAG, "tao tai khoan that bai");
                            Toast.makeText(SignUpActivity.this, "Tài khoản đã tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void putPassWord(String email1, String password) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("pw_user").child(email1);
        reference.setValue(password);
    }

    public void CreateFrofileUser(User user, String email1) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newUser = database.getReference(TABLE_NAME).child(COL_USER).child(email1);
        newUser.setValue(user, (error, ref) -> {
            Log.d(TAG, "tạo user trên firebase thành công... ");
            DatabaseReference keyRef = database.getReference(TABLE_NAME).child("type_user").child(email1);
            keyRef.setValue(2);
        });
    }


    private void initUi() {
        ln_internet_su = findViewById(R.id.ln_internet_su);
        edtEmail = findViewById(R.id.edt_email1);
        edtPass = findViewById(R.id.edt_pass1);
        btnSignUp = findViewById(R.id.btn_signup);
        tvSignIn = findViewById(R.id.tv_signin);
        til_email1 = findViewById(R.id.til_email1);
        til_pass1 = findViewById(R.id.til_pass1);
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.setTitle("Đang tạo tài khoản...");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("us", edtEmail.getText().toString().trim());
        outState.putString("ps", edtPass.getText().toString().trim());
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
    }
}