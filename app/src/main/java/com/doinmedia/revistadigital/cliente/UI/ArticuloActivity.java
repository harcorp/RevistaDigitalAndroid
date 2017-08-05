package com.doinmedia.revistadigital.cliente.UI;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.doinmedia.revistadigital.cliente.Adapters.ComentarioAdapter;
import com.doinmedia.revistadigital.cliente.Fragments.YoutubeFragment;
import com.doinmedia.revistadigital.cliente.Models.Articulo;
import com.doinmedia.revistadigital.cliente.Models.Comentario;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.TextAlert;
import com.doinmedia.revistadigital.cliente.Tools.VoiceAlert;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


/**
 * Created by davidrodriguez on 4/01/17.
 */

public class ArticuloActivity extends AppCompatActivity {

    private static final String TAG = ArticuloActivity.class.getSimpleName();

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_POST_TITLE= "post_title";

    private String mKey, mTitle, mParent, mUserUid;

    private TextView mTitulo, mDescripcion;
    private DatabaseReference mRef;
    public static final int RequestPermissionCode = 1;

    private RecyclerView mRecycler;
    private ComentarioAdapter mAdapter;
    private ArrayList<Comentario> mAdapterItems;
    private ArrayList<String> mAdapterKeys;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articulo);
        SharedPreferences prefs = getSharedPreferences("RevistaPlanteamientosPrefs",Context.MODE_PRIVATE);
        mParent = prefs.getString("last_uid", null);
        mKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mRef = FirebaseDatabase.getInstance().getReference();
        mTitle = getIntent().getStringExtra(EXTRA_POST_TITLE);
        setTitle(mTitle);

        mTitulo = (TextView)findViewById(R.id.single_titulo);
        mDescripcion = (TextView)findViewById(R.id.single_descripcion);
        mTitulo.setText(mTitle);
        configurarRecycler();
        mRef.child("articulos").child(mParent).child(mKey).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Articulo data = dataSnapshot.getValue(Articulo.class);
                cargar_video(data.video);
                mDescripcion.setText(data.descripcion);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if(id == R.id.action_text){
                    Toast.makeText(getApplicationContext(), "Texto", Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    TextAlert alerta = TextAlert.addString(mKey);
                    alerta.show(fragmentManager, "tagAlerta");
                }else if(id == R.id.action_voice){
                    Toast.makeText(getApplicationContext(), "Voice", Toast.LENGTH_LONG).show();
                    if(checkPermission()) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        VoiceAlert dialogo = VoiceAlert.addSomeString(mKey);
                        dialogo.show(fragmentManager, "tagAlerta");
                    } else {
                        requestPermission();
                    }
                }else if(id == R.id.action_video){
                    Toast.makeText(getApplicationContext(), "Video", Toast.LENGTH_LONG).show();

                }
                return false;
            }
        });
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    mUserUid = user.getUid();
                    Log.d(TAG, "Logged");
                    fabSpeedDial.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "NO LOGGED");
                    fabSpeedDial.setVisibility(View.GONE);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void configurarRecycler(){
        mRecycler = (RecyclerView) findViewById(R.id.comentario_recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.addItemDecoration(new SimpleDividerItemDecoration(this));
        Query query = mRef.child("comentarios").child(mKey).orderByChild("aproved").equalTo(true);
        mAdapter = new ComentarioAdapter(getApplicationContext(), query, Comentario.class, mAdapterItems, mAdapterKeys);
        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(ArticuloActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ArticuloActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void cargar_video(String id){
        Bundle args = new Bundle();
        args.putString("video_id", id);
        YoutubeFragment fragment = new YoutubeFragment();
        fragment.setArguments(args);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.main, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(ArticuloActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.destroy();
    }
}
