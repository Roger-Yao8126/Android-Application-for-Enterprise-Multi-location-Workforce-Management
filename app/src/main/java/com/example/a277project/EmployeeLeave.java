package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EmployeeLeave extends AppCompatActivity {

    DatePickerDialog picker;
    EditText et_from, et_to, et_reason;
    TextView tv_interval, tv_leaveState;
    String dateFrom;
    String dateTo;
    Button btnApply, btnDelete;
    String leaveDocId;
    String TAG = "Debug";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Map<String, Object> leavedoc = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_leave);
        et_from=(EditText) findViewById(R.id.et_from);
        et_from.setInputType(InputType.TYPE_NULL);
        et_to=(EditText) findViewById(R.id.et_to);
        et_to.setInputType(InputType.TYPE_NULL);
        et_reason = findViewById(R.id.et_reason);
        et_reason.setInputType(InputType.TYPE_NULL);

        tv_interval = findViewById(R.id.interval);
        tv_leaveState = findViewById(R.id.leaveState);

        btnApply=(Button)findViewById(R.id.buttonapply);
        btnDelete = findViewById(R.id.buttondelete);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final String userId = firebaseUser.getEmail();



        db.collection("Leave").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, "db get work");
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if (document.getString("Username").equals(userId)){
                        String startDate = document.getString("dateFrom");
                        String endDate = document.getString("dateTo");
                        Boolean approved = document.getBoolean("Approved");
                        String reason = document.getString("Reason");
                        leaveDocId = document.getId();
                        tv_interval.setText(startDate + "-" + endDate);
                        if (approved) {
                            tv_leaveState.setText("Approved");
                        } else {
                            tv_leaveState.setText("Pending");
                        }
                        break;
                    }
                }
            }
        });


        et_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EmployeeLeave.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                                dateFrom = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dateFrom = (monthOfYear + 1)  + "/" + dayOfMonth   + "/" + year;
                                et_from.setText(dateFrom);
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        et_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(EmployeeLeave.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                dateTo = (monthOfYear + 1)  + "/" + dayOfMonth   + "/" + year;
                                et_to.setText(dateTo);
                            }
                        }, year, month, day);
                picker.show();
            }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.collection("Leave").document(leaveDocId).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Toast.makeText(EmployeeLeave.this, "Leave record deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EmployeeLeave.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "from date is " + dateFrom);
                Log.d(TAG, "To date is " + dateTo);
                leavedoc.put("Username", userId);
                leavedoc.put("dateFrom",dateFrom);
                leavedoc.put("dateTo", dateTo);
                leavedoc.put("reason", et_reason.getText().toString());
                leavedoc.put("Approved", false);
                db.collection("Leave").add(leavedoc)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(EmployeeLeave.this, "Leave applied", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EmployeeLeave.this, "Error!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, e.toString());
                            }
                        });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.employee_leave,menu);
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
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}