package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.Models.Publicacion;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.UI.PublicacionActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PublicacionAdapter extends FirebaseRecyclerAdapter<PublicacionAdapter.ViewHolder, Publicacion> {


    public static final String TAG = "PublicacionAdapter";

    private Context mContext;
    private ViewPager mViewPager;
    private int currentPage, bannerCount = 0;
    private ArrayList<String> imagesArray = new ArrayList<String>();

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cv;
        public TextView title, count;
        public ViewPager viewPager;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.pub_card_view);
            title = (TextView) itemView.findViewById(R.id.pub_title);
            count = (TextView) itemView.findViewById(R.id.pub_count);
            viewPager = (ViewPager) itemView.findViewById(R.id.pub_slider);

        }
    }

    public PublicacionAdapter(Context context, Query query, Class<Publicacion> itemClass, ArrayList<Publicacion> items,
                              @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.mContext = context;
    }


    @Override
    public PublicacionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.publicacion_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PublicacionAdapter.ViewHolder viewHolder, final int position) {
        mViewPager = viewHolder.viewPager;
        Publicacion model = getItem(position);
        final String key = getKey(position);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("articulos").child(key);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    bannerCount += 1;
                    Publicacion publicacion = data.getValue(Publicacion.class);
                    imagesArray.add(publicacion.thumbnail);
                    if( bannerCount == dataSnapshot.getChildrenCount()){
                        iniciarSliders(viewHolder, position);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        viewHolder.title.setText(model.titulo);
        viewHolder.count.setText(model.descripcion);

        final String title = model.titulo;

        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(c, PublicacionActivity.class);
                intent.putExtra(PublicacionActivity.EXTRA_POST_KEY, key);
                intent.putExtra(PublicacionActivity.EXTRA_POST_TITLE, title);
                c.startActivity(intent);
            }
        });

        viewHolder.viewPager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
                Intent intent = new Intent(c, PublicacionActivity.class);
                intent.putExtra(PublicacionActivity.EXTRA_POST_KEY, key);
                intent.putExtra(PublicacionActivity.EXTRA_POST_TITLE, title);
                c.startActivity(intent);
            }
        });
    }

    @Override
    protected void itemAdded(Publicacion item, String key, int position) {

    }

    @Override
    protected void itemChanged(Publicacion oldItem, Publicacion newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Publicacion item, String key, int position) {

    }

    @Override
    protected void itemMoved(Publicacion item, String key, int oldPosition, int newPosition) {

    }

    private void iniciarSliders(ViewHolder viewHolder, int position){
        ImgPubAdapter adapter = new ImgPubAdapter(this.mContext, imagesArray);
        viewHolder.viewPager.setAdapter(adapter);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == bannerCount) {
                    currentPage = 0;
                }
                mViewPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3500, 3500);
    }
}
