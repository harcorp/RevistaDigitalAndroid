package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.Models.Articulos;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.doinmedia.revistadigital.cliente.Tools.Tools;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class LibreriaAdapter extends FirebaseRecyclerAdapter<LibreriaAdapter.ViewHolder, Articulos> {

    private Context mContext;
    private boolean urlReady = false;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, descripcion, fecha;
        public ImageView thumbnail;
        public Button descargar;

        public ViewHolder(View itemView){
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.file_list_nombre);
            descripcion = (TextView) itemView.findViewById(R.id.file_list_descripcion);
            fecha = (TextView) itemView.findViewById(R.id.file_list_fecha);
            thumbnail = (ImageView) itemView.findViewById(R.id.file_list_image);
            descargar = (Button) itemView.findViewById(R.id.file_list_download);
        }
    }

    public LibreriaAdapter(Context context, Query query, Class<Articulos> itemClass, ArrayList<Articulos> items,
                            @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.mContext = context;
    }

    @Override
    public LibreriaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_list, parent, false);

        return new ViewHolder(view);
    }

    private String getDate(long time) {
        time = time * -1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    @Override
    public void onBindViewHolder(final LibreriaAdapter.ViewHolder view, final int position) {
        final Articulos articulo = getItem(position);
        view.nombre.setText(articulo.nombre);
        view.descripcion.setText(articulo.descripcion);
        view.fecha.setText(articulo.type +  " - " + getDate(articulo.fecha));

        Drawable mDrawable = Tools.getDrawable(mContext, R.drawable.ic_file_download);
        mDrawable.setBounds( 0, 0, 60, 60 );
        mDrawable.setTint(Tools.getColor(mContext, R.color.black));

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(articulo.thumbnail);

        Glide.with(mContext)
                .using(new FirebaseImageLoader())
                .load(ref)
                .fitCenter()
                .placeholder(R.drawable.spinner_animation)
                .into(view.thumbnail);

        view.descargar.setCompoundDrawables(mDrawable, null, null, null);
        /*StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        mStorageRef.child(articulo.file).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                urlReady = true;
                articulo.file = uri.toString();
            }
        });*/
        view.descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            beginDownload(articulo.url);

            }
        });
    }

    private void beginDownload(String url) {
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    @Override
    protected void itemAdded(Articulos item, String key, int position) {

    }

    @Override
    protected void itemChanged(Articulos oldItem, Articulos newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Articulos item, String key, int position) {

    }

    @Override
    protected void itemMoved(Articulos item, String key, int oldPosition, int newPosition) {

    }
}
