package com.doinmedia.revistadigital.cliente.UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.R;


public class ContactoActivity extends AppCompatActivity {

    public static final String TAG = ContactoActivity.class.getSimpleName();

    private static final String urlFacebook = "https://www.facebook.com/Revista-Planteamientos-En-Educaci%C3%B3n-353670338419375/";
    private static final String urlTwitter = "https://twitter.com/rplanteamientos";

    private TextView mVersion;
    private Button mFacebook, mTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        mVersion = (TextView) findViewById(R.id.contacto_version);
        mFacebook = (Button) findViewById(R.id.contacto_facebook);
        mTwitter = (Button) findViewById(R.id.contacto_twitter);

        mFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Entro facebook");
                Uri uri = Uri.parse(urlFacebook); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        mTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri  = Uri.parse(urlTwitter);
                Intent intent  = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
