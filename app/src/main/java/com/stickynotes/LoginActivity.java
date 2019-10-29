package com.stickynotes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private SharedPrefManager sharedPrefManager;
    private Button loginButton;
    private TextView registerBtn;
    private TextInputLayout email, password;
    private String emailText, passwordText;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPrefManager = new SharedPrefManager(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        initComponents();
//        loginWithDefaultCredentials();
    }

    private void initComponents() {
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.signup_button);
        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isFormValid()) {
                            showProgressDialog("Please wait...");
                            loginUser();
                        } else {
                            Constants.showToast(LoginActivity.this, "Please correct the errors");
                        }
                    }
                }
        );

        registerBtn = (TextView) findViewById(R.id.login_here_btn);
        registerBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // start the sign up activity by clearing the stack
                        startActivity(Constants.getSimpleIntent(
                                LoginActivity.this, SignupActivity.class
                        ));
                    }
                }
        );
    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // LOGGED IN SUCCESSFULLY
                                    hideProgressDialog();
                                    Constants.showToast(LoginActivity.this, "Logged in Successfully");
                                    startActivity(
                                            Constants.getClearHistoryIntent(
                                                    LoginActivity.this,
                                                    MainActivity.class
                                            )
                                    );
                                }
                            }
                    ).addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                hideProgressDialog();
                                Constants.showToast(LoginActivity.this, e.getMessage());
                                FormValidationHelper.setError(email, e.getMessage());
                            }
                        }
        );
    }

    private boolean isFormValid() {
        boolean valid = true;

        // EMAIL VALIDATION
        if (!FormValidationHelper.isEmpty(email)) {
            // EMAIL FIELD HAS BEEN SET, CHECK FOR VALIDITY
            emailText = FormValidationHelper.getTextFromEditText(email);
            if (!FormValidationHelper.isValidEmail(emailText)) {
                valid = false;
                Constants.log("EMAIL IS INVALID");
                FormValidationHelper.setError(email, "Kindly enter a valid email address");
            } else {
                FormValidationHelper.clearError(email);
            }

        } else {
            // EMAIL IS EMPTY
            valid = false;
            Constants.log("EMAIL FIELD IS EMPTY");
            FormValidationHelper.setError(email, "Email address is required");
        }

        // PASSWORD VALIDATION
        if (FormValidationHelper.isEmpty(password)) {
            // PASSWORD FIELD IS EMPTY
            valid = false;
            Constants.log("PASSWORD FIELD IS EMPTY");
            FormValidationHelper.setError(password, "Password is required");
        } else {
            passwordText = FormValidationHelper.getTextFromEditText(password);
            FormValidationHelper.clearError(password);
        }

        return valid;
    }

    private void gotToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void loginWithDefaultCredentials() {
        String email = "2016.walsh.fernandes@ves.ac.in";
        String password = "gitbtitw";

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Logged in..", Toast.LENGTH_SHORT).show();
                                gotToHomeScreen();
                            } else {
                                Toast.makeText(LoginActivity.this, "Log in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                );
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }
}
