package com.example.memoriasnap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Lock extends AppCompatActivity {

    EditText txtGetPassword;
    Button goBtn;

    private String txtPassword;
    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        dbConnection = new DatabaseConnection(this);

        txtGetPassword = findViewById(R.id.txtGetPassword);
        goBtn=findViewById(R.id.goBtn);

        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPassword=txtGetPassword.getText().toString().trim();
                if(txtPassword.equals(dbConnection.getPassword())){
                    Log.e("MemoryDetail", "get password is "+dbConnection.getPassword());
                    Log.e("MemoryDetail", "enter password is "+txtGetPassword);
                    Intent intent = new Intent(Lock.this, MemoriaSnapHome.class);
                    startActivity(intent);
                }
                else{
                    Log.e("MemoryDetail", "get password is "+dbConnection.getPassword());
                    Log.e("MemoryDetail", "enter password is "+txtGetPassword);
                    txtGetPassword.setText("");
                    errorMasseage();
                }
            }
        });
    }

    private void errorMasseage() {
        Toast.makeText(this, "wrong password", Toast.LENGTH_SHORT).show();
    }
}