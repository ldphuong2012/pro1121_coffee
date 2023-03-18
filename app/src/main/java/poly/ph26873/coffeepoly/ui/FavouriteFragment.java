package poly.ph26873.coffeepoly.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.HorizontalRCVAdapter;
import poly.ph26873.coffeepoly.models.Product;


public class FavouriteFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }

    private static final String TAG = "zzz";
    private List<Integer> list_id = new ArrayList<>();
    private List<Product> list_product = new ArrayList<>();
    private List<Product> list_product1 = new ArrayList<>();
    private FirebaseDatabase database;
    private RecyclerView mRecyclerView;
    private HorizontalRCVAdapter horizontalRCVAdapter;
    private DatabaseReference reference;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.ryc_fravorite);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        horizontalRCVAdapter = new HorizontalRCVAdapter(getContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String abc = user.getEmail().replaceAll("@gmail.com", "");
        Log.d(TAG, "abc: " + abc);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("coffee-poly/favorite/" + abc + "/list_id_product");
        layListId();

        //lay list product

        //--------------------------------

    }

    private void layListId() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_id.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    list_id.add(dataSnapshot.getValue(Integer.class));
                }
                Log.d(TAG, "list_id: " + list_id);
                layListProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void layListProduct() {
        DatabaseReference databaseReference = database.getReference("coffee-poly/product");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_product.clear();
                for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                    Product product = dataSnapshot1.getValue(Product.class);
                    list_product.add(product);
                }
                Log.d(TAG, "list_product: " + list_product);
                if (list_product.size() > 0) {
                    sosanh();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sosanh() {
        list_product1.clear();
        for (int i = 0; i < list_id.size(); i++) {
            for (int j = 0; j < list_product.size(); j++) {
                if (list_id.get(i) == list_product.get(j).getId()) {
                    list_product1.add(list_product.get(j));
                }
            }
        }
        Log.d(TAG, "list_product1: " + list_product1.size());

        horizontalRCVAdapter.setData(list_product1);
        mRecyclerView.setAdapter(horizontalRCVAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        layListId();
        Log.d(TAG, "onResume: ");
    }

}