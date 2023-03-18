package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.TopProductRCVAdapter;
import poly.ph26873.coffeepoly.models.Product;


public class TopProductFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_product, container, false);
    }

    private RecyclerView topRecyclerView;
    private TopProductRCVAdapter topProductRCVAdapter;
    private List<Product> list;
    private boolean isFirst = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        topRecyclerView = view.findViewById(R.id.topRecyclerView);
        list = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        topRecyclerView.setLayoutManager(manager);
        topRecyclerView.setHasFixedSize(true);
        topProductRCVAdapter = new TopProductRCVAdapter(getContext());
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly/product");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    list.add(product);
                    Collections.sort(list, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return o2.getQuantitySold() - o1.getQuantitySold();
                        }
                    });
                    topProductRCVAdapter.setData(list);
                    if (isFirst == true) {
                        setAL();
                        isFirst = false;
                    }
                    topRecyclerView.setAdapter(topProductRCVAdapter);
                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
                    topRecyclerView.addItemDecoration(itemDecoration);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Product product = snapshot.getValue(Product.class);
                if (product == null || list.isEmpty()) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getId() == product.getId()) {
                        list.set(i, product);
                        topProductRCVAdapter.setData(list);
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

    private void setAL() {
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_animation);
        topRecyclerView.setLayoutAnimation(layoutAnimationController);
    }
}