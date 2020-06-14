package com.example.android.mvvm.Repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.Model.NoteDao;
import com.example.android.mvvm.Model.NoteDatabase;
import com.example.android.mvvm.Utils.NoteMvvmUtils;

import java.util.List;

//The Repository Class provides an abstraction for the raw data to the rest of the App
//The class gets the data from the db then the ViewModel gets the data from it
public class NoteRepository {
    private NoteDao noteDao;
    private NoteDatabase fDatabase;
    private LiveData<List<Note>> allNotes;

    //The ViewModel requires Application so we include it here too
    public NoteRepository(Application application){
        //Application is a subclass of context thus can be used to the Db instance
        fDatabase = NoteDatabase.getInstance(application);
        //Since we created an instance of the db using Builder,
        //Room created the method body for the abstract method noteDao hence we can call noteDao
        noteDao = fDatabase.noteDao();
        //Assign all notes from the NoteDao method getAllNotes
        allNotes = noteDao.getAllNotes();
    }

    //Wrapper methods for all the database operations
    //The ViewModel interacts with these methods
    public void insert(Note note){
        //Instance of the AsyncTask to execute the method
        new InsertNotesAsyncTask(noteDao).execute(note);
    }
    public void update(Note note){
        new UpdateNotesAsyncTask(noteDao).execute(note);
    }
    public void delete(Note note){
        new DeleteNotesAsyncTask(noteDao).execute(note);
    }
    public void deleteAllNotes(){
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        //Room retrieves the data from noteDao for us
        return allNotes;
    }

    //We use AsyncTask for the methods to be executed in the background thread
    private static class InsertNotesAsyncTask extends AsyncTask<Note, Void,Void>{
        private NoteDao noteDao;
        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private InsertNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            //Access the first note
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNotesAsyncTask extends AsyncTask<Note, Void,Void>{
        private NoteDao noteDao;
        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private UpdateNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            //Access the first note
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNotesAsyncTask extends AsyncTask<Note, Void,Void>{
        private NoteDao noteDao;
        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private DeleteNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            //Access the first note
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void,Void>{
        private NoteDao noteDao;
        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private DeleteAllNotesAsyncTask(NoteDao noteDao){
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }


}
