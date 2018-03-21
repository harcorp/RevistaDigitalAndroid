package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.Models.Articulo;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.UI.ArticuloActivity;
import com.google.firebase.database.Query;

import java.util.ArrayList;

/**
 * Created by DRODRIGUEZ on 21/03/2018.
 */

public class IndiceAdapter extends FirebaseRecyclerAdapter<IndiceAdapter.ViewHolder, Articulo> {

    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitulo, mAutor;
        public LinearLayout mLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitulo = (TextView) itemView.findViewById(R.id.indice_titulo);
            mAutor = (TextView) itemView.findViewById(R.id.indice_autor);
            mLayout = (LinearLayout) itemView.findViewById(R.id.indice_layout);
        }
    }

    public IndiceAdapter(Context context, Query query, Class<Articulo> itemClass,
                         ArrayList<Articulo> items, @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.mContext = context;
    }

    @Override
    public IndiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.indice_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IndiceAdapter.ViewHolder viewHolder, int position) {

        Articulo model = getItem(position);
        viewHolder.mTitulo.setText(model.titulo);
        viewHolder.mAutor.setText(model.author);

        final String key = getKey(position);
        final String title = model.titulo;

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context c = view.getContext();
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
