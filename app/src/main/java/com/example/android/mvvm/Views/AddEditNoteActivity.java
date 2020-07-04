package com.example.android.mvvm.Views;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.DialogFragment;

import com.example.android.mvvm.R;
import com.example.android.mvvm.Reminder.DatePickerFragment;
import com.example.android.mvvm.Reminder.TimePickerFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
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
    public static final String EXTRA_DATE_TEXT = "com.example.android.mvvm.EXTRA_REMINDER_DATE_STRING";
    public static final String EXTRA_CATEGORY = "com.example.android.mvvm.EXTRA_CATEGORY";

    public static final String SELECT_DATE_TEXT = "Select Date";
    public static final String SELECT_TIME_TEXT = "Select Time";
    public final String TAG = "TIME:";

    private TextView categoryTextView;
    private String categoryString = "Un-Categorized";

    private EditText titleEditText;
    private EditText descriptionEditText;

    //Holds the date and time set
    String dateSelected = SELECT_DATE_TEXT;
    String timeSelected = SELECT_TIME_TEXT;
    String currentDay = "";

    private Calendar fCalendar;

    private boolean reminderBooleanFromMainActivity = false;
    private boolean editActivityBoolean = false;
    private String dateFromIntent = "";
    private String dateTextFromIntent = "";

    //Reminder TextViews
    private TextView timeTextView;
    private TextView dateTextView;
    private SimpleDateFormat simpleDayFormat;
    public Boolean dateTimeSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //Adding the Close to the action bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Log.d(TAG + " ", "AddEditActivity launched");

        categoryTextView = findViewById(R.id.note_category);

        //Note Title and Description Edits
        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);

        //Time and Date TextViews
        timeTextView = findViewById(R.id.reminder_time_textView);
        dateTextView = findViewById(R.id.reminder_date_textView);

        fCalendar = Calendar.getInstance();
        simpleDayFormat = new SimpleDateFormat("EEEE");

        Switch alarmToggle = findViewById(R.id.alarm_toggle);

        TextView reminderText = findViewById(R.id.bottom_sheet_textView);

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
            dateTextFromIntent = intent.getStringExtra(EXTRA_DATE_TEXT);

            //Get the category from the intent
            categoryString = intent.getStringExtra(EXTRA_CATEGORY);
            categoryTextView.setText(categoryString);

            //Get the date and time from date
            if (dateFromIntent != null && intent.getIntExtra(EXTRA_ID, -1) != -1) {
                //If note has reminder, check if the date has passed
                if (reminderBooleanFromMainActivity) {
                    //Set the EditActivityBoolean to true
                    editActivityBoolean = true;
                    //Get the date from the note
                    Calendar calendarDate = Calendar.getInstance();
                    Date date1 = new Date();
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
                    Log.d(TAG + ": ", "Date is not null: " + dateFromIntent);

                    try {
                        date1 = (Date) simpleDateFormat1.parse(dateFromIntent);
                        Log.d("TIME", "Date Object: " + date1);
                        calendarDate.setTime(date1);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //Compare with the current date
                    Calendar currentDate = fCalendar;
                    if (currentDate.after(calendarDate)) {
                        //if the reminder date is in the past
                        reminderBooleanFromMainActivity = false;
                        editActivityBoolean = false;
                        dateFromIntent = null;
                        dateTextFromIntent = null;
                    } else {
                        //The date will be as from the intent
                        //Update the time and date text holders
                        timeSelected = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendarDate.getTime());

                        //Get the current day
                        currentDay = simpleDayFormat.format(date1);//calendar.getTime() returns a Date
                        dateSelected = currentDay + ", " + DateFormat.getDateInstance().format(calendarDate.getTime());

                        //Set the text to edit reminder and set the toggle to checked
                        reminderText.setText(R.string.edit_reminder_text);

                        alarmToggle.setChecked(true);
                    }
                    Log.d(TAG + " ", "TimeText: " + timeSelected);
                    Log.d(TAG + " ", "DateText: " + dateSelected);

                }
            } else {
                Log.d(TAG + ": ", "Date is null");
            }
        } else {
            setTitle("Add Note");
        }
        //Click category textView to set the category
        categoryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                createCategoryDialog();
            }
        });

        //Reminder Nested ScrollView scroll up and down
        final BottomSheetBehavior sheetBehavior;
        final NestedScrollView reminderScrollView = findViewById(R.id.reminder_nested_scroll_view);
        sheetBehavior = BottomSheetBehavior.from(reminderScrollView);

        //Show the bottom sheet
        reminderText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Scroll", "ScrollView clicked");
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        //The reminder buttons
        final ImageButton pickDate = findViewById(R.id.pick_date);
        final ImageButton pickTime = findViewById(R.id.pick_time);

        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //Check is date or time is set
                    if (!timeSelected.equals(SELECT_TIME_TEXT) || !dateSelected.equals(SELECT_DATE_TEXT)) {
                        //Set the boolean to true
                        setRemainder();
                    } else {
                        buttonView.setChecked(false);
                        StyleableToast.makeText(AddEditNoteActivity.this, "Please select a date or time first", Toast.LENGTH_SHORT, R.style.errorToast).show();
                    }
                } else {
                    cancelRemainder();
                }
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarDialog();
            }
        });
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClockDialog();

            }
        });

        //Clicking the textViews should display the time and calendar dialogs respectively
        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClockDialog();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCalendarDialog();
            }
        });

        //Set the time and date Texts
        timeTextView.setText(timeSelected);
        dateTextView.setText(dateSelected);

    }//End of onCreate

    private void createCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Category");
        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryString = input.getText().toString();
                categoryTextView.setText(categoryString);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createClockDialog() {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    private void createCalendarDialog() {
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.setAllowEnterTransitionOverlap(true);
        datePicker.show(getSupportFragmentManager(), "date picker");
    }


    private void saveNote() {
        //Get the values from the views
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        //Check if title and description fields are empty
        //trim removes the empty spaces before and after the word
        if (title.trim().isEmpty() || description.trim().isEmpty()) {
            StyleableToast.makeText(this, "Please insert a title and description", Toast.LENGTH_SHORT, R.style.errorToast).show();
            return;
        }
        //Pass the data from the note to the MainActivity
        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION, description);
        data.putExtra(EXTRA_REMINDER_BOOLEAN, editActivityBoolean);
        data.putExtra(EXTRA_CATEGORY, categoryString);
        Log.d(TAG + " ", "ReminderBoolean: " + editActivityBoolean);

        //If we are saving a note that we have edited
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        Log.d(TAG + " ", "ID from Intent" + id);

        //Get the selected date as a string
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
        Date date1 = fCalendar.getTime();

        String dateToBeSavedNew = simpleDateFormat.format(date1);//calendar.getTime() returns a Date
        Log.d(TAG, "Date Object: " + date1);

        //Get the day
        String day = simpleDayFormat.format(date1);
        String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(date1);
        String dateToBeSavedText = day + ", " + time;

        Log.d("Date: ", dateToBeSavedText);

        //If it is an edited note
        if (id != -1) {
            Log.d(TAG + " ", "ID: is not -1: " + id);
            //Note to be updated has a reminder
            if (editActivityBoolean) {
                //Check if it had a reminder from MainActivity
                if (reminderBooleanFromMainActivity) {
                    //Check if the updated note has an updated date
                    if (dateTimeSet) {
                        //Use the updated date
                        data.putExtra(EXTRA_DATE, dateToBeSavedNew);
                        data.putExtra(EXTRA_DATE_TEXT, dateToBeSavedText);
                        Log.d(TAG, "Updated Note with updated data: " + dateToBeSavedNew);
                    } else {
                        //Use date from intent
                        data.putExtra(EXTRA_DATE, dateFromIntent);
                        data.putExtra(EXTRA_DATE_TEXT, dateTextFromIntent);
                        Log.d(TAG, "Updated Note with date from Intent: " + dateFromIntent);
                    }
                    //Include the id from the intent
                    data.putExtra(EXTRA_ID, id);
                } else {
                    //Noe to be updated didn't have a reminder before
                    //Get the date set and its id
                    data.putExtra(EXTRA_ID, id);
                    data.putExtra(EXTRA_DATE, dateToBeSavedNew);
                    data.putExtra(EXTRA_DATE_TEXT, dateToBeSavedText);
                    Log.d(TAG, "Updated Note dateNew with first reminder " + dateToBeSavedNew);
                    Log.d(TAG, "Updated Note date text with first reminder " + dateToBeSavedText);
                }
            } else {
                //Note to be updated has no reminder
                dateToBeSavedNew = null;
                dateToBeSavedText = null;
                Log.d(TAG + " ", "Update without reminder: " + id);
                data.putExtra(EXTRA_DATE, dateToBeSavedNew);
                data.putExtra(EXTRA_DATE_TEXT, dateToBeSavedText);
                data.putExtra(EXTRA_ID, id);
            }
        } else if (editActivityBoolean) {
            //New Note but with a reminder
            Log.d(TAG, "New Note with a reminder: " + editActivityBoolean);
            //Get the selected date
            String dayUpdate = simpleDayFormat.format(date1);
            String timeUpdate = DateFormat.getTimeInstance(DateFormat.SHORT).format(date1);
            dateToBeSavedText = dayUpdate + ", " + timeUpdate;

            data.putExtra(EXTRA_DATE, dateToBeSavedNew);
            data.putExtra(EXTRA_DATE_TEXT, dateToBeSavedText);
            Log.d(TAG + " ", "New Note with a reminder: " + id);
            Log.d(TAG, "Date Object: " + date1);
        }
        Log.d(TAG, "HasReminder: " + editActivityBoolean);
        Log.d(TAG, "Date to be saved: " + dateToBeSavedNew);
        Log.d(TAG, "IID: " + id);

        //If saving was correct
        setResult(RESULT_OK, data);
        finish();
    }

    private void setRemainder() {
        Log.d(TAG, "setReminder called");
        //Check is date or time is set
        if (!timeSelected.equals(SELECT_TIME_TEXT) || !dateSelected.equals(SELECT_DATE_TEXT)) {
            //Set the boolean to true
            editActivityBoolean = true;
            Log.d(TAG, "remainderBoolean: " + editActivityBoolean);
        } else {
            Log.d(TAG, "remainderBoolean: " + editActivityBoolean);
            StyleableToast.makeText(this, "Please select a date or time first", Toast.LENGTH_SHORT, R.style.errorToast).show();
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

    //BottomSheetDialog
    @Override
    public void onButtonClicked(String text) {
        if (text.equals("Save")) {
            saveNote();
        } else if (text.equals("Cancel")) {
            finish();
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
        dateTimeSet = true;
        //Get the selected date and time
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(fCalendar.getTime());
        String currentDateString = DateFormat.getDateInstance().format(fCalendar.getTime());

        //Get the selected day as a string
        Date date1 = fCalendar.getTime();
        Log.d(TAG, "Date Object: " + date1);
        String currentDay = simpleDayFormat.format(date1);//calendar.getTime() returns a Date

        //Update the time and date text holders
        timeSelected = currentTimeString;
        dateSelected = currentDay + ", " + currentDateString;
        Log.d(TAG, "Date selected " + dateSelected);
        Log.d(TAG, "Time selected " + timeSelected);
        //Update the TextViews
        timeTextView.setText(timeSelected);
        dateTextView.setText(dateSelected);

    }
}
