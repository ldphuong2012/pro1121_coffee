package poly.ph26873.coffeepoly.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.ManagementRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class ManagementFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_management, container, false);
    }

    private RecyclerView maRecyclerView;
    private ManagementRCVAdapter managementRCVAdapter;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";
    private List<User> listUser = new ArrayList<>();
    private boolean isFirst = true;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Bill> listBill;
    private BroadcastReceiver receiver = null;
    private LinearLayout ln_internet_mana;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "-------------ManagementFragment-----------------");
        database = FirebaseDatabase.getInstance();
        maRecyclerView = view.findViewById(R.id.maRecyclerView);
        ln_internet_mana = view.findViewById(R.id.ln_internet_mana);
        swipeRefreshLayout = view.findViewById(R.id.manaSwipeRefreshLayout);
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        maRecyclerView.setLayoutManager(manager);
        maRecyclerView.setHasFixedSize(true);
        managementRCVAdapter = new ManagementRCVAdapter(getContext());
        layListUser();
        swipeRefreshLayout.setOnRefreshListener(this);
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_mana.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_mana.setLayoutParams(lp);
                    swipeRefreshLayout.setRefreshing(true);
                    anSwipeReferences();
                    layListBill();
                } else {
                    ln_internet_mana.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_mana.setLayoutParams(lp);
                }
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }


    private void layListUser() {
        listUser.clear();
        Log.d(TAG, "layListUser");
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    listUser.add(user);
                    layListBill();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user == null || listUser.isEmpty()) {
                    return;
                }
                for (int i = 0; i < listUser.size(); i++) {
                    if (listUser.get(i).getId() == user.getId()) {
                        listUser.set(i, user);
                        layListBill();
                        break;
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

    private void layListBill() {
        Log.d(TAG, "layListBill");
        listBill = new ArrayList<>();
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        for (int i = 0; i < listUser.size(); i++) {
            reference.child(listUser.get(i).getId()).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill != null && bill.getStatus() == 1) {
                        int a = 0;
                        if (listBill.size() > 0) {
                            for (int j = 0; j < listBill.size(); j++) {
                                if (listBill.get(j).getId().equals(bill.getId())) {
                                    a++;
                                    break;
                                }
                            }
                        }
                        if (a == 0) {
                            listBill.add(bill);
                            Collections.reverse(listBill);
                            managementRCVAdapter.setData(listBill);
                            Log.d(TAG, "listBill: " + listBill);
                            if (isFirst == true) {
                                setAL();
                                isFirst = false;
                            }
                            maRecyclerView.setAdapter(managementRCVAdapter);
                        }
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Bill bill = snapshot.getValue(Bill.class);
                    if (bill == null || listBill.isEmpty()) {
                        return;
                    }
                    for (int j = 0; j < listBill.size(); j++) {
                        if (listBill.get(j).getId() == bill.getId()) {
                            listBill.remove(listBill.get(j));
                            Collections.reverse(listBill);
                            managementRCVAdapter.setData(listBill);
                            break;
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
    }

    private void anSwipeReferences() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(getContext(), "Không có internet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        maRecyclerView.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onRefresh() {
        if (listBill != null) {
            managementRCVAdapter.setData(listBill);
            setAL();
        }
        anSwipeReferences();
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