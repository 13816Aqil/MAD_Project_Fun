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

public class User_Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnUserSignup = (Button) findViewById(R.id.btnUserSignup);
        EditText etuseremail = (EditText) findViewById(R.id.etuseremail);
        EditText etuserpass = (EditText) findViewById(R.id.etuserpass);
        EditText etuserconfirmpassword = (EditText) findViewById(R.id.etuserconfirmpassword);

        btnUserSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etuseremail.getText().toString().trim();
                String password = etuserpass.getText().toString().trim();
                String confirmPassword = etuserconfirmpassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save data to SharedPreferences
                saveToSharedPreferences(email, password);

                // Show success message
                Toast.makeText(getApplicationContext(), "Signup successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), User_Login.class);
                startActivity(intent);

                // Clear input fields
                etuseremail.setText("");
                etuserpass.setText("");
                etuserconfirmpassword.setText("");
            }
        });
    }
    private void saveToSharedPreferences(String email, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }
}