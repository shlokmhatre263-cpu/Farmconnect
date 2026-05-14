package com.example.farmconnect.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.farmconnect.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DiseaseActivity extends BaseActivity {

    private Interpreter interpreter;
    private List<String> labels;
    private static final int IMAGE_SIZE = 224;

    ImageView imageView;
    TextView  resultText;
    Button    selectBtn;

    DatabaseReference reportRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease);

        imageView  = findViewById(R.id.imageView);
        resultText = findViewById(R.id.resultText);
        selectBtn  = findViewById(R.id.selectBtn);

        reportRef = FirebaseDatabase.getInstance().getReference("DiseaseReports");

        loadModel();
        loadLabels();

        selectBtn.setOnClickListener(v -> showImagePickerDialog());
    }

    // ── Image Picker ───────────────────────────────────────────────────────
    private void showImagePickerDialog() {
        // ✅ TRANSLATED — dialog title and options
        String[] options = {
                getString(R.string.camera),
                getString(R.string.gallery)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_image_source));
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) openCamera();
            else openGallery();
        });
        builder.show();
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 100);
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data == null) return;
            Bitmap bitmap = null;
            if (requestCode == 1) {
                bitmap = (Bitmap) data.getExtras().get("data");
            } else if (requestCode == 2) {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                classifyImage(bitmap);
            }
        } catch (Exception e) {
            // ✅ TRANSLATED
            Toast.makeText(this, getString(R.string.image_selection_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Load Model ─────────────────────────────────────────────────────────
    private void loadModel() {
        try {
            AssetFileDescriptor fileDescriptor =
                    getAssets().openFd("main_disease_model.tflite");
            FileInputStream inputStream =
                    new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            MappedByteBuffer buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY,
                    fileDescriptor.getStartOffset(),
                    fileDescriptor.getDeclaredLength());
            interpreter = new Interpreter(buffer);
        } catch (Exception e) {
            // ✅ TRANSLATED
            Toast.makeText(this, getString(R.string.model_load_failed) + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void loadLabels() {
        labels = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("main_disease_labels.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line.trim());
            }
            reader.close();
        } catch (Exception e) {
            // ✅ TRANSLATED
            Toast.makeText(this, getString(R.string.label_load_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Classify ───────────────────────────────────────────────────────────
    private void classifyImage(Bitmap bitmap) {
        if (interpreter == null) {
            // ✅ TRANSLATED
            Toast.makeText(this, getString(R.string.model_not_loaded),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * IMAGE_SIZE * IMAGE_SIZE * 3);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] pixels = new int[IMAGE_SIZE * IMAGE_SIZE];
        resized.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE);
        for (int pixel : pixels) {
            byteBuffer.putFloat(((pixel >> 16) & 0xFF) / 255.0f);
            byteBuffer.putFloat(((pixel >> 8)  & 0xFF) / 255.0f);
            byteBuffer.putFloat((pixel & 0xFF)          / 255.0f);
        }

        float[][] output = new float[1][labels.size()];
        interpreter.run(byteBuffer, output);

        // Temperature scaling
        float temperature = 4.0f;
        float sum = 0f;
        for (int i = 0; i < output[0].length; i++) {
            output[0][i] = (float) Math.pow(output[0][i], 1.0f / temperature);
            sum += output[0][i];
        }
        for (int i = 0; i < output[0].length; i++) {
            output[0][i] /= sum;
        }

        float firstMax  = 0f;
        float secondMax = 0f;
        int   maxIndex  = 0;
        for (int i = 0; i < output[0].length; i++) {
            float confidence = output[0][i];
            if (confidence > firstMax) {
                secondMax = firstMax;
                firstMax  = confidence;
                maxIndex  = i;
            } else if (confidence > secondMax) {
                secondMax = confidence;
            }
        }

        float CONF_THRESHOLD = 0.75f;
        float GAP_THRESHOLD  = 0.20f;

        if (firstMax < CONF_THRESHOLD || (firstMax - secondMax) < GAP_THRESHOLD) {
            // ✅ TRANSLATED
            resultText.setText(getString(R.string.low_confidence_message,
                    String.format("%.2f", firstMax * 100)));
            return;
        }

        String prediction      = labels.get(maxIndex).trim().toLowerCase();
        String confidenceText  = String.format("%.2f", firstMax * 100) + "%";
        String date = new SimpleDateFormat("dd-MM-yyyy HH:mm",
                Locale.getDefault()).format(new Date());

        DiseaseReport report = new DiseaseReport(prediction, confidenceText, date);
        reportRef.push().setValue(report);

        // ✅ TRANSLATED — description and result label
        String description = getDiseaseDescription(prediction);
        resultText.setText(
                getString(R.string.prediction_label) + " " + prediction +
                        "\n" + getString(R.string.confidence_label2) + " " + confidenceText +
                        "\n\n" + description
        );
    }

    // ── Disease Descriptions — ALL TRANSLATED via getString() ──────────────
    private String getDiseaseDescription(String label) {
        switch (label) {

            case "bellpepper_bacterial_spot":
                return getString(R.string.disease_bellpepper_bacterial_spot);

            case "bellpepper_healthy":
                return getString(R.string.disease_bellpepper_healthy);

            case "corn_blight":
                return getString(R.string.disease_corn_blight);

            case "corn_common_rust":
                return getString(R.string.disease_corn_common_rust);

            case "corn_gray_leaf_spot":
                return getString(R.string.disease_corn_gray_leaf_spot);

            case "corn_healthy":
                return getString(R.string.disease_corn_healthy);

            case "cotton_diseased_leaf":
                return getString(R.string.disease_cotton_diseased_leaf);

            case "cotton_diseased_plant":
                return getString(R.string.disease_cotton_diseased_plant);

            case "cotton_fresh_leaf":
                return getString(R.string.disease_cotton_fresh_leaf);

            case "cotton_fresh_plant":
                return getString(R.string.disease_cotton_fresh_plant);

            case "potato_early_blight":
                return getString(R.string.disease_potato_early_blight);

            case "potato_healty":
                return getString(R.string.disease_potato_healthy);

            case "potato_late_blight":
                return getString(R.string.disease_potato_late_blight);

            case "tomato_bacterial_spot":
                return getString(R.string.disease_tomato_bacterial_spot);

            case "tomato_early_blight":
                return getString(R.string.disease_tomato_early_blight);

            case "tomato_healty":
                return getString(R.string.disease_tomato_healthy);

            case "tomato_late_blight":
                return getString(R.string.disease_tomato_late_blight);

            case "tomato_target_spot":
                return getString(R.string.disease_tomato_target_spot);

            case "wheat_blackpoint":
                return getString(R.string.disease_wheat_blackpoint);

            case "wheat_fusariumfootrot":
                return getString(R.string.disease_wheat_fusarium_foot_rot);

            case "wheat_healthyleaf":
                return getString(R.string.disease_wheat_healthy_leaf);

            case "wheat_leafblight":
                return getString(R.string.disease_wheat_leaf_blight);

            case "wheat_wheatblast":
                return getString(R.string.disease_wheat_blast);

            default:
                return getString(R.string.disease_default);
        }
    }
}