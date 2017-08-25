package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.doinmedia.revistadigital.cliente.UI.ArticulosActivity;
import com.doinmedia.revistadigital.cliente.UI.PublicacionActivity;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImgPubAdapter extends PagerAdapter {

    public static final String TAG = "BannerAdapter";

    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;
    private String key, title;

    public ImgPubAdapter(Context context, ArrayList<String> images, String title, String key) {
        this.context = context;
        this.images = images;
        this.title = title;
        this.key = key;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.slider_pub, view, false);

        ImageView myImage = (ImageView) layout
                .findViewById(R.id.image_banner_pub);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(images.get(position));

        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .fitCenter()
                .into(myImage);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PublicacionActivity.class);
                intent.putExtra(PublicacionActivity.EXTRA_POST_KEY, key);
                intent.putExtra(PublicacionActivity.EXTRA_POST_TITLE, title);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        view.addView(layout, 0);
        return layout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
