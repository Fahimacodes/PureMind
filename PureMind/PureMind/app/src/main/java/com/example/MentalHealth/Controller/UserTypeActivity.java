package com.example.MentalHealth.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MentalHealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class UserTypeActivity extends AppCompatActivity {

    private Button counsellorButton, patientButton;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        counsellorButton = (Button) findViewById(R.id.counsellorButton);
        patientButton = (Button) findViewById(R.id.patientButton);

        counsellorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap userMap = new HashMap();
                userMap.put("usertype", 0);

                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NotNull Task task) {
                        if (task.isSuccessful()) {
                            SendUserToSetupActivity();
                                Toast.makeText(UserTypeActivity.this, "User type inserted", Toast.LENGTH_SHORT).show();

                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(UserTypeActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
        });

        patientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap userMap = new HashMap();
                userMap.put("usertype", 1);

                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NotNull Task task) {
                        if (task.isSuccessful()) {
                            SendUserToSetupActivity();
                            Toast.makeText(UserTypeActivity.this, "User type inserted", Toast.LENGTH_SHORT).show();

                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(UserTypeActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent mainIntent = new Intent(UserTypeActivity.this, SetupActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
