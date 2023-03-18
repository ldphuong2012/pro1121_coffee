package poly.ph26873.coffeepoly.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.activities.DetailUserActivity;
import poly.ph26873.coffeepoly.listData.ListData;
import poly.ph26873.coffeepoly.models.Message;
import poly.ph26873.coffeepoly.models.User;
import poly.ph26873.coffeepoly.service.MyReceiver;

public class MessagerRCVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> list;
    private static final String TAG = "zzz";

    public MessagerRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Message> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = list.get(position);
        if (message == null) {
            return ListData.type_user_current;
        }
        return message.getType();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListData.type_user_current || (viewType == 1 && ListData.type_user_current == 0)||(viewType == 0 && ListData.type_user_current == 1)) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mess_user, parent, false);
            return new MessagerUserHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_mess_nv, parent, false);
            return new MessagerNVHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        if (message != null) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String homnay = simpleDateFormat.format(calendar.getTime());
            Log.d(TAG, "getItemViewType =  " + holder.getItemViewType());
            Log.d(TAG, "ListData.type_user_current =  " + ListData.type_user_current);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("user").child(message.getId_user());
            if (holder.getItemViewType() == ListData.type_user_current || (holder.getItemViewType() == 1 && ListData.type_user_current == 0) || (holder.getItemViewType() == 0 && ListData.type_user_current == 1)) {
                MessagerUserHolder userHolder = (MessagerUserHolder) holder;
                userHolder.tv_mess_user.setText(message.getContent());
                if (homnay.equals(message.getTime().replaceAll("_", "/").substring(0, 10))) {
                    userHolder.tv_time_me.setText("Hôm nay, " + message.getTime().replaceAll("_", "/").substring(10));
                } else {
                    userHolder.tv_time_me.setText(message.getTime().replaceAll("_", "/"));
                }
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (message.getType() == 2) {
                            userHolder.tv_name_me.setTextColor(Color.BLACK);
                        } else if (message.getType() == 1) {
                            userHolder.tv_name_me.setTextColor(Color.BLUE);
                        } else {
                            userHolder.tv_name_me.setTextColor(Color.GREEN);
                        }
                        userHolder.tv_name_me.setText(user.getName());
                        if (isValidContextForGlide(context)) {
                            Glide.with(context).load(Uri.parse(user.getImage())).error(R.drawable.image_guest).into(userHolder.imv_avatar_me);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                userHolder.ln_mess_u.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(context, ((MessagerUserHolder) holder).ln_mess_u);
                        MenuInflater menuInflater = popupMenu.getMenuInflater();
                        menuInflater.inflate(R.menu.menu_mess_del_one, popupMenu.getMenu());
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.me_thu_hoi) {
                                    if (MyReceiver.isConnected == false) {
                                        Toast.makeText(context, "Không có kết nối mạng", Toast.LENGTH_LONG).show();
                                    } else {
                                        DatabaseReference reference = database.getReference("coffee-poly").child("messager").child(message.getId_user()).child(message.getTime());
                                        reference.removeValue();
                                    }
                                }
                                return false;
                            }
                        });
                        popupMenu.show();
                        return false;
                    }
                });
            } else {
                MessagerNVHolder nvHolder = (MessagerNVHolder) holder;
                nvHolder.tv_mess_nv.setText(message.getContent());
                if (homnay.equals(message.getTime().replaceAll("_", "/").substring(0, 10))) {
                    nvHolder.tv_time_you.setText("Hôm nay, " + message.getTime().replaceAll("_", "/").substring(10));
                } else {
                    nvHolder.tv_time_you.setText(message.getTime().replaceAll("_", "/"));
                }

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (message.getType() == 2) {
                            nvHolder.tv_name_you.setTextColor(Color.BLACK);
                            nvHolder.tv_name_you.setText(user.getName());
                        } else if (message.getType() == 1) {
                            nvHolder.tv_name_you.setTextColor(Color.BLUE);
                            nvHolder.tv_name_you.setText("Nhân viên: " + user.getName());
                        } else {
                            nvHolder.tv_name_you.setTextColor(Color.GREEN);
                            nvHolder.tv_name_you.setText("Quản lí: " + user.getName());
                        }
                        if (isValidContextForGlide(context)) {
                            Glide.with(context).load(Uri.parse(user.getImage())).error(R.drawable.image_guest).into(nvHolder.imv_avatar_you);
                        }

                        if (ListData.type_user_current != 2) {
                            nvHolder.imv_avatar_you.setOnClickListener(new View.OnClickListener() {
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
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
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

    public class MessagerUserHolder extends RecyclerView.ViewHolder {
        private TextView tv_mess_user, tv_time_me, tv_name_me;
        private ImageView imv_avatar_me;
        private LinearLayout ln_mess_u;

        public MessagerUserHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_me = itemView.findViewById(R.id.tv_name_me);
            tv_mess_user = itemView.findViewById(R.id.tv_mess_user);
            tv_time_me = itemView.findViewById(R.id.tv_time_me);
            imv_avatar_me = itemView.findViewById(R.id.imv_avatar_me);
            ln_mess_u = itemView.findViewById(R.id.ln_mess_u);
        }
    }

    public class MessagerNVHolder extends RecyclerView.ViewHolder {
        private TextView tv_mess_nv, tv_time_you, tv_name_you;
        private ImageView imv_avatar_you;

        public MessagerNVHolder(@NonNull View itemView) {
            super(itemView);
            tv_name_you = itemView.findViewById(R.id.tv_name_you);
            tv_mess_nv = itemView.findViewById(R.id.tv_mess_nv);
            tv_time_you = itemView.findViewById(R.id.tv_time_you);
            imv_avatar_you = itemView.findViewById(R.id.imv_avatar_you);
        }
    }
}
