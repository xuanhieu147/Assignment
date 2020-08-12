package com.example.assignment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.assignment.Model.Photo;

import com.example.assignment.R;
import com.squareup.picasso.Picasso;

import java.util.List;


public class ViewpagerAdapter extends PagerAdapter {
    private Context context;
    private List<Photo> listPhotos;
    private LayoutInflater inflater;

    public ViewpagerAdapter(Context context, List<Photo> listPhotos) {
        this.context = context;
        this.listPhotos = listPhotos;
        this.inflater = LayoutInflater.from(context);
    }

    // đi sang 1 page khác sẽ destroy item ở page cũ
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //trả về số lượng item sẽ đc show
    @Override
    public int getCount() {
        return listPhotos.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view1 = inflater.inflate(R.layout.item_slide, container, false);
        final ImageView imageView = view1.findViewById(R.id.image_slide);
        Picasso.with(context).load(listPhotos.get(position).getUrlL()).into(imageView);
        container.addView(view1, 0);
        return view1;
    }

    //phương thức kiểm tra xem các đối tượng đc trả về bởi instantiateItem() đc liên kết với View được cung cấp
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }
}
