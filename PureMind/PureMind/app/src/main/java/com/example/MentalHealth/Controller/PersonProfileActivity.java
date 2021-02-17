package com.example.MentalHealth.Controller;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView personUsername, personFirstName, personLastName, personLocation, personCounsellorCode, personReligion, personLanguages, personEthnicity, personGender;
    private Button sendConnectionRequestButton, cancelConnectionRequestButton;
    private DatabaseReference ConnectionRequestRef, UsersRef, ConnectionsRef;
    private FirebaseAuth mAuth;
    String senderUserId, receiverUserId, CURRENT_STATE, saveCurrentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();

        senderUserId = mAuth.getCurrentUser().getUid();
        receiverUserId = getIntent().getExtras().get("visit_key").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ConnectionRequestRef = FirebaseDatabase.getInstance().getReference().child("ConnectionRequests");
        ConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections");

        InitializeFields();

        UsersRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myUsername, myFirstName, myLastName, myLocation, myCounsellorCode, myReligion, myLanguages, myEthnicity, myGender;

                    // if usertype ==1 then patient role: hide all fields apart from username, firstname, lastname
                    if (dataSnapshot.child("usertype").getValue().toString().equals("1")) {
                        personLocation.setVisibility(View.INVISIBLE);
                        personCounsellorCode.setVisibility(View.INVISIBLE);
                        personGender.setVisibility(View.INVISIBLE);
                        personEthnicity.setVisibility(View.INVISIBLE);
                        personLanguages.setVisibility(View.INVISIBLE);
                        personReligion.setVisibility(View.INVISIBLE);
                    }

                    if (dataSnapshot.child("username").exists()) {
                        myUsername = dataSnapshot.child("username").getValue().toString();
                        personUsername.setText("@" + myUsername);
                    }
                    if (dataSnapshot.child("firstname").exists()) {
                        myFirstName = dataSnapshot.child("firstname").getValue().toString();
                        personFirstName.setText("First Name: " + myFirstName);
                    }
                    if (dataSnapshot.child("lastname").exists()) {
                        myLastName = dataSnapshot.child("lastname").getValue().toString();
                        personLastName.setText("Last Name: " + myLastName);
                    }
                    if (dataSnapshot.child("location").exists()) {
                        myLocation = dataSnapshot.child("location").getValue().toString();
                        personLocation.setText("Location: " + myLocation);
                    }
                    if (dataSnapshot.child("counsellorcode").exists()) {
                        myCounsellorCode = dataSnapshot.child("counsellorcode").getValue().toString();
                        personCounsellorCode.setText("Counsellor Code: " + myCounsellorCode);
                    }
                    if (dataSnapshot.child("religion").exists()) {
                        myReligion = dataSnapshot.child("religion").getValue().toString();
                        personReligion.setText("Religion: " + myReligion);
                    }
                    if (dataSnapshot.child("languages").exists()) {
                        myLanguages = dataSnapshot.child("languages").getValue().toString();
                        personLanguages.setText("Languages: " + myLanguages);
                    }
                    if (dataSnapshot.child("ethnicity").exists()) {
                        myEthnicity = dataSnapshot.child("ethnicity").getValue().toString();
                        personEthnicity.setText("Ethnicity: " + myEthnicity);
                    }
                    if (dataSnapshot.child("gender").exists()) {
                        myGender = dataSnapshot.child("gender").getValue().toString();
                        personGender.setText("Gender: " + myGender);
                    }

                    MaintainButtonState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
        cancelConnectionRequestButton.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)) {
            sendConnectionRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendConnectionRequestButton.setEnabled(false);

                    if(CURRENT_STATE.equals("not_connected")){
                        SendConnectionRequest();
                    }
                    if (CURRENT_STATE.equals("request_sent")){
                            CancelConnectionRequest();
                    }
                    if (CURRENT_STATE.equals("request_received")){
                        AcceptConnectionRequest();
                    }
                    if (CURRENT_STATE.equals("connected")){
                        DisconnectExistingConnection();
                    }
                }
            });
        } else {
            sendConnectionRequestButton.setVisibility(View.INVISIBLE);
            cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void DisconnectExistingConnection() {
        ConnectionsRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ConnectionsRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendConnectionRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_connected";
                                                sendConnectionRequestButton.setText("SEND CONNECT REQUEST");

                                                cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                                cancelConnectionRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptConnectionRequest() {
        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
        saveCurrentDate = currentDate.format(callForDate.getTime());
        // store date for sender then store for receiver
        ConnectionsRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ConnectionsRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                        // remove connection request from database
                                                ConnectionRequestRef.child(senderUserId).child(receiverUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    ConnectionRequestRef.child(receiverUserId).child(senderUserId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        sendConnectionRequestButton.setEnabled(true);
                                                                                        CURRENT_STATE = "connected";
                                                                                        sendConnectionRequestButton.setText("DISCONNECT");

                                                                                        cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                                                                        cancelConnectionRequestButton.setEnabled(false);
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelConnectionRequest() {
        ConnectionRequestRef.child(senderUserId).child(receiverUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ConnectionRequestRef.child(receiverUserId).child(senderUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendConnectionRequestButton.setEnabled(true);
                                                CURRENT_STATE = "not_connected";
                                                sendConnectionRequestButton.setText("SEND CONNECT REQUEST");

                                                cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                                cancelConnectionRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintainButtonState() {
        ConnectionRequestRef.child(senderUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserId)){
                            String request_type = dataSnapshot.child(receiverUserId)
                                    .child("request_type").getValue().toString();

                            if(request_type.equals("sent")){
                                // for the sender
                                CURRENT_STATE = "request_sent";
                                sendConnectionRequestButton.setText("CANCEL CONNECT REQUEST");

                                cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                cancelConnectionRequestButton.setEnabled(false);
                            } else if(request_type.equals("received")){
                                // for receiver
                                CURRENT_STATE = "request_received";
                                sendConnectionRequestButton.setText("ACCEPT CONNECTION REQUEST");

                                cancelConnectionRequestButton.setVisibility(View.VISIBLE);
                                cancelConnectionRequestButton.setEnabled(true);

                                cancelConnectionRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelConnectionRequest();
                                    }
                                });
                            }

                        } else {
                            ConnectionsRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(receiverUserId)){
                                        CURRENT_STATE = "connected";
                                        sendConnectionRequestButton.setText("DISCONNECT");

                                        cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                        cancelConnectionRequestButton.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendConnectionRequest() {
        ConnectionRequestRef.child(senderUserId).child(receiverUserId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ConnectionRequestRef.child(receiverUserId).child(senderUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                sendConnectionRequestButton.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                sendConnectionRequestButton.setText("CANCEL CONNECT REQUEST");

                                                cancelConnectionRequestButton.setVisibility(View.INVISIBLE);
                                                cancelConnectionRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void InitializeFields() {
        personUsername = (TextView) findViewById(R.id.person_profile_username);
        personFirstName = (TextView) findViewById(R.id.person_profile_first_name);
        personLastName = (TextView) findViewById(R.id.person_profile_last_name);
        personLocation = (TextView) findViewById(R.id.person_profile_location);
        personCounsellorCode = (TextView) findViewById(R.id.person_profile_counsellor_code);
        personReligion = (TextView) findViewById(R.id.person_profile_religion);
        personLanguages = (TextView) findViewById(R.id.person_profile_languages);
        personEthnicity = (TextView) findViewById(R.id.person_profile_ethnicity);
        personGender = (TextView) findViewById(R.id.person_profile_gender);

        sendConnectionRequestButton = (Button) findViewById(R.id.person_send_connection_request);
        cancelConnectionRequestButton = (Button) findViewById(R.id.person_cancel_connection_request);

        CURRENT_STATE = "not_connected";
    }
}
