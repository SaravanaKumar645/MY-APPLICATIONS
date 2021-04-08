package com.example.personallockersplashscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    MediaPlayer mp;
    RelativeLayout relativeLayout1;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    EditText mFullname, mEmail, mPassword, mConfirmPassword, mPhone;
    Button mRegisterbutn, mResendLinkbtn;
    TextView mAlreadyhvacc;
    FirebaseAuth fAuth;
    ProgressBar pgbar;
    FirebaseFirestore fStore;
    String userId1;
    ImageView mshowpassreg, mshowconfirmpassreg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
relativeLayout1=findViewById(R.id.relativeLayout_pgbar_reg);
        mFullname = findViewById(R.id.fullname);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.comfirmpassword);
        mPhone = findViewById(R.id.phonenumber);
        mRegisterbutn = findViewById(R.id.register_button);
        mAlreadyhvacc = findViewById(R.id.alreadyhvacc);
        mshowpassreg = findViewById(R.id.show_pass_btn_reg);
        mshowconfirmpassreg = findViewById(R.id.show_confirmpass_btn_reg);
        mResendLinkbtn = findViewById(R.id.select_btn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        pgbar = findViewById(R.id.progress_bar_passcode);
//        mp = MediaPlayer.create(this, R.raw.sound3);

        getSupportActionBar().hide();

        mshowpassreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.show_pass_btn_reg) {

                    if (mPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        mshowpassreg.setImageResource(R.drawable.ic_eye_icon);

                        //Show Password
                        mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        mPassword.setSelection(mPassword.getText().length());

                    } else {
                        ((ImageView) (view)).setImageResource(R.drawable.ic_eyeicon_hide);

                        //Hide Password
                        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        mPassword.setSelection(mPassword.getText().length());


                    }
                }
            }
        });
        mshowconfirmpassreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.show_confirmpass_btn_reg) {

                    if (mConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                        mshowconfirmpassreg.setImageResource(R.drawable.ic_eye_icon);

                        //Show Password
                        mConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        mConfirmPassword.setSelection(mConfirmPassword.getText().length());

                    } else {
                        ((ImageView) (view)).setImageResource(R.drawable.ic_eyeicon_hide);

                        //Hide Password
                        mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        mConfirmPassword.setSelection(mConfirmPassword.getText().length());


                    }
                }
            }
        });

        mRegisterbutn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mp.start();
                final String email = mEmail.getText().toString().trim();
                String passwd = mPassword.getText().toString().trim();

                final String fullname = mFullname.getText().toString().trim();
                final String phone = mPhone.getText().toString().trim();
                Bundle bundle = new Bundle();
                bundle.putString("Name", fullname);
                bundle.putString("Email", email);
                bundle.putString("Password", passwd);
                bundle.putString("PhoneNo", phone);
                if (!validateName() | !validatePassword() | !validatePhoneNo() | !validateEmail() | !validateConfirmPassword()) {

                    return;
                }
                relativeLayout1.setVisibility(View.VISIBLE);
                pgbar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(getApplicationContext(), VerifyPhoneOTP.class);
                intent.putExtras(bundle);
                startActivity(intent);



                //Register user in the Firebase

               /* fAuth.createUserWithEmailAndPassword(email,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //send verification Link
                            FirebaseUser fuser=fAuth.getCurrentUser();
                            fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this,"Verfication Email has been Sent ! Click to Verify." , Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG,"onFailure: Email not Sent "+e.getMessage());
                                }
                            });

                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            userId1= fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userId1);
                            Map<String,Object> user = new HashMap<>();
                            user.put("FullName",fullname);
                            user.put("E-mail",email);
                            user.put("Mobile-No",phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: User Profile is created for "+ userId1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });
                            startActivity(new Intent(getApplicationContext(), Home.class));

                        }else {
                            Toast.makeText(Register.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            pgbar.setVisibility(View.GONE);
                        }

                    }
                });*/
            }
        });
        mAlreadyhvacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));

                //overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                finish();
            }
        });
    }


    private Boolean validateName() {

        String name = mFullname.getText().toString();
        if (name.isEmpty()) {
            mFullname.setError("Field cannot be empty");
            return false;
        } else if (name.length() >= 20) {
            mFullname.setError("Name Too Long");
            return false;
        } else {
            mFullname.setError(null);
            mFullname.setEnabled(true);
            return true;
        }

    }

    private Boolean validateEmail() {
        String val = mEmail.getText().toString().trim();


        if (val.isEmpty()) {
            mEmail.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattern)) {
            mEmail.setError("Invalid email address");
            return false;
        } else {
            mEmail.setError(null);
            mEmail.setEnabled(true);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = mPassword.getText().toString().trim();

        if (val.isEmpty()) {
            mPassword.setError("Field cannot be empty");
            return false;
        } else if (val.length() <= 5) {
            mPassword.setError("Password must be Atleast 6 characters");
            return false;
        } else {
            mPassword.setError(null);
            mPassword.setEnabled(true);
            return true;
        }
    }

    private Boolean validatePhoneNo() {
        String val = mPhone.getText().toString().trim();

        if (val.isEmpty()) {
            mPhone.setError("Field cannot be empty");
            return false;
        } else {
            mPhone.setError(null);
            mPhone.setEnabled(true);
            return true;
        }
    }

    private Boolean validateConfirmPassword() {
        String val = mConfirmPassword.getText().toString().trim();
        String val2 = mPassword.getText().toString().trim();
        if (val.isEmpty()) {
            mConfirmPassword.setError("Field cannot be empty");
            return false;
        } else if (!val2.equals(val)) {
            mConfirmPassword.setError("Password doesn't match");
            return false;
        } else {
            mConfirmPassword.setError(null);
            mConfirmPassword.setEnabled(true);
            return true;
        }
    }


}