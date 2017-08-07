package com.doinmedia.revistadigital.cliente.UI;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.doinmedia.revistadigital.cliente.Fragments.Camera2VideoFragment;
import com.doinmedia.revistadigital.cliente.R;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();
        }
    }

}
