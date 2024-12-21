package com.example.mad_project_fun;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class SUI_Gas extends AppCompatActivity {

    private ImageView suicapturedImageView;
    private Button suicaptureButton, btnStartVoice1;
    private Bitmap currentBitmap1;
    private EditText suiChatBox, suiemailet;
    private SpeechRecognizer speechRecognizer1;
    private static final int REQUEST_CODE_PERMISSION = 1;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sui_gas);

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
        suicapturedImageView = findViewById(R.id.suicapturedImageView);
        suicaptureButton = findViewById(R.id.suicaptureButton);
        btnStartVoice1 = findViewById(R.id.btn_start_voice1);
        suiemailet = findViewById(R.id.suiemailet);
        suiChatBox = findViewById(R.id.suiChatBox);
    }

    private void checkPermissions() {
        // Request microphone permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQUEST_CODE_PERMISSION);
        } else {
            initializeSpeechRecognizer();
        }

        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 100);
        }

        // Camera button click event
        suicaptureButton.setOnClickListener(v -> {
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
        issueList.add("Gas Load Shedding problem");
        issueList.add("Billing Issue");
        issueList.add("Disconnect connection");
        issueList.add("Customer care representative");
        issueList.add("Others____");

        Spinner suispinner = findViewById(R.id.suispinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, issueList);
        suispinner.setAdapter(adapter);

        suispinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        btnStartVoice1.setOnClickListener(v -> startVoiceRecognition());
    }

    private void setupSubmitButton() {
        Button suisubmitbtn = findViewById(R.id.suisubmitbtn); // Corrected ID
        suisubmitbtn.setOnClickListener(v -> {
            String selectedIssue = ((Spinner) findViewById(R.id.suispinner)).getSelectedItem().toString(); // Corrected ID
            String email = suiemailet.getText().toString();
            String message = suiChatBox.getText().toString();

            if (selectedIssue.equals("---Choose---")) {
                Toast.makeText(getApplicationContext(), "Please select a valid issue", Toast.LENGTH_SHORT).show();
                return;
            }

            if (email.isEmpty()) {
                suiemailet.setError("Email is required");
                return;
            }

            if (message.isEmpty()) {
                suiChatBox.setError("Message is required");
                return;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            String encodedImage = null;
            if (currentBitmap1 != null) {
                currentBitmap1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }

            dbHelper.insertRequest(selectedIssue, email, message, encodedImage);

            Toast.makeText(getApplicationContext(), "Request submitted successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, K_ElectricHistory.class);
            intent.putExtra("issue", selectedIssue);
            intent.putExtra("email", email);
            intent.putExtra("message", message);
            intent.putExtra("image", encodedImage);
            startActivity(intent);

            suiemailet.setText("");
            suiChatBox.setText("");
            ((Spinner) findViewById(R.id.suispinner)).setSelection(0); // Corrected ID
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
                    currentBitmap1 = (Bitmap) extras.get("data");
                    suicapturedImageView.setImageBitmap(currentBitmap1);
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void initializeSpeechRecognizer() {
        speechRecognizer1 = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer1.setRecognitionListener(new SUI_Gas.SpeechRecognitionListener());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer1.startListening(intent);
        } else {
            Toast.makeText(this, "Speech recognition is not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private class SpeechRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Toast.makeText(SUI_Gas.this, "Listening...", Toast.LENGTH_SHORT).show();
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
                suiChatBox.setText(matches.get(0));
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }
}