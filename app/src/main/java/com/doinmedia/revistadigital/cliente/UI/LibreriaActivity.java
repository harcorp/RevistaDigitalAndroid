package com.doinmedia.revistadigital.cliente.UI;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.Adapters.LibreriaAdapter;
import com.doinmedia.revistadigital.cliente.Models.Articulos;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Services.DownloadService;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.doinmedia.revistadigital.cliente.Tools.Tools;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by davidrodriguez on 27/12/16.
 */

public class LibreriaActivity extends AppCompatActivity {

    public static final String TAG = LibreriaActivity.class.getSimpleName();

    private RecyclerView mReciclador;
    private DatabaseReference mRef;
    private LibreriaAdapter mAdapter;


    private ArrayList<Articulos> mAdapterItems;
    private ArrayList<String> mAdapterKeys;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libreria);

        mRef = FirebaseDatabase.getInstance().getReference();

        mReciclador = (RecyclerView)findViewById(R.id.reciclador_libreria);
        mRef = FirebaseDatabase.getInstance().getReference();
        mReciclador.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mReciclador.setLayoutManager(mLayoutManager);
        mReciclador.addItemDecoration(new LibreriaActivity.GridSpacingItemDecoration(2, dpToPx(5), true));
        mReciclador.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new LibreriaAdapter(getApplicationContext(),
                mRef.child("biblioteca_libreria"),
                Articulos.class,
                mAdapterItems, mAdapterKeys);

        mReciclador.setAdapter(mAdapter);

    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
