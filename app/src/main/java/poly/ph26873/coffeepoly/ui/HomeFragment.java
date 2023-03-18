package poly.ph26873.coffeepoly.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.AllProductActivity;
import poly.ph26873.coffeepoly.activities.MessagerActivity;
import poly.ph26873.coffeepoly.adapter.BannerViewPagerAdapter;
import poly.ph26873.coffeepoly.adapter.HorizontalRCVAdapter;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Banner;
import poly.ph26873.coffeepoly.models.Notify_messager;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.service.MyReceiver;


public class HomeFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true); // Add this!
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    private List<Banner> listBanner;
    private final Handler handler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (viewPager.getCurrentItem() == listBanner.size() - 1) {
                viewPager.setCurrentItem(0);
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        }
    };
    private static final String TAG = "zzz";
    private static final String TABLE_NAME = "coffee-poly";
    private static final String COL_PRODUCT = "product";

    private RecyclerView recyclerView_rcm_product, mRecycerView_all_product;
    private FirebaseDatabase database;
    private ProgressDialog progressDialog;
    private TextView tv_home_see_all;
    private HorizontalRCVAdapter adapter;
    private List<Product> list_rcm_product;
    private LinearLayout ln_internet_home;
    private BroadcastReceiver receiver = null;
    private FloatingActionButton fab_mess;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUi(view);
        showBanner();
        database = FirebaseDatabase.getInstance();
        getList_rcm_product();
        broadCast();
        LienHe();
    }

    private void LienHe() {
        fab_mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessagerActivity.class);
                intent.putExtra("id_user",FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com",""));
                startActivity(intent);
            }
        });
    }

    private void broadCast() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (MyReceiver.isConnected == true) {
                    ln_internet_home.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            0, 0);
                    ln_internet_home.setLayoutParams(lp);
                } else {
                    ln_internet_home.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    ln_internet_home.setLayoutParams(lp);
                }
                kiemTraTinNhan();
                thongBaoNhanVien(context);
            }
        };

        getActivity().registerReceiver(receiver, intentFilter);
    }

    private void thongBaoNhanVien(Context context) {
        if (ListData.type_user_current != 2) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager");
            reference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Notify_messager notify_messager = snapshot.getValue(Notify_messager.class);
                    if (notify_messager != null && notify_messager.getStatus() == 0) {
                        MyReceiver.hienThongBao(context);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Notify_messager notify_messager = snapshot.getValue(Notify_messager.class);
                    if (notify_messager != null && notify_messager.getStatus() == 0) {
                        MyReceiver.hienThongBao(context);
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

    private void kiemTraTinNhan() {
        if (ListData.type_user_current == 2) {
            DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(FirebaseAuth.getInstance().getCurrentUser().getEmail().replaceAll("@gmail.com", "")).child("status");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int sta;
                    try {
                        sta = snapshot.getValue(Integer.class);
                        if (sta == 2) {
                            MyReceiver.hienThongBao(getContext());
                            fab_mess.setImageResource(R.drawable.messenger1);
                        } else {
                            fab_mess.setImageResource(R.drawable.messenger);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }


    private void getList_rcm_product() {
        DatabaseReference myProduct = database.getReference(TABLE_NAME).child(COL_PRODUCT);
        myProduct.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressDialog.dismiss();
                Product product = snapshot.getValue(Product.class);
                if (product != null) {
                    list_rcm_product.add(product);
                }
                if (list_rcm_product.size() == 0) {
                    tv_home_see_all.setVisibility(View.INVISIBLE);
                } else {
                    tv_home_see_all.setVisibility(View.VISIBLE);
                    tv_home_see_all.setOnClickListener(v -> {
                        Intent intent = new Intent(getContext(), AllProductActivity.class);
                        intent.putExtra("list", (Serializable) list_rcm_product);
                        startActivity(intent);
                    });
                    showRecommentProduct(list_rcm_product);
                    showAllProduct();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressDialog.dismiss();
                Product product = snapshot.getValue(Product.class);
                if (list_rcm_product.isEmpty() || product == null) {
                    return;
                }
                for (int i = 0; i < list_rcm_product.size(); i++) {
                    if (list_rcm_product.get(i).getId() == product.getId()) {
                        list_rcm_product.set(i, product);
                        showRecommentProduct(list_rcm_product);
                        showAllProduct();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }

    private void showAllProduct() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        mRecycerView_all_product.setLayoutManager(manager);
        mRecycerView_all_product.setHasFixedSize(true);
        Collections.shuffle(list_rcm_product);
        adapter.setData(list_rcm_product);
        mRecycerView_all_product.setAdapter(adapter);
    }

    private void showRecommentProduct(List<Product> list_rcm_product1) {
        LinearLayoutManager manager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, true);
        recyclerView_rcm_product.setLayoutManager(manager);
        recyclerView_rcm_product.setHasFixedSize(true);
        HorizontalRCVAdapter adapter1 = new HorizontalRCVAdapter(getContext());
        List<Product> listProductRecoomment = list_rcm_product1;
        Collections.sort(listProductRecoomment, new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getQuantitySold() - o2.getQuantitySold();
            }
        });
        List<Product> listrcm = new ArrayList<>();
        for (int i = 0; i < listProductRecoomment.size()*0.25; i++) {
            listrcm.add(listProductRecoomment.get(i));
        }

        adapter1.setData(listrcm);
        recyclerView_rcm_product.setAdapter(adapter1);
    }

    private void showBanner() {
        listBanner = getListBanner();
        BannerViewPagerAdapter pagerAdapter = new BannerViewPagerAdapter(listBanner);
        viewPager.setAdapter(pagerAdapter);
        circleIndicator.setViewPager(viewPager);
        handler.postDelayed(runnable, 2000);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void initUi(View view) {
        fab_mess = view.findViewById(R.id.fab_mess);
        fab_mess.setImageResource(R.drawable.messenger);
        ln_internet_home = view.findViewById(R.id.ln_internet_home);
        viewPager = view.findViewById(R.id.viewPager);
        circleIndicator = view.findViewById(R.id.circleIndicator);
        recyclerView_rcm_product = view.findViewById(R.id.mRecyclerView_rcm);
        mRecycerView_all_product = view.findViewById(R.id.mRecycerView_all_product);
        tv_home_see_all = view.findViewById(R.id.tv_home_see_all);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        adapter = new HorizontalRCVAdapter(getContext());
        list_rcm_product = new ArrayList<>();
        mRecycerView_all_product.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab_mess.hide();
                } else {
                    fab_mess.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private List<Banner> getListBanner() {
        List<Banner> list = new ArrayList<>();
        list.add(new Banner(R.drawable.bn_1));
        list.add(new Banner(R.drawable.bn2));
        list.add(new Banner(R.drawable.bn_3));
        list.add(new Banner(R.drawable.bn4));
        list.add(new Banner(R.drawable.bn5));
        return list;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }


    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }
}