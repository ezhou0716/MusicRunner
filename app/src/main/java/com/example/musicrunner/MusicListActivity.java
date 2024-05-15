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

/**
 * Playlist Activity
 */
public class MusicListActivity extends AppCompatActivity {

    /**
     * The playlist view
     */
    RecyclerView recyclerView;

    /**
     * Message to show if no music found
     */
    TextView noMusicTextView;

    /**
     * List of audio models.
     */
    ArrayList<AudioModel> songsList = new ArrayList<>();

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
                Log.w("<><><>", "Song added: " + fields[4]);
                avgBpm += Integer.parseInt(fields[2]);
            }
        }
        catch(IOException e){
            Log.wtf("MainActivity", "Error reading data on line: " + line);
        }
        avgBpm /= songs.size();
        int bpmRange = (songs.get(songs.size()-1).getBpm() - songs.get(0).getBpm())-21;
        Log.w("<><><>", "bpmRange: " + bpmRange);
        int bpmMin  = songs.get(0).getBpm() + 3*(speed-7);
        double time = distance * 60.0 / speed;
        // For pattern 1
        if(pid == 1){
            /* For 0 t < 0.2 slope: .19
               For 0.2 < t < 0.4 slope: -0.105
               For 0.4 < t < 0.6 slope: -0.03
               For 0.6 < t < 0.8 slope: -0.04
               For 0.8 < t < 1 slope: 0.195
             */
            double range = (1.024 - .982)*speed;
            Log.w("<><><>", "Range: " + range);
            double iTotal = 0.0;
            ArrayList<Song> tentativeSongs = new ArrayList<>(songs);
            while(iTotal < time){
                if(tentativeSongs.isEmpty()){
                    Log.w("<><><>", "No more songs in list");
                    break;
                }
                int index = (int)(iTotal/(0.2*time));
                if(index == 0){
                    double instantSpeed = 0.982*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.19) * speed;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.982*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - 0.982*speed)/(range));
                    int max = (int) Math.round((instantBpm + 8));
                    int min = (int) Math.round((instantBpm - 4));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);

                }
                else if (index == 1) {
                    Log.w("<><><>", "I've Made it to 0.2 of the time");
                    double instantSpeed = 1.020*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.105) * speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.982*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - 0.982*speed)/(range));
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 2) {
                    Log.w("<><><>", "I've Made it to 0.4 of the time");
                    double instantSpeed = 0.999*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.03) * speed;
                    double proportionSpeed = 0.999 - iTotal%(0.2)*0.03;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.982*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - 0.982*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 3) {
                    double instantSpeed = 0.993*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.04) * speed;
                    Log.w("<><><>", "I've Made it to 0.6 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.982*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - 0.982*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 4) {
                    double instantSpeed = 0.985*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.195) * speed;
                    Log.w("<><><>", "I've Made it to 0.8 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.982*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - 0.982*speed)/(range));
                    int max = (int) Math.round((instantBpm + 8));
                    int min = (int) Math.round((instantBpm - 4));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else{
                    break;
                }
            }

        }
        // for Pattern 2
        else if(pid == 2){
            /* For 0 t < 0.2 slope: 0.001
               For 0.2 < t < 0.4 slope: 0.215
               For 0.4 < t < 0.6 slope: 0.4845
               For 0.6 < t < 0.8 slope: -0.1615
               For 0.8 < t < 1 slope: -0.1615
             */
            double range = (1.0764 - .9367)*speed;
            double iTotal = 0.0;
            ArrayList<Song> tentativeSongs = new ArrayList<>(songs);
            while(iTotal < time){
                if(tentativeSongs.isEmpty()){
                    Log.w("<><><>", "No more songs in list");
                    break;
                }
                int index = (int)(iTotal/(0.2*time));
                if(index == 0){
                    double instantSpeed = 0.9367*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.001) * speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.9365*speed)/(range))*(bpmRange) + bpmMin;
                    int max = (int) Math.round((instantBpm + 8));
                    int min = (int) Math.round((instantBpm - 4));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    Log.w("<><><>", "Chosen BPM: " + chosenBPM);
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);

                }
                else if (index == 1) {
                    Log.w("<><><>", "I've Made it to 0.2 of the time");
                    double instantSpeed = .9365*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.215) * speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    double proportion = instantSpeed/speed;
                    //bpm range is the range - 40
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    double instantBpm = ((instantSpeed - 0.9365*speed)/(range))*(bpmRange) + bpmMin;
                    int max = (int) Math.round((instantBpm + 8));
                    int min = (int) Math.round((instantBpm - 4));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    Log.w("<><><>", "Chosen BPM: " + chosenBPM);
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 2) {
                    Log.w("<><><>", "I've Made it to 0.4 of the time");
                    double instantSpeed = .9795*speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    instantSpeed += ((iTotal/time)%(0.2)*0.4845) * speed;
                    //double proportionSpeed = 0.999 - iTotal%(0.2)*0.03;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.9365*speed)/(range))*(bpmRange) + bpmMin;
                    int max = (int) Math.round((instantBpm + 8));
                    int min = (int) Math.round((instantBpm - 4));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    Log.w("<><><>", "Chosen BPM: " + chosenBPM);
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 3) {
                    double instantSpeed = 1.0764*speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    instantSpeed -= ((iTotal/time)%(0.2)*0.1516) * speed;
                    Log.w("<><><>", "I've Made it to 0.6 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.9365*speed)/(range))*(bpmRange) + bpmMin;
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    Log.w("<><><>", "Chosen BPM: " + chosenBPM);
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 4) {
                    double instantSpeed = 1.0441*speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    instantSpeed -= ((iTotal/time)%(0.2)*0.1615) * speed;
                    Log.w("<><><>", "I've Made it to 0.8 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.9365*speed)/(range))*(bpmRange) + bpmMin;
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    Log.w("<><><>", "Chosen BPM: " + chosenBPM);
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else{
                    break;
                }
            }
        }
        // for Pattern 3
        else if(pid == 3){
            /* For 0 t < 0.2 slope: -0.2765
               For 0.2 < t < 0.4 slope: -0.221
               For 0.4 < t < 0.6 slope: -0.0331
               For 0.6 < t < 0.8 slope: -.0221
               For 0.8 < t < 1 slope: -0.0111
             */
            double range = (1.105 - .9392)*speed;
            Log.w("<><><>", "Range: " + range);
            double iTotal = 0.0;
            ArrayList<Song> tentativeSongs = new ArrayList<>(songs);
            while(iTotal < time){
                if(tentativeSongs.isEmpty()){
                    Log.w("<><><>", "No more songs in list");
                    break;
                }
                int index = (int)(iTotal/(0.2*time));
                if(index == 0){
                    double instantSpeed = 1.105*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.2765) * speed;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - 0.9392*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9392*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);

                }
                else if (index == 1) {
                    Log.w("<><><>", "I've Made it to 0.2 of the time");
                    double instantSpeed = 1.0497*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.221) * speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9392*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9392*speed)/(range));
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 2) {
                    Log.w("<><><>", "I've Made it to 0.4 of the time");
                    double instantSpeed = 1.0055*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.0331) * speed;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9392*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9392*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 3) {
                    double instantSpeed = 0.9724*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.0221) * speed;
                    Log.w("<><><>", "I've Made it to 0.6 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9392*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9392*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 4) {
                    double instantSpeed = 0.9503*speed;
                    instantSpeed -= ((iTotal/time)%(0.2)*0.0111) * speed;
                    Log.w("<><><>", "I've Made it to 0.8 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9392*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9392*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else{
                    break;
                }
            }
        }
        // for Pattern 4
        else{
            /* For 0 t < 0.2 slope: .0535
               For 0.2 < t < 0.4 slope: .1105
               For 0.4 < t < 0.6 slope: .1655
               For 0.6 < t < 0.8 slope: .221
               For 0.8 < t < 1 slope: .1365
             */
            double range = (1.0773 - .9285)*speed;
            Log.w("<><><>", "Range: " + range);
            double iTotal = 0.0;
            ArrayList<Song> tentativeSongs = new ArrayList<>(songs);
            while(iTotal < time){
                if(tentativeSongs.isEmpty()){
                    Log.w("<><><>", "No more songs in list");
                    break;
                }
                int index = (int)(iTotal/(0.2*time));
                if(index == 0){
                    double instantSpeed = 0.9285*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.0535) * speed;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9285*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9285*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);

                }
                else if (index == 1) {
                    Log.w("<><><>", "I've Made it to 0.2 of the time");
                    double instantSpeed = 0.9503*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.1105) * speed;
                    Log.w("<><><>", "Instant Speed: " + instantSpeed);
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9285*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9285*speed)/(range));
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 2) {
                    Log.w("<><><>", "I've Made it to 0.4 of the time");
                    double instantSpeed = 0.9724*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.1655) * speed;
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9285*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9285*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 3) {
                    double instantSpeed = 1.0055*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.221) * speed;
                    Log.w("<><><>", "I've Made it to 0.6 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9285*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9285*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else if (index == 4) {
                    double instantSpeed = 1.0497*speed;
                    instantSpeed += ((iTotal/time)%(0.2)*0.1365) * speed;
                    Log.w("<><><>", "I've Made it to 0.8 of the time");
                    double proportion = instantSpeed/speed;
                    double instantBpm = ((instantSpeed - .9285*speed)/(range))*(bpmRange) + bpmMin;
                    Log.w("<><><>", "Instant BPM: " + instantBpm);
                    //bpm range  += 2*(Speed)
                    //proportion bpm: (((instantSpeed - minSpeed)/(range))*(maxBPM - minBPM)) + minBPM
                    Log.w("<><><>", "Speed Ratio: " + (instantSpeed - .9285*speed)/(range));
                    int max = (int) Math.round((instantBpm + 4));
                    int min = (int) Math.round((instantBpm - 8));
                    int chosenBPM = (int)(Math.random() * ((max - min) + 1)) + min;
                    int smallIndex = 0;
                    for(int i = 1; i < tentativeSongs.size(); i ++){
                        if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) < Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting smaller
                            smallIndex = i;
                        }
                        else if(Math.abs(tentativeSongs.get(i).getBpm() - chosenBPM) > Math.abs(tentativeSongs.get(smallIndex).getBpm() - chosenBPM)){ //diff is getting bigger
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
                    AudioModel songData = new AudioModel(tentativeSongs.get(smallIndex).getPath(), tentativeSongs.get(smallIndex).getTitle(),tentativeSongs.get(smallIndex).getDuration());
                    songsList.add(songData);
                    Log.w("<><><>", "playlist song added: " + tentativeSongs.get(smallIndex).getTitle());
                    iTotal += (double) tentativeSongs.get(smallIndex).getDuration() /(1000*60);
                    tentativeSongs.remove(smallIndex);
                }
                else{
                    break;
                }
            }
            
        }

        if(songsList.size()==0){
            Log.w("<><><>", "No Songs Found.");
            noMusicTextView.setVisibility(View.VISIBLE);
        }else{
            //recyclerview
            //recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    /**
     * Override Activity onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

    }

    /**
     * Override Activity onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        if(recyclerView!=null){
            recyclerView.setAdapter(new MusicListAdapter(songsList,getApplicationContext()));
        }
    }
}