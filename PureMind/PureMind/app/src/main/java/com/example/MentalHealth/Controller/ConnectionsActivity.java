package com.example.MentalHealth.Controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MentalHealth.Model.Connections;
import com.example.MentalHealth.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ConnectionsActivity extends AppCompatActivity {

    private RecyclerView myConnectionList;
    private DatabaseReference ConnectionsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        myConnectionList = (RecyclerView) findViewById(R.id.connection_list);
        myConnectionList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myConnectionList.setLayoutManager(linearLayoutManager);

        DisplayAllConnections();
    }

    private void DisplayAllConnections() {

        FirebaseRecyclerOptions<Connections> options = new FirebaseRecyclerOptions.Builder<Connections>().
                setQuery(ConnectionsRef, Connections.class).build();

        final FirebaseRecyclerAdapter<Connections, ConnectionsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Connections, ConnectionsViewHolder>
                (options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConnectionsViewHolder holder, int position, @NonNull final Connections model) {

                final String usersID = getRef(position).getKey();

                UsersRef.child(usersID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            final String username = dataSnapshot.child("username").getValue().toString();
                            holder.username.setText(username);
                            final String fullname = dataSnapshot.child("firstname").getValue().toString() + " " + dataSnapshot.child("lastname").getValue().toString();
                            holder.fullname.setText(fullname);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.connectionDate.setText("Connected on: " + model.getDate());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String username = holder.username.getText().toString();
                        final CharSequence options[] = new CharSequence[]{
                                username + "'s Profile",
                                "Send Message"
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionsActivity.this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Intent profileIntent = new Intent(ConnectionsActivity.this, PersonProfileActivity.class);
                                    profileIntent.putExtra("visit_key", usersID);
                                    startActivity(profileIntent);
                                } else if (which == 1) {
                                    String username = holder.username.getText().toString();
                                    Intent chatIntent = new Intent(ConnectionsActivity.this, ChatActivity.class);
                                    chatIntent.putExtra("visit_key", usersID);
                                    chatIntent.putExtra("visit_username", username);

                                    startActivity(chatIntent);
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_users_display_layout, viewGroup, false);
                ConnectionsViewHolder viewHolder = new ConnectionsActivity.ConnectionsViewHolder(view);
                return viewHolder;
            }
        };

        myConnectionList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ConnectionsViewHolder extends RecyclerView.ViewHolder {

        TextView username, fullname, connectionDate;

        public ConnectionsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.all_users_profile_username);
            fullname = itemView.findViewById(R.id.all_users_profile_full_name);
            connectionDate = (TextView) itemView.findViewById(R.id.all_users_profile_date);

        }

    }


}
