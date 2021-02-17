package com.example.MentalHealth.Controller;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MentalHealth.Model.Messages;
import com.example.MentalHealth.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChatToolBar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;

    private RecyclerView listUserMessages;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private String messageReceiverID, messageReceiverUsername, messageSenderID, saveCurrentDate, saveCurrentTime;

    private TextView receiverUsername;

    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        messageReceiverID = getIntent().getExtras().get("visit_key").toString();
        messageReceiverUsername = getIntent().getExtras().get("visit_username").toString();

        InitializeFields();
        DisplayReceiverInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });
        GetMessages();
    }

    private void InitializeFields() {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(action_bar_view);

        receiverUsername = (TextView) findViewById(R.id.custom_profile_username);

        sendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        userMessageInput = (EditText) findViewById(R.id.message_input);

        messagesAdapter = new MessagesAdapter(messagesList);
        listUserMessages = (RecyclerView) findViewById(R.id.list_user_messages);
        linearLayoutManager = new LinearLayoutManager(this);
        listUserMessages.setHasFixedSize(true);
        listUserMessages.setLayoutManager(linearLayoutManager);
        listUserMessages.setAdapter(messagesAdapter);
    }

    private void DisplayReceiverInfo() {
        receiverUsername.setText(messageReceiverUsername);

    }

    private void SendMessage() {
        String messageText = userMessageInput.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please type a message..", Toast.LENGTH_SHORT).show();
        } else {
            String message_sender_ref = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference messageRef = rootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();
            String messagePushKey = messageRef.getKey();

            Calendar calDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
            saveCurrentDate = currentDate.format(calDate.getTime());

            Calendar calTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calTime.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(message_sender_ref + "/" + messagePushKey, messageTextBody);
            messageBodyDetails.put(message_receiver_ref + "/" + messagePushKey, messageTextBody);

            rootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message sent successfully", Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    } else {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error occurred: " + errorMessage, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void GetMessages() {
        rootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
