package com.example.memoriasnap;


import com.example.memoriasnap.DatabaseConnection;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MemoryDetail extends AppCompatActivity {

    private ImageView imageView;
    private TextView txtTiView,txtDaView,txtDesView;

    private ActionBar actionBar;

    private String MemoTilte;
    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Memo Details");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent =getIntent();
        MemoTilte=intent.getStringExtra("MEMORY_TITLE");
        dbConnection=new DatabaseConnection(this);

        imageView=findViewById(R.id.imaView);
        txtTiView=findViewById(R.id.txtTitView);
        txtDaView=findViewById(R.id.txtDaView);
        txtDesView=findViewById(R.id.txtDesView);

        showMemoryDetails();

    }

    private void showMemoryDetails() {
        String selectQuery = "SELECT * FROM " + DatabaseConnection.TABLE_MEMORY + " WHERE " + DatabaseConnection.COLUMN_TITLE + " = ?";
        SQLiteDatabase db = dbConnection.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{MemoTilte});

        if (cursor.moveToFirst()) {
            do {
                int columnIndexImage = cursor.getColumnIndex(DatabaseConnection.COLUMN_IMAGE);
                int columnIndexTitle = cursor.getColumnIndex(DatabaseConnection.COLUMN_TITLE);
                int columnIndexDate = cursor.getColumnIndex(DatabaseConnection.COLUMN_DATE);
                int columnIndexDescription = cursor.getColumnIndex(DatabaseConnection.COLUMN_DESCRIPTION);

                if (columnIndexImage != -1 && columnIndexTitle != -1 && columnIndexDate != -1 && columnIndexDescription != -1) {
                    String image = cursor.getString(columnIndexImage);
                    String title = cursor.getString(columnIndexTitle);
                    String date = cursor.getString(columnIndexDate);
                    String description = cursor.getString(columnIndexDescription);

                    imageView.setImageURI(Uri.parse(image));
                    txtTiView.setText(title);
                    txtDaView.setText(date);
                    txtDesView.setText(description);
                } else {
                    Toast.makeText(this, "Some columns are not found", Toast.LENGTH_SHORT).show();
                    Log.e("MemoryDetail", "One or more columns not found in the cursor");

                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}