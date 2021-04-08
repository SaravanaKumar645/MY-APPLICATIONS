package com.example.scientificcalculator;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class History extends AppCompatActivity {
    Toolbar toolbar;
    String s1,s2,s3;
    ClipboardManager clipboardManager;
    ClipData clipData;
    SimpleAdapter simpleAdapter;
    ListView listViewHistory;
    public  ArrayList<Map<String, String>> list2 = new ArrayList<>();
    public static LinkedHashSet<Map<String, String>> list1 = new LinkedHashSet<>();
     List<String> listExpression = new ArrayList<>();
    List<String> listResult = new ArrayList<>();

    String expression, result, key1, key2;
    private String[] a1, a2;

   /* public History( String exp, String res) {
        //this.context=context;
        this.expression=exp;
        this.result=res;
    }*/

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        menu.setHeaderTitle(Html.fromHtml("<p style=color:blue><b>Select an action :</b></p>"));

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.putAll((Map<? extends String, ? extends String>) simpleAdapter.getItem(info.position));
        int pos=info.position;
        s1=hashMap.get("Exp");
        s2=hashMap.get("Res");
        s3=s1+"="+s2;
        //Log.e("Value1", "onContextItemSelected: "+hashMap+"String is :::::"+s );
        switch (item.getItemId()) {
            case R.id.copy_Exp:
                clipData=ClipData.newPlainText("Expression",s1);
                clipboardManager.setPrimaryClip(clipData);

                View parent = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar.make(parent, Html.fromHtml("Expression Copied"), Snackbar.LENGTH_LONG);
                View view = snackbar.getView();
                TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar.show();
                return true;
            case R.id.copy_Res:
                clipData=ClipData.newPlainText("Result",s2);
                clipboardManager.setPrimaryClip(clipData);

                View parent1 = findViewById(android.R.id.content);
                Snackbar snackbar1 = Snackbar.make(parent1, Html.fromHtml("Result Copied"), Snackbar.LENGTH_LONG);
                View view1 = snackbar1.getView();
                TextView tv1 = (TextView) view1.findViewById(com.google.android.material.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv1.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar1.show();
                return true;
            case R.id.copy_All:
                clipData=ClipData.newPlainText("Expression and Result",s3);
                clipboardManager.setPrimaryClip(clipData);
                View parent2 = findViewById(android.R.id.content);
                Snackbar snackbar2 = Snackbar.make(parent2, Html.fromHtml("Copied "), Snackbar.LENGTH_LONG);
                View view2 = snackbar2.getView();
                TextView tv2 = (TextView) view2.findViewById(com.google.android.material.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv2.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar2.show();
                return true;
            case R.id.delete_history:
               // hashMap.clear();
                View parent3 = findViewById(android.R.id.content);
                Snackbar snackbar3 = Snackbar.make(parent3, Html.fromHtml("Not yet Implemented"), Snackbar.LENGTH_LONG);
                View view3 = snackbar3.getView();
                TextView tv3 = (TextView) view3.findViewById(com.google.android.material.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                } else {
                    tv3.setGravity(Gravity.CENTER_HORIZONTAL);
                }
                snackbar3.show();
                return true;

        }
        return super.onContextItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_activity);
        View parent = findViewById(android.R.id.content);

        Snackbar snackbar = Snackbar.make(parent, Html.fromHtml("<b>Calculation History</b>"), Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(com.google.android.material.R.id.snackbar_text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        snackbar.show();

        clipboardManager=(ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        SharedPreferences sharedPreferences1 = getSharedPreferences("key", MODE_PRIVATE);
        Gson gson = new Gson();
        expression = sharedPreferences1.getString("EXP", "");
        result = sharedPreferences1.getString("RES", "");

        if (expression.isEmpty()) {
            listExpression = new ArrayList<>();
        } else if (result.isEmpty()) {
            listResult = new ArrayList<>();
        } else {
            Type type = new TypeToken<List<String>>() {
            }.getType();
            listExpression = gson.fromJson(expression, type);
            listResult = gson.fromJson(result, type);
        }


        //listExpression.add(expression);
        //listResult.add(result);


        listViewHistory = findViewById(R.id.list_view);
        toolbar = findViewById(R.id.toolbar_id21);
        Window window = History.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(838383);
        Log.e("SEEEEE", "onCreate: " + listExpression);
        Log.e("SEEEEE", "onCreate: " + listResult);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        for (int i = 0; i < listExpression.size(); i++) {
            Map<String, String> copymap = new HashMap<String, String>(2);
            copymap.put("Exp", (listExpression.get(i)));
            copymap.put("Res", (listResult.get(i)));

            list1.add(copymap);
        }
        list2.addAll(list1);
         simpleAdapter = new SimpleAdapter(this, list2,
                android.R.layout.simple_list_item_2,
                new String[]{"Exp", "Res"},
                new int[]{android.R.id.text1,
                        android.R.id.text2});
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, arrayList);

        listViewHistory.setAdapter(simpleAdapter);
        registerForContextMenu(listViewHistory);


    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                startActivity(intent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        super.onBackPressed();
    }
}
