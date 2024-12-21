package com.example.mad_project_fun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class User_Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnuserlogin = (Button) findViewById(R.id.btnuserlogin);
        EditText etemail = (EditText) findViewById(R.id.etemail);
        EditText etpass = (EditText) findViewById(R.id.etpass);
        Button btnsingnup = (Button) findViewById(R.id.btnsingnup);

        btnuserlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString().trim();
                String password = etpass.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check credentials
                if (checkCredentials(email, password)) {
                    // Show success message
                    Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();

                    // Move to the next activity with the email
                    Intent intent = new Intent(getApplicationContext(), UserPanel.class);
                    intent.putExtra("email", email);
                    startActivity(intent);

                    // Finish current activity
                    finish();
                } else {
                    // Show error message
                    Toast.makeText(getApplicationContext(), "Invalid email or password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnsingnup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), User_Signup.class);
                startActivity(intent);
            }
        });
    }


    private boolean checkCredentials(String email, String password) {
        // Retrieve saved email and password from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        String savedEmail = sharedPreferences.getString("email", "");
        String savedPassword = sharedPreferences.getString("password", "");

        // Check if the entered email and password match the saved credentials
        return email.equals(savedEmail) && password.equals(savedPassword);
    }
}