package com.example.MentalHealth.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MentalHealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;

public class RegisterActivity extends AppCompatActivity {

    private EditText textInputEditTextEmail, textInputEditTextPassword, textInputEditTextConfirmPassword;
    private TextView textViewLoginLink;
    private Button registerButton;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        textInputEditTextEmail = (EditText) findViewById(R.id.text);
        textInputEditTextPassword = (EditText) findViewById(R.id.textInputEditTextPassword);
        textInputEditTextConfirmPassword = (EditText) findViewById(R.id.textInputEditTextConfirmPassword);
        textViewLoginLink = (TextView) findViewById(R.id.textViewLinkLogin) ;
        registerButton = (Button) findViewById(R.id.registerButton);
        loadingBar = new ProgressDialog(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

        textViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // user is not authenticated
            SendUserToMainActivity();
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void CreateNewAccount() {
        String email = textInputEditTextEmail.getText().toString();
        String password = textInputEditTextPassword.getText().toString();
        String confirmPassword = textInputEditTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please wait while we  authenticate your account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToUserTypeActivity();
                                Toast.makeText(RegisterActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }
    private void SendUserToUserTypeActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, UserTypeActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
}
