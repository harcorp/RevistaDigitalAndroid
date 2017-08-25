package com.doinmedia.revistadigital.cliente.UI;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.doinmedia.revistadigital.cliente.R;

public class VideoPlayerActivity extends Activity{

    public static final String TAG = VideoPlayerActivity.class.getSimpleName();

    public static final String EXTRA_POST_KEY = "post_key";
    private String mUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        mUrl = getIntent().getStringExtra(EXTRA_POST_KEY);

        Log.d(TAG, mUrl);

    }

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
    }
}
