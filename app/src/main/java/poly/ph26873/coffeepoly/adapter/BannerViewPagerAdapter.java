package poly.ph26873.coffeepoly.adapter;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import com.bumptech.glide.Glide;

import java.util.List;

import poly.ph26873.coffeepoly.R;
import poly.ph26873.coffeepoly.models.Banner;

public class BannerViewPagerAdapter extends PagerAdapter {
    private List<Banner> list;

    public BannerViewPagerAdapter(List<Banner> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_item_banner,container,false);
        ImageView imageView = view.findViewById(R.id.imv_banner);
        Banner banner = list.get(position);
        Glide.with(container.getContext()).load(banner.getResourceId()).error(R.color.red).into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
    //banner
}
