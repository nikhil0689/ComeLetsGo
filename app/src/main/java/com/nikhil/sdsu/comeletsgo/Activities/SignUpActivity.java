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
import com.nikhil.sdsu.comeletsgo.Helpers.ComeLetsGoConstants;
import com.nikhil.sdsu.comeletsgo.R;

public class SignUpActivity extends AppCompatActivity implements ComeLetsGoConstants {
    private EditText contact,emailId,password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button submit,reset;
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
                final String phno = contact.getText().toString().trim();
                if(validInput()){
                    Log.d("rew","user data: "+email+" "+pass+" "+phno);
                    auth.createUserWithEmailAndPassword(email,pass)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.d("rew","Exception: "+task.getException());
                                        Toast.makeText(SignUpActivity.this, ComeLetsGoConstants.REGISTRATION_FAILED, Toast.LENGTH_SHORT).show();
                                        emailId.setError(ComeLetsGoConstants.INVALID_EMAIL);
                                    } else {
                                        FirebaseUser user = auth.getCurrentUser();

                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(phno)
                                                .build();
                                        try {
                                            if(user != null){
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
                                            }

                                        } catch (Exception e) {
                                            Log.d("rew", "failed: "+e);
                                        }
                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(), ComeLetsGoConstants.ENTER_REQUIRED_FIELDS,Toast.LENGTH_SHORT).show();
                }
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailId.setText(ComeLetsGoConstants.EMPTY_STRING);
                password.setText(ComeLetsGoConstants.EMPTY_STRING);
                contact.setText(ComeLetsGoConstants.EMPTY_STRING);
            }
        });
    }

    private boolean validInput() {
        boolean dataValid = true;
        if (TextUtils.isEmpty(emailId.getText().toString())) {
            emailId.setError(ComeLetsGoConstants.ENTER_EMAIL);
            dataValid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(ComeLetsGoConstants.ENTER_PASSWORD);
            dataValid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(ComeLetsGoConstants.PASSWORD_LENGTH);
            Toast.makeText(getApplicationContext(), ComeLetsGoConstants.PASSWORD_LENGTH,
                    Toast.LENGTH_SHORT).show();
            dataValid = false;
        }
        return dataValid;
    }

}
