package com.stickynotes;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    private TextView loginBtn;
    private TextInputLayout email, password, confirmPassword;
    private Button signupBtn;
    private ProgressDialog progressDialog;
    private String emailText, passwordText;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initComponents();
    }

    private void initComponents() {
        loginBtn = (TextView) findViewById(R.id.login_here_btn);
        loginBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(Constants.getSimpleIntent(
                                SignupActivity.this, LoginActivity.class
                        ));
                    }
                }
        );
        email = (TextInputLayout) findViewById(R.id.email);
        password = (TextInputLayout) findViewById(R.id.password);
        confirmPassword = (TextInputLayout) findViewById(R.id.password_confirm);
        signupBtn = (Button) findViewById(R.id.signup_button);
        signupBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: FORM VALIDATION
                        if (isFormValid()) {
                            // FORM IS VALID: GO TO MainActivity
                            // TODO: SIGN THE USER UP TO FIREBASE
                            showProgressDialog("Please wait...");
                            signupUser();
                        } else {
                            // FORM IS INVALID
                            Constants.showToast(SignupActivity.this, "Kindly Correct the errors");
                        }
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
            FormValidationHelper.clearError(password);
        }

        // CONFIRM PASSWORD VALIDATION
        if (FormValidationHelper.isEmpty(confirmPassword)) {
            // PASSWORD FIELD IS EMPTY
            valid = false;
            Constants.log("CONFIRM PASSWORD FIELD IS EMPTY");
            FormValidationHelper.setError(confirmPassword, "Kindly confirm your password");
        } else {
            FormValidationHelper.clearError(confirmPassword);
        }

        if (valid) {
            passwordText = FormValidationHelper.getTextFromEditText(password);
            String confirmPasswordText = FormValidationHelper.getTextFromEditText(password);
            if (!passwordText.equals(confirmPasswordText)) {
                // PASSWORDS DO NOT MATCH
                valid = false;
                Constants.log("PASSWORDS DO NOT MATCH");
                FormValidationHelper.setError(password, "Passwords do not match");
            }
        }

        return valid;
    }

    private void signupUser() {
        // USING THE FIREBASE AUTH INSTANCE TO CREATE USER WITH THE PROVIDED
        // EMAIL ID AND PASSWORD
        firebaseAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        hideProgressDialog();
                        Constants.showToast(
                                SignupActivity.this,
                                "User signed up successfully"
                        );
                        // GO TO MAIN ACTIVITY
                        startActivity(
                                Constants.getClearHistoryIntent(
                                        SignupActivity.this,
                                        MainActivity.class
                                )
                        );
                    }
                }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        e.getStackTrace();
                        FormValidationHelper.setError(email, e.getMessage());
                        Constants.showToast(SignupActivity.this, e.getMessage());
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
