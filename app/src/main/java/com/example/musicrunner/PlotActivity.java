package com.example.musicrunner;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

/**
 *  Plot activity of this app.
 */
public class PlotActivity extends AppCompatActivity {

    /**
     * XYPlot view of the selected speed and distance
     */
    private XYPlot plot;

    /**
     * Button to generate play list (the next page)
     */
    private Button buGPL;

    /**
     * The button to set speed and distance.
     */
    private Button buSet;

    /**
     * Text view to display currently selected speed.
     */
    private TextView speedTv;

    /**
     * Text view to display currently selected distance.
     */
    private TextView distanceTv;

    /**
     * Dialog view to select speed and distance.
     */
    private static Dialog dialog;

    /**
     * Current selected speed.
     */
    private static int speed = 7;

    /**
     * Current selected distance.
     */
    private static int distance = 5;

    /**
     * Current selected pattern ID.
     */
    private static int pid;

    /**
     * Override Activity.onCreate().
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plot);

        /*
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */

        pid = (Integer) getIntent().getSerializableExtra("PATTERN_ID");
        Log.w("<><><>", "In PlotActivity: Pattern id: " + pid);

        speedTv = findViewById(R.id.avg_speed);
        distanceTv = findViewById(R.id.distance);
        plot = (XYPlot) findViewById(R.id.running_plot);
        buGPL = (Button) findViewById(R.id.generate_play_list);
        buSet = (Button) findViewById(R.id.set_speed_distance);


        buGPL.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.w("<><><>", "onClick GPL ");
                Intent intent = new Intent(getApplicationContext(), MusicListActivity.class);

                intent.putExtra("PATTERN_ID",pid);  //  to indicate which pattern is invoked
                intent.putExtra("SPEED",speed);  //  to indicate which pattern is invoked
                intent.putExtra("DISTANCE",distance);  //  to indicate which pattern is invoked

                startActivity(intent);
            }
        });

        buSet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Log.w("<><><>", "onClick Settings: ");
                setSpeedAndDistance();
            }
        });

        drawPlot();
    }

    /**
     * Redraw the plot based on currently selected speed and distance and pattern id.
     */
    public void drawPlot() {

        speedTv.setText("Avg Speed: " + speed + " (mph)");
        distanceTv.setText("Distance: " + distance + " (miles)");

        float minutes = distance * 60.0f / speed;

        // Given pattent id, speed and distance, calculated the X-Axle (Domain) and Y-Axle (Range) for the plot

        int numberMin = (int)Math.ceil(minutes);
        Number[] domainLabels = new Number[numberMin+1];
        for (int i = 0 ; i <= numberMin ; i ++) {
            domainLabels[i] = i;
        }

        Number[] xVals;
        Number[] yVals;
        XYSeries series = null;

        Double maxSpeed = 0.0, minSpeed = 30.0;

        if (pid == 1) {
            // For pattern 1,
            // t = 0 T, v = 0.982 * V
            //  t = 0.1 T,  v = 1.020 * V
            //  t = 0.2 T,  v = 1.020 * V
            //  t = 0.4 T,  v = 0.999 * V
            //  t = 0.6 t,  v = 0.993 * V
            //  t = 0.8 t,  v = 0.985 * V
            //  t = 1.0 t,  v = 1.024 * V
            xVals = new Number [] {0.1 * minutes, 0.2 * minutes, 0.4 * minutes, 0.6 * minutes, 0.8 * minutes, 1.0 * minutes};
            yVals = new Number [] {1.02 * speed, 1.02 * speed, 0.999 * speed, 0.993 * speed, 0.985 * speed, 1.024 * speed};
            series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "my series");

            maxSpeed = 1.024 * speed;
            minSpeed = 0.993 * speed;
        }
        if (pid == 2) {
            // For pattern 2,
            // t = 0 T, v = v = 0.9367 * V
            //  t = 0.1 T,  v = 0.915 * V
            //  t = 0.2 T,  v = .9365 * V
            //  t = 0.4 T,  v = 0.9795 * V
            //  t = 0.6 t,  v = 1.0764 * V
            //  t = 0.8 t,  v = 1.0441 * V
            //  t = 1.0 t,  v = 1.0118 * V
            xVals = new Number [] {0.1 * minutes, 0.2 * minutes, 0.4 * minutes, 0.6 * minutes, 0.8 * minutes, 1.0 * minutes};
            yVals = new Number [] {0.915 * speed, .9365 * speed, 0.9795 * speed, 1.0764 * speed, 1.0441 * speed, 1.0118 * speed};
            series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "my series");

            maxSpeed = 1.0764 * speed;
            minSpeed = 0.915 * speed;
        }
        if (pid == 3) {
            // For pattern 3,
            // t = 0, v = 1.105 * V
            //  t = 0.1 T,  v = 1.0773 * V
            //  t = 0.2 T,  v = 1.0497 * V
            //  t = 0.4 T,  v = 1.0055 * V
            //  t = 0.6 t,  v = 0.9724 * V
            //  t = 0.8 t,  v = 0.9503 * V
            //  t = 1.0 t,  v = 0.9392 * V
            xVals = new Number [] {0.1 * minutes, 0.2 * minutes, 0.4 * minutes, 0.6 * minutes, 0.8 * minutes, 1.0 * minutes};
            yVals = new Number [] {1.077 * speed, 1.0497 * speed, 1.0055 * speed, 0.9724 * speed, 0.9503 * speed, 0.9392 * speed};
            series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "my series");

            maxSpeed = 1.105 * speed;
            minSpeed = 0.9392 * speed;
        }
        if (pid == 4) {
            // For pattern 4,
            // t = 0 T, v = 0.9285 * V
            //  t = 0.1 T,  v = 0.9392 * V
            //  t = 0.2 T,  v = 0.9503 * V
            //  t = 0.4 T,  v = 0.9724 * V
            //  t = 0.6 t,  v = 1.0055 * V
            //  t = 0.8 t,  v = 1.0497 * V
            //  t = 1.0 t,  v = 1.0773 * V
            xVals = new Number [] {0.1 * minutes, 0.2 * minutes, 0.4 * minutes, 0.6 * minutes, 0.8 * minutes, 1.0 * minutes};
            yVals = new Number [] {0.9392 * speed, 0.9503 * speed, .9724 * speed, 1.0055 * speed, 1.0497 * speed, 1.077 * speed};
            series = new SimpleXYSeries(Arrays.asList(xVals), Arrays.asList(yVals), "my series");

            maxSpeed = 1.105 * speed;
            minSpeed = 0.9392 * speed;
        }

        LineAndPointFormatter series1Format =
                new LineAndPointFormatter(Color.RED, Color.GREEN, null, null);

        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        plot.clear();

        plot.addSeries(series, series1Format);

        plot.setRangeBoundaries(Math.max(minSpeed-0.5, 0), maxSpeed+0.5, BoundaryMode.FIXED);

        plot.setDomainBoundaries(0, numberMin, BoundaryMode.FIXED);
        // Adjust the step dynamically
        plot.setDomainStep(StepMode.INCREMENT_BY_VAL, Math.ceil(minutes/10));

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                int i = Math.round(((Number) obj).floatValue());
                Log.w("<><><>", "In format(), i = " + i);

                if (i < 0)
                    return toAppendTo;
                else
                    return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        // redraw
        plot.redraw();
    }

    /**
     * Pop up a dialog to set distance and speed.
     */
    public void setSpeedAndDistance() {
        dialog = new Dialog(PlotActivity.this);
        dialog.setTitle("Set Speed and Distance");
        dialog.setContentView(R.layout.dialog_speed_distance);
        Button bSet = (Button) dialog.findViewById(R.id.b_set);
        Button bCancel = (Button) dialog.findViewById(R.id.b_cancel);

        final NumberPicker sp = (NumberPicker) dialog.findViewById(R.id.speedPicker);
        final NumberPicker dp = (NumberPicker) dialog.findViewById(R.id.distancePicker);

        // speed picker
        sp.setMaxValue(15); // max value 15
        sp.setMinValue(1);   // min value 1
        sp.setValue(speed);
        sp.setWrapSelectorWheel(true);
        //sp.setOnValueChangedListener(this);

        // distance picker
        dp.setMaxValue(20); // max value 20
        dp.setMinValue(1);   // min value 1
        dp.setValue(distance);
        dp.setWrapSelectorWheel(true);
        //dp.setOnValueChangedListener(this);

        bSet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // update speed and distance value
                //tv.setText(String.valueOf(np.getValue())); //set the value to textview
                Log.w("<><><>", "In settings, speed = " + String.valueOf(sp.getValue()));
                Log.w("<><><>", "In settings, distance = " + String.valueOf(dp.getValue()));

                speed = sp.getValue();
                distance = dp.getValue();

                dialog.dismiss();

                drawPlot();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // dismiss the dialog
            }
        });

        dialog.show();
    }
}