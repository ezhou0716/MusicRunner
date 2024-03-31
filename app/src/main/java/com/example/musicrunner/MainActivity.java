package com.example.musicrunner;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
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
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;

    ArrayList<AudioModel> songsList = new ArrayList<>();

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

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noMusicTextView = findViewById(R.id.no_songs_text);

        if(!checkAudioPermission()){
            Log.w("<><><>", "About to request Permission.... ");
            requestAudioPermission();
            return;
        }

        Log.w("<><><>", "external storage directory: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String path=rootPath + "/MyMusic/Samples/m1.m4a";
        File file = new File(path);
        if(file.exists()) {
            Log.w("<><><>", "this song exists: /MyMusic/Samples/m1.m4a");

            AudioModel songData = new AudioModel(path, "m1", "180000", 0, 80); // duration will be reset
            songsList.add(songData);

        }
        else {
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m1.m4a");
        }

        path=rootPath + "/MyMusic/Samples/m2.m4a";
        file = new File(path);
        if(file.exists()) {
            Log.w("<><><>", "this song exists: /MyMusic/Samples/m2.m4a");

            AudioModel songData = new AudioModel(path, "m2", "180000", 80, 120); // duration will be reset
            songsList.add(songData);

        }
        else {
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m2.m4a");
        }

        path=rootPath + "/MyMusic/Samples/m3.m4a";
        file = new File(path);
        if(file.exists()) {
            Log.w("<><><>", "this song exists: /MyMusic/Samples/m3.m4a");

            AudioModel songData = new AudioModel(path, "m3", "180000", 120, 160); // duration will be reset
            songsList.add(songData);

        }
        else {
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m3.m4a");
        }

        path=rootPath + "/MyMusic/Samples/m4.m4a";
        file = new File(path);
        if(file.exists()) {
            Log.w("<><><>", "this song exists: /MyMusic/Samples/m4.m4a");

            AudioModel songData = new AudioModel(path, "m4", "180000", 160, 220); // duration will be reset
            songsList.add(songData);

        }
        else {
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m4.m4a");
        }

        if(songsList.size()==0){
            Log.w("<><><>", "No Songs Found.");
            noMusicTextView.setVisibility(View.VISIBLE);
        }else{
            //recyclerview
            //recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }

    }
    boolean checkAudioPermission() {
        //Android 13
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_MEDIA_AUDIO);
        //ANDROID 10:
        //int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
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

        // ANDROID 10:
        /**if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){
            Log.w("<><><>", "shouldShowRequestPermissionRationale...");
            Toast.makeText(MainActivity.this,"READ PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w("<><><>", "Requesting permission...");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
         */
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }
    }
}