package com.example.android.mvvm.Views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.android.mvvm.R;
import com.example.android.mvvm.Reminder.DatePickerFragment;
import com.example.android.mvvm.Reminder.TimePickerFragment;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddEditNoteActivity extends AppCompatActivity implements EditNoteBottomSheetDialog.BottomSheetListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_ID = "com.example.android.mvvm.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.android.mvvm.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.android.mvvm.EXTRA_DESCRIPTION";
    public static final String EXTRA_REMINDER_BOOLEAN = "com.example.android.mvvm.EXTRA_REMINDER_BOOLEAN";
    public static final String EXTRA_DATE = "com.example.android.mvvm.EXTRA_REMINDER_DATE";

    public static final String SELECT_DATE_TEXT = "Select Date";
    public static final String SELECT_TIME_TEXT = "Select Time";

    private EditText titleEditText;
    private EditText descriptionEditText;

    //Holds the date and time set
    String dateSelected = SELECT_DATE_TEXT;
    String timeSelected = SELECT_TIME_TEXT;

    private Calendar fCalendar;

    private boolean reminderBooleanFromMainActivity = false;
    private boolean editActivityBoolean = false;
    private String dateFromIntent = "";

    //Reminder TextViews
    private TextView timeTextView;
    private TextView dateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //Adding the Close to the action bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Log.d("TIME: ", "AddEditActivity launched");

        //Note Title and Description Edits
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);

        fCalendar = Calendar.getInstance();

        //Setting the title of the action bar
        //Get the id of the note from the intent that started this activity if we are editing the note
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            //Populate the view accordingly
            setTitle("Edit Note");
            titleEditText.setText(intent.getStringExtra(EXTRA_TITLE));
            descriptionEditText.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            reminderBooleanFromMainActivity = intent.getBooleanExtra(EXTRA_REMINDER_BOOLEAN, false);
            dateFromIntent = intent.getStringExtra(EXTRA_DATE);

            //Get the date and time from date
            if (dateFromIntent != null && intent.getIntExtra(EXTRA_ID, -1) != -1) {
                //If note has reminder, check if the date has passed
                if (reminderBooleanFromMainActivity){
                    //Set the EditActivityBoolean to true
                    editActivityBoolean = true;
                    //Get the date from the note
                    Calendar calendarDate = Calendar.getInstance();
                    Date date1;
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
                    Log.d("TIME:: ", "Date is not null: " + dateFromIntent);
                    try {
                        date1 = (Date) simpleDateFormat.parse(dateFromIntent);
                        Log.d("TIME", "Date Object: " + date1);
                        calendarDate.setTime(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Compare with the current date
                    Calendar currentDate = fCalendar;
                    if (currentDate.after(calendarDate)){
                        //if the reminder date is in the past
                        reminderBooleanFromMainActivity = false;
                        editActivityBoolean = false;
                        dateFromIntent = null;
                    } else{
                        //The date will be as from the intent
                        //Update the time and date text holders
                        timeSelected = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendarDate.getTime());
                        dateSelected = DateFormat.getDateInstance().format(calendarDate.getTime());
                    }
                    Log.d("TIME: ", "TimeText: " + timeSelected);
                    Log.d("TIME: ", "DateText: " + dateSelected);

                }
            } else {
                Log.d("TIME:: ", "Date is null");
            }
        } else {
            setTitle("Add Note");
        }

        //The reminder buttons
        Button pickDate = findViewById(R.id.pick_date);
        Button pickTime = findViewById(R.id.pick_time);

        Button setReminderBtn = findViewById(R.id.set_reminder);
        Button cancelReminderBtn = findViewById(R.id.cancel_reminder);

        setReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemainder();
            }
        });
        cancelReminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRemainder();
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        //Set the time and date texts
        timeTextView = findViewById(R.id.reminder_time_textView);
        dateTextView = findViewById(R.id.reminder_date_textView);
        //Set the time and date Texts
        timeTextView.setText(timeSelected);
        dateTextView.setText(dateSelected);

    }//End of onCreate


    private void saveNote() {
        //Get the values from the views
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        //Check if title and description fields are empty
        //trim removes the empty spaces before and after the word
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            StyleableToast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT, R.style.plainToast).show();
            return;
        }
        //Pass the data from the note to the MainActivity
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_REMINDER_BOOLEAN, editActivityBoolean);
        Log.d("TIME: ", "ReminderBoolean: " + editActivityBoolean);

        //If we are saving a note that we have edited
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        Log.d("TIME: ", "ID from Intent" + id);
        //Get the selected date as a string
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
        Date date1 = fCalendar.getTime();
        Log.d("TIME:", "Date Object: " + date1);
        String dateToBeSaved = simpleDateFormat.format(date1);//calendar.getTime() returns a Date

        //If it is an edited note
        if (id != -1) {
            Log.d("TIME: ", "ID: is not -1: " + id);
            //Note to be updated has a reminder
            if (editActivityBoolean) {
                //Check if it had a reminder from MainActivity
                if (reminderBooleanFromMainActivity){
                    //Include the id and the date from the intent
                    data.putExtra(EXTRA_ID, id);
                    data.putExtra(EXTRA_DATE, dateFromIntent);
                    Log.d("TIME: ","Updated Note with date from Intent: " + dateFromIntent);

                } else{
                    //Noe to be updated didn't have a reminder before
                    //Get the date set and its id
                    data.putExtra(EXTRA_ID, id);
                    data.putExtra(EXTRA_DATE, dateToBeSaved);
                    Log.d("TIME: ","Updated Note date with first reminder " + dateToBeSaved);
                }
            } else{
                //Note to be updated has no reminder
                dateToBeSaved = null;
                Log.d("TIME: ", "Update without reminder: " + id);
                data.putExtra(EXTRA_DATE, dateToBeSaved);
                data.putExtra(EXTRA_ID, id);
                Log.d("TITLE: ", "Update Note without reminder: " + dateToBeSaved);
            }
        } else if (editActivityBoolean) {
            //New Note but with a reminder
            Log.d("TIME: ", "New Note with a reminder: " + editActivityBoolean);
            //If it is a new note  with a reminder, get the selected date
            dateToBeSaved = simpleDateFormat.format(date1);//calendar.getTime() returns a Date
            data.putExtra(EXTRA_DATE, dateToBeSaved);
            Log.d("TIME: ", "New Note with a reminder: " + id);
            Log.d("TIME:", "Date Object: " + date1);
        }
        Log.d("TIME: ", "HasReminder: " + editActivityBoolean);
        Log.d("TIME: ", "Date to be saved: " + dateToBeSaved);
        Log.d("TIME: ", "");
        Log.d("TIME: ", "IID: " + id);

        //If saving was correct
        setResult(RESULT_OK, data);
        finish();
    }

    private void setRemainder() {
        Log.d("TIME: ", "setReminder called");
        //Check is date or time is set
        if (!timeSelected.equals(SELECT_TIME_TEXT) || !dateSelected.equals(SELECT_DATE_TEXT)) {
            //Set the boolean to true
            editActivityBoolean = true;
            Log.d("TIME: ", "remainderBoolean: " + true);
        } else {
            Log.d("TIME: ", "remainderBoolean: " + false);
            StyleableToast.makeText(this, "Please select a date or time first", Toast.LENGTH_SHORT, R.style.plainToast).show();
        }
    }

    private void cancelRemainder() {
        //set the reminderBoolean to be false
        editActivityBoolean = false;
        //revert the textViews to the original texts
        timeTextView.setText(SELECT_DATE_TEXT);
        dateTextView.setText(SELECT_DATE_TEXT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.save_note) {
            saveNote();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //If we are saving a note that we have edited
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        //Check if it has an id
        if (id != -1) {
            EditNoteBottomSheetDialog bottomSheetDialog = new EditNoteBottomSheetDialog();
            bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet");
        } else {
            //Check if the fields are empty
            if (!checkIfIsEmpty()) {
                saveNote();
                return;
            }
            super.onBackPressed();
        }
    }

    private boolean checkIfIsEmpty() {
        //Get the values from the views
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        //Check if title and description fields are empty
        //trim removes the empty spaces before and after the word
        return title.trim().isEmpty() || description.trim().isEmpty();
    }

    @Override
    public void onButtonClicked(String text) {
        if (text.equals("Save")) {
            saveNote();
        } else if (text.equals("Cancel")) {
            finish();
        }
    }

    //Returns the time set in the dialog
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        fCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        fCalendar.set(Calendar.MINUTE, minute);

        setDateText();
    }

    //Returns the date set in the dialog
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        fCalendar.set(Calendar.YEAR, year);
        fCalendar.set(Calendar.MONTH, month);
        fCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        setDateText();
    }

    private void setDateText() {
        Log.d("TIME: ", "setDateText called");
        //Get the selected date and time
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(fCalendar.getTime());
        String currentDateString = DateFormat.getDateInstance().format(fCalendar.getTime());
        //Update the time and date text holders
        timeSelected = currentTimeString;
        dateSelected = currentDateString;
        Log.d("TIME: ", "Date selected " + dateSelected);
        Log.d("TIME: ", "Time selected " + timeSelected);
        //Update the TextViews
        timeTextView.setText(timeSelected);
        dateTextView.setText(dateSelected);

    }
}
