package com.example.personallockersplashscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText mEmail,mPassword1;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button mLoginbtn;
    RelativeLayout relativeLayout;
    MediaPlayer mp;
    ProgressBar pgbar1;
    TextView frgtpass,mdonthvacc;
    ImageView mShowhidePassword;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_user);
        relativeLayout=findViewById(R.id.relativeLayout_pgbar);
        mEmail=findViewById(R.id.email);
        mShowhidePassword=findViewById(R.id.show_pass_btn);
        mPassword1=findViewById(R.id.password);
        pgbar1=findViewById(R.id.progressBar1);
        fAuth=FirebaseAuth.getInstance();
        mLoginbtn=findViewById(R.id.loginbtn);
        mdonthvacc=findViewById(R.id.donthvacc);
        frgtpass=findViewById(R.id.frgtpassword);



        getSupportActionBar().hide();

        mShowhidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPasswordButton(view);
            }
        });


        if(fAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(), Passcode.class));
            finish();
        }

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mp = MediaPlayer.create(Login.this, R.raw.sound3);
                /*new Thread() {
                    public void run() {
                        mp = MediaPlayer.create(Login.this, R.raw.sound3);
                        mp.start();
                    }


                }.start();*/
               /* try {
                    mp = MediaPlayer.create(Login.this, R.raw.sound3);
                    if (mp != null) {
                        mp.start();
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            public void onCompletion(MediaPlayer mp) {
                                mp.release();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mp != null) {
                        mp.release();
                    }
                }*/


                String email=mEmail.getText().toString().trim();
                String passwd=mPassword1.getText().toString().trim();

                if(!validateEmail() |  !validatePassword() )
                {

                    return ;
                }
                relativeLayout.setVisibility(View.VISIBLE);
                pgbar1.setVisibility(View.VISIBLE);

                //Authentication of user

                fAuth.signInWithEmailAndPassword(email,passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            startActivity(new Intent(getApplicationContext(), Passcode.class));
                            finish();
                        }else {
                            Toast.makeText(Login.this, "Error ! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            relativeLayout.setVisibility(View.GONE);
                            pgbar1.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
        mdonthvacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
                //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                finish();

            }
        });

        frgtpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Receive Reset Link.");
                passwordResetDialog.setView(resetMail);


                passwordResetDialog.setPositiveButton("Send Reset Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        if(TextUtils.isEmpty(mail)){
                             resetMail.setError("E-mail is Required !");
                             return;
                        }else if(!mail.matches(emailPattern)){
                            resetMail.setError("Invalid Email Address");
                        }
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "Password Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "Error ! Password Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();

            }
        });


    }

    private void showPasswordButton(View view) {
        if(view.getId()==R.id.show_pass_btn){

            if(mPassword1.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                mShowhidePassword.setImageResource(R.drawable.ic_eye_icon);

                //Show Password
                mPassword1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mPassword1.setSelection(mPassword1.getText().length());

            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_eyeicon_hide);

                //Hide Password
                mPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mPassword1.setSelection(mPassword1.getText().length());


            }
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
        String val = mPassword1.getText().toString().trim();

        if (val.isEmpty()) {
            mPassword1.setError("Field cannot be empty");
            return false;
        } else if (val.length()<=5) {
            mPassword1.setError("Password must be Atleast 6 characters");
            return false;
        } else {
            mPassword1.setError(null);
            mPassword1.setEnabled(true);
            return true;
        }
    }
}