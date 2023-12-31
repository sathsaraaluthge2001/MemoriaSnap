package com.example.memoriasnap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.Manifest;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddMemories extends AppCompatActivity {

    ImageView imageadd;
    private EditText title,date,description;
    Button addBtn;

    //permissions

    private static final int  CAMERA_REQUEST_CODE=100;
    private static final int  STORAGE_REQUEST_CODE=101;
    private static final int  IMAGE_PICK_CAMERA_CODE=102;
    private static final int  IMAGE_PICK_GALLERY_CODE=103;

    private String[] camPermissions;
    private  String[] storagePermissions;
    private Uri imageUri;
    private String titleS,dateS,descriptionS;
    //db helper
    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memories);

        imageadd=findViewById(R.id.imageadd);
        title=findViewById(R.id.editTextText4);
        date=findViewById(R.id.editTextText5);
        description=findViewById(R.id.editTextText6);
        addBtn=findViewById(R.id.addmemobtn);

        dbConnection =new DatabaseConnection(this);

        camPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        imageadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePickDialog();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });

    }
    private void inputData() {
        titleS = title.getText().toString().trim();
        dateS = date.getText().toString().trim();
        descriptionS = description.getText().toString().trim();

        if (titleS.isEmpty() || dateS.isEmpty() || descriptionS.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        } else {
            if (imageUri != null) { // Check if imageUri is not null before inserting
                String timestamp = String.valueOf(System.currentTimeMillis());
                long result = dbConnection.insertMemory(
                        imageUri.toString(), // Assuming imageUri is a Uri object
                        titleS,
                        dateS,
                        descriptionS
                );

                if (result > 0) {
                    Toast.makeText(this, "Record Added", Toast.LENGTH_SHORT).show();
                    // Clear input fields after adding data
                    title.setText("");
                    date.setText("");
                    description.setText("");
                    imageadd.setImageResource(R.drawable.ic_action_name); // Reset image view to placeholder
                    imageUri = null; // Reset imageUri after adding to the database
                } else {
                    Toast.makeText(this, "Failed to add record", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please add an image", Toast.LENGTH_SHORT).show();
                // You might want to prompt the user to add an image if imageUri is null
            }
        }
    }
    private void imagePickDialog() {
        String [] options={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("pick image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    if(!checkCameraPermissions()){
                        requestCameraPermission();

                    }
                    else{

                        pickFromCamera();
                    }
                }
                else if(which==1){
                    if(!checkStoragepermissions()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });

        //create/show dialog
        builder.create().show();
    }
    private void pickFromGallery() {
        //intent to pick image from gallery,the image will be returned in onActivityResult method

        Intent galleyIntent=new Intent(Intent.ACTION_PICK);
        galleyIntent.setType("image/*");//only image
        startActivityForResult(galleyIntent,IMAGE_PICK_GALLERY_CODE);
    }
    private void pickFromCamera() {

        //intent to pick image from CAMERA,the image will be returned in onActivityResult method
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Image Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image Description");
        //put image uri
        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to open camera for image
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);

    }
    private boolean checkStoragepermissions(){

        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result;

    }
    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

    }
    private boolean checkCameraPermissions(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,camPermissions,CAMERA_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //result of permission allowed/denied

        switch(requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    //if allowed returns true other wise false
                    boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]== PackageManager.PERMISSION_GRANTED;

                    if(cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this,"camera and storage permissions are required",Toast.LENGTH_SHORT).show();

                    }
                 }
            }

            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    //if allowed returns true other wise false
                    boolean storageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;

                    if(storageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this,"storage permission is required",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                // Picked from gallery, start crop activity
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            } else if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                // Picked from camera, start crop activity
                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                // Crop activity result
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (result != null) {
                    if (result.isSuccessful()) {
                        // Crop successful, retrieve and set the cropped image
                        Uri resultUri = result.getUri();
                        imageUri = resultUri;
                        imageadd.setImageURI(resultUri);
                        // Handle the cropped image here (save to database, etc.)
                    } else if (result.getError() != null) {
                        // Crop failed or was canceled, show error message
                        Exception error = result.getError();
                        Toast.makeText(this, "Crop failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
            super.onActivityResult(requestCode, resultCode, data);

        }

}