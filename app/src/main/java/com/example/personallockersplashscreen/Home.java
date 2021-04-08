package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Home extends AppCompatActivity {
    private long pressedTime=0;
    MediaPlayer mp;
    MenuItem mSearch;
    SearchView searchView;
    private RecyclerView mrecyclerView2;
    FloatingActionButton mFloat;
    private FirebaseFirestore firestoreCloud;
    private String userId;
    List<String> fileList;
    MyAdapter myAdapter;


    private static final String TAG = "HOME CLASS";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        mSearch = menu.findItem(R.id.search_btn);
        searchView = (SearchView) mSearch.getActionView();
        searchView.setIconified(false);
        //searchView.setFocusable(false);
        searchView.setIconifiedByDefault(false);
        //searchView.onActionViewExpanded();
        //searchView.onActionViewCollapsed();
        searchView.setQueryRefinementEnabled(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Search your Files...");
        //searchView.setBackgroundColor(000000);

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (searchView.hasFocus() == false) {
                    startActivity(new Intent(getApplicationContext(), Home.class));
                    finish();

                }
                return;
            }
        });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {

                    searchView.setQuery("", false);
                }
              //  searchView.setQuery(null, true);

                // searchView.clearFocus();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                myAdapter.getFilter().filter(newText);

                return false;
            }
        });


        return true;

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.search_btn:

                System.out.println("called.......................................");
                //add the function to perform here
                return(true);*/
            case R.id.about_app_menuBtn:

                //add the function to perform here
                return (true);
            case R.id.exit_app_menuBtn:
                //mp=MediaPlayer.create(this,R.raw.exit1);
               // mp.start();
                finish();
                System.exit(0);
                //add the function to perform here
                return (true);
            case R.id.logout_menuBtn:
                //add the function to perform here
                return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        //mLogout=findViewById(R.id.logout_menuBtn);


        // slideInLeft= AnimationUtils.loadAnimation(this,R.anim.slide_in_left);

        //slideOutRight= AnimationUtils.loadAnimation(this,R.anim.slide_out_right);

        mFloat = findViewById(R.id.floatingActionButton);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestoreCloud = FirebaseFirestore.getInstance();
        mrecyclerView2 = findViewById(R.id.recycleView2);
        mrecyclerView2.setHasFixedSize(true);
        //logout=findViewById(R.id.logout);

        //logout.setTypeface(Typeface.MONOSPACE,Typeface.BOLD_ITALIC);

       /* DocumentReference documentReference=firestoreCloud.collection("UploadedFiles").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Extension=value.getString("Extension");
                Log.w(TAG,"SEE THIS  "+Extension);
            }
        });*/


        getFileNamefromCloud();


        //mrecyclerView2.setLayoutManager(new LinearLayoutManager(Home.this));


        mFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), UploadArea.class));
                //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);

            }
        });


        //Creation of Run time Permission.......................................
        Dexter.withContext(Home.this).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    //When Permission granted.............
                } else {
                    Toast.makeText(Home.this, "All Permissions Not Granted.\nCannot Upload Files !", Toast.LENGTH_SHORT).show();
                    showSettingsDialog();

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        }).onSameThread()
                .check();
    }  //Outside onCreate Method(below).........................

    public void getFileNamefromCloud() {
        firestoreCloud.collection("UploadedFiles")
                .whereEqualTo("UserId", userId)

                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    fileList = new ArrayList<String>();
                    Log.d(TAG, "DOCS SIZE :" + task.getResult().size());
                    for (DocumentSnapshot doc : task.getResult()) {
                        fileList.add(doc.getString("FileName")+"@"+doc.getString("URL")+"@"+doc.getString("Extension"));

                    }
                    Collections.sort(fileList);
                    //FileNameItem fileNameItem=new FileNameItem(fileList);
                    myAdapter = new MyAdapter(Home.this, userId, fileList);
                    myAdapter.notifyDataSetChanged();
                    mrecyclerView2.setLayoutManager(new LinearLayoutManager(Home.this));
                    mrecyclerView2.setAdapter(myAdapter);


                } else {
                    Log.d(TAG, "Error Getting Documents :", task.getException());
                }
            }
        });

    }


    public void showSettingsDialog() {                        //Creating Alert Dialog to request for Permission................
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("Need Permissions !");
        builder.setCancelable(false);

        builder.setMessage("Personal Vault needs certain Permissions to run.\nYou can grant them in App Settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Home.this.finish();
                System.exit(0);     //exits from the app.............
            }
        });
        builder.show();

    }

    public void openSettings() {                           //code to open settings in mobile............
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
        try {
            onBackPressed();
        } catch (Exception e) {
            finish();
        }


    }
    //Ending of Run Time Permissions Code......................................

    public void logout(MenuItem item) {
        //mp=MediaPlayer.create(this,R.raw.logout);
        //mp.start();
        FirebaseAuth.getInstance().signOut();
        finish();//to log out
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    public void aboutApplication(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setTitle("About Personal Vault");
        builder.setCancelable(true);

        builder.setMessage("Personal Vault is a file upload and download application. You can upload your local files to the cloud storage and can access those files anywhere.You can upload all format of files.\n\nThis application is still under development process. You may not experience the full features now.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });
        builder.show();
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


}