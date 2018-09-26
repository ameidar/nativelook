package com.work.nativelook.nativelook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btnRecord,btnStopRecord,btnPlay,btnStop;
    MediaRecorder mediaRecorder ;
    MediaPlayer mediaPlayer;


    public static final  int REQUEST_PERMISSION_WRITE = 1000;
    private boolean permissionGranted;
    private String pathSave;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecord = findViewById(R.id.btnStartRecord);
        btnStopRecord = findViewById(R.id.btnStopRecord);
        btnPlay = findViewById(R.id.btnPlay);
        btnStop = findViewById(R.id.btnStop);


        checkPermissions();

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pathSave = Environment.getExternalStorageDirectory().getAbsolutePath()
                        +"/"+ UUID.randomUUID().toString() + "_audio.3gp";
                setupMediaRecorder();
                try {
                    mediaRecorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaRecorder.start();
                btnPlay.setEnabled(false);
                btnStop.setEnabled(false);
                Toast.makeText(MainActivity.this, "Recording...", Toast.LENGTH_SHORT).show();

            }
        });

        btnStopRecord.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                btnStopRecord.setEnabled( false );
                btnPlay.setEnabled( true );
                btnRecord.setEnabled( true );
                btnStop.setEnabled( false );
            }
        } );

        btnPlay.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStop.setEnabled( true );
                btnStopRecord.setEnabled( false );
                btnRecord.setEnabled( false );

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource( pathSave );
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
               Toast.makeText( MainActivity.this, "Playing..." , Toast.LENGTH_SHORT ).show();


            }
        } );

        btnStop.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStopRecord.setEnabled(false );
                btnRecord.setEnabled( true );
                btnPlay.setEnabled( true );
                btnStop.setEnabled( false );

                if (mediaPlayer!= null)
                {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    setupMediaRecorder();
                }


            }
        } );




    }

    private void setupMediaRecorder() {

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource( MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat( MediaRecorder.OutputFormat.THREE_GPP );
        mediaRecorder.setAudioEncoder( MediaRecorder.OutputFormat.AMR_NB );
        mediaRecorder.setOutputFile( pathSave );
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    // Initiate request for permissions.
    private boolean checkPermissions() {

        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Toast.makeText(this, "This app only works on devices with usable external storage",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED  || record_audio_result != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                 Manifest.permission.RECORD_AUDIO},
                    REQUEST_PERMISSION_WRITE);
            return false;
        } else {
            return true;
        }
    }

    // Handle permissions result
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                    Toast.makeText(this, "External storage permission granted",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
