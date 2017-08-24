package com.doinmedia.revistadigital.cliente.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.Adapters.ArticulosAdapter;
import com.doinmedia.revistadigital.cliente.Models.Articulo;
import com.doinmedia.revistadigital.cliente.Models.Publicacion;
import com.doinmedia.revistadigital.cliente.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by davidrodriguez on 4/01/17.
 */

public class PublicacionActivity extends AppCompatActivity {

    private static final String TAG = PublicacionActivity.class.getSimpleName();

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_POST_TITLE= "post_title";

    private String mKey;
    private String mTitle;
    private ArrayList<Articulo> mAdapterItems;
    private ArrayList<String> mAdapterKeys;

    private TextView mTitulo, mDescripcion;
    private RecyclerView mReciclador;
    private ProgressBar mProgress;
    private ArticulosAdapter mAdapter;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publicacion);

        mKey = getIntent().getStringExtra(EXTRA_POST_KEY);

        if(mKey == null){
            SharedPreferences prefs =
                    getSharedPreferences("RevistaPlanteamientosPrefs",Context.MODE_PRIVATE);

            mKey = prefs.getString("last_uid", null);
        }else{
            SharedPreferences prefs =
                    getSharedPreferences("RevistaPlanteamientosPrefs",Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("last_uid", mKey);
            editor.commit();
        }
        mTitle = getIntent().getStringExtra(EXTRA_POST_TITLE);
        setTitle(mTitle);

        mRef = FirebaseDatabase.getInstance().getReference();

        mTitulo = (TextView)findViewById(R.id.publicacion_titulo);
        mDescripcion = (TextView)findViewById(R.id.publicacion_descripcion);
        mReciclador = (RecyclerView)findViewById(R.id.publicacion_reciclador);
        mProgress = (ProgressBar)findViewById(R.id.publicacion_progress);

        mReciclador.setVisibility(View.GONE);
        mTitulo.setText(mTitle);
        setupRecycler();
        setupDatos();

    }

    private void setupDatos(){
        mRef.child("publicaciones/" + mKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Publicacion data = dataSnapshot.getValue(Publicacion.class);

                mDescripcion.setText(data.descripcion);
                mTitulo.setText(data.titulo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setupRecycler(){
        mReciclador.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mReciclador.setLayoutManager(mLayoutManager);
        mReciclador.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        mReciclador.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ArticulosAdapter(getApplicationContext(), mRef.child("articulos/" + mKey), Articulo.class, mAdapterItems, mAdapterKeys);
        mReciclador.setAdapter(mAdapter);
        mReciclador.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
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
        mAdapter.destroy();
    }
}
