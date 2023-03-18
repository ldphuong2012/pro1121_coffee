package poly.ph26873.coffeepoly.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailUserActivity;
import poly.ph26873.coffeepoly.activities.MessagerActivity;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class ListMessagerRCVAdapter extends RecyclerView.Adapter<ListMessagerRCVAdapter.UserHolder> {
    private Context context;
    private List<User> list;

    public ListMessagerRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<User> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListMessagerRCVAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_mess, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListMessagerRCVAdapter.UserHolder holder, int position) {
        User user = list.get(position);
        if (user != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            if (MyReceiver.isConnected == false) {
                if (user.getEnable() == 1) {
                    holder.onclick_mess.setBackgroundResource(R.color.black1);
                } else {
                    holder.onclick_mess.setBackgroundResource(R.color.white);
                }
                Glide.with(context).load(Uri.parse(user.getImage())).error(R.drawable.image_guest).into(holder.imv_avatar_lm);
            } else {
                DatabaseReference reference = database.getReference("coffee-poly").child("user").child(user.getId());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.getValue(User.class);
                        if (user1 != null) {
                            if (user1.getEnable() == 1) {
                                holder.onclick_mess.setBackgroundResource(R.color.black1);
                            } else {
                                holder.onclick_mess.setBackgroundResource(R.color.white);
                            }
                            Glide.with(context).load(Uri.parse(user1.getImage())).error(R.drawable.image_guest).into(holder.imv_avatar_lm);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            holder.tv_name_lm.setText(user.getName());

            DatabaseReference reference = database.getReference("coffee-poly").child("Notify_messager").child(user.getId()).child("status");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status;
                    try {
                        status = snapshot.getValue(Integer.class);
                        if (status == 0) {
                            holder.imv_new.setVisibility(View.VISIBLE);
                        } else {
                            holder.imv_new.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            holder.onclick_mess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessagerActivity.class);
                    intent.putExtra("id_user", user.getId());
                    context.startActivity(intent);
                }
            });
            holder.onclick_mess.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(false);
                    builder.setTitle("Xác nhận xóa đoạn chat này?");
                    builder.setPositiveButton("Tiếp tục", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.show();
                            DatabaseReference reference1 = database.getReference("coffee-poly").child("Notify_messager").child(user.getId());
                            reference1.removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    list.remove(user);
                                    notifyDataSetChanged();
                                    progressDialog.dismiss();
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
                    return false;
                }
            });
            holder.imv_avatar_lm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailUserActivity.class);
                    intent.putExtra("user", user);
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

    public class UserHolder extends RecyclerView.ViewHolder {
        private ImageView imv_avatar_lm, imv_new;
        private TextView tv_name_lm;
        private LinearLayout onclick_mess;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            imv_avatar_lm = itemView.findViewById(R.id.imv_avatar_lm);
            imv_new = itemView.findViewById(R.id.imv_new);
            tv_name_lm = itemView.findViewById(R.id.tv_name_lm);
            onclick_mess = itemView.findViewById(R.id.onclick_mess);
        }
    }
}
