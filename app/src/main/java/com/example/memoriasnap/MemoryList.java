package com.example.memoriasnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;

import android.view.MenuItem;

import java.util.ArrayList;

public class MemoryList extends AppCompatActivity {


    private RecyclerView memoriesRV;
    private AdapterMemories adapter;

    ActionBar actionBar;
    private DatabaseConnection dbConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

        memoriesRV = findViewById(R.id.memoriesRV); // Replace with your actual RecyclerView ID
        memoriesRV.setLayoutManager(new LinearLayoutManager(this));

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("All Memories");
        }

        dbConnection = new DatabaseConnection(this);
        loadMemories();
    }

    private void loadMemories() {
        adapter = new AdapterMemories(this, dbConnection.getAllMemories());
        memoriesRV.setAdapter(adapter);

        if (actionBar != null) {
            actionBar.setSubtitle("Total: " + dbConnection.getRecordCount());
        }
    }

    private void searchMemories(String query){
        AdapterMemories adapterMemories =new AdapterMemories(MemoryList.this,
                dbConnection.searchMemories(query));

        memoriesRV.setAdapter(adapterMemories);

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMemories();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_menu,menu);

        MenuItem item=menu.findItem(R.id.actionSearch);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMemories(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchMemories(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}