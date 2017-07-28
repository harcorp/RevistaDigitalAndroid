package com.doinmedia.revistadigital.cliente.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.R;

/**
 * Created by davidrodriguez on 4/01/17.
 */

public class LoginActivity extends AppCompatActivity {

    private TextView mRegistrarse;
    private EditText mCorreo, mPassword;
    private Button mIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRegistrarse = (TextView) findViewById(R.id.login_registrarse);
        mCorreo = (EditText) findViewById(R.id.login_correo);
        mPassword = (EditText) findViewById(R.id.login_password);
        mIngresar = (Button) findViewById(R.id.login_ingresar);


        mIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
