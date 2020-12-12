package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeeDetail extends AppCompatActivity {
    private TextView username, role, workDays, workShifts;
    private EditText enterLocationId;
    private Button pickWorkdays, showLocations,saveInformation , goBack, pickShift;
    private ListView locationList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Store> storeList = new ArrayList<>();
    private static final String TAG = "EmployeeDetailActivity";
    private String prevWork, newWork, prevNum, newNum;
    private boolean changedLocation = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_detail);
        Intent intent = getIntent();
        String usernameS = intent.getStringExtra(EmployeeSetting.KEY_NAME);
        String roleS = intent.getStringExtra(EmployeeSetting.KEY_ROLE);
        prevWork = intent.getStringExtra(EmployeeSetting.KEY_LOCATION);
        username = findViewById(R.id.username);
        role = findViewById(R.id.role);
        workDays = findViewById(R.id.workDays);
        workShifts = findViewById(R.id.workShifts);
        enterLocationId = findViewById(R.id.enterLocationID);
        pickWorkdays = findViewById(R.id.pickWorkDays);
        showLocations = findViewById(R.id.showLocations);
        saveInformation = findViewById(R.id.saveInformation);
        goBack = findViewById(R.id.goBack);
        pickShift = findViewById(R.id.pickShift);
        locationList = findViewById(R.id.locationList);
        username.setText(usernameS);
        role.setText(roleS);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(EmployeeDetail.this, EmployeeSetting.class);
                startActivity(intent1);
            }
        });
        pickWorkdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDetail.this);
                final String[] dayArray = new String[]{"M", "T", "W", "H", "F", "S", "U"};
                final boolean[] checkedDayArray = new boolean[]{false, false,false,false,false,false,false};
                final List<String> dayList = Arrays.asList(dayArray);
                builder.setTitle("Select Workdays");
                builder.setIcon(R.drawable.ic_list);
                builder.setMultiChoiceItems(dayArray, checkedDayArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checkedDayArray[which] = isChecked;
                        String curItem = dayList.get(which);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workDays.setText("");
                        for (int i = 0; i < checkedDayArray.length; i ++){
                            boolean checked = checkedDayArray[i];
                            if (checked){
                                workDays.setText(workDays.getText() + dayList.get(i));
                            }
                        }
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        pickShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeDetail.this);
                final String[] shiftArray = new String[]{"Morning", "Afternoon"};
                final boolean[] checkedShiftArray = new boolean[]{false, false};
                final List<String> shiftList = Arrays.asList(shiftArray);
                builder.setTitle("Select Shift");
                builder.setIcon(R.drawable.ic_list);
                builder.setSingleChoiceItems(shiftArray, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkedShiftArray[which] = true;
                        String curItem = shiftList.get(which);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workShifts.setText("");
                        for (int i = 0; i < checkedShiftArray.length; i ++){
                            boolean checked = checkedShiftArray[i];
                            if (checked){
                                workShifts.setText(workShifts.getText() + shiftList.get(i));
                            }
                        }
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        showLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("Location").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        String data = "";
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            Store store = document.toObject(Store.class);
                            storeList.add(store);


                        }
                        //textViewData.setText(data);
                        StoreAdapter storeArrayAdaptor = new StoreAdapter(EmployeeDetail.this, storeList);
                        locationList.setAdapter(storeArrayAdaptor);



                    }
                });
            }
        });
        saveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameS = username.getText().toString().trim();
                String locationID = enterLocationId.getText().toString().trim();
                String workDayStr = workDays.getText().toString().trim();
                String shift = workShifts.getText().toString().trim();
                if (locationID.isEmpty()){
                    Toast.makeText(EmployeeDetail.this, "Location is required", Toast.LENGTH_SHORT).show();
                }if (workDayStr.isEmpty()){
                    Toast.makeText(EmployeeDetail.this, "Work Day is required", Toast.LENGTH_SHORT).show();
                }if (shift.isEmpty()){
                    Toast.makeText(EmployeeDetail.this, "Shift is required", Toast.LENGTH_SHORT).show();
                }else{
                    List<String> workDay = new ArrayList<>();
                    for(int i = 0; i < workDayStr.length(); i ++){
                        workDay.add(String.valueOf(workDayStr.charAt(i)));
                    }
                    db.collection("Notebook").document(usernameS).update("Location", locationID)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                    db.collection("Notebook").document(usernameS).update("Workday", workDay)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                    db.collection("Notebook").document(usernameS).update("Shift", shift)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
//                    String prevNewNum = String.valueOf(Integer.parseInt(prevNum) - 1);
//                    String newNewNum = String.valueOf(Integer.parseInt(newNum) + 1);
                    if (!locationID.equals(prevWork) && !changedLocation && !prevWork.equals("0")){
                        db.collection("Location").document(prevWork).update("employee", FieldValue.increment(-1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                        db.collection("Location").document(locationID).update("employee", FieldValue.increment(1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                        changedLocation = true;

                    }
                    else if (prevWork.equals("") || prevWork.equals("0")){
                        db.collection("Location").document(locationID).update("employee", FieldValue.increment(1))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EmployeeDetail.this, "Note saved", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EmployeeDetail.this, "Error!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, e.toString());
                                    }
                                });
                        changedLocation = true;
                    }
                    Intent intent1 = new Intent(EmployeeDetail.this, EmployeeSetting.class);
                    startActivity(intent1);
                }
            }
        });
    }



}