package com.doinmedia.revistadigital.cliente.Adapters;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.doinmedia.revistadigital.cliente.Models.Comentario;
import com.doinmedia.revistadigital.cliente.R;

import com.doinmedia.revistadigital.cliente.UI.VideoPlayerActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by davidrodriguez on 27/12/16.
 */

public class ComentarioAdapter extends FirebaseRecyclerAdapter<ComentarioAdapter.ViewHolder, Comentario> {

    private Context mContext;
    private DatabaseReference mRef;
    private MediaPlayer mMediaPlayer;
    private Boolean mIsPrepared = false;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mNombreUsuario, mComentarioTexto;
        public LinearLayout mAudio;
        public SeekBar mAudioControl;
        public Button mAudioPlay, mAudioPause, mVideoPlay;

        public ViewHolder(View view){
            super(view);

            mNombreUsuario = (TextView)view.findViewById(R.id.comentario_usuario);
            mComentarioTexto = (TextView)view.findViewById(R.id.comentario_texto);
            mAudio = (LinearLayout)view.findViewById(R.id.comentario_audio_layout);
            mAudioControl = (SeekBar)view.findViewById(R.id.comentario_audio_control);
            mAudioPlay = (Button)view.findViewById(R.id.comentario_audio_play);
            mAudioPause = (Button)view.findViewById(R.id.comentario_audio_pause);
            mVideoPlay = (Button) view.findViewById(R.id.comentario_video_play);
        }
    }

    public ComentarioAdapter(Context context, Query query, Class<Comentario> itemClass, ArrayList<Comentario> items,
                              @Nullable ArrayList<String> keys) {
        super(query, itemClass, items, keys);
        this.mContext = context;
    }


    @Override
    public ComentarioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comentario_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ComentarioAdapter.ViewHolder viewHolder, final int position) {
        Comentario model = getItem(position);
        mRef = FirebaseDatabase.getInstance().getReference();
        viewHolder.mAudioControl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        switch (model.getType()){
            case 1:
                viewHolder.mAudio.setVisibility(View.GONE);
                viewHolder.mVideoPlay.setVisibility(View.GONE);
                viewHolder.mComentarioTexto.setVisibility(View.VISIBLE);
                viewHolder.mComentarioTexto.setText(model.texto);
                break;
            case 2:
                viewHolder.mAudio.setVisibility(View.VISIBLE);
                viewHolder.mVideoPlay.setVisibility(View.GONE);
                viewHolder.mComentarioTexto.setText(String.valueOf(model.getType()));
                controlVoice(viewHolder.mAudioPlay, viewHolder.mAudioPause, viewHolder.mAudioControl, model.file);
                break;
            case 3:
                viewHolder.mComentarioTexto.setVisibility(View.GONE);
                viewHolder.mAudio.setVisibility(View.GONE);
                viewHolder.mVideoPlay.setVisibility(View.VISIBLE);
                controlVideo(viewHolder.mVideoPlay, model.file);
        }
        mRef.child("users/" + model.uid_user + "/nombre").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String nombre = dataSnapshot.getValue().toString();
                viewHolder.mNombreUsuario.setText(nombre);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void itemAdded(Comentario item, String key, int position) {

    }

    @Override
    protected void itemChanged(Comentario oldItem, Comentario newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Comentario item, String key, int position) {

    }

    @Override
    protected void itemMoved(Comentario item, String key, int oldPosition, int newPosition) {

    }

    private void controlVideo(final Button play, final String file){

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StorageReference mRef = FirebaseStorage.getInstance().getReference();
                mRef.child(file).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent intent = new Intent(mContext, VideoPlayerActivity.class);
                        intent.putExtra(VideoPlayerActivity.EXTRA_POST_KEY, uri.toString());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                    }
                });
            }
        });
    }

    private void controlVoice(final Button play, final Button pause, final SeekBar control, final String file){
        final StorageReference mRef = FirebaseStorage.getInstance().getReference();
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
                final MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        // briefly show the mediacontroller
                        final Handler seekHandler = new Handler();
                        Runnable updateSeekBar = new Runnable() {
                            public void run() {
                                if(mMediaPlayer != null){
                                    long totalDuration = mMediaPlayer.getDuration();
                                    long currentDuration = mMediaPlayer.getCurrentPosition();

                                    // Displaying Total Duration time
                                    //remaining.setText(""+ milliSecondsToTimer(totalDuration-currentDuration));
                                    // Displaying time completed playing
                                    //elapsed.setText(""+ milliSecondsToTimer(currentDuration));

                                    // Updating progress bar
                                    control.setProgress((int)currentDuration);

                                    // Call this thread again after 15 milliseconds => ~ 1000/60fps
                                    seekHandler.postDelayed(this, 15);
                                    if(!mMediaPlayer.isPlaying()){
                                        seekHandler.removeCallbacks(this);
                                        control.setProgress(0);
                                        pause.setVisibility(View.GONE);
                                        play.setVisibility(View.VISIBLE);
                                    }
                                }



                            }
                        };

                        try{
                            mIsPrepared = true;
                            mMediaPlayer.start();
                            control.setMax(mMediaPlayer.getDuration());
                            // Updating progress bar
                            seekHandler.postDelayed(updateSeekBar, 15);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mRef.child(file).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.reset();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                        try {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setOnPreparedListener(mPreparedListener);
                            mIsPrepared = false;
                            mMediaPlayer.setDataSource(uri.toString());
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mMediaPlayer.prepareAsync();
                        } catch (IOException ex) {
                            Log.w("MyAudioView", "Unable to open content: " + uri, ex);
                            return;
                        } catch (IllegalArgumentException ex) {
                            Log.w("MyAudioView", "Unable to open content: " + uri, ex);
                            return;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("TAG", e.getMessage());
                    }
                });

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);

                if (mMediaPlayer != null) {
                    mMediaPlayer.pause();
                    //mMediaPlayer.release();
                    //mMediaPlayer = null;
                }

            }
        });
    }
}
