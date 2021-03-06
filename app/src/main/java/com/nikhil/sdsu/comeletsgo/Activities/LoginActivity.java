package com.nikhil.sdsu.comeletsgo.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.nikhil.sdsu.comeletsgo.Helpers.ComeLetsGoConstants;
import com.nikhil.sdsu.comeletsgo.R;

public class LoginActivity extends AppCompatActivity implements ComeLetsGoConstants {
    private EditText emailId;
    private EditText password;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button signIn;
        Button signUp;
        emailId =  findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        signIn = findViewById(R.id.login_submit);
        signUp = findViewById(R.id.login_sign_up);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpActivity = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpActivity);
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validInput()){
                    authenticateUser(emailId.getText().toString().trim(),password.getText().toString().trim());
                }
            }

        });
    }

    private void authenticateUser(String email, String pass) {
        Log.d("","Authenticating user");
        firebaseAuth.signInWithEmailAndPassword(email,pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, ComeLetsGoConstants.INVALID_EMAIL_PW_TOAST, Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private boolean validInput() {
        boolean valid = true;
        if(TextUtils.isEmpty(emailId.getText().toString())){
            emailId.setError(ComeLetsGoConstants.ENTER_EMAIL);
            valid = false;
        }
        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError(ComeLetsGoConstants.ENTER_PASSWORD);
            valid = false;
        }

        if (password.getText().toString().length() < 6) {
            password.setError(ComeLetsGoConstants.PASSWORD_LENGTH);
            valid = false;
        }
        return valid;
    }
}
