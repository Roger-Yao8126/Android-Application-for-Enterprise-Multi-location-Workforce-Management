package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddLocation extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private EditText enterLocation, enterID;
    private Button submit;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, Object> note = new HashMap<>();
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String TAG = "AddLocationActivity";
    private ArrayList<Store> storeList = new ArrayList<>();
    private ListView locationList;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        enterLocation = findViewById(R.id.enterAddress);
        enterID = findViewById(R.id.enterID);
        locationList = findViewById(R.id.locationList);
        submit = findViewById(R.id.submit_location);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = enterLocation.getText().toString();
                String ID = enterID.getText().toString();
                if (location.length() > 1 && !location.toLowerCase().equals("google")){
                    GeoPoint geoPoint = getLocationFromAddress(location);
                    double lattitude = geoPoint.getLatitude();
                    double longitude = geoPoint.getLongitude();
                    note.put("id" , ID);
                    note.put("name", location);
                    note.put(LATITUDE, String.valueOf(lattitude));
                    note.put(LONGITUDE, String.valueOf(longitude));
                    note.put("employee", 0);
                    db.collection("Location").document(ID).set(note)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AddLocation.this, "Address saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddLocation.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                    db.collection("Location").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String data = "";
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                Store store = document.toObject(Store.class);
                                storeList.add(store);


                            }
                            //textViewData.setText(data);
                            StoreAdapter storeArrayAdaptor = new StoreAdapter(AddLocation.this, storeList);
                            locationList.setAdapter(storeArrayAdaptor);


                        }
                    });
                }else{
                    Toast.makeText(AddLocation.this, "Please input a valid address", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public GeoPoint getLocationFromAddress(String strAddress){
        if (strAddress.length() < 1){
            Toast.makeText(AddLocation.this, "Please input a longer address", Toast.LENGTH_SHORT).show();
            return null;
        }else{
            Geocoder coder = new Geocoder(this);
            List<Address> address;
            GeoPoint p1 = null;

            try {
                address = coder.getFromLocationName(strAddress,5);
                if (address==null) {
                    return null;
                }
                Address location=address.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new GeoPoint((double) (location.getLatitude()),
                        (double) (location.getLongitude()));

                return p1;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
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
                Intent intent = new Intent(AddLocation.this, ManagerProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_locationAdd:
                break;
            case R.id.nav_map:
                Intent intent2 = new Intent(AddLocation.this, MyMap.class);
                startActivity(intent2);
                break;
            case R.id.nav_employee:
                Intent intent3 = new Intent(AddLocation.this, EmployeeSetting.class);
                startActivity(intent3);
                break;
            case R.id.nav_vocation:
                Intent intent4 = new Intent(AddLocation.this, ManagerLeave.class);
                startActivity(intent4);
                break;
            case R.id.nav_signout:
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                finish();
                Intent intent5 = new Intent(AddLocation.this, Login.class);
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