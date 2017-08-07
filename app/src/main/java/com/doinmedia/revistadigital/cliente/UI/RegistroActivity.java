package com.doinmedia.revistadigital.cliente.UI;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.doinmedia.revistadigital.cliente.Adapters.FirebaseRecyclerAdapter;
import com.doinmedia.revistadigital.cliente.Models.Usuario;
import com.doinmedia.revistadigital.cliente.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistroActivity extends BaseActivity {

    public static final String TAG = RegistroActivity.class.getSimpleName();

    private Button mBoton;
    private EditText mNombre, mCorreo, mPassword;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mBoton = (Button) findViewById(R.id.registro_button);
        mNombre = (EditText) findViewById(R.id.registro_nombre);
        mCorreo = (EditText) findViewById(R.id.registro_correo);
        mPassword = (EditText) findViewById(R.id.registro_password);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mRef = database.getReference();

        mBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registro(mNombre.getText().toString(), mCorreo.getText().toString(), mPassword.getText().toString());
            }
        });
    }

    private void registro(final String nombre, String correo, String password){
        if(!validateForm()){
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(correo, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = task.getResult().getUser();
                    final String uid = user.getUid();
                    Log.d(TAG, uid);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(nombre)
                            .build();
                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "Actualizao nombre");
                            Usuario usuario = new Usuario(nombre, null);
                            mRef.child("users").child(uid).setValue(usuario)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d(TAG, "Creo registro");
                                            Toast.makeText(RegistroActivity.this, "Registro Completo", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }
                    });

                }
            }
        });
    }

    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean validateForm() {
        boolean valid = true;

        String nombre = mNombre.getText().toString();
        if(TextUtils.isEmpty(nombre)){
            mNombre.setError("Requerido");
            valid = false;
        } else {
            mNombre.setError(null);
        }

        String email = mCorreo.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mCorreo.setError("Requerido.");
            valid = false;
        } else if (!isValidEmail(email)){
            mCorreo.setError("Correo invalido");
            valid = false;
        } else {
            mCorreo.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Requerido.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }
}
