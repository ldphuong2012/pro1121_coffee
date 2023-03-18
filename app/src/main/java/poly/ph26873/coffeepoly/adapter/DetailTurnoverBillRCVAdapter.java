package poly.ph26873.coffeepoly.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailProductActivity;
import poly.ph26873.coffeepoly.models.Item_Bill;
import poly.ph26873.coffeepoly.models.Product;
import poly.ph26873.coffeepoly.models.QuantitySoldInMonth;

public class DetailTurnoverBillRCVAdapter extends RecyclerView.Adapter<DetailTurnoverBillRCVAdapter.DTurnoverHolder> {
    private Context context;
    private List<Item_Bill> list;

    public DetailTurnoverBillRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Item_Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DetailTurnoverBillRCVAdapter.DTurnoverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_dt_turnover, parent, false);
        return new DTurnoverHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailTurnoverBillRCVAdapter.DTurnoverHolder holder, int position) {
        Item_Bill item_bill = list.get(position);
        if (item_bill != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("product").child(String.valueOf(item_bill.getId_product()));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        if (isValidContextForGlide(context)){
                            Glide.with(context).load(Uri.parse(product.getImage())).error(R.color.red).into(holder.imv_prd_in_turn_avatar);
                        }
                        holder.tv_prd_in_turn_name.setText(product.getName());
                        holder.tv_prd_in_turn_price.setText("Đơn giá: " + product.getPrice() + "K");
                        holder.tv_prd_in_turn_quantity.setText("Số lương : " + item_bill.getQuantity());
                        DatabaseReference reference1 = database.getReference("coffee-poly").child("type_product").child(String.valueOf(product.getType())).child("country");
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.tv_prd_in_turn_country.setText("Nguồn gốc: " + snapshot.getValue(String.class));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        holder.imv_prd_in_turn_avatar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(context, DetailProductActivity.class);
                                intent.putExtra("product", product);
                                context.startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class DTurnoverHolder extends RecyclerView.ViewHolder {
        private ImageView imv_prd_in_turn_avatar;
        private TextView tv_prd_in_turn_name, tv_prd_in_turn_price, tv_prd_in_turn_country, tv_prd_in_turn_quantity;

        public DTurnoverHolder(@NonNull View itemView) {
            super(itemView);
            imv_prd_in_turn_avatar = itemView.findViewById(R.id.imv_prd_in_turn_avatar);
            tv_prd_in_turn_name = itemView.findViewById(R.id.tv_prd_in_turn_name);
            tv_prd_in_turn_price = itemView.findViewById(R.id.tv_prd_in_turn_price);
            tv_prd_in_turn_country = itemView.findViewById(R.id.tv_prd_in_turn_country);
            tv_prd_in_turn_quantity = itemView.findViewById(R.id.tv_prd_in_turn_quantity);
        }
    }
}
