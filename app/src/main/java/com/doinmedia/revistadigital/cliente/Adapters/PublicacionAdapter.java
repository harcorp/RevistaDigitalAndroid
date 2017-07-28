package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.Models.Publicacion;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.doinmedia.revistadigital.cliente.UI.PublicacionActivity;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by davidrodriguez on 27/12/16.
 */

public class PublicacionAdapter extends FirebaseRecyclerAdapter<PublicacionAdapter.ViewHolder, Publicacion> {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cv;
        public TextView title, count;
        public ImageView thumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.pub_card_view);
            title = (TextView) itemView.findViewById(R.id.pub_title);
            count = (TextView) itemView.findViewById(R.id.pub_count);
            thumbnail = (ImageView) itemView.findViewById(R.id.pub_thumbnail);

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
        Publicacion model = getItem(position);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(model.thumbnail);
        viewHolder.title.setText(model.titulo);
        viewHolder.count.setText("Articulos: " + model.count.toString());
        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(ref)
                .fitCenter()
                .placeholder(R.drawable.spinner_animation)
                .into(viewHolder.thumbnail);

        final String key = getKey(position);
        final String title = model.titulo;
        viewHolder.thumbnail.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Context c = v.getContext();
                Intent intent = new Intent(c, PublicacionActivity.class);
                intent.putExtra(PublicacionActivity.EXTRA_POST_KEY, key);
                intent.putExtra(PublicacionActivity.EXTRA_POST_TITLE, title);
                c.startActivity(intent);
            }
        });
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
}
