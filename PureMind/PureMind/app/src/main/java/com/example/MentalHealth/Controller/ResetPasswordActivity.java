package com.example.MentalHealth.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.MentalHealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button buttonResetPassword;
    private EditText editTextResetPasswordEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mToolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword);
        editTextResetPasswordEmail = (EditText) findViewById(R.id.editTextResetPasswordEmail);
        mAuth = FirebaseAuth.getInstance();

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = editTextResetPasswordEmail.getText().toString();
                if (TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(ResetPasswordActivity.this, "Please enter valid email..", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ResetPasswordActivity.this, "Reset password link sent", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                            } else {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
