package com.example.bloggingapp.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bloggingapp.MainActivity;
import com.example.bloggingapp.R;
import com.example.bloggingapp.db.DatabaseHelper;
import com.example.bloggingapp.model.UsersModel;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText username, password;
    TextView textView_register;
    AppCompatButton button_login;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.main));

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button_login = findViewById(R.id.button_login);
        textView_register = findViewById(R.id.textView_register);
        helper=new DatabaseHelper(this);

        textView_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean flag=false;
                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Username should not be empty", Toast.LENGTH_SHORT).show();
                }else  if (password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "password should not be empty", Toast.LENGTH_SHORT).show();
                }else{
                    List<UsersModel> list;
                    list=helper.getAllUsers();

                    for (UsersModel users:list){
                        if(username.getText().toString().equals(users.getUserName()) && password.getText().toString().equals(users.getPassword())){
                            flag=true;
                            Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            break;
                        }else {
                            flag=false;
                        }
                    }
                    if (!flag){
                        Toast.makeText(LoginActivity.this, "Error check user or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}