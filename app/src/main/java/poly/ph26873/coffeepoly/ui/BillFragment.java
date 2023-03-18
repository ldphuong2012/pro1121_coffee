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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.BillRCVAdapter;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class BillFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bill, container, false);
    }

    private RecyclerView billRecyclerView;
    private BillRCVAdapter billRCVAdapter;
    private boolean isFirst = true;
    private LinearLayout ln_internet_bill;
    private BroadcastReceiver receiver = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("zzz", "-----------BillFragment-------------- ");
        billRecyclerView = view.findViewById(R.id.billRecyclerView);
        ln_internet_bill = view.findViewById(R.id.ln_internet_bill);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        billRecyclerView.setLayoutManager(manager);
        billRecyclerView.setHasFixedSize(true);
        billRCVAdapter = new BillRCVAdapter(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/bill/" + email);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Bill> bills = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Bill bill = dataSnapshot.getValue(Bill.class);
                    if (bill.getStatus() == 0 || bill.getStatus() == 1) {
                        bills.add(bill);
                    }
                }
                if (bills.size() > 0) {
                    Collections.reverse(bills);
                    billRCVAdapter.setData(bills);
                    if (isFirst == true) {
                        setAL();
                        isFirst = false;
                    }
                    billRecyclerView.setAdapter(billRCVAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_bill.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_bill.setLayoutParams(lp);
                } else {
                    ln_internet_bill.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_bill.setLayoutParams(lp);
                }
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        billRecyclerView.setLayoutAnimation(layoutAnimationController);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}