package com.example.mad_project_fun;

import android.content.Intent;
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

public class Admin_Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnAdminLogin = (Button) findViewById(R.id.btnAdminLogin);
        EditText etadminuname = (EditText) findViewById(R.id.etadminuname);
        EditText etadminpass = (EditText) findViewById(R.id.etadminpass);

        btnAdminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etadminuname.getText().toString().trim();
                String password = etadminpass.getText().toString().trim();
                if (username.equals("admin") && password.equals("admin")) {
                    // Navigate to AdminDash activity
                    Intent intent = new Intent(getApplicationContext(), AdminPanel.class);
                    startActivity(intent);
                    // Optional: Close the current activity
                    finish();
                } else {
                    // Show error message
                    Toast.makeText(getApplicationContext(), "Invalid Username or Password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}