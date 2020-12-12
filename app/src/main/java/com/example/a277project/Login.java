package com.example.a277project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.google.api.Authentication;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Login extends AppCompatActivity{
    private EditText enterName;
    private EditText enterPassword;
    private TextView showName, showPassword;
    private Button logIn;
    private Button register;
//    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String KEY_NAME = "Username";
    private static final String KEY_PASSWORD = "Password";
    public static final String KEY_ROLE = "Role";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        enterName = findViewById(R.id.EnterUserName);
        enterPassword = findViewById(R.id.EnterPassword);
        logIn = findViewById(R.id.LogIn);
        register = findViewById(R.id.Register);
        showName = findViewById(R.id.showUserName);
        showPassword = findViewById(R.id.showPassword);
//        mAuth = FirebaseAuth.getInstance();
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameS = enterName.getText().toString();
                //String confirmPasswordS = confirmPassword.getText().toString().trim();
                String passwordS = enterPassword.getText().toString();
                //String roleS = role.getText().toString();
                if (usernameS.isEmpty()){
                    Toast.makeText(Login.this, "Username is Required", Toast.LENGTH_SHORT).show();
                }
                if (passwordS.isEmpty()){
                    Toast.makeText(Login.this, "Password is Required", Toast.LENGTH_SHORT).show();
                }
//                if (!confirmPasswordS.equals(passwordS)){
//                    Toast.makeText(Login.this, "Password needs to match", Toast.LENGTH_SHORT).show();
//                }
//                if (passwordS.length() < 6){
//
//                    Toast.makeText(Register.this, "Password too short", Toast.LENGTH_SHORT).show();
//                }
//                if (roleS.isEmpty()){
//                    Toast.makeText(Register.this, "Please pick your role", Toast.LENGTH_SHORT).show();
//                }
                else{
//                    database.firbaseLogin(mAuth, usernameS, passwordS);
                    FirebaseAuth mAuth;
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(usernameS, passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Login.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                                db.collection("Notebook").document(usernameS).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            String userName = documentSnapshot.getString(KEY_NAME);
                                            String role = documentSnapshot.getString(KEY_ROLE);
                                            if (role.equals("Manager")){
                                                Intent intent = new Intent(Login.this, ManagerProfile.class);
                                                intent.putExtra(KEY_NAME, userName);
                                                intent.putExtra(KEY_ROLE, role);
                                                startActivity(intent);
                                            } else if (role.equals("Employee")) {
                                                Intent intent = new Intent(Login.this, EmployeeCheckin.class);
                                                intent.putExtra(KEY_NAME, userName);
                                                intent.putExtra(KEY_ROLE, role);
                                                startActivity(intent);
                                            }

                                        } else {
                                            Toast.makeText(Login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(Login.this, "Log in failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

            }
        });
    }



}