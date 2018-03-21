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
import com.doinmedia.revistadigital.cliente.Models.Articulo;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.doinmedia.revistadigital.cliente.UI.ArticuloActivity;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * Created by davidrodriguez on 21/01/17.
 */

public class ArticulosAdapter extends FirebaseRecyclerAdapter<ArticulosAdapter.ViewHolder, Articulo>  {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public ImageView thumbnail;

        public ViewHolder(View itemView){
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.articulo_card_view);
            thumbnail = (ImageView) itemView.findViewById(R.id.articulo_thumbnail);
        }
    }



    public ArticulosAdapter(Context context, Query query, Class<Articulo> itemClass, ArrayList<Articulo> items,
                            @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.mContext = context;
    }

    @Override
    public ArticulosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.articulo_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ArticulosAdapter.ViewHolder viewHolder, final int position) {
        Articulo model = getItem(position);
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(model.thumbnail);
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
                Intent intent = new Intent(c, ArticuloActivity.class);
                intent.putExtra(ArticuloActivity.EXTRA_POST_KEY, key);
                intent.putExtra(ArticuloActivity.EXTRA_POST_TITLE, title);
                c.startActivity(intent);
            }
        });
        viewHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context c = v.getContext();
                Intent intent = new Intent(c, ArticuloActivity.class);
                intent.putExtra(ArticuloActivity.EXTRA_POST_KEY, key);
                intent.putExtra(ArticuloActivity.EXTRA_POST_TITLE, title);
                c.startActivity(intent);
            }
        });
    }

    @Override
    protected void itemAdded(Articulo item, String key, int position) {

    }

    @Override
    protected void itemChanged(Articulo oldItem, Articulo newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Articulo item, String key, int position) {

    }

    @Override
    protected void itemMoved(Articulo item, String key, int oldPosition, int newPosition) {

    }


}
