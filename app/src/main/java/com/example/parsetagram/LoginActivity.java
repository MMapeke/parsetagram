package com.example.parsetagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //if already logged in
        if(ParseUser.getCurrentUser() != null) goMainActivity();

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"onClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username,password);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"onClick Register button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                registerUser(username,password);
            }
        });
    }

    private void registerUser(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG,"Registering User Error",e);
                    return;
                }
                //sign in for user using new acct
                goMainActivity();
                Toast.makeText(LoginActivity.this,"New Account Created",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser(String username, String password) {
        Log.i(TAG,"Attempting to login user");
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    //TODO: Better error handling
                    Log.e(TAG,"Issue w login",e);
                    Toast.makeText(LoginActivity.this,"Issue with login",Toast.LENGTH_SHORT).show();
                    return;
                }
                //if signed in properly navigate to main activity
                goMainActivity();
                Toast.makeText(LoginActivity.this,"Success!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}