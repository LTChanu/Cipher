package com.home.cipher;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignIn extends AppCompatActivity implements SharedVariable.DataSnapshotListener {

    private GoogleSignInClient client;
    private TextInputLayout email;
    private String stringEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        email = findViewById(R.id.inEmail);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        client = GoogleSignIn.getClient(this, options);

        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
    }

    public void login(View view) {
        stringEmail = Objects.requireNonNull(email.getEditText()).getText().toString();
        if (EmailValidator.isValidEmail(stringEmail)) {
            common.startLoading(this, "Sending OTP");
            String dbEmail = stringEmail.replaceAll("[.$\\[\\]#/\\\\]", "");
            String genOTP = common.generateOTP();
            String body = "Dear Friend, \n\nWelcome to Cipher. \n\nYour Login OTP is " + genOTP + ".\n\n If didn't you request this, Please ignore this massage";
            sendEmail se = new sendEmail(stringEmail, "Cipher Registration OTP", body);
            boolean isSent = se.getIsSent();
            common.stopLoading();
            if (isSent) {
                Toast.makeText(this, "Email sent.", Toast.LENGTH_LONG).show();
                Button sendBtn = findViewById(R.id.logBtn);
                sendBtn.setEnabled(false); // disable the button initially

                //popup
                PopupGetString popup = new PopupGetString(getLayoutInflater(), getWindow());
                popup.showAndWaitForInput("Email Verification", "Enter OTP you received", "OTP");
                popup.ok.setOnClickListener(v -> {
                    //check OTP
                    if (genOTP.equals(popup.text.getText().toString())) {
                        popup.popupWindow.dismiss();
                        CheckRegister(dbEmail);
                    } else
                        Toast.makeText(this, "Wrong OTP", Toast.LENGTH_LONG).show();
                });
                popup.cansel.setOnClickListener(v -> popup.popupWindow.dismiss());


                new CountDownTimer(60000, 1000) { // 60000 milliseconds = 1 minutes
                    public void onTick(long millisUntilFinished) {
                        // do nothing
                    }

                    public void onFinish() {
                        sendBtn.setEnabled(true); // enable the button after 5 minutes
                    }
                }.start();
            } else
                Toast.makeText(this, "Email not send!", Toast.LENGTH_LONG).show();
        } else
            common.showMessage(this, "Wrong Email", "Please check your email and retry.");
    }

    public boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean r = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!r) {
            common.stopLoading();
            Toast.makeText(SignIn.this, "No internet connection available", Toast.LENGTH_LONG).show();
        }
        return r;
    }

    public void signInWithGoogle() {
        Intent i = client.getSignInIntent();
        startActivityForResult(i, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (isNetworkConnected()) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == 1234) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            assert currentUser != null;
                            String email = currentUser.getEmail();
                            String name = currentUser.getDisplayName();
                            assert email != null;
                            stringEmail = email;
                            String dbEmail = email.replaceAll("[.$\\[\\]#/\\\\]", "");

                            CheckRegister(dbEmail, name);
                        } else {
                            Toast.makeText(SignIn.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void Register(String dbEmail, String name) {
        if (isNetworkConnected()) {
            common.startLoading(this, "Registering");
            DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
            FirebaseApp.initializeApp(this);

            Data.child("user/" + dbEmail + "/name").setValue(name);
            Data.child("user/" + dbEmail + "/email").setValue(stringEmail).addOnSuccessListener(unused -> {
                SharedVariable sharedVariable = new SharedVariable(SignIn.this);
                sharedVariable.setWhileLogin(dbEmail, true);
                common.stopLoading();
                startActivity(new Intent(SignIn.this, Sever.class));
                finish();
            });
        }
    }

    private void CheckRegister(String dbEmail) {
        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        Data.child("user/"+dbEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                common.startLoading(SignIn.this, "Checking");
                if (snapshot.exists()) {
                    common.stopLoading();
                    SharedVariable sharedVariable = new SharedVariable(SignIn.this);
                    sharedVariable.setWhileLogin(dbEmail, true);
                    startActivity(new Intent(SignIn.this, Sever.class));
                    finish();
                } else {
                    common.stopLoading();
                    setName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void CheckRegister(String dbEmail, String name) {
        common.startLoading(this, "Checking");
        FirebaseApp.initializeApp(this);
        DatabaseReference Data = FirebaseDatabase.getInstance().getReference();
        Data.child("user/"+dbEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    common.stopLoading();
                    SharedVariable sharedVariable = new SharedVariable(SignIn.this);
                    sharedVariable.setWhileLogin(dbEmail, true);
                    startActivity(new Intent(SignIn.this, Sever.class));
                    finish();
                } else {
                    common.stopLoading();
                    Register(dbEmail, name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setName() {
        common.stopLoading();
        String[] enteredText = {null};
        PopupGetString popup1 = new PopupGetString(getLayoutInflater(), getWindow());
        popup1.showAndWaitForInput("User Details", "Enter your name", "Name");
        popup1.text.setText("");
        popup1.ok.setOnClickListener(v -> {
            enteredText[0] = popup1.text.getText().toString();
            stringEmail = Objects.requireNonNull(email.getEditText()).getText().toString();
            String dbEmail = stringEmail.replaceAll("[.$\\[\\]#/\\\\]", "");

            Register(dbEmail, enteredText[0]);

            popup1.popupWindow.dismiss();
        });

        popup1.cansel.setOnClickListener(v -> popup1.popupWindow.dismiss());
    }

    @Override
    public void onDataSnapshotChanged(String dataSnapshot) {

    }
}