package com.example.musicrunner;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MusicListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;

    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_list);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        Log.w("<><><>", "in onCreate of main activity ");

        int pid = (Integer) getIntent().getSerializableExtra("PATTERN_ID");
        int speed = (Integer) getIntent().getSerializableExtra("SPEED");
        int distance = (Integer) getIntent().getSerializableExtra("DISTANCE");

        Log.w("<><><>", "Pattern id: " + pid);
        Log.w("<><><>", "Speed: " + speed);
        Log.w("<><><>", "Distance: " + distance);

        // TODO: Generate play list based on pattern, speed, and distance.

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noMusicTextView = findViewById(R.id.no_songs_text);

        // TODO: choose the song list based on the pattern id

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