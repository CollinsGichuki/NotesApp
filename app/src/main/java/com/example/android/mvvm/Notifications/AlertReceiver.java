package com.example.android.mvvm.Notifications;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.R;
import com.example.android.mvvm.ViewModel.NoteViewModel;
import com.example.android.mvvm.ViewModel.NoteViewModelFactory;
import com.example.android.mvvm.Views.AddEditNoteActivity;
import com.example.android.mvvm.Views.MainActivity;

import java.util.Calendar;

import static com.example.android.mvvm.Views.AddEditNoteActivity.EXTRA_DATE;
import static com.example.android.mvvm.Views.MainActivity.NOTIFICATION_DESC_ID;
import static com.example.android.mvvm.Views.MainActivity.NOTIFICATION_NOTE_ID;
import static com.example.android.mvvm.Views.MainActivity.NOTIFICATION_TIME_BOOLEAN_ID;
import static com.example.android.mvvm.Views.MainActivity.NOTIFICATION_TIME_ID;
import static com.example.android.mvvm.Views.MainActivity.NOTIFICATION_TITLE_ID;
import static com.example.android.mvvm.Views.MainActivity.noteViewModel;

public class AlertReceiver extends BroadcastReceiver {
    private static final String NOTIFICATION_CHANNEL_ID = "primary_channel_id";
    private static final int NOTIFICATION_ID = 2;
    private NotificationManager fNotificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        fNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Log.d("TIME: ", "Alert received at " + Calendar.getInstance().getTime());
        String title = intent.getStringExtra(NOTIFICATION_TITLE_ID);
        String desc = intent.getStringExtra(NOTIFICATION_DESC_ID);
       // String time = intent.getStringExtra(NOTIFICATION_TIME_ID);
        String time = Calendar.getInstance().getTime().toString();
        int id = intent.getIntExtra(NOTIFICATION_NOTE_ID, -1);
        boolean reminderBoolean = intent.getBooleanExtra(NOTIFICATION_TIME_BOOLEAN_ID, false);
        //Update the Note
        Note note = new Note(title, desc, null, false);
        note.setId(id);
        noteViewModel.update(note);
        Log.d("TIME: ", "Note updated");
        Log.d("TIME: ", "reminderBoolean received: " + reminderBoolean);
        deliverNotification(context, title, desc, time, id);
    }

    private void deliverNotification(Context context, String title, String desc, String time, int id) {
        String notificationTitle = "Reminder: " + title;
        String notificationBigText = desc + "\n" + "\n" + time;
        //Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_subject)
                .setContentTitle(notificationTitle)
                .setContentText(desc)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBigText))
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setAutoCancel(true)//Dismiss the notification when tapped
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM);
        //Launch MainActivity when notification is clicked
        Intent editActivityIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, editActivityIntent, 0);
        builder.setContentIntent(resultPendingIntent);

        //Display the notification
        fNotificationManager.notify(NOTIFICATION_ID, builder.build());
        Calendar calendar = Calendar.getInstance();
        Log.d("TIME:", "Notification created at " + calendar.getTime());
        Log.d("TIME:", "Date String Notification created with " + time);
    }
}
