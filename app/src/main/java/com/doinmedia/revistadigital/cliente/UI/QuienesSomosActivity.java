package com.doinmedia.revistadigital.cliente.UI;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Tools.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class QuienesSomosActivity extends AppCompatActivity {

    private DatabaseReference mRef;
    private ImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quienessomos);

        mRef = FirebaseDatabase.getInstance().getReference();
        mImage = (ImageView) findViewById(R.id.quienessomos_image);

        mRef.child("datos").child("quienesSomos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(dataSnapshot.getValue().toString());
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(ref)
                        .fitCenter()
                        .placeholder(R.drawable.spinner_animation)
                        .into(mImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
