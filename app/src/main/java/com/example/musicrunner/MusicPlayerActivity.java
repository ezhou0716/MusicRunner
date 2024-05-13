package com.example.musicrunner;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Music Player Activity
 */
public class MusicPlayerActivity extends AppCompatActivity implements SensorEventListener {

    /**
     * Text views to display title, current time, total time.
     */
    TextView titleTv,currentTimeTv,totalTimeTv;

    /**
     * Seekbar for the song progress.
     */
    SeekBar seekBar;

    /**
     * Image views for the player buttons and music icon.
     */
    ImageView pausePlay,nextBtn,previousBtn,musicIcon;
    //TextView stepCountTv;

    /**
     * Text views to display pace and speed.
     */
    TextView paceTv, speedTv;

    /**
     * Sensor manager for step sensor.
     */
    private SensorManager sensorManager;

    /**
     * Step sensor
     */
    private Sensor stepCounterSensor;

    /**
     * Average distance per step.
     */
    final static float FEET_PER_STEP = 2.5f; // THIS IS AN AVERAGE

    static private int initialCount = -1;

    /**
     * Step count.
     */
    static private int stepCount = 0;

    /**
     * Previous step count.
     */
    static private int prevCount = 0;
    static private int calculatedCount = 0;

    /**
     * Previous timestamp.
     */
    static private long previousTime = -1;

    /**
     * Previous pace.
     */
    static private float previousPace = 0.0f;

    /**
     * Current pace.
     */
    static private float currentPace = 0.0f;

    /**
     * Song list
     */
    ArrayList<AudioModel> songsList;

    /**
     * Current song.
     */
    AudioModel currentSong;

    /**
     * Reference to MediaPlayer instance;
     */
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    /**
     * Used in rotation display of the music icon.
     */
    int x=0;

    /**
     * Override activity onCreate().
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */

        titleTv = findViewById(R.id.song_title);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        seekBar = findViewById(R.id.seek_bar);
        pausePlay = findViewById(R.id.pause_play);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);
        musicIcon = findViewById(R.id.music_icon_big);
        //stepCountTv = findViewById(R.id.step_count);
        paceTv = findViewById(R.id.pace);
        speedTv = findViewById(R.id.e_speed);

        titleTv.setSelected(true);

        // Check and request step counter permission.
        if(!checkStepCounterPermission()){
            Log.w("<><><>", "About to request step counter Permission.... ");
            requestStepCounterPermission();
            //return; // do we need to return?
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepCounterSensor == null) {
            //stepCountTv.setText("Step Counter not available");
            paceTv.setText("Step Counter not available");
        }

        // Get the song list from intent
        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        // Play the current song
        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTimeTv.setText(convertToMMSS(mediaPlayer.getCurrentPosition()+""));

                    if(mediaPlayer.isPlaying()){
                        pausePlay.setImageResource(R.drawable.ic_baseline_pause_circle_outline_24);
                        musicIcon.setRotation(x++);
                    }else{
                        pausePlay.setImageResource(R.drawable.ic_baseline_play_circle_outline_24);
                        musicIcon.setRotation(0);
                    }

                }
                new Handler().postDelayed(this,100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer!=null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * Set resources and play the current song using MediaPlayer.
     */
    void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());

        totalTimeTv.setText(convertToMMSS(currentSong.getDuration()));

        pausePlay.setOnClickListener(v-> pausePlay());
        nextBtn.setOnClickListener(v-> playNextSong());
        previousBtn.setOnClickListener(v-> playPreviousSong());

        //stepCountTv.setText("Steps: " + stepCount);
        paceTv.setText(String.format(java.util.Locale.US, "Pace: %.1f s/min", currentPace));

        float speed = currentPace * 60 * FEET_PER_STEP / 5280.0f;
        speedTv.setText(String.format(java.util.Locale.US, "Speed: %.1f mph", speed));

        // Play music
        playMusic();
    }


    /**
     * Play music using MediaPlayer.
     */
    private void playMusic(){

        mediaPlayer.reset();
        try {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            totalTimeTv.setText(convertToMMSS(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Play the next song on the list
     */
    private void playNextSong(){

        if(MyMediaPlayer.currentIndex== songsList.size()-1)
            return;
        MyMediaPlayer.currentIndex +=1;
//        mediaPlayer.reset();
        setResourcesWithMusic();

    }

    /**
     * Play the previous song on the list
     */
    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex== 0)
            return;
        MyMediaPlayer.currentIndex -=1;
//        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    /**
     * Pause playing.
     */
    private void pausePlay(){
        if(mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }


    /**
     * Convert duration from Long to String format "MM:SS".
     * @param duration
     * @return
     */
    public static String convertToMMSS(String duration){
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Current milliseconds to String format "MM:SS"
     * @param millis
     * @return
     */
    public static String convertToMMSS(int millis){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    /**
     * Returns true if the app has ACTIVITY_RECOGNITION permission.
     * @return
     */
    boolean checkStepCounterPermission() {
        int result = ContextCompat.checkSelfPermission(MusicPlayerActivity.this, android.Manifest.permission.ACTIVITY_RECOGNITION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Request ACTIVITY_RECOGNITION permission.
     */
    void requestStepCounterPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(MusicPlayerActivity.this, android.Manifest.permission.ACTIVITY_RECOGNITION)){
            Log.w("<><><>", "shouldShowRequestPermissionRationale...");
            Toast.makeText(MusicPlayerActivity.this,"ACTIVITY PERMISSION IS REQUIRED,PLEASE ALLOW FROM SETTINGS",Toast.LENGTH_SHORT).show();
        }
        else {
            Log.w("<><><>", "Requesting permission...");
            ActivityCompat.requestPermissions(MusicPlayerActivity.this, new String[]{ android.Manifest.permission.ACTIVITY_RECOGNITION}, 2);
        }
    }

    /**
     * Triggered by step sensor
     * @param event the {@link android.hardware.SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int totalStepSinceReboot = (int) event.values[0];

            if(initialCount == -1){
                initialCount = totalStepSinceReboot;
            }

            stepCount = totalStepSinceReboot - initialCount;
            Log.i("<><><>", "New step count: " + stepCount);

            //stepCountTv.setText("Steps: " + stepCount);

            // we do not calculate for every steps. Instead, we calcuate every 5 times the sensor is triggered to even out the variations.
            if (calculatedCount % 10 == 0 ) {

                if (previousTime == -1) {
                    previousTime = System.currentTimeMillis();
                } else {
                    long currentTime = System.currentTimeMillis();

                    Log.i("<><><>", "c_count: " + calculatedCount + ", previous Time: " + previousTime + ", currentTime: " + currentTime + ", previous count: " + prevCount + ", current Count: " + stepCount);

                    currentPace = (stepCount - prevCount) * 60000.0f / (currentTime - previousTime);

                    paceTv.setText(String.format(java.util.Locale.US, "Pace: %.1f s/min", currentPace));

                    float speed = currentPace * 60 * FEET_PER_STEP / 5280.0f;
                    speedTv.setText(String.format(java.util.Locale.US, "Speed: %.1f mph", speed));

                    previousTime = currentTime;

                    Log.i("<><><>", "Check song with previous pace: " + previousPace + " and current pace : " + currentPace);

                    // check if current song in the range
                    if (!currentSong.inPaceRange(previousPace) && !currentSong.inPaceRange(currentPace)) {
                        // twice pace out of range, consider switching songs.
                        // use current pace as standard

                        Log.i("<><><>", "both previous pace: " + previousPace + " and current pace : " + currentPace + " are out of range of this song (" + currentSong.getPaceLow() + "," + currentSong.getPaceHigh() + ")");

                        for (int i = 0 ; i < songsList.size() ; i ++) {
                            AudioModel song = songsList.get(i);
                            if (song.inPaceRange(currentPace)) {
                                currentSong = song;
                                MyMediaPlayer.currentIndex = i;

                                Log.i("<><><>", "find song for the current pace: " + song.getTitle());
                                // play the new song
                                setResourcesWithMusic();

                                break;
                            }
                        }
                    }

                    previousPace = currentPace;
                }

                prevCount = stepCount;
            }

            calculatedCount ++;
        }

    }

    /**
     * Override SensorEventListener.OnAccuracyChanged().
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * Override Activity onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (stepCounterSensor !=null) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * Override Activity onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}