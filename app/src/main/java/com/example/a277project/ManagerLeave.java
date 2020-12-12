package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagerLeave extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private ListView lv_leave;
    ArrayAdapter<String> adapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String TAG = "Debug";
    List<Leave> leaveList;
//    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_leave);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        view = findViewById(R.id.view_main);

        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        lv_leave = (ListView) findViewById(R.id.leave_list);

        leaveList = new ArrayList<>();


        db.collection("Leave").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful())  {
                            QuerySnapshot querySnapshot = task.getResult();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Log.d(TAG, "doc ID is " + document.getId());
                                Leave leave = document.toObject(Leave.class);
                                leave.setDocID(document.getId());
                                leaveList.add(leave);
                            }
                        }

                        LeaveAdapter adapter = new LeaveAdapter(ManagerLeave.this, leaveList);

                        lv_leave.setAdapter(adapter);
                    }
                });

        lv_leave.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Leave leave = (Leave) lv_leave.getItemAtPosition(position);
                final String documentId = leave.docID;



//                Snackbar snackbar = Snackbar
//                        .make(view, "Internet Connection Error", Snackbar.LENGTH_LONG);
//                snackbar.show();

                Snackbar snackbar = Snackbar
                        .make(view, "Approve the leave?", Snackbar.LENGTH_LONG)
                        .setAction("YES", new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                db.collection("Leave").document(documentId)
                                        .update("Approved", true)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                finish();
                                                startActivity(getIntent());
                                                Snackbar mSnackbar = Snackbar.make(view, "Message successfully deleted.", Snackbar.LENGTH_SHORT);
                                                mSnackbar.show();
                                            }
                                        });
                            }
                        });

                snackbar.show();

            }
        });

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
                Intent intent = new Intent(ManagerLeave.this, ManagerProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_locationAdd:
                Intent intent2 = new Intent(ManagerLeave.this, AddLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_map:
                Intent intent3 = new Intent(ManagerLeave.this, MyMap.class);
                startActivity(intent3);
                break;
            case R.id.nav_employee:
                Intent intent4 = new Intent(ManagerLeave.this, EmployeeSetting.class);
                startActivity(intent4);
                break;
            case R.id.nav_vocation:
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                finish();
                Intent intent5 = new Intent(ManagerLeave.this, Login.class);
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

    private interface FirestoreCallback {
        //        void onCallback(List<String> list);
        void onCallback(HashMap<String, String> user);
    }

    private void getLeave(final FirestoreCallback firestoreCallback) {
        db.collection("Leave").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful())  {
                    QuerySnapshot querySnapshot = task.getResult();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        Leave leave = document.toObject(Leave.class);
//                        Log.d(TAG, "leave name is " + leave.Username);
                    }
                }
            }
        });
    }

}