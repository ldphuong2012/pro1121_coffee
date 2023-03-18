package poly.ph26873.coffeepoly.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.HistoryRCVAdapter;
import poly.ph26873.coffeepoly.models.History;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class HistoryFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    private RecyclerView hisRecyclerView;
    private HistoryRCVAdapter historyRCVAdapter;
    private List<History> list;
    private boolean isFirst = true;
    private LinearLayout ln_internet_his;
    private BroadcastReceiver receiver = null;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ln_internet_his = view.findViewById(R.id.ln_internet_his);
        hisRecyclerView = view.findViewById(R.id.hisRecyclerView);
        list = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        hisRecyclerView.setLayoutManager(manager);
        hisRecyclerView.setHasFixedSize(true);
        historyRCVAdapter = new HistoryRCVAdapter(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail().replaceAll("@gmail.com", "");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/history/" + email);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                History history = snapshot.getValue(History.class);
                if (history != null && history.getStatus() == 0) {
                    list.add(history);
                    Collections.reverse(list);
                    historyRCVAdapter.setData(list);
                    if (isFirst == true) {
                        setAL();
                        isFirst = false;
                    }
                    hisRecyclerView.setAdapter(historyRCVAdapter);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                History history = snapshot.getValue(History.class);
                if (list == null || history == null) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == history.getId()) {
                        list.remove(list.get(i));
                        Collections.reverse(list);
                        historyRCVAdapter.setData(list);
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

        broadcast();
    }

    private void broadcast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_his.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_his.setLayoutParams(lp);
                } else {
                    ln_internet_his.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_his.setLayoutParams(lp);
                }
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        hisRecyclerView.setLayoutAnimation(layoutAnimationController);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
}