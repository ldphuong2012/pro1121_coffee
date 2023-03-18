package poly.ph26873.coffeepoly.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Notify;
import poly.ph26873.coffeepoly.ui.BillDaGiaoFragment;
import poly.ph26873.coffeepoly.ui.BillFaildFragment;
import poly.ph26873.coffeepoly.ui.BillFragment;
import poly.ph26873.coffeepoly.ui.CartFragment;
import poly.ph26873.coffeepoly.ui.FavouriteFragment;
import poly.ph26873.coffeepoly.ui.HistoryFragment;
import poly.ph26873.coffeepoly.ui.HomeFragment;
import poly.ph26873.coffeepoly.ui.ListUserFragment;
import poly.ph26873.coffeepoly.ui.ManagementFragment;
import poly.ph26873.coffeepoly.ui.PassWordFragment;
import poly.ph26873.coffeepoly.ui.ProductFragment;
import poly.ph26873.coffeepoly.ui.SMSFragment;
import poly.ph26873.coffeepoly.ui.SettingFragment;
import poly.ph26873.coffeepoly.ui.ShipingFragment;
import poly.ph26873.coffeepoly.ui.ThongKeSanPhamFragment;
import poly.ph26873.coffeepoly.ui.TopProductFragment;
import poly.ph26873.coffeepoly.ui.TurnoverFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private int count = 0;
    private ImageView img_avatar, imv_back_layout_header;
    private TextView tv_name, tv_email;
    private static final String TAG = "zzz";
    private final SettingFragment settingFragment = new SettingFragment();
    public static final int MY_REQUESTCODE = 511;
    public static int IDmenu = -1;
    private Intent intent;
    private ProgressDialog progressDialog;

    public static String tt;
    public static int idMain;
    public static Fragment fragment = null;
    private FrameLayout redCircle;
    private TextView countTextView;
    private int alertCount = 0;


    final private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            Intent intent = result.getData();
            if (intent == null) {
                return;
            }
            Uri uri = intent.getData();
            settingFragment.setmUri(uri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                settingFragment.setBitmapImageview(bitmap);
            } catch (IOException e) {
                Log.d(TAG, "set bitmap: error");
                e.printStackTrace();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Đang tải dữ liệu...");
        progressDialog.show();
        Log.d(TAG, "---------------MainActivity---------------");
        intent = getIntent();
        initUi();
        setSupportActionBar(toolbar);
        toolbarAddNav();
        showInfomationUser();
//        fix();
    }



    private void fix() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        for (int i = 0; i < ListData.listPrd.size(); i++) {
            DatabaseReference reference = database.getReference("coffee-poly").child("product").child(ListData.listPrd.get(i).getId()+"").child("status");
            reference.setValue(0);
        }
    }


    private void checkAccountType() {
        if (ListData.type_user_current == 2) {
            tt = "Trang chủ";
            fragment = new HomeFragment();
            IDmenu = R.id.nav_home;
            idMain = R.id.nav_home;
            navigationView.getMenu().findItem(R.id.nav_all_setting_1).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_all_setting_2).setVisible(false);
        } else if (ListData.type_user_current == 1) {
            navigationView.getMenu().findItem(R.id.nav_all_setting_1).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_all_setting_0).setVisible(false);
            replaceFragmemt(new ManagementFragment());
            hieuUngChecked(R.id.nav_order_management);
            showToolBar("Xác nhận đơn hàng");
            IDmenu = R.id.nav_order_management;
            idMain = R.id.nav_order_management;
            tt = "Xác nhận đơn hàng";
            fragment = new ManagementFragment();
        } else if (ListData.type_user_current == 0) {
//            navigationView.getMenu().findItem(R.id.nav_all_setting_2).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_all_setting_0).setVisible(false);
            replaceFragmemt(new TurnoverFragment());
            hieuUngChecked(R.id.nav_turnover);
            showToolBar("Thống kê doanh thu");
            IDmenu = R.id.nav_turnover;
            idMain = R.id.nav_turnover;
            tt = "Thống kê doanh thu";
            fragment = new TurnoverFragment();
        }
        progressDialog.dismiss();
        Log.d("aaa", "checkAccountType: " + ListData.type_user_current);
    }

    public void showInfomationUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        String name = user.getDisplayName();
        String email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        assert email != null;
        DatabaseReference reference = database.getReference("coffee-poly").child("user").child(email.replaceAll("@gmail.com", "")).child("image");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.getValue(String.class);
                Uri uri = Uri.parse(image);
                Log.d(TAG, "onDataChange: Uri " + uri.toString());
                if (isValidContextForGlide(MainActivity.this)==true) {
                    Glide.with(MainActivity.this).load(uri).error(Uri.parse("https://firebasestorage.googleapis.com/v0/b/coffepoly-f7e3b.appspot.com/o/avatar.jpg?alt=media&token=131ad1fb-e9c5-49e6-a2b8-429955b12588")).into(img_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (name != null && !name.trim().isEmpty()) {
            tv_name.setText(name);
        } else {
            name = email.replaceAll("@gmail.com", "");
            tv_name.setText(name);
        }
        tv_email.setText(email);
        Log.d(TAG, "name user: " + name);
        Log.d(TAG, "email user: " + email);
        checkAccountType();
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;

        }
        return true;
    }


    public void showToolBar(String title) {
        int max = title.length();
        SpannableString string = new SpannableString(title);
        string.setSpan(new RelativeSizeSpan(1.5f), 0, max, 0);
        string.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, max, 0);
        string.setSpan(new UnderlineSpan(), 0, max, 0);
        toolbar.setTitle(string);
    }

    private void initUi() {

        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navgation_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        img_avatar = navigationView.getHeaderView(0).findViewById(R.id.img_avatar);
        tv_name = navigationView.getHeaderView(0).findViewById(R.id.tv_name);
        tv_email = navigationView.getHeaderView(0).findViewById(R.id.tv_email);
        imv_back_layout_header = navigationView.getHeaderView(0).findViewById(R.id.imv_back_layout_header);

    }

    private void toolbarAddNav() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        String gotoFrg = intent.getStringExtra("goto");
        if (gotoFrg == null) {
            showToolBar("Trang chủ");
            navigationView.setCheckedItem(R.id.nav_home);
            IDmenu = R.id.nav_home;
            fragment = new HomeFragment();
            replaceFragmemt(fragment);
            navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        } else {
            if (gotoFrg.equals("cart")) {
                showToolBar("Đặt hàng");
                navigationView.setCheckedItem(R.id.nav_cart);
                IDmenu = R.id.nav_cart;
                fragment = new CartFragment();
                replaceFragmemt(fragment);
                navigationView.getMenu().findItem(R.id.nav_cart).setChecked(true);
            } else if (gotoFrg.equals("bill")) {
                showToolBar("Đơn hàng của bạn");
                navigationView.setCheckedItem(R.id.nav_bill);
                IDmenu = R.id.nav_bill;
                fragment = new BillFragment();
                replaceFragmemt(fragment);
                navigationView.getMenu().findItem(R.id.nav_bill).setChecked(true);
            } else {
                showToolBar("Lịch sử");
                navigationView.setCheckedItem(R.id.nav_history);
                IDmenu = R.id.nav_history;
                fragment = new HistoryFragment();
                replaceFragmemt(fragment);
                navigationView.getMenu().findItem(R.id.nav_history).setChecked(true);
            }

        }
        imv_back_layout_header.setOnClickListener(v -> closeNavigation());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "idu: " + id);
        idFragment(id);
        return false;
    }

    private void idFragment(int id) {
        switch (id) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                showToolBar("Trang chủ");
                closeNavigation();
                IDmenu = id;
                break;
            case R.id.nav_cart:
                fragment = new CartFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                showToolBar("Giỏ hàng");
                closeNavigation();
                IDmenu = id;
                break;

            case R.id.nav_bill:
                fragment = new BillFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                showToolBar("Đơn hàng của bạn");
                closeNavigation();
                IDmenu = id;
                break;
            case R.id.nav_favourite:
                fragment = new FavouriteFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Yêu thích");
                IDmenu = id;
                break;
            case R.id.nav_history:
                fragment = new HistoryFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Lịch sử");
                IDmenu = id;
                break;


            case R.id.nav_order_management:
                fragment = new ManagementFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Quản lí hóa đơn");
                IDmenu = id;
                break;

            case R.id.nav_order_management_scfl:
                fragment = new ShipingFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Đơn hàng đang giao");
                IDmenu = id;
                break;

            case R.id.nav_history_bill:
                fragment = new BillDaGiaoFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Đơn hàng thành công");
                IDmenu = id;
                break;

            case R.id.nav_bill_faild:
                fragment = new BillFaildFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Đơn hàng thất bại");
                IDmenu = id;
                break;
            case R.id.nav_sms:
                fragment = new SMSFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Tin nhắn");
                IDmenu = id;
                break;
            case R.id.nav_prd:
                fragment = new ProductFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Sản phẩm");
                IDmenu = id;
                break;


            case R.id.nav_turnover:
                fragment = new TurnoverFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Doanh thu");
                IDmenu = id;
                break;

            case R.id.nav_turnover_product:
                fragment = new ThongKeSanPhamFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thống kê sản phẩm");
                IDmenu = id;
                break;

            case R.id.nav_top_product:
                fragment = new TopProductFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Bảng xếp hạng sản phẩm");
                IDmenu = id;
                break;
            case R.id.nav_list_user:
                fragment = new ListUserFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Danh sách người dùng");
                IDmenu = id;
                break;
            case R.id.nav_setting:
                fragment = new SettingFragment();
                replaceFragmemt(settingFragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thiết lập tài khoản");
                IDmenu = id;
                break;
            case R.id.nav_password:
                fragment = new PassWordFragment();
                replaceFragmemt(fragment);
                hieuUngChecked(id);
                closeNavigation();
                showToolBar("Thay đổi mật khẩu");
                IDmenu = id;
                break;

            case R.id.nav_logout:
                hieuUngChecked(id);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Bạn muốn đăng xuất tài khoản?");
                builder.setMessage("Hãy nhấn đắng xuất.");
                builder.setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Toast.makeText(MainActivity.this, "Logout account", Toast.LENGTH_SHORT).show();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> {
                    closeNavigation();
                    hieuUngChecked(IDmenu);
                });
                builder.setCancelable(false);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;

        }
    }

    public void hieuUngChecked(int id) {
        int[] mang = {R.id.nav_prd, R.id.nav_sms, R.id.nav_list_user, R.id.nav_turnover_product, R.id.nav_order_management_scfl, R.id.nav_home, R.id.nav_cart, R.id.nav_favourite, R.id.nav_history, R.id.nav_setting, R.id.nav_logout, R.id.nav_bill, R.id.nav_turnover, R.id.nav_top_product, R.id.nav_order_management, R.id.nav_history_bill, R.id.nav_bill_faild};
        for (int j : mang) {
            navigationView.getMenu().findItem(j).setChecked(id == j);
        }
    }

    public void replaceFragmemt(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).addToBackStack("back");
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed() {
        count++;
        closeNavigation();
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

    private void closeNavigation() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select picture"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUESTCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Hãy cấp quyền truy cập bộ nhớ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notify, menu);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String em = Objects.requireNonNull(user.getEmail()).replace("@gmail.com", "");
        List<Notify> list = new ArrayList<>();
        alertCount = 0;
        if (ListData.type_user_current == 2) {
            DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child(em);
            layData(myRef1, list, menu);
        } else {
            DatabaseReference myRef1 = database.getReference("coffee-poly").child("notify").child("Staff_Ox3325");
            layData(myRef1, list, menu);
        }

        hieuUng(menu);
        return true;
    }

    private void layData(DatabaseReference myRef1, List<Notify> list, Menu menu) {
        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && notify.getStatus() == 0) {
                    list.add(notify);
                    alertCount = list.size();
                    hieuUng(menu);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify notify = snapshot.getValue(Notify.class);
                if (notify != null && !list.isEmpty()) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getTime().equals(notify.getTime())) {
                            if (notify.getStatus() == 1) {
                                list.remove(i);
                                alertCount = list.size();
                                hieuUng(menu);
                                break;
                            }
                        }
                    }
                }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notify) {
            Intent intent = new Intent(this, NotifycationActivity.class);
            startActivity(intent);
            alertCount = 0;
            hieuUngChuyenMan();
        }
        return super.onOptionsItemSelected(item);
    }

    public void hieuUngChuyenMan() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            overridePendingTransition(R.anim.next_enter, R.anim.next_exit);
        }
    }


    private void hieuUng(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.action_notify);
        if (alertMenuItem == null) {
            return;
        }
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();
        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        countTextView = rootView.findViewById(R.id.view_alert_count_textview);
        rootView.setOnClickListener(v -> onOptionsItemSelected(alertMenuItem));
        updateAlertIcon();
    }

    private void updateAlertIcon() {
        if (0 < alertCount && alertCount < 10) {
            countTextView.setText(String.valueOf(alertCount));
        } else {
            countTextView.setText("");
        }
        redCircle.setVisibility((alertCount > 0) ? VISIBLE : GONE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("id", IDmenu);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            idFragment(savedInstanceState.getInt("id"));
        }

    }

}