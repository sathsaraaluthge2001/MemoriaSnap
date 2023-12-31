package com.example.memoriasnap;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MemoriaSnapHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memoria_snap_home);

        View addMemoLayout =findViewById(R.id.addMemoLayout);
        View viewMemoLayout=findViewById(R.id.viewMe);
        View lockMemoLayot=findViewById(R.id.lockMemoLayot);
        View CalMemoLayot=findViewById(R.id.calMemoLayot);

        addMemoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MemoriaSnapHome.this, AddMemories.class);
                startActivity(intent);
            }
        });

        viewMemoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewMemoLayoutClick", "View Memo Layout Clicked");
                Intent intent = new Intent(MemoriaSnapHome.this, MemoryList.class);
                startActivity(intent);
            }
        });

        lockMemoLayot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewMemoLayoutClick", "View Memo Layout Clicked");
                Intent intent = new Intent(MemoriaSnapHome.this, AppLock.class);
                startActivity(intent);
            }
        });

        CalMemoLayot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ViewMemoLayoutClick", "View Memo Layout Clicked");
                Intent intent = new Intent(MemoriaSnapHome.this, Calender.class);
                startActivity(intent);
            }
        });






    }
}