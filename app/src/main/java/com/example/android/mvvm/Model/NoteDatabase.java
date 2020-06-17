package com.example.android.mvvm.Model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//The Database Class
//Version Number is used when making changes to the database
@Database(entities = Note.class, version = 2, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class NoteDatabase extends RoomDatabase {
    //Make the class to be a singleton/Can't be able to make multiple instances of the class
    //This instance will then be used across the app which can be accessed thu the static property
    private static NoteDatabase instance;

    //Abstract method = no body
    //It is used to access our Dao
    public abstract NoteDao noteDao();

    //The Database
    //Synchronized = only one thread can access the method at a time so as not to create multiple instances of the db
    public static synchronized NoteDatabase getInstance(final Context context) {
        if (instance == null) {
            //This returns an instance of the db
            //roomCallback inserts data to the db when first created
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .addMigrations(MIGRATION_1_2)
                   // .fallbackToDestructiveMigration()
                  //  .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    //Migration from Version 1 to 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //Add the new columns
            database.execSQL("ALTER TABLE note_table"
                    + " ADD COLUMN reminderDate INTEGER");
            database.execSQL("ALTER TABLE note_table"
                    + " ADD COLUMN reminderBoolean INTEGER");
            //Deleted one column and added two other columns
            //Create a back up for the previous table
            database.execSQL ("CREATE TABLE new_note_table(id INTEGER PRIMARY KEY NOT NULL, " +
                    "title TEXT, description TEXT, reminderDate INTEGER, reminderBoolean INTEGER)");
            //Copy the data from prev version
            database.execSQL ("INSERT INTO new_note_table(id, title, description, reminderDate, reminderBoolean) " +
                    "SELECT id, title, description, reminderDate, reminderBoolean FROM note_table");
            //Remove the old table
            database.execSQL ("DROP TABLE note_table");
            //Change the name to the correct one
            database.execSQL("ALTER TABLE new_note_table RENAME TO note_table");
        }
    };

    /*
    To create the db with data in it(for testing purposes), we access the onCreate method
    We populate the database using AsyncTask
    */
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
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
    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        private PopulateDbAsyncTask(NoteDatabase db) {
            noteDao = db.noteDao();
        }

        //Comment/ delete this out, if you don't want to have data pre inserted in the db
        @Override
        protected Void doInBackground(Void... voids) {
//            noteDao.insert(new Note("Title 1", "Description 1", null, false));
//            noteDao.insert(new Note("Title 2", "Description 2", null, false));
//            noteDao.insert(new Note("Title 3", "Description 3", null, false));
//            noteDao.insert(new Note("Title 4", "Description 4", null, false));
            return null;
        }
    }
}
