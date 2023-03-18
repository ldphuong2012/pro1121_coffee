package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailTurnoverActivity;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Notify;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class BillRCVAdapter extends RecyclerView.Adapter<BillRCVAdapter.BillHolder> {
    private Context context;
    private List<Bill> list;

    public BillRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BillRCVAdapter.BillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill, parent, false);
        return new BillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillRCVAdapter.BillHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            holder.ln_bill_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailTurnoverActivity.class);
                    intent.putExtra("id_bill", bill.getId());
                    context.startActivity(intent);
                }
            });
            holder.tv_bill_time.setText("Thời gian: " + bill.getId().replaceAll("_", "/"));
            holder.tv_bill_name.setText("Họ và tên: " + bill.getName());
            String note = bill.getNote().toString();
            note.substring(0, note.length() - 2);
            note.replaceAll("-", "\n");
            holder.tv_bill_note.setText(note);
            holder.tv_bill_address.setText("Địa chỉ: " + bill.getAddress());
            holder.tv_bill_number_phone.setText("Số điện thoại: " + bill.getNumberPhone());
            holder.tv_bill_total.setText("Tổng tiền: " + bill.getTotal() + "K");
            holder.tv_bill_mess.setText("Ghi chú: " + bill.getMess());
            if (bill.getStatus() == 0) {
                holder.tv_bill_status.setTextColor(Color.GREEN);
                holder.tv_bill_status.setText("Trạng thái: Đang giao hàng");
                holder.btn_bill_cancle.setVisibility(View.INVISIBLE);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        0, 0);
                holder.btn_bill_cancle.setLayoutParams(lp);
            } else {
                holder.btn_bill_cancle.setVisibility(View.VISIBLE);
                holder.tv_bill_status.setTextColor(Color.BLACK);
                holder.tv_bill_status.setText("Trạng thái: Đang chờ xác nhận");
                holder.btn_bill_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Bạn chắc chắn muốn hủy đơn hàng này");
                        builder.setMessage("Hãy nhấn hủy để giữ lại đơn hàng");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Hủy đơn hàng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (MyReceiver.isConnected == false) {
                                    Toast.makeText(builder.getContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(bill.getId_user()).child(bill.getId()).child("status");
                                reference.setValue(2, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(builder.getContext(), "Hủy đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                        list.remove(bill);
                                        CapNhatThongBao(bill);
                                        notifyDataSetChanged();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });
            }
        }
    }

    private void CapNhatThongBao(Bill bill) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy kk:mm:ss");
        String thoigian = simpleDateFormat.format(calendar.getTime());
        Notify notify = new Notify();
        notify.setTime(thoigian);
        notify.setContent("Đơn hàng " + bill.getId() + " đã hủy");
        notify.setStatus(0);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("notify").child(bill.getId_user()).child(thoigian);
        reference.setValue(notify);
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class BillHolder extends RecyclerView.ViewHolder {
        private TextView tv_bill_mess, tv_bill_name, tv_bill_number_phone, tv_bill_time, tv_bill_note, tv_bill_address, tv_bill_total, tv_bill_status;
        private Button btn_bill_cancle;
        private LinearLayout ln_bill_1;

        public BillHolder(@NonNull View itemView) {
            super(itemView);
            tv_bill_number_phone = itemView.findViewById(R.id.tv_bill_number_phone);
            tv_bill_name = itemView.findViewById(R.id.tv_bill_name);
            tv_bill_mess = itemView.findViewById(R.id.tv_bill_mess);
            tv_bill_time = itemView.findViewById(R.id.tv_bill_time);
            tv_bill_note = itemView.findViewById(R.id.tv_bill_note);
            tv_bill_address = itemView.findViewById(R.id.tv_bill_address);
            tv_bill_total = itemView.findViewById(R.id.tv_bill_total);
            tv_bill_status = itemView.findViewById(R.id.tv_bill_status);
            btn_bill_cancle = itemView.findViewById(R.id.btn_bill_cancle);
            ln_bill_1 = itemView.findViewById(R.id.ln_bill_1);
        }
    }
}
