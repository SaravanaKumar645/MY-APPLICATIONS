package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PasscodeSet extends AppCompatActivity {
EditText mPasscode, mCPasscode;
ProgressBar pgBar;
RelativeLayout relativeLayout3;
Button mSubmit;
FirebaseFirestore firestoreCloud;
FirebaseAuth mAuth;
String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_set);
        getSupportActionBar().hide();
        mAuth=FirebaseAuth.getInstance();

        firestoreCloud=FirebaseFirestore.getInstance();
        mPasscode=findViewById(R.id.passcode);
        relativeLayout3=findViewById(R.id.relativeLayout_pgbar_passcode);
        pgBar=findViewById(R.id.progress_bar_passcode);
        mCPasscode=findViewById(R.id.confirm_passcode);
        mSubmit=findViewById(R.id.passcode_btn);
        userId= mAuth.getCurrentUser().getUid();
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!validatePassCode()){
                    return;
                }
                relativeLayout3.setVisibility(View.VISIBLE);
                pgBar.setVisibility(View.VISIBLE);
                String passcode=mPasscode.getText().toString().trim();
                DocumentReference documentReference = firestoreCloud.collection("PasscodeDetail").document(userId);
                Map<String,Object> user = new HashMap<>();
                user.put("Passcode",passcode);
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        relativeLayout3.setVisibility(View.GONE);
                        pgBar.setVisibility(View.GONE);
                        Toast.makeText(PasscodeSet.this, "Welcome !", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(),Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                        // Log.d(TAG, "onSuccess: User Profile is created for "+ userId1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        relativeLayout3.setVisibility(View.GONE);
                        pgBar.setVisibility(View.GONE);
                        Toast.makeText(PasscodeSet.this, "Error Occured ! Re-enter the Passcode", Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getApplicationContext(),PasscodeSet.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });



            }
        });

    }
    private Boolean validatePassCode() {
        String val = mPasscode.getText().toString().trim();
        String val1 = mCPasscode.getText().toString().trim();

        if (val.isEmpty()) {
            mPasscode.setError("Field cannot be empty");
            return false;
        } else if (val.length() > 4) {
            mPasscode.setError("PassCode must be 4 digits");
            return false;
        } else if (val1.isEmpty()) {
            mCPasscode.setError("Field cannot be empty");
            return false;
        } else if(!(val.matches(val1))){
            mCPasscode.setError("Passcode doesn't match");
            return false;
        }
        else {
            mPasscode.setError(null);
            mCPasscode.setError(null);
            mCPasscode.setEnabled(true);
            mPasscode.setEnabled(true);
            return true;
        }
    }
    }