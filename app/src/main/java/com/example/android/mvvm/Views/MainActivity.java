package com.example.android.mvvm.Views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.Observer;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.mvvm.Adapter.NoteAdapter;
import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.Notifications.AlertReceiver;
import com.example.android.mvvm.R;
import com.example.android.mvvm.ViewModel.NoteViewModel;
import com.example.android.mvvm.ViewModel.NoteViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.example.android.mvvm.Repository.NoteRepository.noteId;
import static com.example.android.mvvm.Views.AddEditNoteActivity.EXTRA_DATE_TEXT;
import static com.example.android.mvvm.Views.AddEditNoteActivity.EXTRA_REMINDER_BOOLEAN;
import static com.example.android.mvvm.Views.AddEditNoteActivity.EXTRA_DATE;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "TIME:";
    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static NoteViewModel noteViewModel;
    //For Notifications
    private NotificationManager fNotificationManager;
    private AlarmManager alarmManager;
    private static final String NOTIFICATION_CHANNEL_ID = "primary_channel_id";
    public static final String NOTIFICATION_TITLE_ID = "Notifications_Message";
    public static final String NOTIFICATION_DESC_ID = "Notification_Desc";
    public static final String NOTIFICATION_NOTE_ID = "Notification_NOTE";
    public static final String NOTIFICATION_TIME_ID = "Notification_TIME";
    public static final String NOTIFICATION_TIME_BOOLEAN_ID = "Notification_TIME";

    private Runnable runnable;

    private Calendar calendarDate;
    private String date;
    private String title;
    private String description;
    private boolean reminderBoolean;
    public String dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        fNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Initialize the Calendar object
        calendarDate = Calendar.getInstance();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //If you're sure recycler view size won't change, makes the rv more efficient
        //recyclerView.setHasFixedSize(true);

        final NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);//Since the initial list is empty, we call it in onChanged

        final FloatingActionButton fab = findViewById(R.id.button_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this in onClick points to the OnClickListener
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                //We use StartActivityForResult because we want to receive data from AddEditNoteActivity
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
        //Hide and show the fab according to the scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //dy is > 0 when user scrolls down
                //Hide the fab when user scrolls down
                //Show the fab when user scrolls up
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        /*
        Ask Android to create a ViewModel the first time the system calls an activity's onCreate() method.
        Re-created activities receive the same MyViewModel instance created by the first activity.
        Retrieving the ViewModel
        */
        noteViewModel = new ViewModelProvider(this, new NoteViewModelFactory(this.getApplication())).get(NoteViewModel.class);
        final LiveData<List<Note>> noteList = noteViewModel.getAllNotes();
        //Observe is a LiveData method
        noteList.observe(this, new Observer<List<Note>>() {
            //Triggered when data in the LiveData object changes/ activity is in the foreground or orientation changed
            @Override
            public void onChanged(List<Note> notes) {
                //SubmitList is a method from the Adapter class(sio yangu)
                noteAdapter.submitList(notes);
            }
        });
        //Note variable to store the deleted note
        final Note[] note = {null};

        //Class that makes the recyclerView touchable
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                //Rearrange the items
                int oldPosition = viewHolder.getAdapterPosition();
                int newPosition = target.getAdapterPosition();
                
                noteAdapter.notifyItemMoved(oldPosition, newPosition);
                return true;
            }

            //Swipe Left to delete
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                //Get the position in the adapter of the view we swiped and store it
                //Delete the note
                //When a user clicks the undo action, we reinsert the note
                note[0] = noteAdapter.getNoteAt(viewHolder.getAdapterPosition());
                noteViewModel.delete(note[0]);

                Snackbar snackbar = Snackbar.make(findViewById(R.id.my_coordinator), "Note deleted", Snackbar.LENGTH_SHORT);
                snackbar.setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        noteViewModel.insert(note[0]);
                    }
                });
                snackbar.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.WHITE);
                snackbar.show();
            }

            //Method to display background icon to swipe gestures
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                //Use a library to draw the background icon, color and text
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.colorPrimaryDark))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);


        //Handling the card click
        noteAdapter.setItemClickListener(new NoteAdapter.OnCardClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                //Send the note details
                //Db needs a primary key, the note ID
                intent.putExtra(AddEditNoteActivity.EXTRA_ID, note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE, note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.getDescription());
                intent.putExtra(EXTRA_REMINDER_BOOLEAN, note.isReminderBoolean());
                //note.getReminder() returns a Date Object, we have to turn it to a String
                Date date = note.getReminderDate();
                Log.d("TIME::", "DATE VALUE: " + note.getReminderDate());
                Log.d("TIME::", "ReminderBoolean VALUE: " + note.isReminderBoolean());
                String stringDate;
                if (date != null) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
                    stringDate = simpleDateFormat.format(date);
                } else {
                    stringDate = null;
                }
                intent.putExtra(EXTRA_DATE, stringDate);
                Log.d("TIME::", "DATE String VALUE: " + stringDate);
                Log.d("TIME::", "Note Clicked");
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
                //Animations for the activity transitions
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        //Run in a background thread
        runnable = new Runnable() {
            @Override
            public void run() {
                long id = noteId.getValue();
                int noteIntId = (int) id;
                Log.d("TIME: ", "Background Thread 2 idLong: " + id);
                Log.d("TIME: ", "Background Thread 2 idInt: " + noteIntId);
                Log.d("TIME: ", "Background thread finished");
                //Call createReminder
                createReminder(calendarDate, dateText, noteIntId, reminderBoolean, title, description);
            }
        };

        createNotificationChannel();
    }

    private void createNotificationChannel() {
        fNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //Notification Channel is only in Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Alarm Manager",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Notifications for the WorkManager App");

            fNotificationManager.createNotificationChannel(channel);
            Log.d("TIME: ", "Notification Channel created");
        }
    }

    private void createReminder(Calendar calendar, String date, int id, boolean reminderBoolean, String title, String description) {
        if (date != null) {
            //Intent for the AlertReceiver Class
            Intent intent = new Intent(this.getApplicationContext(), AlertReceiver.class);
            intent.putExtra(NOTIFICATION_TITLE_ID, title);
            intent.putExtra(NOTIFICATION_DESC_ID, description);
            intent.putExtra(NOTIFICATION_TIME_BOOLEAN_ID, reminderBoolean);
            intent.putExtra(NOTIFICATION_TIME_ID, date);//Time to be displayed in the notification
            intent.putExtra(NOTIFICATION_NOTE_ID, id);
            Log.d("TIME: ", "ID sent with Notification: " + id);
            Log.d("TIME: ", "Title sent with Notification: " + title);
            Log.d("TIME: ", "Desc sent with Notification: " + description);
            Log.d("TIME: ", "Boolean sent with Notification: " + reminderBoolean);
            Log.d("TIME: ", "Date sent with Notification: " + date);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            //Set the alarm
            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Find out which request we are handling(requestCode) and if we got a result back(resultCode
        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            //Extract the results sent over for the new note
            title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            reminderBoolean = data.getBooleanExtra(AddEditNoteActivity.EXTRA_REMINDER_BOOLEAN, false);

            //Get the date string
            date = data.getStringExtra(EXTRA_DATE);
            //Get the date Text
            dateText = data.getStringExtra(EXTRA_DATE_TEXT);
            Log.d("TIME: ", "Date String to be stored from AddEditActivity: " + date);
            Log.d("TIME: ", "Date Text to be stored from AddEditActivity: " + dateText);

            Date date1 = new Date();

            //check if there is a date
            if (date != null) {
                Log.d("TIME: ", "Date is not null");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
                try {
                    date1 = (Date) simpleDateFormat.parse(date);
                    Log.d("TIME", "Date value is parsed");
                    calendarDate.setTime(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                date1 = null;
                //calendarDate.setTime(date1);
                date = null;
                Log.d("TIME:: ", "Date is NULL");
            }
            Log.d("TIME", "Date value stored: " + date1);

            //Create a new note
            //Note saves the reminder as a date
            Note note = new Note(title, description, date1, reminderBoolean);
            noteViewModel.insert(note);

            //Start a background thread and read the value of the live data ID value
            Handler handler = new Handler();
            //Execute the runnable after a second
            handler.postDelayed(runnable, 1000);
            //Call the createReminder

            // Reminder uses the Calendar to set the time,
            // note for the note details
            // date to check if it is null

           // Log.d("TIME: ", "Calendar from Saving the note: " + calendarDate);

            StyleableToast.makeText(this, "New Note added", Toast.LENGTH_SHORT, R.style.saveNoteToast).show();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            Log.d("TIME: ", "Note Update");
            assert data != null;
            int idFromEdit = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);
            Log.d("TIME: ", "ID from Edit: " + idFromEdit);

            int id = -2;
            if (idFromEdit != -1) {
                id = idFromEdit;
                Log.d("TIME: ", "Note ID: " + id);
            } else if (id == -1) {
                StyleableToast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT, R.style.noteNotSavedToast).show();
                return;//leave the if statement
            }

            //Extract the results sent over
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            boolean reminderBoolean = data.getBooleanExtra(AddEditNoteActivity.EXTRA_REMINDER_BOOLEAN, false);
            //Set the Boolean value

            //Get the date string
            String dateUpdate = data.getStringExtra(EXTRA_DATE);
            Log.d("TIME:", "Date String to be updated from AddEditActivity: " + dateUpdate);
            String dateUpdateText = data.getStringExtra(EXTRA_DATE_TEXT);
            Log.d(TAG, "Date Text to be updated from AddEditActivity: " + dateUpdateText);

            Date date1 = new Date();
            Calendar calendarDateUpdate = Calendar.getInstance();
            //check if there is a date
            if (dateUpdate != null) {
                Log.d("TIME:", "Date is not null");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'S'");
                try {
                    date1 = (Date) simpleDateFormat.parse(dateUpdate);
                    Log.d("TIME", "Date value is parsed");
                    calendarDateUpdate.setTime(date1);
                    Log.d(TAG, "Calendar value: " + calendarDateUpdate);
                    Log.d(TAG, "Calendar value: " + calendarDateUpdate.getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                date1 = null;
                Log.d("TIME:: ", "Date is NULL");
            }
            Log.d("TIME", "Date value updated: " + date1);

            //Create a new note
            Note note = new Note(title, description, date1, reminderBoolean);
            note.setId(id);
            noteViewModel.update(note);

            // Reminder uses the Calendar to set the time,
            // note for the note details
            // date to check if it is null
            Log.d("TIME: ", "Updated note id: " + id);

            //createReminder(calendarDate, date, id, reminderBoolean);
            createReminder(calendarDateUpdate, dateUpdateText, id, reminderBoolean, title, description);

            StyleableToast.makeText(this, "Note Updated", Toast.LENGTH_SHORT, R.style.saveNoteToast).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                deleteAll();
                return true;
            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deleteAll() {
        //Show an Alert Dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Do you really want to delete all the notes?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                noteViewModel.deleteAllNotes();
                StyleableToast.makeText(getApplicationContext(), "All Notes Deleted", Toast.LENGTH_SHORT, R.style.plainToast).show();
            }
        });
        //Cancel option
        alertDialog.setNegativeButton("lol, no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }
}
