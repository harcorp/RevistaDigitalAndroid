package com.doinmedia.revistadigital.cliente.UI;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.R;

/**
 * Created by davidrodriguez on 4/01/17.
 */

public class ContactoActivity extends AppCompatActivity {

    private TextView mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        mVersion = (TextView) findViewById(R.id.contacto_version);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            mVersion.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
