package poly.ph26873.coffeepoly.adapter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailTurnoverActivity;
import poly.ph26873.coffeepoly.models.Bill;
import poly.ph26873.coffeepoly.models.Notify;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class ManagementRCVAdapter extends RecyclerView.Adapter<ManagementRCVAdapter.BillHolder> {
    private Context context;
    private List<Bill> list;
    private static final String TAG = "zzz";

    public ManagementRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Bill> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManagementRCVAdapter.BillHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_bill_management, parent, false);
        return new BillHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManagementRCVAdapter.BillHolder holder, int position) {
        Bill bill = list.get(position);
        if (bill != null) {
            if (bill.getStatus() == 1) {
                holder.ln_mana.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetailTurnoverActivity.class);
                        intent.putExtra("id_bill", bill.getId());
                        intent.putExtra("id_user",bill.getId_user());
                        Log.d(TAG, "id_bill: "+bill.getId());
                        context.startActivity(intent);
                    }
                });
                holder.tv_bill_time_m.setText("Thời gian: " + bill.getId().replaceAll("_", "/"));
                holder.tv_bill_name_m.setText("Họ và tên: " + bill.getName());
                String note = bill.getNote().toString();
                note.substring(0, note.length() - 2);
                note.replaceAll("-", "\n");
                holder.tv_bill_note_m.setText(note);
                holder.tv_bill_address_m.setText("Địa chỉ: " + bill.getAddress());
                holder.tv_bill_number_phone_m.setText("Số điện thoại: " + bill.getNumberPhone());
                holder.tv_bill_mess_m.setText("Ghi chú: " + bill.getMess());
                holder.tv_bill_total_m.setText("Tổng tiền: " + bill.getTotal() + "K");
                holder.tv_bill_status_m.setText("Trạng thái: Đang chờ đang xác nhận");
                holder.btn_bill_cancle_m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                        builder.setTitle("Xác nhận đơn hàng đặt thành công?");
                        builder.setMessage("Hãy nhấn xác nhận");
                        builder.setCancelable(true);
                        builder.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (MyReceiver.isConnected == false) {
                                    Toast.makeText(builder.getContext(), "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("coffee-poly").child("bill").child(bill.getId_user()).child(bill.getId()).child("status");
                                reference.setValue(0, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                        Toast.makeText(builder.getContext(), "Xác nhân hàng thành công", Toast.LENGTH_SHORT).show();
                                        CapNhatthongBao(bill, 0);
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
                holder.imv_bill_cancel_m.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context, holder.imv_bill_cancel_m);
                        MenuInflater menuInflater = popupMenu.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_item_bill, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {
                                    case R.id.mn_contact:
                                        PermissionListener permissionlistener = new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted() {
                                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + bill.getNumberPhone()));
                                                context.startActivity(intent);
                                            }

                                            @Override
                                            public void onPermissionDenied(List<String> deniedPermissions) {
                                                Toast.makeText(context, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        };
                                        TedPermission.create()
                                                .setPermissionListener(permissionlistener)
                                                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                                                .setPermissions(Manifest.permission.CALL_PHONE)
                                                .check();
                                        break;
                                    case R.id.mn_cancel:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                        builder.setTitle("Xác nhận hủy đơn?");
                                        builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
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
                                                        Toast.makeText(builder.getContext(), "Hủy đơn thành công", Toast.LENGTH_SHORT).show();
                                                        CapNhatthongBao(bill, 2);
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                            }
                                        });
                                        builder.setNegativeButton("Quay lại", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.create().show();
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

        }
    }

    private void CapNhatthongBao(Bill bill, int a) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy kk:mm:ss");
        String thoigian = simpleDateFormat.format(calendar.getTime());
        Notify notify = new Notify();
        notify.setStatus(0);
        notify.setTime(thoigian);
        if (a == 0) {
            notify.setContent("Đơn hàng " + bill.getId() + " đã được nhân viên xác nhận");
        } else {
            notify.setContent("Đơn hàng " + bill.getId() + " đã được hủy");
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("coffee-poly").child("notify").child(bill.getId_user()).child(thoigian);
        reference.setValue(notify, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                DatabaseReference reference1 = database.getReference("coffee-poly").child("notify").child("Staff_Ox3325").child(thoigian);
                if (a == 0) {
                    notify.setContent("Đơn hàng " + bill.getId() + " đã được nhân viên xác nhận thành công");
                } else {
                    notify.setContent("Đơn hàng " + bill.getId() + " đã hủy bởi nhân viên");
                }
                reference1.setValue(notify);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class BillHolder extends RecyclerView.ViewHolder {
        private TextView tv_bill_mess_m, tv_bill_time_m, tv_bill_name_m, tv_bill_number_phone_m, tv_bill_note_m, tv_bill_address_m, tv_bill_total_m, tv_bill_status_m;
        private Button btn_bill_cancle_m;
        private ImageView imv_bill_cancel_m;
        private LinearLayout ln_mana;
        public BillHolder(@NonNull View itemView) {
            super(itemView);
            ln_mana = itemView.findViewById(R.id.ln_mana);
            tv_bill_number_phone_m = itemView.findViewById(R.id.tv_bill_number_phone_m);
            tv_bill_mess_m = itemView.findViewById(R.id.tv_bill_mess_m);
            tv_bill_name_m = itemView.findViewById(R.id.tv_bill_name_m);
            tv_bill_time_m = itemView.findViewById(R.id.tv_bill_time_m);
            tv_bill_note_m = itemView.findViewById(R.id.tv_bill_note_m);
            tv_bill_address_m = itemView.findViewById(R.id.tv_bill_address_m);
            tv_bill_total_m = itemView.findViewById(R.id.tv_bill_total_m);
            tv_bill_status_m = itemView.findViewById(R.id.tv_bill_status_m);
            btn_bill_cancle_m = itemView.findViewById(R.id.btn_bill_cancle_m);
            imv_bill_cancel_m = itemView.findViewById(R.id.imv_bill_cancel_m);
        }
    }
}
