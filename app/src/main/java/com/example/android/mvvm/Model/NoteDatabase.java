package com.example.android.mvvm.Model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.android.mvvm.Utils.NoteMvvmUtils;

//The Database Class
//Version Number is used when making changes to the database
@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    //Make the class to be a singleton/Can't be able to make multiple instances of the class
    //This instance will then be used across the app which can be accessed thu the static property
    private static NoteDatabase instance;
    //Abstract method = no body
    //It is used to access our Dao
    public abstract NoteDao noteDao();
    //The Database
    //Synchronized = only one thread can access the method at a time so as not to create multiple instances of the db
    public static synchronized NoteDatabase getInstance(final Context context){
        if (instance == null){
            //This returns an instance of the db
            //roomCallback inserts data to the db when first created
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    /*
    To create the db with data in it(for testing purposes), we access the onCreate method
    We populate the database using AsyncTask
    */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            //Create the db wit data in it
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    /*
     <<<<<<<<<THIS IS FOR TESTING WHETHER THE DB WORKS>>>>>>>>>>
    */
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void>{
        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db){
            noteDao = db.noteDao();
        }
        //Comment/ delete this out, if you don't want to have data pre inserted in the db
        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1", "Description 1", 1));
            noteDao.insert(new Note("Title 2", "Description 2", 2));
            noteDao.insert(new Note("Title 3", "Description 3", 3));
            noteDao.insert(new Note("Title 4", "Description 4", 4));
            return null;
        }
    }
}
