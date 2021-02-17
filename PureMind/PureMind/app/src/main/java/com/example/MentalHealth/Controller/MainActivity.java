package com.example.MentalHealth.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.MentalHealth.Model.FindCounsellors;
import com.example.MentalHealth.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;

    private ImageButton searchButton;
    private EditText searchInputText;
    private RecyclerView searchResultList;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID = mAuth.getCurrentUser().getUid();

        searchResultList = (RecyclerView) findViewById(R.id.main_search_result_list);
        searchResultList.setHasFixedSize(true);
        searchResultList.setLayoutManager(new LinearLayoutManager(this));

        searchButton = (ImageButton) findViewById(R.id.main_search_friends_button);
        searchInputText = (EditText) findViewById(R.id.main_search_box_input);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedText = searchInputText.getText().toString();
                DisplayAllUsers(searchedText);
            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
    }

    private void DisplayAllUsers(String searchedText) {

        Toast.makeText(this, "Searching...", Toast.LENGTH_SHORT).show();

        Query searchCounsellorsQuery = UsersRef.orderByChild("username")
                .startAt(searchedText).endAt(searchedText + "\uf8ff");

        FirebaseRecyclerOptions<FindCounsellors> options = new FirebaseRecyclerOptions.Builder<FindCounsellors>().
                setQuery(searchCounsellorsQuery, FindCounsellors.class).build();

        FirebaseRecyclerAdapter<FindCounsellors, MainActivity.FindCounsellorsViewHolder> adapter
                = new FirebaseRecyclerAdapter<FindCounsellors, MainActivity.FindCounsellorsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MainActivity.FindCounsellorsViewHolder holder,
                                            final int position, @NonNull FindCounsellors model) {

                final String VisitKey = getRef(position).getKey();
                holder.username.setText(model.getUsername());
                holder.fullname.setText("Name: " + model.getFirstname() + " " + model.getLastname());

                // 47 - sending the unique user id for PersonProfileActivity
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(MainActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_key", VisitKey);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public MainActivity.FindCounsellorsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_display_layout, parent, false);
                MainActivity.FindCounsellorsViewHolder viewHolder =
                        new MainActivity.FindCounsellorsViewHolder(view);
                return viewHolder;
            }
        };
        searchResultList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindCounsellorsViewHolder extends RecyclerView.ViewHolder {
        TextView username, fullname, date;

        public FindCounsellorsViewHolder(View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.all_users_profile_username);
            fullname = itemView.findViewById(R.id.all_users_profile_full_name);
            date = itemView.findViewById(R.id.all_users_profile_date);
        }
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                SendUserToMainActivity();
                break;
            case R.id.nav_profile:
                SendUserToProfileActivity();
                break;
            case R.id.nav_connections:
                SendUserToConnectionsActivity();
                break;
            case R.id.nav_logout:
                LogoutUserSendToLoginActivity();
                break;
        }
    }

    private void SendUserToMainActivity() {
        Intent homeIntent = new Intent(this, MainActivity.class);
        startActivity(homeIntent);
    }

    private void SendUserToProfileActivity() {
        Intent profileIntent = new Intent(this, ProfileActivity.class);
        startActivity(profileIntent);
    }

    private void SendUserToConnectionsActivity() {
        Intent connectionsIntent = new Intent(this, ConnectionsActivity.class);
        startActivity(connectionsIntent);
    }

    private void LogoutUserSendToLoginActivity() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
