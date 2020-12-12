package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmployeeProfile extends AppCompatActivity {

    Context context;
    String TAG = "Debug";
    TextView tv_email, tv_firstname, tv_lastname, tv_homeaddress, tv_location, tv_workday, tv_shift;
    Button  btn_update;

    String location ;
    String shift;

    HashMap<String, String> workdayMap;

    private ArrayList<String> locationList;
    private ArrayList<String> userList;
//    private String userLoction;

    // firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);


        workdayMap = new HashMap<>();

        workdayMap.put("M", "Mon");
        workdayMap.put("T", "Tue");
        workdayMap.put("W", "Wed");
        workdayMap.put("H", "Thu");
        workdayMap.put("F", "Fri");
        workdayMap.put("S", "Sat");
        workdayMap.put("U", "Sun");

        tv_email = (TextView) findViewById(R.id.emailShow);
        tv_firstname = (TextView) findViewById(R.id.firstnameShow) ;
        tv_lastname = (TextView) findViewById(R.id.lastnameShow);
        tv_homeaddress = (TextView) findViewById(R.id.homeaddressShow);
        tv_location = (TextView) findViewById(R.id.location);
        tv_workday =(TextView) findViewById(R.id.workdays);
        tv_shift  = (TextView) findViewById(R.id.shift);


        btn_update = findViewById(R.id.editbutton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String userId = firebaseUser.getEmail();

        locationList = new ArrayList<>();
        userList = new ArrayList<>();
//        userLoction = "";

        Log.d(TAG, "firebaseUser email is " + userId);

        btn_update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmployeeProfile.this, EmployeeEdit.class);
                startActivity(intent);
            }
        });

        readUserLocation(new FirestoreCallback() {
            @Override
            public void onCallback(final HashMap user) {
                String locationId =  (String) user.get("locationId");
                if( locationId.length() > 0){
                    readLocation(new FirestoreCallbackString() {
                                     @Override
                                     public void onCallback(String location) {

                                         tv_email.setText((String) user.get("accountName"));
                                         tv_firstname.setText((String) user.get("firstName"));
                                         tv_lastname.setText((String) user.get("lastName"));
                                         tv_homeaddress.setText((String) user.get("homeaddress"));
                                         tv_location.setText(location);
                                         tv_workday.setText((String) user.get("workdays"));
                                         tv_shift.setText((String) user.get("shift"));
                                     }
                                 }
                            , locationId);
                } else {

                    tv_email.setText((String) user.get("accountName"));
                    tv_firstname.setText((String) user.get("firstName"));
                    tv_lastname.setText((String) user.get("lastName"));
                    tv_homeaddress.setText((String) user.get("homeaddress"));
                    tv_location.setText("Unassigned");
                    tv_workday.setText((String) user.get("workdays"));
                    tv_shift.setText((String) user.get("shift"));
                }

            }
        }
        , userId);
    }

    private interface FirestoreCallback {
//        void onCallback(List<String> list);
       void onCallback(HashMap<String, String> user);
    }

    private interface FirestoreCallbackString {
        void onCallback(String location);
//        void onCallback(User user);
    }

    private void readLocation(final FirestoreCallbackString firestoreCallback, String locationId) {
        db.collection("Location")
                .document(locationId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            location = document.getString("name");
                            firestoreCallback.onCallback(location);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    private void readUserLocation(final FirestoreCallback firestoreCallback, final String userName) {

        db.collection("Notebook").document(userName)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    String userLoction = document.getString("Location");
                    String shift = document.getString("Shift");
                    String firstName = document.getString("FirstName");
                    String lastName = document.getString("LastName");
                    String homeAddress = document.getString("Address");
                    ArrayList<String> workdays = (ArrayList<String>) document.get("Workday");
                    StringBuilder wd = new StringBuilder();
                    for(int i =0; i < workdays.size(); i++) {
                        if (i == 0) {
                            wd.append(workdayMap.get(workdays.get(i)));
                        } else {
                            wd.append(" " + workdayMap.get(workdays.get(i)));
                        }
                    }

                    Log.d(TAG, "Workdays is :" +  wd.toString());
                    HashMap<String, String> newUser = new HashMap<>();
                    newUser.put("accountName", userName) ;
                    newUser.put("locationId", userLoction);
                    newUser.put("shift", shift);
                    newUser.put("firstName", firstName);
                    newUser.put("lastName", lastName);
                    newUser.put("homeaddress", homeAddress);
                    newUser.put("workdays", wd.toString());

                    firestoreCallback.onCallback(newUser);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.employee_profile,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            mAuth.signOut();
            Intent intent = new Intent(EmployeeProfile.this, Login.class);
            startActivity(intent);

            return true;
        } else if (id == R.id.askforleave) {
            Intent intent = new Intent(EmployeeProfile.this, EmployeeLeave.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}