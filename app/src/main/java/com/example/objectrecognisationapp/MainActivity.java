package com.example.objectrecognisationapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button chooseImageFromGallery_btn, chooseImageFromCamera_btn;
    TextView result;

    final int IMAGE_FROM_GALLERY = 1;
    final int IMAGE_FROM_CAMERA = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            imageView = findViewById(R.id.imageView);
            chooseImageFromGallery_btn = findViewById(R.id.chooseImageFromGallery_btn);
            chooseImageFromCamera_btn = findViewById(R.id.chooseImageFromCamera_btn);
            result = findViewById(R.id.result);

            chooseImageFromGallery_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    result.setText("");
                    startActivityForResult(intent, IMAGE_FROM_GALLERY);
                }
            });

            chooseImageFromCamera_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    result.setText("");
                    startActivityForResult(intent, IMAGE_FROM_CAMERA);
                }
            });

            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_FROM_CAMERA)
        {
            Bitmap bitmap =  (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            InputImage image;
            try {
                image = InputImage.fromBitmap(bitmap, 0);

                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {

                                float highestConfidence = 0;
                                String highestConfidenceText = null;
                                
                                for (ImageLabel label : labels)
                                {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();

                                    //if you want to print all the elements of the label simply use the code :
                                    // result.append(text + " " + confidence);

                                    if(confidence > highestConfidence)
                                    {
                                        highestConfidence = confidence;
                                        highestConfidenceText = text;
                                    }
                                    
                                    result.setText(highestConfidenceText + " " + highestConfidence);

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == IMAGE_FROM_GALLERY)
        {
            imageView.setImageURI(data.getData());

            InputImage image;
            try {
                image = InputImage.fromFilePath(getApplicationContext(), data.getData());

                ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
                labeler.process(image)
                        .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                            @Override
                            public void onSuccess(List<ImageLabel> labels) {

                                float highestConfidence = 0;
                                String highestConfidenceText = null;

                                for (ImageLabel label : labels)
                                {
                                    String text = label.getText();
                                    float confidence = label.getConfidence();
                                    int index = label.getIndex();

                                    //if you want to print all the elements of the label simply use the code :
                                    // result.append(text + " " + confidence);

                                    if(confidence > highestConfidence)
                                    {
                                        highestConfidence = confidence;
                                        highestConfidenceText = text;
                                    }

                                    result.setText(highestConfidenceText + " " + highestConfidence);

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}