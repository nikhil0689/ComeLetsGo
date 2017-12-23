package com.nikhil.sdsu.comeletsgo;

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

public class SignUpActivity extends AppCompatActivity {
    private EditText name;
    private EditText contact;
    private EditText emailId;
    private EditText password;
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
                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("rew", "User profile updated.");
                                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                finish();
                                                            }
                                                        }
                                                    });
                                        } catch (NullPointerException e) {
                                            Log.d("rew", "failed");
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
        return dataValid;
    }

}
