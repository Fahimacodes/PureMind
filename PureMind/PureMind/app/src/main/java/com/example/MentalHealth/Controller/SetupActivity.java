package com.example.MentalHealth.Controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MentalHealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {
    private EditText setup_username, setup_first_name, setup_last_name, setup_counsellor_code, setup_location, setup_religion, setup_gender, setup_ethnicity, setup_languages;
    private Button saveButton;

    String currentUserID;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef, userTypeUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        userTypeUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("usertype");

        setup_username = (EditText) findViewById(R.id.setup_username);
        setup_first_name = (EditText) findViewById(R.id.setup_first_name);
        setup_last_name = (EditText) findViewById(R.id.setup_last_name);
        setup_counsellor_code = (EditText) findViewById(R.id.setup_counsellor_code);
        setup_location = (EditText) findViewById(R.id.setup_location);
        setup_religion = (EditText) findViewById(R.id.setup_religion);
        setup_gender = (EditText) findViewById(R.id.setup_gender);
        setup_ethnicity = (EditText) findViewById(R.id.setup_ethnicity);
        setup_languages = (EditText) findViewById(R.id.setup_languages);
        saveButton = (Button) findViewById(R.id.saveButton);

        loadingBar = new ProgressDialog(this);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("usertype").getValue().toString().equals("1")) {
                    setup_location.setVisibility(View.INVISIBLE);
                    setup_counsellor_code.setVisibility(View.INVISIBLE);
                    setup_religion.setVisibility(View.INVISIBLE);
                    setup_languages.setVisibility(View.INVISIBLE);
                    setup_ethnicity.setVisibility(View.INVISIBLE);
                    setup_gender.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {

            }
        });

    }

    private void SaveAccountSetupInformation() {
        String username = setup_username.getText().toString();
        String firstName = setup_first_name.getText().toString();
        String lastName = setup_last_name.getText().toString();
        String counsellorCode = setup_counsellor_code.getText().toString();
        String location = setup_location.getText().toString();
        String religion = setup_religion.getText().toString();
        String gender = setup_gender.getText().toString();
        String ethnicity = setup_ethnicity.getText().toString();
        String languages = setup_languages.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Please enter first name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Please enter last name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(counsellorCode) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter valid code", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(location) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter current location", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(religion) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter religion followed", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter gender", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ethnicity) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter ethnic background", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(languages) && userTypeUsersRef.equals("0")) {
            Toast.makeText(this, "Please enter languages spoken", Toast.LENGTH_SHORT).show();
        } else {

            loadingBar.setTitle("Saving information");
            loadingBar.setMessage("Please wait while we save information");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            HashMap userMap = new HashMap();
            userMap.put("username", username);
            userMap.put("firstname", firstName);
            userMap.put("lastname", lastName);
            userMap.put("counsellorcode", counsellorCode);
            userMap.put("location", location);
            userMap.put("religion", religion);
            userMap.put("ethnicity", ethnicity);
            userMap.put("languages", languages);
            userMap.put("gender", gender);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NotNull Task task) {
                    if (task.isSuccessful()) {
                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
