package poly.ph26873.coffeepoly.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.MessagerRCVAdapter;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Message;
import poly.ph26873.coffeepoly.models.Notify_messager;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class MessagerActivity extends AppCompatActivity {
    private ImageView imv_back_layout_mess, imv_mess, imv_del_mess;
    private EditText edt_mess;
    private RecyclerView recyclerView;
    private List<Message> list;
    private String id;
    private String id_user;
    private LinearLayout ln_internet_me;
    private BroadcastReceiver receiver = null;
    private FirebaseDatabase database;
    private MessagerRCVAdapter adapter;
    private TextView tv_connect_nv;
    private int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messager);
        database = FirebaseDatabase.getInstance();
        id = FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", "");
        initUi();
        back();
        showListMess();
        if (ListData.type_user_current != 2) {
            DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
            reference.setValue(3);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 0);
            tv_connect_nv.setVisibility(View.INVISIBLE);
            tv_connect_nv.setLayoutParams(lp1);
        }
        sendMess();
        XoaTinNhan();
        broadCast();
    }

    private void XoaTinNhan() {
        imv_del_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(MessagerActivity.this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MessagerActivity.this);
                builder.setCancelable(false);
                builder.setTitle("Xác nhận xóa đoạn chat?");
                builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog progressDialog = new ProgressDialog(MessagerActivity.this);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        DatabaseReference reference = database.getReference("coffee-poly").child("messager").child(id_user);
                        reference.removeValue(new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                                reference.setValue(1, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        progressDialog.dismiss();
                                    }
                                });

                            }
                        });
                    }
                });
                builder.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    private void broadCast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, 0);
                    ln_internet_me.setVisibility(View.INVISIBLE);
                    ln_internet_me.setLayoutParams(lp);
                    if(ListData.type_user_current==2){
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        tv_connect_nv.setVisibility(View.VISIBLE);
                        tv_connect_nv.setLayoutParams(lp1);
                    }
                } else {
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_me.setVisibility(View.VISIBLE);
                    ln_internet_me.setLayoutParams(lp);
                    if(ListData.type_user_current==2){
                        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT, 0);
                        tv_connect_nv.setVisibility(View.INVISIBLE);
                        tv_connect_nv.setLayoutParams(lp1);
                    }
                }
                DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int s;
                        try {
                            s = snapshot.getValue(Integer.class);
                            if (s == 3) {
                                tv_connect_nv.setText("Đã kết nối với nhân viên thành công");
                            } else {
                                tv_connect_nv.setText("Đang kết nối với nhân viên");
                            }
                        } catch (Exception e) {

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };

        registerReceiver(receiver, intentFilter);
    }

    private void showListMess() {
        Intent intent = getIntent();
        id_user = intent.getStringExtra("id_user");
        if (id_user != null && id_user.length() > 0) {
            DatabaseReference reference = database.getReference("coffee-poly").child("messager").child(id_user);
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null) {
                        list.add(message);
                        adapter.setData(list);
                        recyclerView.setAdapter(adapter);
                        recyclerView.scrollToPosition(list.size() - 1);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null && !list.isEmpty()) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getTime() == message.getTime()) {
                                list.remove(i);
                                adapter.setData(list);
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
    }

    private void sendMess() {
        edt_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKeybroad();
            }
        });
        imv_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(MessagerActivity.this, "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (edt_mess.getText().toString().trim().isEmpty()) {
                    edt_mess.requestFocus();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy kk:mm:ss");
                String time = simpleDateFormat.format(calendar.getTime());
                Message message = new Message(id, edt_mess.getText().toString(), time, ListData.type_user_current);
                DatabaseReference reference = database.getReference("coffee-poly").child("messager").child(id_user).child(time);
                reference.setValue(message, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        edt_mess.setText("");
                        edt_mess.clearFocus();
                        a++;
                        if (ListData.type_user_current == 2) {
                            Notify_messager notify_messager = new Notify_messager(id_user, 0);
                            DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id);
                            reference1.setValue(notify_messager);
                        } else {
                            Notify_messager notify_messager = new Notify_messager(id_user, 2);
                            DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id_user);
                            reference1.setValue(notify_messager);
                        }

                    }
                });
            }
        });
    }

    private void checkKeybroad() {
        final View activityRootView = findViewById(R.id.root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                activityRootView.getWindowVisibleDisplayFrame(rect);
                int height = activityRootView.getRootView().getHeight() - rect.height();
                if (height > 0.25 * activityRootView.getRootView().getHeight()) {
                    if (list.size() > 0) {
                        recyclerView.scrollToPosition(list.size() - 1);
                        activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            }
        });

    }


    private void back() {
        imv_back_layout_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initUi() {
        imv_back_layout_mess = findViewById(R.id.imv_back_layout_mess);
        imv_del_mess = findViewById(R.id.imv_del_mess);
        tv_connect_nv = findViewById(R.id.tv_connect_nv);
        imv_mess = findViewById(R.id.imv_mess);
        edt_mess = findViewById(R.id.edt_mess);
        ln_internet_me = findViewById(R.id.ln_internet_me);
        recyclerView = findViewById(R.id.meRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new MessagerRCVAdapter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if (a == 0) {
            if (ListData.type_user_current != 2) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                        reference1.setValue(1);
                    }
                }, 20000);
            } else {
                DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                reference1.setValue(1);
            }
        } else {
            if (ListData.type_user_current != 2) {
                DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                reference1.setValue(2);
            } else {
                DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id_user).child("status");
                reference1.setValue(0);
            }

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("mess", edt_mess.getText().toString().trim());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            edt_mess.setText(savedInstanceState.getString("mess"));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
//        if (ListData.type_user_current == 2) {
//            DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(id).child("status");
//            reference1.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    int sta;
//                    try {
//                        sta = snapshot.getValue(Integer.class);
//                        if (sta == 1) {
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Calendar calendar = Calendar.getInstance();
//                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy kk:mm:ss");
//                                    String time = simpleDateFormat.format(calendar.getTime());
//                                    Message message = new Message("nhanvien1", "Xin chào, bạn cần giúp đỡ?", time, 1);
//                                    DatabaseReference reference = database.getReference("coffee-poly").child("messager").child(id_user).child(time);
//                                    reference.setValue(message);
//                                }
//                            }, 2000);
//                        }
//                    } catch (Exception e) {
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//        }
    }
}