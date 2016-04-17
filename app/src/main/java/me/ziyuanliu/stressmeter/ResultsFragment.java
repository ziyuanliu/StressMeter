package me.ziyuanliu.stressmeter;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by ziyuanliu on 4/16/16.
 */
public class ResultsFragment extends Fragment{
    CSVReader reader;
    ArrayList<String[]> rows;

    public void setupTableView(View view){
        TableLayout lView = (TableLayout)view.findViewById(R.id.resultsList);
        TableRow row;
        TextView t1, t2;

        rows.add(0, new String[]{"TimeStamp","Stress Level"});

        // Initializing a ShapeDrawable
        ShapeDrawable sd = new ShapeDrawable();

        // Specify the shape of ShapeDrawable
        sd.setShape(new RectShape());

        // Specify the border color of shape
        sd.getPaint().setColor(Color.BLACK);

        // Set the border width
        sd.getPaint().setStrokeWidth(10f);

        // Specify the style is a Stroke
        sd.getPaint().setStyle(Paint.Style.STROKE);
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1f);

        for (int i = 0; i < rows.size(); i++){
            String[] r = rows.get(i);

            row = new TableRow(getContext());

            t1 = new TextView(getContext());
            t2 = new TextView(getContext());

            t1.setText(r[0]);
            t2.setText(r[1]);

            t1.setTypeface(null, 1);
            t2.setTypeface(null, 1);

            t1.setTextSize(15);
            t2.setTextSize(15);

            row.setBackgroundDrawable(sd);
//            t2.setBackgroundDrawable(sd);
            row.addView(t1);
            row.addView(t2);


            lView.addView(row, new TableLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        }
        lView.setShrinkAllColumns(true);
        lView.setStretchAllColumns(true);
        lView.setColumnStretchable(0, true);
        lView.setColumnStretchable(2, true);
    }

    public void drawChart(View view){
        if (rows.size()==0) return;
        List<PointValue> values = new ArrayList<PointValue>();
        String[] first = rows.get(0);


        for (int i = 0; i < rows.size(); i ++){
            String[] r = rows.get(i);
            float x = Float.valueOf(i);
            float stress = Float.valueOf(r[1]);

            values.add(new PointValue((int)x, (int)stress));
        }

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        Axis yLeft = Axis.generateAxisFromRange(0,15,3);
        yLeft.setName("Stress Level").setHasLines(true);
        data.setAxisYLeft(yLeft);

        int incr = rows.size() <= 10 ? 1 : (int)rows.size()/10;
        Axis xBottom = Axis.generateAxisFromRange(0,rows.size()-1,incr);
        xBottom.setName("Instances");
        data.setAxisXBottom(xBottom);
        data.setBaseValue(Float.NEGATIVE_INFINITY);


        LineChartView chart = (LineChartView) view.findViewById(R.id.chart);
        chart.setLineChartData(data);
        setViewport(chart, rows.size());
    }

    private void setViewport(LineChartView chart, int nums) {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 18;
        v.left = 0;
        v.right = nums-1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_results, container, false);
        super.onCreate(savedInstanceState);
        reader = MainActivity.getCSVReader();

        rows = new ArrayList<String[]>();

        String[] row;
        try {
            while ((row = reader.readNext()) != null)
            {
                rows.add(row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        drawChart(view);
        setupTableView(view);
        return view;
    }
}
