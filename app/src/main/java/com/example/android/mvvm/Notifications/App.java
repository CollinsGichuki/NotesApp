package com.example.android.mvvm.Notifications;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.android.mvvm.R;

//Wraps the whole app with activities and services
public class App extends Application {
    public static final String CHANNEL_1_ID = "channel1";

    //Called on the start of the app not whenever an activity is created
    @Override
    public void onCreate() {
        super.onCreate();
        createChannel();
    }

    private void createChannel() {
        //Check if we are android oreo or higher, notifications class isn't available on lower apis
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //<<<<<<<<<<<<Channel 1>>>>>>>>>>>>>>>>
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,//Channel ID
                    "Reminder",//Name for the channel, visible to the user
                    NotificationManager.IMPORTANCE_HIGH//Importance of the channel
            );
            channel1.setDescription("Contains reminder notification from the Notes App");

            //Create the channels
            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channel1);
        }
    }
}
