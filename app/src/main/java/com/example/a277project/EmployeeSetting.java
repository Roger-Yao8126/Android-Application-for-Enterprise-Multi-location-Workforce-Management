package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EmployeeSetting extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private Button showAll;
    private ListView listViewAll;
    private TextView textViewData;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    //private UserAdapter employeeArrayAdaptor;
    private ArrayList<User> employeeList = new ArrayList<>();
    public static final String KEY_NAME = "Username";
    public static final String KEY_ROLE = "Role";
    public static final String KEY_LOCATION = "Location";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_setting);
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
        showAll = findViewById(R.id.showAll);
        listViewAll = findViewById(R.id.listViewAll);
        //textViewData = findViewById(R.id.text_view_data);
        showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Notebook").whereEqualTo(KEY_ROLE, "Employee").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        employeeList.clear();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            String Username = document.getString("Username");
                            String Role = document.getString("Role");
                            String Shift = document.getString("Shift");
                            String Location = document.getString("Location");
                            ArrayList<String> WorkDayList = (ArrayList<String>) document.get("Workday");
                            String Workday = WorkDayList.toString();
                            User user = new User(Username, Role, Location, Shift, Workday);
                            //User user = document.toObject(User.class);
                            employeeList.add(user);
//                            user.setUsername(document.getId());
//                            user.setRole("Employee");
//                            String userName = user.getUsername();
//                            String role = user.getRole();
//                            data += "Username: " + userName
//                                    + "\nRole: " + role  + "\n\n";


                        }
                        //textViewData.setText(data);
                        UserAdapter employeeArrayAdaptor = new UserAdapter(EmployeeSetting.this, employeeList);
                        listViewAll.setAdapter(employeeArrayAdaptor);



                    }
                });
            }
        });

        listViewAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) listViewAll.getItemAtPosition(position);
                String userName = user.getusername();
                String role = user.getrole();
                String prevWork = user.getLocation();
                Intent intent = new Intent(EmployeeSetting.this, EmployeeDetail.class);
                intent.putExtra(KEY_NAME, userName);
                intent.putExtra(KEY_ROLE, role);
                intent.putExtra(KEY_LOCATION, prevWork);
                startActivity(intent);

            }

        });

//        textViewData.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
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
                Intent intent = new Intent(EmployeeSetting.this, ManagerProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_locationAdd:
                Intent intent2 = new Intent(EmployeeSetting.this, AddLocation.class);
                startActivity(intent2);
                break;
            case R.id.nav_map:
                Intent intent3 = new Intent(EmployeeSetting.this, MyMap.class);
                startActivity(intent3);
                break;
            case R.id.nav_employee:
                break;
            case R.id.nav_vocation:
                Intent intent4 = new Intent(EmployeeSetting.this, ManagerLeave.class);
                startActivity(intent4);
                break;
            case R.id.nav_signout:
                mAuth.signOut();
                finish();
                Intent intent5 = new Intent(EmployeeSetting.this, Login.class);
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