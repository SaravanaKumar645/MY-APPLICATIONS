package com.example.scientificcalculator;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.VolumeShaper;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private EditText display;
    ImageView imageView;
    public  static String  jsonExp;
    public static String jsonRes;
    DrawerLayout drawerLayout;
    RelativeLayout relativeLayout;
    NavigationView navigationView;

    SensorEventListener sensorEventListener;
    Toolbar toolbar;
     public static List<String> listExp = new ArrayList<>();
     public static List<String> listRes = new ArrayList<>();
    private TextView prevCalculation;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
         imageView=findViewById(R.id.imageButton);
        relativeLayout=findViewById(R.id.theme_holder);
        display = findViewById(R.id.display);

        drawerLayout = findViewById(R.id.drawer_layout21);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar_id21);
        navigationView.bringToFront();
        navigationView.setNestedScrollingEnabled(true);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT) {
            Window window = MainActivity.this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(24420817);
            //window.setNavigationBarColor(255255255);
        }
        //toolbar.setTitle("Calculator");
        prevCalculation = findViewById(R.id.previos_calc);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.normal_calc)
                .setDrawerLayout(drawerLayout)
                .build();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.normal_calc);
       /* if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HistoryFragment()).commit();
            navigationView.setCheckedItem(R.id.history);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            display.setShowSoftInputOnFocus(false);
        }
         int[] flag = {0};
        //Theme Settter On click starts
        imageView.setSelected(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag[0] ==0){

                    imageView.setImageResource(R.drawable.ic_outline_light_mode_24);
                   getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    flag[0] =1;

                }else if (flag[0] ==1){
                    //imageView.setImageResource(R.drawable.ic_baseline_nightlight_24);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    flag[0] =0;

                }

            }
        });


    }

    private void updateText(String string) {
        String oldStr = display.getText().toString();
        int cursorPos = display.getSelectionStart();
        String leftStr = oldStr.substring(0, cursorPos);
        String rightStr = oldStr.substring(cursorPos);

        display.setText(String.format("%s%s%s", leftStr, string, rightStr));
        display.setSelection(cursorPos + string.length());
    }


    public void zero(View view) {
        updateText(getResources().getString(R.string.zero));
    }

    public void one(View view) {
        updateText(getResources().getString(R.string.one));
    }

    public void two(View view) {
        updateText(getResources().getString(R.string.two));
    }

    public void three(View view) {
        updateText(getResources().getString(R.string.three));
    }

    public void four(View view) {
        updateText(getResources().getString(R.string.four));
    }

    public void five(View view) {
        updateText(getResources().getString(R.string.five));
    }

    public void six(View view) {
        updateText(getResources().getString(R.string.six));
    }

    public void seven(View view) {
        updateText(getResources().getString(R.string.seven));
    }

    public void eight(View view) {
        updateText(getResources().getString(R.string.eight));
    }


    public void nine(View view) {
        updateText(getResources().getString(R.string.nine));
    }

    public void multiply(View view) {
        updateText(getResources().getString(R.string.multiplication));
    }

    public void modulo(View view) {
        updateText(getResources().getString(R.string.modulo));
    }

    public void divide(View view) {
        updateText(getResources().getString(R.string.division));
    }

    public void subtract(View view) {
        updateText(getResources().getString(R.string.subtraction));
    }

    public void addition(View view) {
        updateText(getResources().getString(R.string.addition));
    }

    public void clear(View view) {
        display.setText("");
        prevCalculation.setText("");
    }

    public void parOpen(View view) {
        updateText(getResources().getString(R.string.parenthesis_open));
    }

    public void parClose(View view) {
        updateText(getResources().getString(R.string.parenthesis_close));
    }

    public void decimal(View view) {
        updateText(getResources().getString(R.string.decimal));
    }

    public void delete(View view) {
        int cursorPos = display.getSelectionStart();
        int textLen = display.getText().length();

        if (cursorPos != 0 && textLen != 0) {
            SpannableStringBuilder selection = (SpannableStringBuilder) display.getText();
            selection.replace(cursorPos - 1, cursorPos, "");
            display.setText(selection);
            display.setSelection(cursorPos - 1);
        }
    }

    public void equals(View view) {
        String userExp = display.getText().toString();
        String expcopy = userExp;
        prevCalculation.setText(userExp);

        userExp = userExp.replaceAll(getResources().getString(R.string.modulo), "#");
        userExp = userExp.replaceAll(getResources().getString(R.string.division), "/");
        userExp = userExp.replaceAll(getResources().getString(R.string.multiplication), "*");

        Expression exp = new Expression(userExp);
        String result = String.valueOf(exp.calculate());

        display.setText(result);
        display.setSelection(result.length());
        listExp.addAll(Collections.singleton(expcopy));
        listRes.addAll(Collections.singleton(result));
        Log.e("SEEEEE", "onCreate: " + listExp);
        Log.e("SEEEEE", "onCreate: " + listRes);
        //Modelclass modelclass=new Modelclass(expcopy,result);
        SharedPreferences sharedPreferences=getSharedPreferences("key",MODE_PRIVATE);
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        jsonExp = gson.toJson(listExp);
        jsonRes = gson.toJson(listRes);
        editor.putString("EXP", jsonExp);
        editor.putString("RES", jsonRes);
        editor.apply();
    }
    public void trigSinBTNPush(View view){
        updateText("sin(");
    }

    public void trigCosBTNPush(View view){
        updateText("cos(");
    }

    public void trigTanBTNPush(View view){
        updateText("tan(");
    }

    public void trigArcSinBTNPush(View view){
        updateText("arcsin(");
    }

    public void trigArcCosBTNPush(View view){
        updateText("arccos(");
    }

    public void trigArcTanBTNPush(View view){
        updateText("arctan(");
    }

    public void naturalLogBTNPush(View view){
        updateText("ln(");
    }

    public void logBTNPush(View view){
        updateText("log(");
    }

    public void sqrtBTNPush(View view){
        updateText("sqrt(");
    }

    public void absBTNPush(View view){
        updateText("abs(");
    }

    public void piBTNPush(View view){
        updateText("pi");
    }

    public void eBTNPush(View view){
        updateText("e");
    }

    public void xSquaredBTNPush(View view){
        updateText("^(2)");
    }

    public void xPowerYBTNPush(View view){
        updateText("^(");
    }

    public void primeBTNPush(View view){
        updateText("ispr(");
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal_calc:
               /* sensorEventListener=new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                            navigationView.setCheckedItem(R.id.scientific_calc);
                        }
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {

                    }
                };*/

                // navigationView.setCheckedItem(R.id.normal_calc);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                Snackbar snackbar2=Snackbar.make(this.findViewById(R.id.normal_calc), Html.fromHtml("<b>Standard Calculator</b>"), Snackbar.LENGTH_SHORT);
                View view2=snackbar2.getView();
                TextView tv2=(TextView)view2.findViewById(com.google.android.material.R.id.snackbar_text);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }else {
                    tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar2.show();

                break;
            case R.id.scientific_calc:
                // navigationView.setCheckedItem(R.id.scientific_calc);
                View parent1=findViewById(android.R.id.content);

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                Snackbar snackbar1=Snackbar.make(parent1, Html.fromHtml("<b>Scientific Calculator</b>"), Snackbar.LENGTH_SHORT);
                View view1=snackbar1.getView();
                TextView tv1=(TextView)view1.findViewById(com.google.android.material.R.id.snackbar_text);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }else {
                    tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar1.show();

                break;
            case R.id.history:




                startActivity(new Intent(getApplicationContext(), History.class));
                //snackbar.show();
                finish();

                // getSupportFragmentManager().beginTransaction().replace(R.id.frag_room,new HistoryFragment()).commit();
                break;
            case R.id.rate_us:
                View parent=findViewById(android.R.id.content);
                Snackbar snackbar3=Snackbar.make(parent, Html.fromHtml("<b>Rate the App in Play Store</b>"), Snackbar.LENGTH_SHORT);
                View view3=snackbar3.getView();
                TextView tv3=(TextView)view3.findViewById(com.google.android.material.R.id.snackbar_text);

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }else {
                    tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar3.show();
                break;
            case R.id.about:
                AlertDialog.Builder dialog=new AlertDialog.Builder(this);
                dialog.setTitle("About Calculator");
                dialog.setCancelable(false);
                dialog.setMessage("Performs some basic calculations.\nAlso we provide a Scientific Calculator for some advanced Calculations.\nYou can view the calculation history too.\n\n\n\nNote:\n\nThis app is still under development.So you may not experience full features.  ");
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();
                break;

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        // navigationView.setCheckedItem(R.id.null_item);
        return true;
    }


}