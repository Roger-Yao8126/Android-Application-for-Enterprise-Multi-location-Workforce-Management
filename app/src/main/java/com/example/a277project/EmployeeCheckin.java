package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

// The workState is used to get the checkinState in firebase ,  has "off-duty" , "working"
// when  checking time ,  read in the firebase value, if assign the firebase value to  workState ,
//      then check whether time match. if matched, assign time match to  true
// When Click "checkin "  1 . check workState whether is offduty  2. check whether time match and location match

public class EmployeeCheckin extends AppCompatActivity {

    String TAG = "Debug";
    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean locationMatch = false;
    private boolean timeMatch = false;
    private String workState = "";
    ArrayList<Store> stores;

    private HashMap<String,String [] > shiftMap;

    String pattern = "HH:mm";
    SimpleDateFormat df = new SimpleDateFormat(pattern);

    private Button btnPrepare;
    private Button btnCheckin;
    private Button btnCheckout;
    private LatLng currentlatlng;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_checkin);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String userId = firebaseUser.getEmail();

        final char[] dayArray = new char[]{'U', 'M', 'T', 'W', 'H', 'F', 'S'};

        Log.d(TAG, "firebaseUser email is " + userId);

        shiftMap = new HashMap<>();

        btnPrepare = findViewById(R.id.buttonprepare);
        btnCheckin = findViewById(R.id.buttonCheckin);
        btnCheckout = findViewById(R.id.buttonCheckout);

        shiftMap.put("Morning", new String[]{"06:05", "13:55"});
        shiftMap.put("Afternoon", new String[]{"14:05", "21:55"});

        Log.d(TAG, "username is " + userId);

        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

        // Initialize fused Location
        client = LocationServices.getFusedLocationProviderClient(this);

        // check the current state
        btnPrepare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserLocation(new FirestoreCallback() {
                     @Override
                     public void onCallback(final HashMap user) {
                        Log.d(TAG, "account Name is " + (String) user.get("accountName"));
                         // check location
                         getLocation(new FirestoreCallback() {
                              @Override
                              public void onCallback(HashMap<String,String> location) {
                                  Log.d(TAG, "Location name is " + location.get("name"));
                                  Log.d(TAG, "Latitude is " + location.get("latitude"));
                                  Log.d(TAG, "Longitutde is " + location.get("longitude"));
                                  if (currentlatlng == null) {
                                    Log.d(TAG, "button clicked, currentlatlng is empty");
                                  }
                                  if(Math.abs(currentlatlng.latitude - Float.parseFloat(location.get("latitude")))
                                    < 0.001 && Math.abs(currentlatlng.longitude - Float.parseFloat(location.get("longitude")))
                                    < 0.001) {
                                        locationMatch = true;
                                        Toast.makeText(getApplicationContext(),"Location matched."
                                                ,Toast.LENGTH_SHORT).show();

                                    } else {
                                      Log.d(TAG, "Location doesn't match");
                                      Toast.makeText(getApplicationContext(),"You are not at the work place."
                                              ,Toast.LENGTH_SHORT).show();
                                  }

                                  }
                              }
                              , (String) user.get("locationId"));

                         // check working state (time , shift , checkin state)
                         Date currentTime = Calendar.getInstance().getTime();
                         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                         String formattedDate = sdf.format(currentTime);
                         Calendar c = Calendar.getInstance();
                         c.setTime(currentTime);
                         final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
                         Log.d(TAG, "Today is " + dayOfWeek);
                         final String currTime = formattedDate.split(" ")[1];
                         Log.d(TAG, "currTime is " + currTime);
                         workState = (String) user.get("checkinState");
                         String shift = (String) user.get("shift");
                         String wkdays = (String) user.get("workdays");

                         if (wkdays.indexOf(dayArray[dayOfWeek - 1]) > -1) {
                             if (workState.equals("offduty"))  {
                                 if (currTime.compareTo(shiftMap.get(shift)[0]) < 0 )  {
                                     timeMatch = true;
                                     Log.d(TAG, "Time matched");
                                     Toast.makeText(getApplicationContext(),"Time Matched",
                                             Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(getApplicationContext(),"You are late. Unable to check in",
                                             Toast.LENGTH_SHORT).show();
                                 }

                             } else if (workState.equals("working")) {

                                 if (currTime.compareTo(shiftMap.get(shift)[1]) > 0) {
                                     timeMatch = true;
                                     Toast.makeText(getApplicationContext(), "Time Matched",
                                             Toast.LENGTH_SHORT).show();
                                 } else {
                                     Toast.makeText(getApplicationContext(),"You are on duty. It is too early to check out",
                                             Toast.LENGTH_SHORT).show();
                                 }
                             }
                         } else {
                             Toast.makeText(getApplicationContext(),"Today is not your work day",
                                     Toast.LENGTH_SHORT).show();
                         }

                     }
                 }
                 , userId);

                // display ready to check in / out
                if(timeMatch && locationMatch)  {
                    Toast.makeText(getApplicationContext(), "Ready for Check in/Out",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"workState is " + workState + ". locationMatch is " + locationMatch + " timeMatch is " + timeMatch );
                if(workState.equals("offduty") && locationMatch && timeMatch) {
                    locationMatch = false;
                    timeMatch = false;

                    db.collection("Notebook").document(userId)
                            .update("checkinState","working")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeCheckin.this, "Checked In", Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                } else {
                    Log.d(TAG, "Unable to check in, confirm your current state");
                }
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (workState.equals("working") && locationMatch && timeMatch) {
                    locationMatch = false;
                    timeMatch = false;
                    db.collection("Notebook").document(userId)
                            .update("checkinState", "offduty")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeCheckin.this, "Checked Out", Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                } else {
                    Toast.makeText(EmployeeCheckin.this, "Unable to check out , confirm your current state",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Unable to check out , confirm your current state");
                }

            }
        });

        // Check permission
        if (ActivityCompat.checkSelfPermission(EmployeeCheckin.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // When permission granted
            // call method
            getCurrentLocation();

        } else {
            // When permission denied
            // Request permission
            ActivityCompat.requestPermissions(EmployeeCheckin.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

    }

    private void getCurrentLocation() {
        // Initialize task location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            Log.d( "getCurrentLocation: ", "not");
            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                // when success
                if (location != null) {
                    // Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback(){

                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // Initialize lat lng
                            LatLng latLng = new LatLng(location.getLatitude()
                                    ,location.getLongitude());
                            // Create marker options
                            currentlatlng = latLng;
                            MarkerOptions options = new MarkerOptions().position(latLng)
                                    .title("I am there");
                            // Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                            // Add marker on map
                            googleMap.addMarker(options);

                        }
                    });

                } else {
                    Log.d("TAG", "Unable to get location");
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // When permission granted
                // call method
                getCurrentLocation();

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.empolyee_checkin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.toolbar_person) {
            Intent intent = new Intent(this,EmployeeProfile.class);
            startActivity(intent);
            return true;
        }  else if (id == R.id.Logout) {
            mAuth.signOut();
            finish();
            Intent intent = new Intent(EmployeeCheckin.this, Login.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.askforleave) {
            Intent intent = new Intent(EmployeeCheckin.this, EmployeeLeave.class);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private interface FirestoreCallback {
        //        void onCallback(List<String> list);
        void onCallback(HashMap<String, String> user);
    }

    private interface FirestoreCallbackString {
        void onCallback(String location);
//        void onCallback(User user);
    }

    private void getLocation(final FirestoreCallback firestoreCallback, String locationId) {
        if (locationId.length() == 0) {
            Toast.makeText(EmployeeCheckin.this, "Haven't assigned work location yet",
                    Toast.LENGTH_LONG).show();
            return;
        }
        db.collection("Location")
                .document(locationId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            HashMap<String, String> location = new HashMap<>();
                            if (document.exists()) {

                                location.put("name", document.getString("name"));
                                location.put("latitude", document.getString("latitude"));
                                location.put("longitude", document.getString("longitude"));
                            } else {
                                Log.d(TAG, "No such document");
                            }

                            firestoreCallback.onCallback(location);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void getUserLocation(final FirestoreCallback firestoreCallback, final String userName) {
        db.collection("Notebook").document(userName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    String userLoction = document.getString("Location");
                    String shift = document.getString("Shift");
                    String firstName = document.getString("" + "FirstName");
                    String lastName = document.getString("LastName");
                    String homeAddress = document.getString("Address");
                    String checkinState = document.getString("checkinState");
                    ArrayList<String> workdays = (ArrayList<String>) document.get("Workday");

                    Log.d(TAG, "Workdays is :" +  workdays.toString());
                    HashMap<String, String> newUser = new HashMap<>();
                    newUser.put("accountName", userName) ;
                    newUser.put("locationId", userLoction);
                    newUser.put("shift", shift);
                    newUser.put("workdays", workdays.toString());
                    newUser.put("checkinState", checkinState);
                    firestoreCallback.onCallback(newUser);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }

}