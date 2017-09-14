package com.doinmedia.revistadigital.cliente.Tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doinmedia.revistadigital.cliente.Models.Comentario;
import com.doinmedia.revistadigital.cliente.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class TextAlert extends DialogFragment {

    private static String mKey, mUid, mParent;
    private TextView mInfoText, mContador;
    private EditText mComentario;
    private Button mEnviar;

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    public static TextAlert addString(String temp, String parent){
        TextAlert f = new TextAlert();
        mKey = temp;
        mParent = parent;
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_text, null);
        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        mInfoText = (TextView)v.findViewById(R.id.comentario_info_text);
        mContador = (TextView)v.findViewById(R.id.comentario_text_contador);
        mComentario = (EditText)v.findViewById(R.id.comentario_text_edit);
        mEnviar = (Button)v.findViewById(R.id.comentario_text_publicar);
        mRef = FirebaseDatabase.getInstance().getReference();

        mComentario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String tamanoString = String.valueOf(s.length());
                mContador.setText(tamanoString + "/3000");
                if(s.length() == 3000){
                    mContador.setTextColor(getColor(getActivity(), R.color.colorAccent));
                }
            }
        });

        mEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mComentario.length() > 10){
                    publicarComentario();
                }else{
                    Toast.makeText(getActivity(), "Comentario debe ser mayor a 10 carácteres", Toast.LENGTH_LONG).show();
                    getDialog().dismiss();
                }
            }
        });

        builder.setView(v);

        return builder.create();
    }

    private void publicarComentario(){
        String key = mRef.child("comentarios/" + mKey).push().getKey();
        String coment = mComentario.getText().toString();
        Comentario comentario = new Comentario(mUid, null, coment, false, 1, mParent);
        Map<String, Object> postValues = comentario.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comentarios/" + mKey + "/" + key, postValues);
        childUpdates.put("/user-comentarios/" + mUid + "/" + mKey + "/" + key, postValues);
        mRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Comentario publicado. Debe ser aprobado!", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
    }

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }
}
