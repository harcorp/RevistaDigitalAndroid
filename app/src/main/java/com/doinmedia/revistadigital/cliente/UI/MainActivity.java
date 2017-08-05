package com.doinmedia.revistadigital.cliente.UI;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;


import com.doinmedia.revistadigital.cliente.Adapters.PublicacionAdapter;
import com.doinmedia.revistadigital.cliente.Models.Publicacion;
import com.doinmedia.revistadigital.cliente.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference mRef;
    private RecyclerView recycler;
    private PublicacionAdapter mAdapter;
    private ArrayList<Publicacion> mAdapterItems;
    private ArrayList<String> mAdapterKeys;
    public Context mContext;
    private ProgressBar mProgress;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mRef = FirebaseDatabase.getInstance().getReference();
        mContext = this.getApplicationContext();
        mProgress = (ProgressBar) findViewById(R.id.load_publicaciones);
        mProgress.setVisibility(View.VISIBLE);
        setupRecyclerView();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(TAG, "loggeado");
                    showLoggedMenu(true, false);
                } else {
                    Log.d(TAG, "no loggeado");
                    showLoggedMenu(false, true);
                }
            }
        };
    }

    public void setupRecyclerView(){
        recycler = (RecyclerView) findViewById(R.id.reciclador_publicaciones);
        recycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(mLayoutManager);
        recycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new PublicacionAdapter(getApplicationContext(), mRef.child("publicaciones/"), Publicacion.class, mAdapterItems, mAdapterKeys);
        recycler.setAdapter(mAdapter);
        recycler.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.main, menu);

        if(mUser != null){
            showLoggedMenu(true, false);
        }else{
            showLoggedMenu(false, true);
        }
        return true;
    }

    public void showLoggedMenu(boolean showUno, boolean showDos){
        if(this.menu == null)
            return;
        this.menu.setGroupVisible(R.id.action_group_logged, showUno);
        this.menu.findItem(R.id.action_login).setVisible(showDos);
    }



    @Override
    public void onStart() {
        super.onStart();
        mUser = mAuth.getCurrentUser();
        mAuth.addAuthStateListener(mAuthListener);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if(id == R.id.action_logout){
            signOut();
        } else if(id == R.id.action_perfil){
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_libreria) {
            Intent intent = new Intent(MainActivity.this, LibreriaActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_articulos) {
            Intent intent = new Intent(MainActivity.this, ArticulosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_quienes_somos) {
            Intent intent = new Intent(MainActivity.this, QuienesSomosActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_historia) {
            Intent intent = new Intent(MainActivity.this, HistoriaActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_contacto) {
            Intent intent = new Intent(MainActivity.this, ContactoActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;


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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }

    private void signOut() {
        mAuth.signOut();
    }
}
