package com.example.mad_project_fun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import android.content.Intent;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class K_ElectricHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelectric_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        // Retrieve the data passed through the Intent
        String issue = intent.getStringExtra("issue");
        String email = intent.getStringExtra("email");
        String message = intent.getStringExtra("message");
        String encodedImage = getIntent().getStringExtra("image");

        Boolean status = false;

        // Decode the image from Base64
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        // Find TextViews in your layout
        TextView issueTextView = findViewById(R.id.issueTextView);
        TextView emailTextView = findViewById(R.id.emailTextView);
        TextView messageTextView = findViewById(R.id.messageTextView);
        ImageView imageView = findViewById(R.id.kecapturedImageView);
        TextView statusTextView = findViewById(R.id.statusTextView);

        issueTextView.setText("Issue: " + issue);
        emailTextView.setText("Email: " + email);
        messageTextView.setText("Message: " + message);
        imageView.setImageBitmap(decodedBitmap);
        statusTextView.setText("Status: " + status);
    }
}



