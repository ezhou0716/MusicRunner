package com.example.musicrunner;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 *  Main activity of this app.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Text view for title
     */
    TextView patternsTv;

    /**
     * Image view for the pattern 1
     */
    ImageView pattern1;

    /**
     * Image view for the pattern 2
     */
    ImageView pattern2;

    /**
     * Image view for the pattern 3
     */
    ImageView pattern3;

    /**
     * Image view for the pattern 4
     */
    ImageView pattern4;

    /**
     * Override Activity onCreate().
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        Log.w("<><><>", "in onCreate of main activity ");

        patternsTv = findViewById(R.id.patterns_text);
        pattern1 = findViewById(R.id.pattern1);
        pattern2 = findViewById(R.id.pattern2);
        pattern3 = findViewById(R.id.pattern3);
        pattern4 = findViewById(R.id.pattern4);

        // The audio permission is no longer needed since music is streamed over internet.
//        if(!checkAudioPermission()){
//            Log.w("<><><>", "About to request Permission.... ");
//            requestAudioPermission();
//            return;
//        }

        pattern1.setOnClickListener(v-> runPattern(1));
        pattern2.setOnClickListener(v-> runPattern(2));
        pattern3.setOnClickListener(v-> runPattern(3));
        pattern4.setOnClickListener(v-> runPattern(4));

        // TODO more patterns to be added

    }

    /**
     * Returns true if the app has been granted READ_MEDIA_AUDIO permission
     * @return
     */
    boolean checkAudioPermission() {
        // android 13
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO);
        // android 10
        //int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Requests granting READ_MEDIA_AUDIO permission.
     */
    void requestAudioPermission() {
        /** ANDROID 13*/
        /*
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO)){
            Log.w("<><><>", "shouldShowRequestPermissionRationale...");
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w("<><><>", "Requesting permission...");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.READ_MEDIA_AUDIO}, 1);
        }
        */

        /** android 10
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.w("<><><>", "shouldShowRequestPermissionRationale...");
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w("<><><>", "Requesting permission...");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
         */
    }

    /**
     * Upon chosen a pattern, app enters the PlotActivity/page.
     * @param pid
     */
    private void runPattern(int pid){
        Log.w("<><><>", "In runPattern with pid: " + pid);

//        Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
        Intent intent = new Intent(getApplicationContext(), PlotActivity.class);
        intent.putExtra("PATTERN_ID",pid);  //  to indicate which pattern is invoked
        startActivity(intent);
    }

}