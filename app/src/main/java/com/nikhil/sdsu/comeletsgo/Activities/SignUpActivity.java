package com.nikhil.sdsu.comeletsgo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.nikhil.sdsu.comeletsgo.Helpers.DatabaseHelper;
import com.nikhil.sdsu.comeletsgo.R;
import com.nikhil.sdsu.comeletsgo.Pojo.SignUpDetailsPOJO;

public class SignUpActivity extends AppCompatActivity {
    private EditText name,contact,emailId,password,carName,carColor,carLisence,homeAddress;
    private Button submit,reset;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        name = findViewById(R.id.sign_up_name);
        contact = findViewById(R.id.sign_up_contact);
        emailId = findViewById(R.id.sign_up_email);
        password = findViewById(R.id.signup_password);
        carName = findViewById(R.id.signup_car_name);
        carColor = findViewById(R.id.signup_car_color);
        carLisence = findViewById(R.id.signup_car_number);
        homeAddress = findViewById(R.id.signup_address);
        submit = findViewById(R.id.sign_up_submit);
        reset = findViewById(R.id.sign_up_reset_button);
        auth = FirebaseAuth.getInstance();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String dispName = name.getText().toString().trim();
                String phno = contact.getText().toString().trim();
                final String displayName = phno.concat("-").concat(dispName);
                if(validInput()){
                    final SignUpDetailsPOJO signUpDetailsPOJO = new SignUpDetailsPOJO();
                    signUpDetailsPOJO.setName(dispName);
                    signUpDetailsPOJO.setCarName(carName.getText().toString().trim());
                    signUpDetailsPOJO.setCarColor(carColor.getText().toString().trim());
                    signUpDetailsPOJO.setCarLicence(carLisence.getText().toString().trim());
                    signUpDetailsPOJO.setEmailId(email);
                    signUpDetailsPOJO.setContact(phno);
                    signUpDetailsPOJO.setAddress(homeAddress.getText().toString().trim());
                    Log.d("rew","user data: "+email+" "+pass+" "+displayName);
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d("rew","Exception: "+task.getException());
                                        Toast.makeText(SignUpActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                                        emailId.setError("Invalid Email ID");
                                    } else {
                                        FirebaseUser user = auth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(displayName)
                                                .build();
                                        try {
                                            DatabaseHelper databaseHelper = new DatabaseHelper(SignUpActivity.this);
                                            boolean dbUpdate = databaseHelper.insertUser(signUpDetailsPOJO);
                                            if(dbUpdate){
                                                user.updateProfile(profileUpdates)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d("rew", "User profile updated.");
                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                    finish();
                                                                }else{
                                                                    Log.d("rew","user profile update failed");
                                                                }
                                                            }
                                                        });
                                            }else{
                                                Log.d("rew","db update failed");
                                            }
                                        } catch (Exception e) {
                                            Log.d("rew", "failed: "+e);
                                        }
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), "Enter data",Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId.setText("");
                password.setText("");
                name.setText("");
                contact.setText("");
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError("Enter Email ID");
            dataValid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Enter Password");
            dataValid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError("Invalid Password");
            Toast.makeText(getApplicationContext(), "Min 6 Characters",
                    Toast.LENGTH_SHORT).show();
            dataValid = false;
        }

        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("Enter name");
            dataValid = false;
        }
        if(name.getText().toString().matches(".*\\d+.*")){
            name.setError("No Numbers please");
            dataValid = false;
        }
        if(TextUtils.isEmpty(contact.getText().toString())){
            contact.setError("Enter Phone No.");
            dataValid = false;
        }
        if(TextUtils.isEmpty(carName.getText().toString())){
            contact.setError("Enter Car Name");
            dataValid = false;
        }
        if(TextUtils.isEmpty(carLisence.getText().toString())){
            contact.setError("Enter Phone No.");
            dataValid = false;
        }
        if(TextUtils.isEmpty(carColor.getText().toString())){
            contact.setError("Enter Phone No.");
            dataValid = false;
        }
        if(TextUtils.isEmpty(homeAddress.getText().toString())){
            contact.setError("Enter Phone No.");
            dataValid = false;
        }

        return dataValid;
    }

}
