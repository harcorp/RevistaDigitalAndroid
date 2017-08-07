package com.doinmedia.revistadigital.cliente.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashActivity.this, MainActivity.class));

        finish();
    }
}
