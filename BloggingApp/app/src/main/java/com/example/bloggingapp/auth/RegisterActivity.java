package com.example.bloggingapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.bloggingapp.R;
import com.example.bloggingapp.db.DatabaseHelper;
import com.example.bloggingapp.model.UsersModel;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, password;
    TextView textView_login;
    AppCompatButton button_register;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        textView_login = findViewById(R.id.textView_login);
        button_register = findViewById(R.id.button_register);
        db = new DatabaseHelper(this);

        textView_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "username should not be empty", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email should not be empty", Toast.LENGTH_SHORT).show();
                } else if (!(Patterns.EMAIL_ADDRESS).matcher(email.getText().toString()).matches()) {
                    Toast.makeText(RegisterActivity.this, "Email should be in correct format", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password should not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    UsersModel users = new UsersModel(username.getText().toString(), email.getText().toString(), password.getText().toString());
                    db.register(users);
                    Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });

    }
}