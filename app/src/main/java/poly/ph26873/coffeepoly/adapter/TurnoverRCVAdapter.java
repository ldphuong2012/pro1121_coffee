package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailTurnoverActivity;
import poly.ph26873.coffeepoly.models.Turnover;

public class TurnoverRCVAdapter extends RecyclerView.Adapter<TurnoverRCVAdapter.TurnoverHolder> {
    private Context context;
    private List<Turnover> list;

    public TurnoverRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Turnover> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TurnoverRCVAdapter.TurnoverHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_turnover, parent, false);
        return new TurnoverHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurnoverRCVAdapter.TurnoverHolder holder, int position) {
        Turnover turnover = list.get(position);
        if (turnover != null) {
            holder.tv_turn_id.setText("ID: " + turnover.getId().replaceAll(" ",""));
            holder.tv_turn_time.setText("Thời gian: " + turnover.getTime().replaceAll("_","/"));
            holder.tv_turn_total.setText("Tổng tiền: " + turnover.getTotal() + "K");
            holder.onclick_detail_item_turnover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailTurnoverActivity.class);
                    intent.putExtra("turnover", turnover);
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

    public class TurnoverHolder extends RecyclerView.ViewHolder {
        private TextView tv_turn_time, tv_turn_total, tv_turn_id;
        private LinearLayout onclick_detail_item_turnover;

        public TurnoverHolder(@NonNull View itemView) {
            super(itemView);
            tv_turn_time = itemView.findViewById(R.id.tv_turn_time);
            tv_turn_total = itemView.findViewById(R.id.tv_turn_total);
            tv_turn_id = itemView.findViewById(R.id.tv_turn_id);
            onclick_detail_item_turnover = itemView.findViewById(R.id.onclick_detail_item_turnover);
        }
    }
}
