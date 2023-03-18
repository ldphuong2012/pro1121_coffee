package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailTurnoverActivity;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.History;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class HistoryRCVAdapter extends RecyclerView.Adapter<HistoryRCVAdapter.HistoryHolder> {
    private Context context;
    private List<History> list;

    public HistoryRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<History> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryRCVAdapter.HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRCVAdapter.HistoryHolder holder, int position) {
        History history = list.get(position);
        if (history != null) {
            if (history.getStatus() == 0) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getEmail().replaceAll("@gmail.com", "");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("coffee-poly/bill/" + email + "/" + history.getId());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Bill bill = snapshot.getValue(Bill.class);
                        if (bill != null) {
                            holder.tv_his_time.setText("Thời gian: " + history.getId().replaceAll("_","/"));
                            holder.tv_his_name.setText("Họ và tên: " + bill.getName());
                            String note = bill.getNote();
                            note.substring(0, note.length() - 2);
                            note.replaceAll("-", "\n");
                            holder.tv_his_note.setText(note);
                            holder.tv_his_address.setText("Địa chỉ: " + bill.getAddress());
                            holder.tv_his_number_phone.setText("Số điện thoại: " + bill.getNumberPhone());
                            holder.tv_his_total.setText("Tổng tiền: " + bill.getTotal() + "K");
                            holder.tv_his_mess.setText("Ghi chú: " + bill.getMess());
                            if (bill.getStatus() == 0) {
                                holder.tv_his_status.setTextColor(Color.BLUE);
                                holder.tv_his_status.setText("Trạng thái đơn hàng: Đã xác nhận đơn");
                            } else if (bill.getStatus() == 1) {
                                holder.tv_his_status.setTextColor(Color.BLACK);
                                holder.tv_his_status.setText("Trạng thái đơn hàng: Đang chờ nhận đơn");
                            } else if (bill.getStatus() == 2) {
                                holder.tv_his_status.setTextColor(Color.RED);
                                holder.tv_his_status.setText("Trạng thái đơn hàng: Đã hủy đơn");
                            } else if (bill.getStatus() == 4) {
                                holder.tv_his_status.setTextColor(Color.GREEN);
                                holder.tv_his_status.setText("Trạng thái đơn hàng: Đã giao hàng thành công");
                            }else {
                                holder.tv_his_status.setTextColor(Color.RED);
                                holder.tv_his_status.setText("Trạng thái đơn hàng: Đã hủy đơn bởi nhân viên");
                            }
                            holder.onClick_del_his.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Xóa lịch sử đơn hàng này?");
                                    builder.setCancelable(true);
                                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (MyReceiver.isConnected == false) {
                                                Toast.makeText(builder.getContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            history.setStatus(1);
                                            DatabaseReference reference1 = database.getReference("coffee-poly/history/" + email + "/" + history.getId());
                                            reference1.setValue(history, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                    Toast.makeText(context, "Xóa lịch sử đơn hàng thành công", Toast.LENGTH_SHORT).show();
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
                                    return true;
                                }
                            });
                            holder.onClick_del_his.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, DetailTurnoverActivity.class);
                                    intent.putExtra("id_bill",bill.getId());
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
        } else {
            list.remove(position);
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
        private TextView tv_his_mess, tv_his_time, tv_his_name, tv_his_number_phone, tv_his_note, tv_his_address, tv_his_total, tv_his_status;
        private LinearLayout onClick_del_his;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            tv_his_number_phone = itemView.findViewById(R.id.tv_his_number_phone);
            tv_his_mess = itemView.findViewById(R.id.tv_his_mess);
            tv_his_name = itemView.findViewById(R.id.tv_his_name);
            tv_his_time = itemView.findViewById(R.id.tv_his_time);
            tv_his_note = itemView.findViewById(R.id.tv_his_note);
            tv_his_address = itemView.findViewById(R.id.tv_his_address);
            tv_his_total = itemView.findViewById(R.id.tv_his_total);
            tv_his_status = itemView.findViewById(R.id.tv_his_status);
            onClick_del_his = itemView.findViewById(R.id.onClick_del_his);
        }
    }


}
