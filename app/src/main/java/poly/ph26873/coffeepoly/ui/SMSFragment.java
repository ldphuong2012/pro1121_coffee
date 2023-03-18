package poly.ph26873.coffeepoly.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.ListMessagerRCVAdapter;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Notify_messager;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class SMSFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_s_m_s, container, false);
    }

    private LinearLayout ln_internet_sms;
    private BroadcastReceiver receiver = null;
    private RecyclerView recyclerView;
    private ListMessagerRCVAdapter adapter;
    private FirebaseDatabase database;
    private List<Notify_messager> list_nm;
    List<User> listUser;
    private String TAG = "zzz";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        LayListUserMess();
        broadcast();
    }


    private void LayListUserMess() {
        list_nm.clear();
        Log.d(TAG, "LayListUserMess: ---------------");
        DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify_messager notify_messager = snapshot.getValue(Notify_messager.class);
                if (notify_messager != null) {
                    int a = 0;
                    for (int i = 0; i < list_nm.size(); i++) {
                        if (list_nm.get(i).getId_user().equals(notify_messager.getId_user())) {
                            a++;
                            break;
                        }
                    }
                    if (a == 0) {
                        list_nm.add(notify_messager);
                        Log.d(TAG, "list_nm: " + list_nm.size());
                        sosanh();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Notify_messager notify_messager = snapshot.getValue(Notify_messager.class);
                if (notify_messager != null && !list_nm.isEmpty()) {
                    for (int i = 0; i < list_nm.size(); i++) {
                        if (notify_messager.getId_user().equals(list_nm.get(i).getId_user())) {
                            list_nm.set(i, notify_messager);
                            sosanh();
                            break;
                        }
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Notify_messager notify_messager = snapshot.getValue(Notify_messager.class);
                if (notify_messager != null && !list_nm.isEmpty()) {
                    for (int i = 0; i < list_nm.size(); i++) {
                        if (notify_messager.getId_user().equals(list_nm.get(i).getId_user())) {
                            list_nm.remove(i);
                            sosanh();
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
        Log.d(TAG, "LayListUserMess: end");
    }

    private void sosanh() {
        Log.d(TAG, "sosanh: ");
        if (list_nm.size() > 0) {
            for (int i = 0; i < list_nm.size(); i++) {
                if (i == 0) {
                    listUser.clear();
                }
                for (int j = 0; j < ListData.listUser.size(); j++) {
                    if (list_nm.get(i).getId_user().equals(ListData.listUser.get(j).getId())) {
                        listUser.add(ListData.listUser.get(j));
                    }
                }
            }
            Log.d(TAG, "listUser: " + listUser.size());
            adapter.setData(listUser);
            recyclerView.setAdapter(adapter);
        }
    }

    private void initUI(View view) {
        ln_internet_sms = view.findViewById(R.id.ln_internet_sms);
        recyclerView = view.findViewById(R.id.smsRecyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new ListMessagerRCVAdapter(getContext());
        database = FirebaseDatabase.getInstance();
        list_nm = new ArrayList<>();
        listUser = new ArrayList<>();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_sms.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_sms.setLayoutParams(lp);
                    LayListUserMess();
                } else {
                    ln_internet_sms.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_sms.setLayoutParams(lp);
                }
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}