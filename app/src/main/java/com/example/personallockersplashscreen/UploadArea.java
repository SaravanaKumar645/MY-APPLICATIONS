package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadArea extends AppCompatActivity {
    private static final String TAG="UploadActivity";
    Button mSelect, mUpload2;
    //AppCompatActivity appCompatActivity;
    String fName, fExtension,fNamedevice;
     String userPath,userId;
    TextView mNotify,mSelectedFile;
    ProgressDialog progressDialog;
    Uri fileUri;
    FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestoreCloud;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_area);

        mSelectedFile=findViewById(R.id.selected_file);
        mSelect=findViewById(R.id.select_btn);
        mUpload2=findViewById(R.id.upload_btn_normal);
        mNotify=findViewById(R.id.notify);
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        userPath="user/"+userId+"/";

        firebaseStorage=FirebaseStorage.getInstance();
        firestoreCloud=FirebaseFirestore.getInstance();
        getSupportActionBar().setTitle("Cloud  Upload");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectFile();
            }
        });

        mUpload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileUri!=null){
                    uploadFile(fileUri);
                }else{
                    Toast.makeText(UploadArea.this, "Select a File", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                this.finish();
                Intent intent = new Intent(getApplicationContext(), Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //startActivity(new Intent(getApplicationContext(),Home.class));
                //finish();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void uploadFile(Uri fileUri){

        final String toFilePath=userPath+fName;
        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setProgress(0);
        //progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storageReference=firebaseStorage.getReference();
        StorageReference uploadRef=storageReference.child(toFilePath);

        uploadRef.putFile(fileUri).
                         addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                 progressDialog.dismiss();


                                 uploadRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                     @Override
                                     public void onSuccess(Uri uri) {
                                         Uri downloadUrl=uri;
                                         String docId=userId+fName;
                                         //Toast.makeText(UploadArea.this, "Upload Done !", Toast.LENGTH_SHORT).show();
                                         Map<String,Object> uploadedFile=new HashMap<>();
                                         uploadedFile.put("URL",downloadUrl.toString());
                                         uploadedFile.put("FileName", fName);
                                         uploadedFile.put("Extension",fExtension);
                                         uploadedFile.put("Date", getTodaysDate());
                                         uploadedFile.put("UserId", userId);
                                         firestoreCloud.collection("UploadedFiles").document(docId).set(uploadedFile)
                                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         Log.d(TAG,"File Details added to FireStore.");

                                                     }
                                                 }).addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                 Log.e(TAG,"Error Adding File Details.",e);
                                             }
                                         });

                                     }
                                 });
                                 Toast.makeText(UploadArea.this, "File Uploaded Successfully !", Toast.LENGTH_LONG).show();

                                 mSelectedFile.setVisibility(View.INVISIBLE);
                                 mNotify.setVisibility(View.INVISIBLE);
                                 mNotify.setText("");
                             }
                         }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadArea.this, "File Upload Failed ! !", Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                
                int currentProgress=(int)(100*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
                progressDialog.incrementProgressBy(currentProgress);
                progressDialog.getProgress();


            }
        });

    }





    public void selectFile(){
        Intent intent=new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100&&resultCode==RESULT_OK&&data!=null)
        {
            fileUri=data.getData();
            fExtension= getFileExtension(fileUri);
            String name;

            Cursor cursor = getContentResolver().query(fileUri,null,null,null,null);
            if(cursor == null) name=fileUri.getPath();
            else{
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                name = cursor.getString(idx);
                cursor.close();
            }

            fName = name.substring(0,name.lastIndexOf("."))+fExtension;
            //String extension = name.substring(name.lastIndexOf(".")+1);

            fNamedevice= (data.getData().getLastPathSegment());
            //mSelectedFile.setEnabled(true);
            mSelectedFile.setVisibility(View.VISIBLE);
            mNotify.setVisibility(View.VISIBLE);
            //mNotify.setEnabled(true);
            mNotify.setText(fName);
        }else{
            Toast.makeText(UploadArea.this, "Please Select a File !", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        String extension;
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension="."+(mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)));
        return extension;
    }




    /*private void addFilenametoCloud(final String fileNAME) {
        String docKey = userId+fileNAME;
        DocumentReference documentReference = firestoreCloud.collection("uploadedfiles").document(docKey);
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("storagePath", fileNAME);
        mp.put("date", getTodaysDate());
        mp.put("userId", userId);

        documentReference
                .set(mp)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "File name has been added to Cloud ");
                        } else {
                            Log.e(TAG, "Failed to add file name to Cloud " + fileNAME);
                        }

                    }
                });
    }*/

    private String getTodaysDate() {
        Date currentDate = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("MM/dd/YYYY");
        return simpleDateFormat.format(currentDate);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        //startActivity(new Intent(getApplicationContext(),Home.class));
        //finish();
        super.onBackPressed();
    }
}