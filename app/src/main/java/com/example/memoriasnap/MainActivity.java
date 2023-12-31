package com.example.memoriasnap;



import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startbtn);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLock appLock = new AppLock(); // Create an instance of AppLock
                if (appLock.isLockEnabled()) {
                    launchLockScreen();
                } else {
                    Intent intent = new Intent(MainActivity.this, MemoriaSnapHome.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void launchLockScreen() {
        Intent intent = new Intent(MainActivity.this, Lock.class);
        startActivity(intent);
        finish();
    }
}