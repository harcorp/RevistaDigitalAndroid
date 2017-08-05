package com.doinmedia.revistadigital.cliente.UI;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.doinmedia.revistadigital.cliente.Models.Articulos;
import com.doinmedia.revistadigital.cliente.R;
import com.doinmedia.revistadigital.cliente.Services.DownloadService;
import com.doinmedia.revistadigital.cliente.Tools.Tools;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by davidrodriguez on 27/12/16.
 */

public class LibreriaActivity extends AppCompatActivity {

    public static final String TAG = LibreriaActivity.class.getSimpleName();

    private ListView mLista;
    private DatabaseReference mRef;
    private FirebaseListAdapter<Articulos> mAdapter;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;
    private String fileUrl;
    private StorageReference mStorageRef;
    private boolean urlReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libreria);

        mLista = (ListView)findViewById(R.id.file_list_view_2);
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case DownloadService.DOWNLOAD_COMPLETED:
                        // Get number of bytes downloaded
                        long numBytes = intent.getLongExtra(DownloadService.EXTRA_BYTES_DOWNLOADED, 0);

                        // Alert success
                        showMessageDialog(getString(R.string.success), String.format(Locale.getDefault(),
                                "%d bytes downloaded from %s",
                                numBytes,
                                intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                    case DownloadService.DOWNLOAD_ERROR:
                        // Alert failure
                        showMessageDialog("Error", String.format(Locale.getDefault(),
                                "Failed to download from %s",
                                intent.getStringExtra(DownloadService.EXTRA_DOWNLOAD_PATH)));
                        break;
                }
            }

        };

        mAdapter = new FirebaseListAdapter<Articulos>(this, Articulos.class, R.layout.file_list, mRef.child("biblioteca_libreria")) {
            @Override
            protected void populateView(View view, Articulos articulo, int position) {
                ((TextView)view.findViewById(R.id.file_list_nombre)).setText(articulo.getNombre());
                ((TextView)view.findViewById(R.id.file_list_descripcion)).setText(articulo.getDescripcion());
                ((TextView)view.findViewById(R.id.file_list_fecha)).setText(getDate(articulo.getFecha()));
                Drawable mDrawable = Tools.getDrawable(getApplicationContext(), R.drawable.ic_file_download);
                mDrawable.setBounds( 0, 0, 60, 60 );
                mDrawable.setTint(Tools.getColor(getApplicationContext(), R.color.black));
                final Button mDescargar = (Button)view.findViewById(R.id.file_list_download);
                mDescargar.setCompoundDrawables(mDrawable, null, null, null);
                fileUrl = articulo.getFile();
                mStorageRef.child(fileUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        urlReady = true;
                        fileUrl = uri.toString();
                    }
                });
                mDescargar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(urlReady){
                            beginDownload(fileUrl);
                        }else{
                            Toast.makeText(getApplicationContext(), "No se ha podido descargar. Intente de nuevo", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        };
        mLista.setAdapter(mAdapter);
    }

    private String getDate(long time) {
        time = time * -1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mAdapter.cleanup();
    }

    private void beginDownload(String url) {
        // Kick off MyDownloadService to download the file
        /*Intent intent = new Intent(this, DownloadService.class)
                .putExtra(DownloadService.EXTRA_DOWNLOAD_PATH, url)
                .setAction(DownloadService.ACTION_DOWNLOAD);
        startService(intent);

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_downloading));*/
        Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, DownloadService.getIntentFilter());
    }

    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    private void showMessageDialog(String title, String message) {
        AlertDialog ad = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .create();
        ad.show();
    }

    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
