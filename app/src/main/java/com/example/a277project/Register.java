package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity {
    private EditText username, password, confirmPassword, firstName, lastName, address;
    private Button manager, employee, register, gotoLogin;
    private TextView role;
    private FirebaseAuth mAuth;
    private static final String TAG = "MainActivity";
    private List<String> store = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String KEY_NAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_ROLE = "Role";
    private Map<String, Object> note = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        username = findViewById(R.id.enterName);
        password = findViewById(R.id.enterPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        address = findViewById(R.id.address);
        manager = findViewById(R.id.manager);
        employee = findViewById(R.id.employee);
        register = findViewById(R.id.register);
        gotoLogin = findViewById(R.id.gotoLogin);
        role = findViewById(R.id.showRole);
        mAuth = FirebaseAuth.getInstance();

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role.setText("Manager");
            }
        });
        employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                role.setText("Employee");
            }
        });
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                String usernameS = username.getText().toString().trim();
                String confirmPasswordS = confirmPassword.getText().toString().trim();
                String passwordS = password.getText().toString().trim();
                String firstNameS = firstName.getText().toString().trim();
                String lastNameS = lastName.getText().toString().trim();
                String addressS = address.getText().toString().trim();
                String roleS = role.getText().toString();
                if (usernameS.isEmpty()){
                    Toast.makeText(Register.this, "Username is Required", Toast.LENGTH_SHORT).show();
                }
                if (passwordS.isEmpty()){
                    Toast.makeText(Register.this, "Password is Required", Toast.LENGTH_SHORT).show();
                }
                if (!confirmPasswordS.equals(passwordS)){
                    Toast.makeText(Register.this, "Password needs to match", Toast.LENGTH_SHORT).show();
                }
                if (passwordS.length() < 6){

                    Toast.makeText(Register.this, "Password too short", Toast.LENGTH_SHORT).show();
                }
                if (roleS.isEmpty()){
                    Toast.makeText(Register.this, "Please pick your role", Toast.LENGTH_SHORT).show();
                }
                if (firstNameS.isEmpty()){
                    Toast.makeText(Register.this, "Please enter your first name", Toast.LENGTH_SHORT).show();
                }
                if (lastNameS.isEmpty()){
                    Toast.makeText(Register.this, "Please enter your last name", Toast.LENGTH_SHORT).show();
                }
                if (addressS.isEmpty()){
                    Toast.makeText(Register.this, "Please enter your address", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (mAuth.getCurrentUser() != null){
                        Intent intent = new Intent(Register.this, Login.class);
                        startActivity(intent);
                    }
                    mAuth.createUserWithEmailAndPassword(usernameS, passwordS)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                    } else {

                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });
                    byte[] hash;
                    MessageDigest digest;
                    //Map<String, String> note = new HashMap<>();
                    note.put(KEY_NAME, usernameS);
                    try{
                        digest = MessageDigest.getInstance("SHA-256");
                        hash = digest.digest(passwordS.getBytes(StandardCharsets.UTF_8));
                        note.put(KEY_PASSWORD, Arrays.toString(hash));
                    }catch (NoSuchAlgorithmException e){
                        Toast.makeText(Register.this, "Failed to Hash Password", Toast.LENGTH_SHORT).show();
                    }
                    note.put(KEY_ROLE, roleS);
                    note.put("FirstName", firstNameS);
                    note.put("LastName", lastNameS);
                    note.put("Address", addressS);
                    if (roleS.equals("Employee")){
                        note.put("Location", "0");
                        note.put("Shift", "");
                        note.put("Workday", new ArrayList<String>());
                        note.put("checkinState", "offduty");
                    }

                    db.collection("Notebook").document(usernameS).set(note)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Note saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Register.this, "Error!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, e.toString());
                                }
                            });
                }

            }
        });

    }
    private String getPassword(){
        if (password.getText().toString().equals(confirmPassword.getText().toString()) && password.getText().toString().length() > 0) {
            return password.getText().toString();
        }
        return null;
    }
}