package com.doinmedia.revistadigital.cliente.Tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.doinmedia.revistadigital.cliente.Models.Comentario;
import com.doinmedia.revistadigital.cliente.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by davidrodriguez on 24/01/17.
 */

public class VoiceAlert extends DialogFragment {


    String TAG = "Prueba";
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    String NameOfFile = null;
    MediaPlayer mediaPlayer ;
    FloatingActionButton record;
    private LinearLayout mConfirmar;
    private Button mReplay;
    private Button mAgregar;
    private ProgressBar mProgress;
    private DatabaseReference mRef;
    private static String mKey, mUid;

    public TextView timerTextView, mInfoText;
    private long startHTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long updatedTime = 0L;
    private static final Integer MAX_DURATION = 59;
    private FirebaseAuth mAuth;

    public static VoiceAlert addSomeString(String temp){
        VoiceAlert f = new VoiceAlert();
        mKey = temp;
        return f;
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_voice_recoder, null);

        builder.setView(v);

        mAuth = FirebaseAuth.getInstance();
        mUid = mAuth.getCurrentUser().getUid();

        record = (FloatingActionButton) v.findViewById(R.id.voice_record);
        Button reproducir = (Button) v.findViewById(R.id.voice_play);
        timerTextView = (TextView) v.findViewById(R.id.duration_text);
        mInfoText = (TextView) v.findViewById(R.id.info_text);
        mConfirmar = (LinearLayout) v.findViewById(R.id.voice_confirmar);
        mReplay = (Button) v.findViewById(R.id.voice_again);
        mAgregar = (Button) v.findViewById(R.id.voice_add);
        mProgress = (ProgressBar) v.findViewById(R.id.voice_progress);
        mRef = FirebaseDatabase.getInstance().getReference();
        Log.d(TAG, mKey);
        reproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(AudioSavePathInDevice);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    Toast.makeText(getActivity(), "Recording Playing",
                            Toast.LENGTH_LONG).show();

            }
        });

        mReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }else{
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
                mConfirmar.setVisibility(View.GONE);
                record.setVisibility(View.VISIBLE);
            }
        });

        mAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AudioSavePathInDevice != null){
                    mConfirmar.setVisibility(View.GONE);
                    mInfoText.setText("Publicando...");
                    mProgress.setVisibility(View.VISIBLE);
                    cargarDatos();
                }else {
                    Toast.makeText(getActivity(), "No se puede subir el comentario. Intente de nuevo", Toast.LENGTH_LONG).show();
                }
            }
        });

        record.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("TAG", "touched down");
                        grabarAudio();
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i("TAG", "touched up");
                        pararGrabacion();
                        break;
                }
                return true;
            }
        });

        return builder.create();
    }

    private void cargarDatos(){
        String key = mRef.child("comentarios/" + mKey).push().getKey();
        final String filename = "audios/" + mKey + "/" + NameOfFile + ".3gp";
        Comentario comentario = new Comentario(mUid, filename, null, false, 2);
        Map<String, Object> postValues = comentario.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/comentarios/" + mKey + "/" + key, postValues);
        childUpdates.put("/user-comentarios/" + mUid + "/" + mKey + "/" + key, postValues);
        mRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                subirAudio(filename);
            }
        });

    }

    private void subirAudio(String filename){
        Uri file = Uri.fromFile(new File(AudioSavePathInDevice));
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/3gp")
                .build();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        UploadTask uploadTask = storageReference.child(filename).putFile(file, metadata);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d(TAG, "Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                File dir = new File(AudioSavePathInDevice);
                delete(dir);
                Toast.makeText(getActivity(), "Comentario publicado, debe ser aprobado.", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
    }

    private void pararGrabacion(){
        timerTextView.setVisibility(View.GONE);
        record.setVisibility(View.GONE);
        mConfirmar.setVisibility(View.VISIBLE);
        mediaRecorder.stop();
        //timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
        Toast.makeText(getActivity(), "Recording Completed",
                Toast.LENGTH_LONG).show();
    }

    private void grabarAudio(){
        if(AudioSavePathInDevice == null){
            random = new Random();
            NameOfFile = CreateRandomAudioFileName(10) + mUid;
            AudioSavePathInDevice =
                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                             NameOfFile + ".3gp";
        }
        timerTextView.setVisibility(View.VISIBLE);

        MediaRecorderReady();

        try {
            Log.d(TAG, AudioSavePathInDevice);
            mediaRecorder.setMaxDuration(MAX_DURATION * 1000);
            mediaRecorder.prepare();
            mediaRecorder.start();
            startHTime = SystemClock.uptimeMillis();
            customHandler.postDelayed(updateTimerThread, 0);
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Toast.makeText(getActivity(), "Recording started",
                Toast.LENGTH_LONG).show();

    }

    public void MediaRecorderReady(){
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startHTime;

            updatedTime = timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            //secs = secs % 60;
            if (timerTextView != null)
                if(secs > MAX_DURATION - 15){
                    timerTextView.setTextColor(getColor(getActivity(), R.color.colorAccent));
                }
                if(secs > MAX_DURATION){
                    pararGrabacion();
                }else{
                    //timerTextView.setText("" + String.format("%02d", mins) + ":"
                            //+ String.format("%02d", secs));
                    timerTextView.setText("00:" + String.format("%02d", secs) + " / 00:59");
                }

            customHandler.postDelayed(this, 0);
        }

    };

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

    public static boolean delete(File path) {
        boolean result = true;
        if (path.exists()) {
            if (path.isDirectory()) {
                for (File child : path.listFiles()) {
                    result &= delete(child);
                }
                result &= path.delete(); // Delete empty directory.
            } else if (path.isFile()) {
                result &= path.delete();
            }
            return result;
        } else {
            return false;
        }
    }
}