package me.ziyuanliu.stressmeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ziyuanliu on 4/16/16.
 */
public class ImageSubmissionActivity extends AppCompatActivity {
    int gridIndex, photoIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_submission);
        Bundle extras = getIntent().getExtras();

        ImageView imgView = (ImageView) findViewById(R.id.submission_image_view);
        if (extras != null) {
            gridIndex = extras.getInt("gridIndex", 1);
            photoIndex = extras.getInt("photoIndex",0);

            imgView.setImageResource(PSM.getGridById(gridIndex+1)[photoIndex]);
        }

    }

    public void cancelClicked(View view){
        finish();
    }

    public void saveClicked(View view){
        MainActivity.writeToCSV(PSM.getScore(photoIndex));
        Intent resultIntent = new Intent();
        resultIntent.putExtra("EXIT", true);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
