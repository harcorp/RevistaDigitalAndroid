package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by davidrodriguez on 21/08/17.
 */

public class BannerAdapter extends PagerAdapter {

    public static final String TAG = "BannerAdapter";

    private LayoutInflater inflater;
    private ArrayList<String> images, links;
    private Context context;

    public BannerAdapter(Context context, ArrayList<String> images, ArrayList<String> links) {
        this.context = context;
        this.images = images;
        this.links = links;
        inflater = LayoutInflater.from(context);
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
        View myImageLayout = inflater.inflate(R.layout.slider, view, false);
        final String link = this.links.get(position);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image_banner);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(images.get(position));
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(link); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .fitCenter()
                .into(myImage);

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
