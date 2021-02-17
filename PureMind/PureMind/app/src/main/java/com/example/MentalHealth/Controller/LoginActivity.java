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

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText textInputEditTextEmail, textInputEditTextPassword;
    private TextView textViewLinkRegister, textViewLinkForgottenPassword;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        textInputEditTextEmail = (EditText) findViewById(R.id.text);
        textInputEditTextPassword = (EditText) findViewById(R.id.textInputEditTextPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        textViewLinkRegister = (TextView) findViewById(R.id.textViewLinkRegister);
        textViewLinkForgottenPassword = (TextView) findViewById(R.id.forgot_password_link);
        loadingBar = new ProgressDialog(this);

        textViewLinkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegisterActivity();
            }
        });
        textViewLinkForgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }


    private void UserLogin() {
        String email = textInputEditTextEmail.getText().toString();
        String password = textInputEditTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Login to Account");
            loadingBar.setMessage("Please wait while we login to account");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }
}

