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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

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
        InputStream is = getResources().openRawResource(R.raw.songdata);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = "";
        ArrayList<Song> songs = new ArrayList<Song>();
        double avgBpm = 0.0;
        try{
            while((line = reader.readLine()) != null){
                String [] fields  = line.split(",");
                songs.add(new Song(fields[4], Integer.parseInt(fields[3]), Integer.parseInt(fields[2]), fields[0], fields[1]));
                avgBpm += Integer.parseInt(fields[2]);
            }
        }
        catch(IOException e){
            Log.wtf("MainActivity", "Error reading data on line: " + line);
        }
        avgBpm /= songs.size();
        int bpmRange = (songs.get(songs.size()-1).getBpm() - songs.get(0).getBpm());
        double time = distance * 60.0 / speed;
        if(pid == 1){
            /* For 0 t < 0.2 slope: .19
               For 0.2 < t < 0.4 slope: -0.105
               For 0.4 < t < 0.6 slope: -0.03
               For 0.6 < t < 0.8 slope: -0.04
               For 0.8 < t < 1 slope: 0.195
             */
            double range = 1.024 - .993;
            double iTotal = 0.0;
            ArrayList<Song> tentativeSongs = new ArrayList<>(songs);
            while(iTotal < time){
                int index = (int)(iTotal/(0.2*time));
                if(index == 0){
                    double instantSpeed = 0.982*speed;
                    instantSpeed += iTotal%(0.2*time)*0.19;
                    double proportion = (instantSpeed - 0.993)/range;
                    double instantBpm = (proportion*bpmRange)+ songs.get(0).getBpm();
                    int max = (int) Math.round((instantBpm + 10));
                    int min = (int) Math.round((instantBpm - 5));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1));
                    int smallIndex = 0;
                    for(int i = 0; i < songs.size(); i ++){
                        if(Math.abs(songs.get(i).getBpm() - chosenBPM) < Math.abs(songs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(songs.get(i).getBpm() - chosenBPM) > Math.abs(songs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
                            break;
                        }
                        else{//same diff, choses one by random
                            double num = Math.random();
                            if(num < 0.5){
                                smallIndex = i;
                            }
                        }
                    }
                     // duration will be reset
                    AudioModel songData = new AudioModel(songs.get(smallIndex).getPath(), songs.get(smallIndex).getTitle(),songs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    tentativeSongs.remove(smallIndex);


                }
                else if (index == 1) {
                    double instantSpeed = 1.020*speed;
                    instantSpeed -= iTotal%(0.2*time)*0.105;
                }
                else if (index == 2) {
                    double instantSpeed = 0.999*speed;
                    instantSpeed -= iTotal%(0.2*time)*0.03;
                }
                else if (index == 3) {
                    double instantSpeed = 0.993*speed;
                    instantSpeed -= iTotal%(0.2*time)*0.04;
                }
                else if (index == 4) {
                    double instantSpeed = 0.985*speed;
                    instantSpeed += iTotal%(0.2*time)*0.195;
                }
                else{
                    double instantSpeed = 1.024*speed;
                }
            }

        }
        else if(pid == 2){
            /* For 0 t < 0.2 slope: 0.001
               For 0.2 < t < 0.4 slope: 0.215
               For 0.4 < t < 0.6 slope: 0.4845
               For 0.6 < t < 0.8 slope: -0.1615
               For 0.8 < t < 1 slope: -0.1615
             */
        }
        else if(pid == 3){
            /* For 0 t < 0.2 slope: -0.2765
               For 0.2 < t < 0.4 slope: -0.221
               For 0.4 < t < 0.6 slope: -0.0331
               For 0.6 < t < 0.8 slope: -.0221
               For 0.8 < t < 1 slope: -0.0111
             */
        }
        else{
            /* For 0 t < 0.2 slope: .0535
               For 0.2 < t < 0.4 slope: .1105
               For 0.4 < t < 0.6 slope: .1655
               For 0.6 < t < 0.8 slope: .221
               For 0.8 < t < 1 slope: .1365
             */
        }


        /*
        Log.w("<><><>", "external storage directory: " + Environment.getExternalStorageDirectory().getAbsolutePath());
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String path=rootPath + "/MyMusic/Samples/m1.m4a"; //Should change to raw file location?
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
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m5.m4a");
        }
        path=rootPath + "/MyMusic/Samples/m5.m4a";
        file = new File(path);
        if(file.exists()) {
            Log.w("<><><>", "this song exists: /MyMusic/Samples/m4.m4a");

            AudioModel songData = new AudioModel(path, "m4", "180000", 160, 220); // duration will be reset
            songsList.add(songData);

        }
        else {
            Log.w("<><><>", "this song does not exists: /MyMusic/Samples/m4.m4a");
        }
        */
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