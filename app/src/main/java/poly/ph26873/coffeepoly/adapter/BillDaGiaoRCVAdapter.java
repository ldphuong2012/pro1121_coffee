package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailTurnoverActivity;
import poly.ph26873.coffeepoly.models.Bill;

public class BillDaGiaoRCVAdapter extends RecyclerView.Adapter<BillDaGiaoRCVAdapter.HistoryHolder> implements Filterable {
    private Context context;
    private List<Bill> list;
    private List<Bill> listNew;

    public BillDaGiaoRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        this.listNew = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillDaGiaoRCVAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_bill_da_giao, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillDaGiaoRCVAdapter.HistoryHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            holder.ln_bill_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailTurnoverActivity.class);
                    intent.putExtra("id_user", bill.getId_user());
                    intent.putExtra("id_bill", bill.getId());
                    context.startActivity(intent);
                }
            });
            holder.tv_his_time1.setText("Thời gian: " + bill.getId().replaceAll("_","/"));
            holder.tv_his_name1.setText("Họ và tên: " + bill.getName());
            String note = bill.getNote();
            note.substring(0, note.length() - 2);
            note.replaceAll("-", "\n");
            holder.tv_his_note1.setText(note);
            holder.tv_his_address1.setText("Địa chỉ: " + bill.getAddress());
            holder.tv_his_number_phone1.setText("Số điện thoại: " + bill.getNumberPhone());
            holder.tv_his_mess_1.setText("Ghi chú: " + bill.getMess());
            holder.tv_his_total1.setText("Tổng tiền: " + bill.getTotal() + "K");
            if (bill.getStatus() == 2) {
                holder.tv_his_status1.setTextColor(Color.RED);
                holder.tv_his_status1.setText("Trạng thái đơn hàng: Đã hủy đơn");
            } else {
                holder.tv_his_status1.setTextColor(Color.GREEN);
                holder.tv_his_status1.setText("Trạng thái đơn hàng: Đã giao thành công");
            }

        }
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        private TextView tv_his_mess_1, tv_his_time1, tv_his_name1, tv_his_number_phone1, tv_his_note1, tv_his_address1, tv_his_total1, tv_his_status1;
        private LinearLayout ln_bill_ok;
        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tv_his_number_phone1 = itemView.findViewById(R.id.tv_his_number_phone_1);
            ln_bill_ok = itemView.findViewById(R.id.ln_bill_ok);
            tv_his_name1 = itemView.findViewById(R.id.tv_his_name_1);
            tv_his_mess_1 = itemView.findViewById(R.id.tv_his_mess_1);
            tv_his_time1 = itemView.findViewById(R.id.tv_his_time_1);
            tv_his_note1 = itemView.findViewById(R.id.tv_his_note_1);
            tv_his_address1 = itemView.findViewById(R.id.tv_his_address_1);
            tv_his_total1 = itemView.findViewById(R.id.tv_his_total_1);
            tv_his_status1 = itemView.findViewById(R.id.tv_his_status_1);

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
                    List<Bill> list1 = new ArrayList<>();
                    for (Bill bill : listNew) {
                        if (covertToString(bill.getId_user().toLowerCase()).contains(covertToString(strSearch.toLowerCase().trim()))) {
                            list1.add(bill);
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
                list = (List<Bill>) results.values;
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
