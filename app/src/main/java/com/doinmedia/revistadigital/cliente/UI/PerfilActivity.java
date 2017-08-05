package com.doinmedia.revistadigital.cliente.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class PerfilActivity extends BaseActivity {

    public static final String TAG = PerfilActivity.class.getSimpleName();

    private FirebaseAuth mAuth;

    private TextView mNombrePerfil;
    private ImageView mPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();

        mNombrePerfil = (TextView) findViewById(R.id.perfil_name);
        mPerfil = (ImageView) findViewById(R.id.perfil_imagen);

        if(mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            mNombrePerfil.setText(user.getDisplayName());
            if(user.getPhotoUrl() == null){
                Glide.with(this).load(R.drawable.profile_image).centerCrop()
                        .bitmapTransform(new CropCircleTransformation(this)).into(mPerfil);
            }else{
                Glide.with(this).load(user.getPhotoUrl()).centerCrop()
                        .bitmapTransform(new CropCircleTransformation(this)).into(mPerfil);
            }
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}
