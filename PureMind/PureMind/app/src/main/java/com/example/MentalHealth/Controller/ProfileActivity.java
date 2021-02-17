package com.example.MentalHealth.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.MentalHealth.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private TextView userUsername, userFirstName, userLastName, userLocation, userCounsellorCode, userReligion, userEthnicity, userLanguages, userGender;
    private Button edit_profile_button, myConnections;
    private DatabaseReference UsersRef, ConnectionsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private int connectionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        ConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections");

        userUsername = (TextView) findViewById(R.id.my_profile_username);
        userFirstName = (TextView) findViewById(R.id.my_profile_first_name);
        userLastName = (TextView) findViewById(R.id.my_profile_last_name);
        userLocation = (TextView) findViewById(R.id.my_profile_location);
        userCounsellorCode = (TextView) findViewById(R.id.my_counsellor_code);
        userReligion  = (TextView) findViewById(R.id.my_religion);
        userEthnicity = (TextView) findViewById(R.id.my_ethnicity);
        userLanguages = (TextView) findViewById(R.id.my_languages);
        userGender  = (TextView) findViewById(R.id.my_gender);

        edit_profile_button = (Button) findViewById(R.id.edit_profile_button);

        edit_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSettingsActivity();
            }
        });

        myConnections = (Button) findViewById(R.id.no_of_connections);

        myConnections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToConnectionsActivity();
            }
        });

        ConnectionsRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // count no. of child under reference of user
                    connectionCount = (int) dataSnapshot.getChildrenCount();
                    myConnections.setText(Integer.toString(connectionCount) + " Connections");
                } else {
                    myConnections.setText("0 Connections");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage, myUsername, myFirstName, myLastName, myLocation, myCounsellorCode, myReligion, myEthnicity, myLanguages, myGender;

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
                        userUsername.setText("@" + myUsername);
                    }
                    if (dataSnapshot.child("firstname").exists()) {
                        myFirstName = dataSnapshot.child("firstname").getValue().toString();
                        userFirstName.setText("First Name: " + myFirstName);
                    }
                    if (dataSnapshot.child("lastname").exists()) {
                        myLastName = dataSnapshot.child("lastname").getValue().toString();
                        userLastName.setText("Last Name: " + myLastName);
                    }
                    if (dataSnapshot.child("location").exists()) {
                        myLocation = dataSnapshot.child("location").getValue().toString();
                        userLocation.setText("Location: " + myLocation);
                    }
                    if (dataSnapshot.child("counsellorcode").exists()) {
                        myCounsellorCode = dataSnapshot.child("counsellorcode").getValue().toString();
                        userCounsellorCode.setText("Counsellor Code: " + myCounsellorCode);
                    }
                    if (dataSnapshot.child("religion").exists()) {
                        myReligion = dataSnapshot.child("religion").getValue().toString();
                        userReligion.setText("Religion: " + myReligion);
                    }
                    if (dataSnapshot.child("ethnicity").exists()) {
                        myEthnicity = dataSnapshot.child("ethnicity").getValue().toString();
                        userEthnicity.setText("Ethnicity: " + myEthnicity);
                    }
                    if (dataSnapshot.child("languages").exists()) {
                        myLanguages = dataSnapshot.child("languages").getValue().toString();
                        userLanguages.setText("Languages: " + myLanguages);
                    }
                    if (dataSnapshot.child("gender").exists()) {
                        myGender = dataSnapshot.child("gender").getValue().toString();
                        userGender.setText("Gender: " + myGender);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSettingsActivity() {
        Intent editProfileIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(editProfileIntent);
        finish();
    }
    private void SendUserToConnectionsActivity() {
        Intent connectionsIntent = new Intent(ProfileActivity.this, ConnectionsActivity.class);
        startActivity(connectionsIntent);
    }
}
