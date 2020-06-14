package com.example.android.mvvm.Views;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.DialogFragment;

import com.example.android.mvvm.AlertReceiver;
import com.example.android.mvvm.R;
import com.example.android.mvvm.Reminder.DatePickerFragment;
import com.example.android.mvvm.Reminder.TimePickerFragment;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.util.Calendar;

import static com.example.android.mvvm.Notifications.App.CHANNEL_1_ID;

public class AddEditNoteActivity extends AppCompatActivity implements EditNoteBottomSheetDialog.BottomSheetListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final String EXTRA_ID = "com.example.android.mvvm.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.android.mvvm.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "com.example.android.mvvm.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY = "com.example.android.mvvm.EXTRA_PRIORITY";
    //For the notifications
    private NotificationManagerCompat fNotificationManagerCompat;

    private EditText titleEditText;
    private EditText descriptionEditText;
    private NumberPicker numberPickerPriority;

    private ImageView imageView;
    //Holds the date and time set
    String dateSelected = "";
    String timeSelected = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        //Adding the Close to the action bar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        fNotificationManagerCompat = NotificationManagerCompat.from(this);


        titleEditText = findViewById(R.id.edit_text_title);
        descriptionEditText = findViewById(R.id.edit_text_description);
        numberPickerPriority = findViewById(R.id.number_picker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        Calendar globalCalendar = Calendar.getInstance();

        //The reminder buttons
        Button pickDate = findViewById(R.id.pick_date);
        Button pickTime = findViewById(R.id.pick_time);

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


        //Notification icon
        imageView = findViewById(R.id.notification_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRemainder();
            }
        });


        //Setting the title of the action bar
        //Get the id of the note from the intent that started this activity if we are editing the note
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            //Populate the view accordingly
            setTitle("Edit Note");
            titleEditText.setText(intent.getStringExtra(EXTRA_TITLE));
            descriptionEditText.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY, 1));
        } else {
            setTitle("Add Note");
        }

    }//End of onCreate


    private void saveNote() {
        //Get the values from the views
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();
        int priority = numberPickerPriority.getValue();

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
        data.putExtra(EXTRA_PRIORITY, priority);

        //If we are saving a note that we have edited
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        //-1 is an invalid id
        if (id != -1){
            //Include the id as the one for the original unedited note
            data.putExtra(EXTRA_ID, id);
        }

        //If saving was correct
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //If we are saving a note that we have edited
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        //Check if it has an id
        if (id != -1){
            EditNoteBottomSheetDialog bottomSheetDialog = new EditNoteBottomSheetDialog();
            bottomSheetDialog.show(getSupportFragmentManager(), "Bottom Sheet");
        } else {
            //Check if the fields are empty
            if (!checkIfIsEmpty()){
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
        if (text.equals("Save")){
            saveNote();
        } else if (text.equals("Cancel")){
            finish();
        }
    }
    //Returns the time set in the dialog
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        TextView timeTextView = findViewById(R.id.reminder_time_textView);
        //Add the time to our global var
        timeSelected = currentTimeString;
        timeTextView.setText(currentTimeString);



    }
    //Returns the date set in the dialog
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());
        TextView dateTextView = findViewById(R.id.reminder_date_textView);
        //Add the date to the global var
        dateSelected = currentDateString;
        dateTextView.setText(currentDateString);

    }


    private void setRemainder() {
        String notificationText = timeSelected + " " + dateSelected;
        //Get the values from the views
        String title = titleEditText.getText().toString();
        String description = descriptionEditText.getText().toString();

        //Check is date or time is set
        if (!timeSelected.equals("") || !dateSelected.equals("")){
            //Set the remainder(alarm and notification)
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            //Initialize alarm
           // alarmManager.setExact(AlarmManager.RTC_WAKEUP, );

            //Open AddEditActivity when notification is clicked
            Intent activityIntent = new Intent(this, AddEditNoteActivity.class);
            //Use a pending intent that wraps the intent to enable the NotificationManagerCompat to execute the intent
            //requestCode is used to update or cancel the intent
            PendingIntent contentIntent = PendingIntent.getActivity(this,
                    0,activityIntent, 0);

            Notification notificationI = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.ic_subject)
                    .setContentTitle("Reminder: " + title )
                    .setContentText(description)
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setAutoCancel(true)//Dismiss the notification when tapped
                    .build();

            //Show the notification
            fNotificationManagerCompat.notify(1, notificationI);
        }else {
            StyleableToast.makeText(this, "Please select a date or time first", Toast.LENGTH_SHORT, R.style.plainToast).show();
        }
    }

}
