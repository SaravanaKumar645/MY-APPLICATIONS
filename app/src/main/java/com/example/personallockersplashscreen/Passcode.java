package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hanks.passcodeview.PasscodeView;

import java.util.ArrayList;

public class Passcode extends AppCompatActivity {
PasscodeView mPasscode;
    FirebaseFirestore firestoreCloud;
    FirebaseAuth mAuth;
    FirebaseUser fUser;
    String userId,passCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);
        getSupportActionBar().hide();
//        fUser=mAuth.getCurrentUser();
        mPasscode=findViewById(R.id.passcodeView);
        mAuth=FirebaseAuth.getInstance();
        firestoreCloud=FirebaseFirestore.getInstance();
        userId= mAuth.getCurrentUser().getUid();
        Log.e("ATTENTION", "userID: "+userId );
        mPasscode.setWrongInputTip("Incorrect Passcode.");
        /*if(mAuth.getCurrentUser()!=null) {
            mPasscode.setPasscodeLength(4).setLocalPasscode("1111").setListener(new PasscodeView.PasscodeViewListener() {
                @Override
                public void onFail() {

                    Toast.makeText(Passcode.this, "Enter Correct Password", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String number) {
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }
            });*/
            DocumentReference documentReference = firestoreCloud.collection("PasscodeDetail").document(userId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot != null) {
                            passCode = documentSnapshot.getString("Passcode");
                            Log.e("SEEEEE THISSSS", "CODE: " + passCode);

                            mPasscode.setPasscodeLength(4).setPasscodeType(PasscodeView.PasscodeViewType.TYPE_CHECK_PASSCODE).setLocalPasscode(passCode).setListener(new PasscodeView.PasscodeViewListener() {
                                @Override
                                public void onFail() {

                                    Toast.makeText(Passcode.this, "Check your Passcode and Try Again !", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(String number) {
                                    Toast.makeText(Passcode.this, "Welcome !", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Home.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                }
                            });

                        } else {
                            Toast.makeText(Passcode.this, "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
    }





}