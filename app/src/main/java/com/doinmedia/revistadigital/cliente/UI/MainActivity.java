package com.doinmedia.revistadigital.cliente.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.Adapters.ArticulosAdapter;
import com.doinmedia.revistadigital.cliente.Adapters.BannerAdapter;
import com.doinmedia.revistadigital.cliente.Adapters.IndiceAdapter;
import com.doinmedia.revistadigital.cliente.Adapters.PublicacionAdapter;
import com.doinmedia.revistadigital.cliente.Fragments.YoutubeFragment;
import com.doinmedia.revistadigital.cliente.Models.Articulo;
import com.doinmedia.revistadigital.cliente.Models.Banner;
import com.doinmedia.revistadigital.cliente.Models.Dato;
import com.doinmedia.revistadigital.cliente.Models.Publicacion;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = MainActivity.class.getSimpleName();

    private DatabaseReference mRef;
    private RecyclerView recycler, indiceRecycler;
    private ArticulosAdapter mAdapter;
    private IndiceAdapter mIndiceAdapter;
    private ArrayList<Articulo> mAdapterItems;
    private ArrayList<String> mAdapterKeys;
    private ImageView mImagenInicio, mImagen2;
    private Button mPlay, mStop;
    private SeekBar mSeek;
    public Context mContext;
    private MediaPlayer mMediaPlayer;
    private Boolean mIsPrepared = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private Menu menu;

    private ViewPager mViewPager;
    private int bannerCount = 0;
    private ArrayList<String> imagesArray = new ArrayList<String>();
    private ArrayList<String> linksArray = new ArrayList<String>();
    private int currentPage = 0;
    private TextView mAudDes, mVideoDes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextAppearance(this, R.style.ActionBarTitle);

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
        mViewPager = (ViewPager) findViewById(R.id.banners_inicio);
        mImagenInicio = (ImageView) findViewById(R.id.inicio_imagen);
        mImagen2 = (ImageView) findViewById(R.id.inicio_imagen2);
        mSeek = (SeekBar) findViewById(R.id.inicio_audio_seek);
        mPlay = (Button) findViewById(R.id.inicio_audio_play);
        mStop = (Button) findViewById(R.id.inicio_audio_pause);
        mAudDes = (TextView) findViewById(R.id.inicio_audDescription);
        mVideoDes = (TextView) findViewById(R.id.videoDescription);


        mRef.child("datos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Dato dato = dataSnapshot.getValue(Dato.class);

                cargar_video(dato.videoId);
                mVideoDes.setText(dato.videoText);
                controlVoice(dato.audio);
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(dato.imagen);
                StorageReference ref2 = FirebaseStorage.getInstance().getReference().child(dato.imagen2);

                mAudDes.setText(dato.texto);
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .fitCenter()
                        .placeholder(R.drawable.spinner_animation)
                        .into(mImagenInicio);
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(ref2)
                        .fitCenter()
                        .placeholder(R.drawable.spinner_animation)
                        .into(mImagen2);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("banners").orderByChild("type").equalTo("2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    bannerCount += 1;
                    Banner banner = data.getValue(Banner.class);
                    imagesArray.add(banner.image);
                    linksArray.add(banner.link);
                    if( bannerCount == dataSnapshot.getChildrenCount()){
                        iniciarSliders();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        setupRecyclerView();
        setupIndiceRecycler();

    }

    private void cargar_video(String id){
        Bundle args = new Bundle();
        args.putString("video_id", id);
        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(args);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.inicio_video, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void iniciarSliders(){
        mViewPager.setAdapter(new BannerAdapter(MainActivity.this,imagesArray, linksArray));

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

    public void setupRecyclerView(){
        SharedPreferences prefs = getSharedPreferences("RevistaPlanteamientosPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_uid", "-Kay5nfVMHrstAhg6EOk");
        editor.apply();
        recycler = (RecyclerView) findViewById(R.id.reciclador_publicaciones);
        recycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recycler.setLayoutManager(mLayoutManager);

        recycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        recycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new ArticulosAdapter(getApplicationContext(), mRef.child("articulos/-Kay5nfVMHrstAhg6EOk").orderByChild("timestamp"), Articulo.class, mAdapterItems, mAdapterKeys);
        recycler.setAdapter(mAdapter);
        recycler.setVisibility(View.VISIBLE);
    }

    public void setupIndiceRecycler() {
        SharedPreferences prefs = getSharedPreferences("RevistaPlanteamientosPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("last_uid", "-Kay5nfVMHrstAhg6EOk");
        editor.apply();
        indiceRecycler = (RecyclerView) findViewById(R.id.reciclador_indice);
        indiceRecycler.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        indiceRecycler.setLayoutManager(mLayoutManager);
        indiceRecycler.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(5), true));
        indiceRecycler.setItemAnimator(new DefaultItemAnimator());
        mIndiceAdapter = new IndiceAdapter(getApplicationContext(), mRef.child("articulos/-Kay5nfVMHrstAhg6EOk").orderByChild("timestamp"), Articulo.class, mAdapterItems, mAdapterKeys);
        indiceRecycler.setAdapter(mIndiceAdapter);
        indiceRecycler.setVisibility(View.VISIBLE);
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
            LoginManager.getInstance().logOut();
            mAuth.signOut();
            Toast.makeText(mContext, "Cierre de sesión exitoso.", Toast.LENGTH_SHORT).show();

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

    private void controlVoice(final String file){
        final StorageReference mRef = FirebaseStorage.getInstance().getReference();
        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlay.setVisibility(View.GONE);
                mStop.setVisibility(View.VISIBLE);
                final MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        // briefly show the mediacontroller
                        final Handler seekHandler = new Handler();
                        Runnable updateSeekBar = new Runnable() {
                            public void run() {
                                if(mMediaPlayer != null){
                                    long totalDuration = mMediaPlayer.getDuration();
                                    long currentDuration = mMediaPlayer.getCurrentPosition();

                                    // Displaying Total Duration time
                                    //remaining.setText(""+ milliSecondsToTimer(totalDuration-currentDuration));
                                    // Displaying time completed playing
                                    //elapsed.setText(""+ milliSecondsToTimer(currentDuration));

                                    // Updating progress bar
                                    mSeek.setProgress((int)currentDuration);

                                    // Call this thread again after 15 milliseconds => ~ 1000/60fps
                                    seekHandler.postDelayed(this, 15);
                                    if(!mMediaPlayer.isPlaying()){
                                        seekHandler.removeCallbacks(this);
                                        mSeek.setProgress(0);
                                        mStop.setVisibility(View.GONE);
                                        mPlay.setVisibility(View.VISIBLE);
                                    }
                                }



                            }
                        };

                        try{
                            mIsPrepared = true;
                            mMediaPlayer.start();
                            mSeek.setMax(mMediaPlayer.getDuration());
                            // Updating progress bar
                            seekHandler.postDelayed(updateSeekBar, 15);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mRef.child(file).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                        try {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setOnPreparedListener(mPreparedListener);
                            mIsPrepared = false;
                            mMediaPlayer.setDataSource(uri.toString());
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mMediaPlayer.prepareAsync();
                        } catch (IOException ex) {
                            Log.w("MyAudioView", "Unable to open content: " + uri, ex);
                            return;
                        } catch (IllegalArgumentException ex) {
                            Log.w("MyAudioView", "Unable to open content: " + uri, ex);
                            return;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

            }
        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mStop.setVisibility(View.GONE);
                mPlay.setVisibility(View.VISIBLE);

                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    //mMediaPlayer.release();
                    //mMediaPlayer = null;
                }

            }
        });
    }

    private void signOut() {
        mAuth.signOut();
    }

    @Override
    public void onStop(){
        super.onStop();

    }
}
