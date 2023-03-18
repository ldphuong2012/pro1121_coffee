package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailProductActivity;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;

public class CartRCVAdapter extends RecyclerView.Adapter<CartRCVAdapter.ItemBillHolder> implements Filterable {
    private Context context;
    private List<Item_Bill> list;
    private List<Item_Bill> listNew;
    private static final String TAG = "zzz";

    public CartRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Item_Bill> list) {
        this.list = list;
        this.listNew = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartRCVAdapter.ItemBillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_bill, parent, false);
        return new ItemBillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartRCVAdapter.ItemBillHolder holder, int position) {
        Item_Bill item_bill = list.get(position);
        if (item_bill != null && item_bill.getId_product() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String email = user.getEmail().replaceAll("@gmail.com", "");
            List<Product> products = new ArrayList<>();
            List<Item_Bill> itemBills = new ArrayList<>();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly/bill_current/" + email);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    itemBills.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        itemBills.add(dataSnapshot.getValue(Item_Bill.class));
                    }
                    if (itemBills.size() == 0) {
                        holder.chk_item_bill_selected.setChecked(false);
                        return;
                    }

                    for (int i = 0; i < itemBills.size(); i++) {
                        if (itemBills.get(i).getTime().equals(item_bill.getTime()) && itemBills.get(i).getId_product() == item_bill.getId_product() && itemBills.get(i).getQuantity() == item_bill.getQuantity()) {
                            if (item_bill.getTime().equals(itemBills.get(i).getTime())) {
                                holder.chk_item_bill_selected.setChecked(true);
                            } else {
                                holder.chk_item_bill_selected.setChecked(false);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference myRef = database.getReference("coffee-poly").child("product");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        products.add(dataSnapshot.getValue(Product.class));
                    }
                    for (int i = 0; i < products.size(); i++) {
                        if (products.get(i).getId() == item_bill.getId_product()) {
                            Product product = products.get(i);
                            Glide.with(context).load(Uri.parse(product.getImage())).error(R.color.red).into(holder.imv_item_bill_image);
                            holder.tv_item_bill_name.setText(products.get(i).getName());
                            holder.tv_item_bill_price.setText("Đơn giá: " + product.getPrice() + "K");
                            holder.tv_item_bill_total.setText("Tổng tiền: " + item_bill.getQuantity() * product.getPrice() + "K");
                            holder.tv_item_bill_quantity.setText(item_bill.getQuantity() + "");
                            DatabaseReference reference2 = database.getReference("coffee-poly/cart/" + email + "/" + item_bill.getTime() + "/quantity");
                            holder.imv_item_bill_remove.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int sl = Integer.parseInt(holder.tv_item_bill_quantity.getText().toString().trim());
                                    if (sl == 1) {
                                        Toast.makeText(context, "Số lượng ít nhất bằng 1", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    sl--;
                                    holder.tv_item_bill_quantity.setText(sl + "");
                                    holder.tv_item_bill_total.setText("Tổng tiền: " + sl * product.getPrice() + "K");
                                    reference2.setValue(sl);
                                    if (holder.chk_item_bill_selected.isChecked()) {
                                        DatabaseReference reference4 = database.getReference("coffee-poly/bill_current/" + email + "/" + item_bill.getTime() + "/quantity");
                                        reference4.setValue(sl);
                                    }
                                }
                            });
                            holder.imv_item_bill_add.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int sl = Integer.parseInt(holder.tv_item_bill_quantity.getText().toString().trim());
                                    sl++;
                                    holder.tv_item_bill_quantity.setText(sl + "");
                                    holder.tv_item_bill_total.setText("Tổng tiền: " + sl * product.getPrice() + "K");
                                    reference2.setValue(sl);
                                    if (holder.chk_item_bill_selected.isChecked()) {
                                        DatabaseReference reference4 = database.getReference("coffee-poly/bill_current/" + email + "/" + item_bill.getTime() + "/quantity");
                                        reference4.setValue(sl, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                notifyDataSetChanged();
                                            }
                                        });
                                    }
                                }
                            });

                            holder.chk_item_bill_selected.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DatabaseReference reference4 = database.getReference("coffee-poly/bill_current/" + email + "/" + item_bill.getTime());
                                    if (holder.chk_item_bill_selected.isChecked() == true) {
                                        reference4.setValue(item_bill, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Log.d(TAG, "đã lưu vào bill tạm thời với key " + item_bill.getTime());
                                            }
                                        });
                                    } else {
                                        reference4.removeValue(new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                Log.d(TAG, "đã xoá kỏi bill tạm thời với key " + item_bill.getTime());
                                            }
                                        });
                                    }
                                }
                            });

                            holder.imv_item_bill_image.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, DetailProductActivity.class);
                                    intent.putExtra("product", product);
                                    context.startActivity(intent);
                                }
                            });
                            holder.onClick_delete.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Xác nhận xóa sản phẩm này");
                                    builder.setCancelable(false);
                                    builder.setMessage("Nhấn hủy để giữ lại sản phẩm này");
                                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            ProgressDialog progressDialog = new ProgressDialog(context);
                                            progressDialog.setMessage("Đang xóa sản phẩm...");
                                            progressDialog.show();
                                            DatabaseReference reference3 = database.getReference("coffee-poly/cart/" + email + "/" + item_bill.getTime());
                                            reference3.removeValue();
                                            DatabaseReference reference4 = database.getReference("coffee-poly/bill_current/" + email + "/" + item_bill.getTime());
                                            reference4.removeValue();
                                            progressDialog.dismiss();
                                        }
                                    });
                                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    return true;
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }


    public class ItemBillHolder extends RecyclerView.ViewHolder {
        private ImageView imv_item_bill_image, imv_item_bill_remove, imv_item_bill_add;
        private TextView tv_item_bill_name, tv_item_bill_price, tv_item_bill_total, tv_item_bill_quantity;
        public CheckBox chk_item_bill_selected;
        private LinearLayout onClick_delete;

        public ItemBillHolder(@NonNull View itemView) {
            super(itemView);
            imv_item_bill_image = itemView.findViewById(R.id.imv_item_bill_image);
            imv_item_bill_remove = itemView.findViewById(R.id.imv_item_bill_remove);
            imv_item_bill_add = itemView.findViewById(R.id.imv_item_bill_add);
            tv_item_bill_name = itemView.findViewById(R.id.tv_item_bill_name);
            tv_item_bill_price = itemView.findViewById(R.id.tv_item_bill_price);
            tv_item_bill_total = itemView.findViewById(R.id.tv_item_bill_total);
            tv_item_bill_quantity = itemView.findViewById(R.id.tv_item_bill_quantity);
            chk_item_bill_selected = itemView.findViewById(R.id.chk_item_bill_selected);
            onClick_delete = itemView.findViewById(R.id.onClick_delete);
        }
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    list = listNew;
                } else {
                    List<Item_Bill> list1 = new ArrayList<>();
                    for (Item_Bill item_bill : listNew) {
                        for (int i = 0; i < ListData.listPrd.size(); i++) {
                            if (item_bill.getId_product() == ListData.listPrd.get(i).getId()) {
                                if (covertToString(ListData.listPrd.get(i).getName().toLowerCase()).contains(covertToString(strSearch.toLowerCase().trim()))) {
                                    list1.add(item_bill);
                                }
                            }
                        }
                    }
                    list = list1;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (List<Item_Bill>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static String covertToString(String value) {
        try {
            String temp = Normalizer.normalize(value, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            return pattern.matcher(temp).replaceAll("");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
