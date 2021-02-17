package com.example.MentalHealth.Controller;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText userUsername, userFirstName, userLastName, userLocation, userCounsellorCode, userReligion, userEthnicity, userLanguages, userGender;
    private Button updateAccountButton;
    private ProgressDialog loadingBar;
    private DatabaseReference settingsUserRef, userTypeUserRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        settingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        userTypeUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("usertype");

        mToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Edit Profile");

        // when back arrow clicked go back to MainActivity check AndroidManifest.xml
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingBar = new ProgressDialog(this);

        userUsername = (EditText) findViewById(R.id.settings_username);
        userFirstName = (EditText) findViewById(R.id.settings_first_name);
        userLastName = (EditText) findViewById(R.id.settings_last_name);
        userLocation = (EditText) findViewById(R.id.settings_location);
        userCounsellorCode = (EditText) findViewById(R.id.settings_counsellor_code);
        userReligion = (EditText) findViewById(R.id.settings_religion);
        userEthnicity = (EditText) findViewById(R.id.settings_ethnicity);
        userLanguages = (EditText) findViewById(R.id.settings_languages);
        userGender = (EditText) findViewById(R.id.settings_gender);

        updateAccountButton = (Button) findViewById(R.id.update_account_settings);


        settingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage, myUsername, myFirstName, myLastName, myLocation, myCounsellorCode, myReligion, myEthnicity, myLanguages, myGender;;
                    // if usertype ==1 then patient role: hide all fields apart from username, firstname, lastname
                    if (dataSnapshot.child("usertype").getValue().toString().equals("1")) {
                        userLocation.setVisibility(View.INVISIBLE);
                        userCounsellorCode.setVisibility(View.INVISIBLE);
                        userReligion.setVisibility(View.INVISIBLE);
                        userEthnicity.setVisibility(View.INVISIBLE);
                        userLanguages.setVisibility(View.INVISIBLE);
                        userGender.setVisibility(View.INVISIBLE);
                    }

                    if (dataSnapshot.child("username").exists()) {
                        myUsername = dataSnapshot.child("username").getValue().toString();
                        userUsername.setText(myUsername);
                    }
                    if (dataSnapshot.child("firstname").exists()) {
                        myFirstName = dataSnapshot.child("firstname").getValue().toString();
                        userFirstName.setText(myFirstName);
                    }
                    if (dataSnapshot.child("lastname").exists()) {
                        myLastName = dataSnapshot.child("lastname").getValue().toString();
                        userLastName.setText(myLastName);
                    }
                    if (dataSnapshot.child("location").exists()) {
                        myLocation = dataSnapshot.child("location").getValue().toString();
                        userLocation.setText(myLocation);
                    }
                    if (dataSnapshot.child("counsellorcode").exists()) {
                        myCounsellorCode = dataSnapshot.child("counsellorcode").getValue().toString();
                        userCounsellorCode.setText(myCounsellorCode);
                    }
                    if (dataSnapshot.child("religion").exists()) {
                        myReligion = dataSnapshot.child("religion").getValue().toString();
                        userReligion.setText(myReligion);
                    }
                    if (dataSnapshot.child("ethnicity").exists()) {
                        myEthnicity = dataSnapshot.child("ethnicity").getValue().toString();
                        userEthnicity.setText(myEthnicity);
                    }
                    if (dataSnapshot.child("languages").exists()) {
                        myLanguages = dataSnapshot.child("languages").getValue().toString();
                        userLanguages.setText(myLanguages);
                    }
                    if (dataSnapshot.child("gender").exists()) {
                        myGender = dataSnapshot.child("gender").getValue().toString();
                        userGender.setText(myGender);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        updateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

    }

    private void ValidateAccountInfo() {
        String username = userUsername.getText().toString();
        String firstname = userFirstName.getText().toString();
        String lastname = userLastName.getText().toString();
        String location = userLocation.getText().toString();
        String counsellorcode = userCounsellorCode.getText().toString();
        String religion = userReligion.getText().toString();
        String ethnicity = userEthnicity.getText().toString().toString();
        String languages = userLanguages.getText().toString();
        String gender = userGender.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please fill in username...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(firstname)) {
            Toast.makeText(this, "Please fill in first name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(lastname)) {
            Toast.makeText(this, "Please fill in last name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(location) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in location...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(counsellorcode) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in counsellor code...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(religion) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in religion...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ethnicity) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in ethnicity...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(languages) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in languages spoken...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(gender) && userTypeUserRef.equals("0")) {
            Toast.makeText(this, "Please fill in gender...", Toast.LENGTH_SHORT).show();
        } else {
            UpdateAccountInfo(username, firstname, lastname, location, counsellorcode, religion, ethnicity, languages, gender);
        }
    }

    private void UpdateAccountInfo(String username, String firstname, String lastname, String location, String counsellorcode, String religion, String ethnicity, String languages, String gender) {
        // using HashMap as we are inserting 6 fields
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("firstname", firstname);
        userMap.put("lastname", lastname);
        userMap.put("location", location);
        userMap.put("counsellorcode", counsellorcode);
        userMap.put("religion", religion);
        userMap.put("ethnicity", ethnicity);
        userMap.put("languages", languages);
        userMap.put("gender", gender);
        settingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(SettingsActivity.this, "Account details updated!", Toast.LENGTH_SHORT).show();
                    SendUserToProfileActivity();
                    loadingBar.dismiss();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error occurred when updating account details", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });

    }

    private void SendUserToProfileActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, ProfileActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
