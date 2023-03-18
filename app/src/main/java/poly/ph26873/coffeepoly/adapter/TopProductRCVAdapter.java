package poly.ph26873.coffeepoly.adapter;

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

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailProductActivity;
import poly.ph26873.coffeepoly.models.Product;


public class TopProductRCVAdapter extends RecyclerView.Adapter<TopProductRCVAdapter.ProductHolder> {
    private List<Product> list;
    private Context context;

    public TopProductRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Product> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TopProductRCVAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_top_product, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopProductRCVAdapter.ProductHolder holder, int position) {
        Product product = list.get(position);
        if (product != null) {
            Glide.with(context).load(Uri.parse(product.getImage())).error(R.color.red).into(holder.imv_top_avatar);
            holder.tv_top_name.setText(product.getName());
            holder.tv_top_price.setText("Đơn giá: " + product.getPrice()+"K");
            holder.tv_top_quantitySold.setText("Số lượng đã bán: " + product.getQuantitySold());
            holder.imv_top_avatar.setOnClickListener(new View.OnClickListener() {
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
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ProductHolder extends RecyclerView.ViewHolder {
        private ImageView imv_top_avatar;
        private TextView tv_top_name, tv_top_price, tv_top_quantitySold;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);
            imv_top_avatar = itemView.findViewById(R.id.imv_top_avatar);
            tv_top_name = itemView.findViewById(R.id.tv_top_name);
            tv_top_price = itemView.findViewById(R.id.tv_top_price);
            tv_top_quantitySold = itemView.findViewById(R.id.tv_top_quantitySold);
        }
    }
}
