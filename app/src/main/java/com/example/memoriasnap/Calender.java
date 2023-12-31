package com.example.memoriasnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Calender extends AppCompatActivity {

    private DatabaseConnection dbConnection;
    EditText event;
    Button save,delete;
    CalendarView calendarView;
    private String selectDate, eventString, getSelectDate;

    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        event = findViewById(R.id.event);
        calendarView = findViewById(R.id.calendarView);
        dbConnection = new DatabaseConnection(this);
        save = findViewById(R.id.save);
        delete=findViewById(R.id.delete);


        selectDate = ""; // Initialize selectDate
        getSelectDate = ""; // Initialize getSelectDate

        if (calendarView == null) {
            Log.e("CalenderActivity", "calendarView is null");
            // Handle the case where calendarView is null
        } else {
            // Your existing code to set listeners, etc.
            Log.d("CalenderActivity", "calendarView found successfully");
            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    selectDate = String.format(Locale.getDefault(), "%04d%02d%02d", year, month + 1, dayOfMonth);
                    getSelectDate = selectDate; // Assign the selected date to getSelectDate
                    getEvent();// Fetch the event associated with the selected date
                }
            });
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEvent();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbConnection.deleteEvent(getSelectDate);
                event.setText("");
            }
        });
    }

    private void addEvent() {
        eventString = event.getText().toString().trim(); // Get the event text

        if (eventString.isEmpty() || getSelectDate.isEmpty()) {
            Toast.makeText(this, "Select date and write event", Toast.LENGTH_SHORT).show();
        } else {
            long result = dbConnection.insertEvent(getSelectDate, eventString);
            if (result > 0) {
                Toast.makeText(this, "Event Add", Toast.LENGTH_SHORT).show();
                event.setText("");

                scheduleAlarmForEvent(getSelectDate);
            } else {
                Toast.makeText(this, "Cannot add event", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleAlarmForEvent(String selectedDate) {
        // Parse the selected date to set the alarm
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        try {
            Date selectedEventDate = sdf.parse(selectedDate);
            if (selectedEventDate != null) {
                // Get the current date
                Calendar currentCalendar = Calendar.getInstance();

                // Compare the selected date with the current date or allow setting events for the current day
                if (selectedEventDate.after(currentCalendar.getTime()) || isSameDay(selectedEventDate, currentCalendar.getTime())) {
                    // Proceed with setting the alarm
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(selectedEventDate);

                    // Set the alarm time to 6:10 PM on the selected date
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);

                    // Create an Intent for AlarmReceiver
                    Intent alarmIntent = new Intent(this, AlarmReceiver.class);
                    alarmIntent.putExtra("event_description", eventString);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE
                    );

                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                    if (alarmManager != null) {
                        // Set the alarm
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                        Toast.makeText(this, "Alarm added for event on " + selectedDate, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("CalenderActivity", "AlarmManager is null");
                    }
                } else {
                    // Handle the case where the event date has already passed
                    Toast.makeText(this, "Selected date has already passed", Toast.LENGTH_SHORT).show();
                    Log.d("CalenderActivity", "Selected Date: " + selectedEventDate.toString());
                    Log.d("CalenderActivity", "Current Date: " + currentCalendar.getTime().toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Function to check if two dates are on the same day
    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }



    private  void  getEvent() {
        String selectQuery = "SELECT " + DatabaseConnection.eventCol + " FROM " + DatabaseConnection.TableCal + " WHERE " + DatabaseConnection.dateCol + " = '" + selectDate + "'";
        SQLiteDatabase db = dbConnection.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            int columnIndexEvent = cursor.getColumnIndex(DatabaseConnection.eventCol);

            if (columnIndexEvent != -1) {
                String eventt = cursor.getString(columnIndexEvent);
                event.setText(eventt);
            } else {
                Toast.makeText(this, "Column is not found", Toast.LENGTH_SHORT).show();
                Log.e("MemoryDetail", "Column not found");
            }
        } else {
            // If no event is found for the selected date, set the EditText to an empty string
            event.setText("");
        }

    }
}
