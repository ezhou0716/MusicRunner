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

public class PlotActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {

    private XYPlot plot;
    private Button buGPL;
    private Button buSet;
    private TextView speedTv;
    private TextView distanceTv;

    private static Dialog dialog;

    private static int speed = 7;
    private static int distance = 5;

    private static int pid;

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

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}