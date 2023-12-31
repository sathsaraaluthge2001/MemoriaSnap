package com.example.memoriasnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class AppLock extends AppCompatActivity {

    private static Switch lockSwitch;
    private EditText passwordEditText;
    private String passwordS;
    private Button saveButton;
    private DatabaseConnection dbConnection;
    private static SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock);

        lockSwitch = findViewById(R.id.switch1);
        passwordEditText = findViewById(R.id.txtPassword);
        saveButton = findViewById(R.id.passwordSaveBtn);

        dbConnection = new DatabaseConnection(this);
        sharedPreferences = getSharedPreferences("LockState", Context.MODE_PRIVATE);

        boolean isLockEnabled = sharedPreferences.getBoolean("isLockEnabled", false);
        lockSwitch.setChecked(isLockEnabled);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassword();
            }
        });

        lockSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLockEnabled", isChecked);
                editor.apply();

                if (isChecked) {
                    Toast.makeText(AppLock.this, "Lock Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AppLock.this, "Lock Disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static boolean isLockEnabled() {
        // Implement logic to return the actual state of lockSwitch
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean("isLockEnabled", false);
        }
        return false;
    }

    private void setPassword(){

        passwordS = passwordEditText.getText().toString().trim();

        if (passwordS.isEmpty()) {
            Toast.makeText(this, "enter the password", Toast.LENGTH_SHORT).show();
        }
        else{

            int deleteResult = dbConnection.deleteAllPasswords();
            if(deleteResult >= 0){
                long result = dbConnection.insertPassword(passwordS);

                if (result > 0) {
                    Toast.makeText(this, "Password Add", Toast.LENGTH_SHORT).show();
                    // Clear input fields after adding data
                    passwordEditText.setText("");
                } else {
                    Toast.makeText(this, "Faild to set Password", Toast.LENGTH_SHORT).show();
                }
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isLockEnabled", true); // Set the lock as enabled when a password is set
            editor.apply();
        }
    }
}