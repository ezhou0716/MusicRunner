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

public class MainActivity extends AppCompatActivity {

    TextView patternsTv;
    ImageView pattern1;
    ImageView pattern2;
    ImageView pattern3;
    ImageView pattern4;

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

        if(!checkAudioPermission()){
            Log.w("<><><>", "About to request Permission.... ");
            requestAudioPermission();
            return;
        }

        pattern1.setOnClickListener(v-> runPattern(1));
        pattern2.setOnClickListener(v-> runPattern(2));
        pattern3.setOnClickListener(v-> runPattern(3));
        pattern4.setOnClickListener(v-> runPattern(4));

        // TODO more patterns to be added

    }

    boolean checkAudioPermission() {
        // android 13
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO);
        //int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }
    void requestAudioPermission() {
        /** ANDROID 13*/
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO)){
            Log.w("<><><>", "shouldShowRequestPermissionRationale...");
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w("<><><>", "Requesting permission...");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.READ_MEDIA_AUDIO}, 1);
        }


        // android 10
        /**
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

    private void runPattern(int pid){
        Log.w("<><><>", "In runPattern with pid: " + pid);

        Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);
        intent.putExtra("PATTERN_ID",pid);  //  to indicate which pattern is invoked
        startActivity(intent);
    }

}