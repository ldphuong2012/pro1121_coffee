package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.ShipingRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class ShipingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_shiping, container, false);
    }


    private RecyclerView recyclerView;
    private ShipingRCVAdapter adapter;
    private FirebaseDatabase database;
    private static final String TAG = "zzz";
    private boolean isFirst = true;
    private LinearLayout ln_internet_ship;
    private BroadcastReceiver receiver = null;
    private SwipeRefreshLayout shipSwipeRefreshLayout;
    private List<Bill> listBill;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        shipSwipeRefreshLayout = view.findViewById(R.id.shipSwipeRefreshLayout);
        ln_internet_ship = view.findViewById(R.id.ln_internet_ship);
        recyclerView = view.findViewById(R.id.shipRecyclerView);
        adapter = new ShipingRCVAdapter(getContext());
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        database = FirebaseDatabase.getInstance();
        layListUser();
        progressDialog.dismiss();
        broadcast();
        shipSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_ship.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_ship.setLayoutParams(lp);
                    shipSwipeRefreshLayout.setRefreshing(true);
                    layListUser();
                    anSwipeReferences();
                } else {
                    ln_internet_ship.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_ship.setLayoutParams(lp);
                }
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }


    private void layListUser() {
        Log.d(TAG, "layListUser");
        List<User> listUser = new ArrayList<>();
        DatabaseReference refuser = database.getReference("coffee-poly").child("user");
        refuser.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    if (user.getEnable() == 0) {
                        if (listUser.isEmpty()) {
                            DatabaseReference r1 = database.getReference("coffee-poly").child("type_user").child(user.getId());
                            r1.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int type = snapshot.getValue(Integer.class);
                                    if (type == 2) {
                                        listUser.add(user);
                                        layListBill(listUser);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            int a = 0;
                            for (int i = 0; i < listUser.size(); i++) {
                                if (listUser.get(i).getId().equals(user.getId())) {
                                    a++;
                                    break;
                                }
                            }
                            if (a == 0) {
                                DatabaseReference r1 = database.getReference("coffee-poly").child("type_user").child(user.getId());
                                r1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int type = snapshot.getValue(Integer.class);
                                        if (type == 2) {
                                            listUser.add(user);
                                            layListBill(listUser);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                User user = snapshot.getValue(User.class);
                if (user.getEnable() == 1 && !listUser.isEmpty()) {
                    DatabaseReference r1 = database.getReference("coffee-poly").child("type_user").child(user.getId());
                    r1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int type = snapshot.getValue(Integer.class);
                            if (type == 2) {
                                for (int i = 0; i < listUser.size(); i++) {
                                    if (listUser.get(i).getId() == user.getId()) {
                                        listUser.remove(i);
                                        layListBill(listUser);
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            int type = snapshot.getValue(Integer.class);
                            if (type == 2) {
                                listUser.add(user);
                                layListBill(listUser);
                            }
                        }
                    });

                } else if (user.getEnable() == 0) {
                    DatabaseReference r1 = database.getReference("coffee-poly").child("type_user").child(user.getId());
                    r1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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

    private void layListBill(List<User> listUser) {
        Log.d(TAG, "listUser.size: " + listUser.size());
        DatabaseReference reference = database.getReference("coffee-poly/bill");
        listBill = new ArrayList<>();
            for (int i = 0; i < listUser.size(); i++) {
                reference.child(listUser.get(i).getId()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildAdded: ");
                        Bill bill = snapshot.getValue(Bill.class);
                        if (bill != null && bill.getStatus() == 0) {
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
                                Log.d(TAG, "list can tim: " + listBill.size());
                                adapter.setData(listBill);
                                if (isFirst == true) {
                                    setAL();
                                    isFirst = false;
                                }
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Log.d(TAG, "onChildChanged: ");
                        Bill bill = snapshot.getValue(Bill.class);
                        if (bill != null) {
                            Log.d(TAG, "bill.sttus: "+bill.getStatus());
                            if (bill.getStatus() == 0) {
                                int a = 0;
                                for (int j = 0; j < listBill.size(); j++) {
                                    if (listBill.get(j).getId().equals(bill.getId())) {
                                        a++;
                                        break;
                                    }
                                }
                                if(a==0){
                                    Log.d(TAG, "onChildChanged: loai 1");
                                    listBill.add(bill);
                                    Collections.reverse(listBill);
                                    adapter.setData(listBill);
                                }
                            } if (bill.getStatus() != 0) {
                                if(listBill.size()!=0){
                                    for (int j = 0; j < listBill.size(); j++) {
                                        if (listBill.get(j).getId().equals(bill.getId())) {
                                            Log.d(TAG, "onChildChanged: loai 2");
                                            listBill.remove(listBill.get(j));
                                            Collections.reverse(listBill);
                                            adapter.setData(listBill);
                                            break;
                                        }
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }

        });
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onRefresh() {
        if (listBill != null) {
            adapter.setData(listBill);
            setAL();
        }
        anSwipeReferences();
    }

    private void anSwipeReferences() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shipSwipeRefreshLayout.setRefreshing(false);
                if (MyReceiver.isConnected == false) {
                    Toast.makeText(getContext(), "Không có internet", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }
}