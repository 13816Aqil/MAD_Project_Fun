package com.example.mad_project_fun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class K_Electric extends AppCompatActivity {

    private ImageView kecapturedImageView;
    private Button kecaptureButton, btnStartVoice;
    private Bitmap currentBitmap;
    private EditText keChatBox, keemailet;
    private SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelectric);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);


        // Initialize views
        initializeViews();

        // Check and request necessary permissions
        checkPermissions();

        // Setup Spinner
        setupSpinner();

        // Setup Submit button
        setupSubmitButton();

        // Voice recognition button setup
        setupVoiceRecognition();
    }

    private void initializeViews() {
        kecapturedImageView = findViewById(R.id.kecapturedImageView);
        kecaptureButton = findViewById(R.id.kecaptureButton);
        btnStartVoice = findViewById(R.id.btn_start_voice);
        keemailet = findViewById(R.id.keemailet);
        keChatBox = findViewById(R.id.keChatBox);
    }

    private void checkPermissions() {
        // Request microphone permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Camera button click event
        kecaptureButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        ArrayList<String> issueList = new ArrayList<>();
        issueList.add("---Choose---");
        issueList.add("New meter Request");
        issueList.add("Load Shedding problem");
        issueList.add("Billing Issue");
        issueList.add("Disconnect connection");
        issueList.add("Customer care representative");
        issueList.add("Others____");

        Spinner kespinner = findViewById(R.id.kespinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, issueList);
        kespinner.setAdapter(adapter);

        kespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Toast.makeText(getApplicationContext(), "Please select a valid option.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupVoiceRecognition() {
        btnStartVoice.setOnClickListener(v -> startVoiceRecognition());
    }

    private void setupSubmitButton() {
        Button KEsubmitbtn = findViewById(R.id.KEsubmitbtn);
        KEsubmitbtn.setOnClickListener(v -> {
            String selectedIssue = ((Spinner) findViewById(R.id.kespinner)).getSelectedItem().toString();
            String email = keemailet.getText().toString();
            String message = keChatBox.getText().toString();

            if (selectedIssue.equals("---Choose---")) {
                Toast.makeText(getApplicationContext(), "Please select a valid issue", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                keemailet.setError("Email is required");
                return;
            }

            if (message.isEmpty()) {
                keChatBox.setError("Message is required");
                return;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (currentBitmap != null) {
                currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            }

            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            dbHelper.insertRequest(selectedIssue, email, message, encodedImage);

            // Show success message
            Toast.makeText(getApplicationContext(), "Request submitted successfully", Toast.LENGTH_SHORT).show();

            // Pass data to the next activity
            Intent intent = new Intent(this, K_ElectricHistory.class);
            intent.putExtra("issue", selectedIssue);
            intent.putExtra("email", email);
            intent.putExtra("message", message);
            intent.putExtra("image", encodedImage);
            startActivity(intent);

            // Clear inputs
            keemailet.setText("");
            keChatBox.setText("");
            ((Spinner) findViewById(R.id.kespinner)).setSelection(0);
        });
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    currentBitmap = (Bitmap) extras.get("data");
                    kecapturedImageView.setImageBitmap(currentBitmap);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer.startListening(intent);
        } else {
            Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private class SpeechRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(K_Electric.this, "Listening...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            Log.e("SpeechRecognition", "Error Code: " + error);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                keChatBox.setText(matches.get(0));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }
}
