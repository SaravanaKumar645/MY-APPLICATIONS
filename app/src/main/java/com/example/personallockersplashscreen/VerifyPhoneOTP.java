package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneOTP extends AppCompatActivity {
    public static final String TAG = "PHONE_SIGNIN";
    FirebaseFirestore firestoreCloud;
    MediaPlayer buttonSound;
    FirebaseAuth mAuth;
    RelativeLayout relativeLayout4;
    Button mVerify;
    ProgressBar mVerify_pgBar;
    EditText mOTP_area;
    String verificationCodeBySystem, phoneNo, mail, name, password, userId1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_phone_otp);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firestoreCloud = FirebaseFirestore.getInstance();
        mVerify = findViewById(R.id.verify_otp_btn);
        mOTP_area = findViewById(R.id.otp_enter_area);
        mVerify_pgBar = findViewById(R.id.progressBar_verifyOTP);
relativeLayout4=findViewById(R.id.relativeLayout_pgbar_otp);
        name = getIntent().getStringExtra("Name");
        mail = getIntent().getStringExtra("Email");
        password = getIntent().getStringExtra("Password");
        phoneNo = getIntent().getStringExtra("PhoneNo");
       // buttonSound= MediaPlayer.create(this, R.raw.sound3);


        sendVerificationCodeToUser(phoneNo);

        mVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // buttonSound.start();
                String code = mOTP_area.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    mOTP_area.setError("OTP is Invalid");
                    mOTP_area.requestFocus();
                    return;
                }
               // mVerify_pgBar.setVisibility(View.VISIBLE);
                verfiyCode(code);
            }
        });

    }


    private void sendVerificationCodeToUser(String mphoneNo) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mphoneNo)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationCodeBySystem = s;
        }

        @Override

        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
               // mVerify_pgBar.setVisibility(View.VISIBLE);
                verfiyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneOTP.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verfiyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codeByUser);

        /*mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {*/
                            //mVerify_pgBar.setVisibility(View.GONE);

                            mAuth.createUserWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    relativeLayout4.setVisibility(View.VISIBLE);
                                    mVerify_pgBar.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        //send verification Link
                                        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()){

                                                    Intent intent = new Intent(getApplicationContext(), PasscodeSet.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    relativeLayout4.setVisibility(View.GONE);
                                                    mVerify_pgBar.setVisibility(View.GONE);
                                                    startActivity(intent);
                                                }else {
                                                    Toast.makeText(VerifyPhoneOTP.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        });
                                        FirebaseUser fuser = mAuth.getCurrentUser();
                                        Toast.makeText(VerifyPhoneOTP.this, "User Created.", Toast.LENGTH_SHORT).show();


                                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(VerifyPhoneOTP.this, "Verfication Email has been Sent ! Click to Verify.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Email not Sent " + e.getMessage());
                                            }
                                        });

                                    }
                                    userId1 = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = firestoreCloud.collection("UserDetails").document(userId1);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("FullName", name);
                                    user.put("E-mail", mail);
                                    user.put("Mobile-No", phoneNo);
                                    user.put("Password",password);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "onSuccess: User Profile is created for " + userId1);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "onFailure: " + e.toString());
                                        }
                                    });
                                }
                            });




                        /*} else {
                            Toast.makeText(VerifyPhoneOTP.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/
    }


}
