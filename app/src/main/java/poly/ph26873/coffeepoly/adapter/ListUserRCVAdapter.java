package poly.ph26873.coffeepoly.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
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
import poly.ph26873.coffeepoly.activities.DetailUserActivity;
import poly.ph26873.coffeepoly.activities.ImageActivity;
import poly.ph26873.coffeepoly.models.User;

public class ListUserRCVAdapter extends RecyclerView.Adapter<ListUserRCVAdapter.UserHolder> implements Filterable {
    private Context context;
    private List<User> list;
    private List<User> listNew;

    public ListUserRCVAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<User> list) {
        this.list = list;
        this.listNew = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListUserRCVAdapter.UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_list_user, parent, false);
        return new UserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListUserRCVAdapter.UserHolder holder, int position) {
        User user = list.get(position);
        if (user != null) {
            Log.d("zzz", "user: "+user.getId());
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("coffee-poly").child("user").child(user.getId()).child("enable");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        int type = snapshot.getValue(Integer.class);
                        if (type == 1) {
                            holder.ln_item_list_user.setBackgroundResource(R.color.black1);
                        } else {
                            holder.ln_item_list_user.setBackgroundResource(R.color.white);
                        }
                    }catch (Exception  exception){
                        holder.ln_item_list_user.setBackgroundResource(R.color.white);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            Glide.with(context).load(user.getImage()).error(R.drawable.image_guest).into(holder.imv_avatar_list_user);
            holder.tv_name_list_user.setText(user.getName());
            holder.tv_email_list_user.setText(user.getEmail());
            holder.imv_more_vert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, holder.imv_more_vert);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.menu_item_list_user, popupMenu.getMenu());
                    if (user.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        popupMenu.getMenu().findItem(R.id.mv_unlock).setVisible(false);
                        popupMenu.getMenu().findItem(R.id.mv_lock).setVisible(false);
                    } else {
                        if (user.getEnable() == 0) {
                            popupMenu.getMenu().findItem(R.id.mv_unlock).setVisible(false);
                        } else {
                            popupMenu.getMenu().findItem(R.id.mv_lock).setVisible(false);
                        }
                    }

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            switch (id) {
                                case R.id.mv_seenInfo:
                                    Intent intent = new Intent(context, DetailUserActivity.class);
                                    intent.putExtra("user", user);
                                    context.startActivity(intent);
                                    break;
                                case R.id.mv_lock:
                                    DatabaseReference reference1 = database.getReference("coffee-poly").child("user").child(user.getId()).child("enable");
                                    reference1.setValue(1);
                                    break;
                                case R.id.mv_unlock:
                                    DatabaseReference reference2 = database.getReference("coffee-poly").child("user").child(user.getId()).child("enable");
                                    reference2.setValue(0);
                                    break;
                            }

                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            holder.imv_avatar_list_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageActivity.class);
                    intent.putExtra("image", user.getImage());
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
        private LinearLayout ln_item_list_user;
        private ImageView imv_avatar_list_user, imv_more_vert;
        private TextView tv_name_list_user, tv_email_list_user;

        public UserHolder(@NonNull View itemView) {
            super(itemView);
            ln_item_list_user = itemView.findViewById(R.id.ln_item_list_user);
            imv_avatar_list_user = itemView.findViewById(R.id.imv_avatar_list_user);
            imv_more_vert = itemView.findViewById(R.id.imv_more_vert);
            tv_name_list_user = itemView.findViewById(R.id.tv_name_list_user);
            tv_email_list_user = itemView.findViewById(R.id.tv_email_list_user);
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
                    List<User> list1 = new ArrayList<>();
                    for (User user : listNew) {
                        if (covertToString(user.getId().toLowerCase()).contains(covertToString(strSearch.toLowerCase().trim()))) {
                            list1.add(user);
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
                list = (List<User>) results.values;
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
