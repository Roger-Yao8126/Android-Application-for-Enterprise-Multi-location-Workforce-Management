package com.example.a277project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;

public class ManagerProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawerLayout;
    private TextView username, role, firstname, lastname, address;
    private EditText etfirstname, etlastname, etaddress;
    private Button submit, signOut;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        username = findViewById(R.id.username);
        role = findViewById(R.id.showRole);
        firstname = findViewById(R.id.FirstName);
        lastname = findViewById(R.id.LastName);
        address = findViewById(R.id.Address);
        etfirstname = findViewById(R.id.editFirstName);
        etlastname = findViewById(R.id.editLastName);
        etaddress = findViewById(R.id.editAddress);
        submit = findViewById(R.id.submit);
        signOut = findViewById(R.id.signOut);
        Intent intent = getIntent();
        String usernameS = intent.getStringExtra(Login.KEY_NAME);
        String roleS = intent.getStringExtra(Login.KEY_ROLE);
        username.setText(firebaseUser.getEmail());
        role.setText("Manager");
        db.collection("Notebook").document(username.getText().toString()).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String FirstName = documentSnapshot.get("FirstName").toString();
                        String LastName = documentSnapshot.get("LastName").toString();
                        String Address = documentSnapshot.get("Address").toString();
                        firstname.setText(FirstName);
                        lastname.setText(LastName);
                        address.setText(Address);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManagerProfile.this, "Error!", Toast.LENGTH_SHORT).show();
                        //Log.d(TAG, e.toString());
                    }
                });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newFirstName = etfirstname.getText().toString();
                String newLastName = etlastname.getText().toString();
                String newAddress = etaddress.getText().toString();
                HashMap<String, Object> store = new HashMap<>();
                if (!newFirstName.isEmpty()){
                    store.put("FirstName", newFirstName);
                }
                if (!newLastName.isEmpty()){
                    store.put("LastName", newLastName);
                }
                if (!newAddress.isEmpty()){
                    store.put("Address", newAddress);
                }
                if (!store.isEmpty()){
                    db.collection("Notebook").document(username.getText().toString()).update(store);
                }

            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                Intent intent = new Intent(ManagerProfile.this, Login.class);
                startActivity(intent);
            }
        });
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.manager_profile, menu);
        return true;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_profile:
                break;
            case R.id.nav_locationAdd:
                Intent intent = new Intent(ManagerProfile.this, AddLocation.class);
                startActivity(intent);
                break;
            case R.id.nav_map:
                Intent intent2 = new Intent(ManagerProfile.this, MyMap.class);
                startActivity(intent2);
                break;
            case R.id.nav_employee:
                Intent intent3 = new Intent(ManagerProfile.this, EmployeeSetting.class);
                startActivity(intent3);
                break;
            case R.id.nav_vocation:
                Intent intent4 = new Intent(ManagerProfile.this, ManagerLeave.class);
                startActivity(intent4);
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                finish();
                Intent intent5 = new Intent(ManagerProfile.this, Login.class);
                startActivity(intent5);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}