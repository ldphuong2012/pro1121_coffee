package poly.ph26873.coffeepoly.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.adapter.HorizontalRCVAdapter;
import poly.ph26873.coffeepoly.models.Product;

public class AllProductActivity extends AppCompatActivity {

    private ImageView imv_back_layout_all_product;
    private RecyclerView recyclerView;
    private HorizontalRCVAdapter adapter;
    private FloatingActionButton fab_mess_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        initUi();
        back();
        showAllProduct();
        LienHe();
    }

    private void LienHe() {
        fab_mess_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllProductActivity.this, MessagerActivity.class);
                intent.putExtra("id_user", FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", ""));
                startActivity(intent);
            }
        });
    }

    private void showAllProduct() {
        List<Product> list;
        Intent intent = getIntent();
        list = (List<Product>) intent.getSerializableExtra("list");
        if (list.size() > 0) {
            adapter.setData(list);
            recyclerView.setAdapter(adapter);
        }
    }

    private void back() {
        imv_back_layout_all_product.setOnClickListener(v -> finish());
    }

    private void initUi() {
        Toolbar toolbar = findViewById(R.id.toolbarAP);
        setSupportActionBar(toolbar);
        fab_mess_all = findViewById(R.id.fab_mess_all);
        imv_back_layout_all_product = findViewById(R.id.imv_back_layout_all_product);
        recyclerView = findViewById(R.id.allRecyclerView);
        GridLayoutManager manager = new GridLayoutManager(AllProductActivity.this, 3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        adapter = new HorizontalRCVAdapter(AllProductActivity.this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab_mess_all.hide();
                } else {
                    fab_mess_all.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
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
        return true;
    }
}