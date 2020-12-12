package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeEdit extends AppCompatActivity {


    private Context context;
    private String TAG = "Debug";
    EditText et_firstname, et_lastname, et_homeaddress ;
    TextView tv_email, tv_firstname, tv_lastname, tv_homeaddress;
    private Button btn_update;

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
        setContentView(R.layout.activity_employee_edit);


        workdayMap = new HashMap<>();

        workdayMap.put("M", "Mon");
        workdayMap.put("T", "Tue");
        workdayMap.put("W", "Wed");
        workdayMap.put("H", "Thu");
        workdayMap.put("F", "Fri");
        workdayMap.put("S", "Sat");
        workdayMap.put("U", "Sun");

        et_firstname = (EditText) findViewById(R.id.firstname);
        et_lastname = (EditText) findViewById(R.id.lastname);
        et_homeaddress = (EditText) findViewById(R.id.homeaddress);

        btn_update = findViewById(R.id.update);

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
                Log.d(TAG, "update clicked");
                String firstName = et_firstname.getText().toString();
                String lastName = et_lastname.getText().toString();
                String homeaddress = et_homeaddress.getText().toString();

                DocumentReference doc = db.collection("Notebook").document(userId);
                if(firstName.trim().length() > 0 )
                    doc.update("FirstName", firstName).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeEdit.this, "First Name updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeEdit.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });

                if(lastName.trim().length() > 0 )
                    doc.update("LastName", lastName).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeEdit.this, "Last Name Updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeEdit.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });

                if(homeaddress.trim().length() > 0 )
                    doc.update("Address", homeaddress).addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeEdit.this, "Home Address Updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeEdit.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
            }
        });

        readUserLocation(new FirestoreCallback() {
                             @Override
                             public void onCallback(final HashMap user) {
                                 et_firstname.setHint((String) user.get("firstName"));
                                 et_lastname.setHint((String) user.get("lastName"));
                                 et_homeaddress.setHint((String) user.get("homeaddress"));
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
                    String firstName = document.getString("firstName");
                    String lastName = document.getString("lastName");
                    String homeAddress = document.getString("homeaddress");
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
            Intent intent = new Intent(EmployeeEdit.this, Login.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}