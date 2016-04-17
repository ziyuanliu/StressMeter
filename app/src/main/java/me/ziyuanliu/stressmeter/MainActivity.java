package me.ziyuanliu.stressmeter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Date;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Fragment currentFrag;
    private Fragment stressFrag;
    private Fragment resultsFrag;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static final String CSV_NAME = "stressmeter.csv";

    public static CSVReader getCSVReader(){
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + CSV_NAME;
        File f = new File(filePath );
        CSVReader reader;
        FileReader fileReader;

        try
        {
            if(f.exists() && !f.isDirectory()){
                fileReader = new FileReader(filePath);
                reader = new CSVReader(fileReader);
                return reader;
            }
        }catch (Exception e){
            Log.d("fucked", e.toString());
        }

        return null;
    }


    public static CSVWriter getCSVWriter(){
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = baseDir + File.separator + CSV_NAME;
        File f = new File(filePath );
        CSVWriter writer;
        FileWriter fileWriter;

        // File exist catch
        try
        {
            if(f.exists() && !f.isDirectory()){
                fileWriter = new FileWriter(filePath , true);
                writer = new CSVWriter(fileWriter);
            }
            else {
                writer = new CSVWriter(new FileWriter(filePath));
            }

            return writer;
        }catch (Exception e){
            e.printStackTrace();

        }


        return null;
    }

    public static void writeToCSV(int stressLevel){
        CSVWriter writer = getCSVWriter();

        String epochStr = String.valueOf(System.currentTimeMillis());
        String[] data = {epochStr, String.valueOf(stressLevel)};

        writer.writeNext(data);
        try {
            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        stressFrag = currentFrag = new StressMeterFragment();
        resultsFrag = new ResultsFragment();
        // insert the fragment over the current fragment layout
        onNavigationItemSelected(navigationView.getMenu().getItem(0));
        verifyStoragePermissions(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // initialize a fragment class
        Class fragmentCls;

        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction fragTrans = fragMgr.beginTransaction();

        try {

            switch (id) {
                case R.id.results:
                    fragTrans.replace(R.id.fragment_layout, resultsFrag);
                    break;

                case R.id.stress_meter:
                    fragTrans.replace(R.id.fragment_layout, stressFrag);
                    currentFrag = stressFrag;
                    break;

                default:
                    fragTrans.replace(R.id.fragment_layout, stressFrag);
                    currentFrag = stressFrag;
                    break;

            }
            fragTrans.commit();
//            FrameLayout v = (FrameLayout)findViewById(R.id.fragment_layout);
//            Log.d("wtf", String.valueOf(v.getChildCount()));
        }catch (Exception e){
            e.printStackTrace();
        }

        // bring up the fragment class that we just selected
        // first lets initialize it, try catch in case its not instantiated



        // insert the fragment over the current fragment layout

        item.setChecked(true);
        setTitle(item.getTitle());

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);super.onActivityResult(requestCode, resultCode, data);
        if (data!=null && data.getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
}
