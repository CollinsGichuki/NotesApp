package com.example.android.mvvm.Repository;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.Model.NoteDao;
import com.example.android.mvvm.Model.NoteDatabase;

import java.util.Arrays;
import java.util.List;

//The Repository Class provides an abstraction for the raw data to the rest of the App
//The class gets the data from the db then the ViewModel gets the data from it
public class NoteRepository {
    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;
    public static MutableLiveData<Long> noteId;

    //The ViewModel requires Application so we include it here too
    public NoteRepository(Application application) {
        //Application is a subclass of context thus can be used to the Db instance
        NoteDatabase database = NoteDatabase.getInstance(application);
        //Since we created an instance of the db using Builder,
        //Room created the method body for the abstract method noteDao hence we can call noteDao
        noteDao = database.noteDao();
        //Assign all notes from the NoteDao method getAllNotes
        allNotes = noteDao.getAllNotes();
        noteId = new MutableLiveData<>();
    }

    //Wrapper methods for all the database operations
    //The ViewModel interacts with these methods
    public void insert(Note note) {
        //Instance of the AsyncTask to execute the method
        new InsertNotesAsyncTask(noteDao, new InsertNotesAsyncTask.AsyncResponse() {
            @Override
            public void processFinish(long noteIDD) {
                noteId.postValue(noteIDD);
                Log.d("TIME: ", "Inserted Note_IDD: " + noteIDD);
            }
        }).execute(note);
    }

    //Get Note with a certain ID
    public LiveData<Note> getNoteWithId(int id){
        return noteDao.getNoteAtId(id);
    }

    public void update(Note note) {
        new UpdateNotesAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNotesAsyncTask(noteDao).execute(note);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(noteDao).execute();
    }

    public LiveData<List<Note>> getAllNotes() {
        //Room retrieves the data from noteDao for us
        return allNotes;
    }

    //We use AsyncTask for the methods to be executed in the background thread
    private static class InsertNotesAsyncTask extends AsyncTask<Note, Void, Long> {
        private NoteDao noteDao;

        public interface AsyncResponse {
            void processFinish(long noteIDD);
        }
        public AsyncResponse delegate;

        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private InsertNotesAsyncTask(NoteDao noteDao, AsyncResponse delegate) {
            this.noteDao = noteDao;
            this.delegate = delegate;
        }

        @Override
        protected Long doInBackground(Note... notes) {
            //Access the first note
            return noteDao.insert(notes[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            delegate.processFinish(aLong);
        }
    }

    private static class UpdateNotesAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private UpdateNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            //Access the first note
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNotesAsyncTask extends AsyncTask<Note, Void, Void> {
        private NoteDao noteDao;

        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private DeleteNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            //Access the first note
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        //Since the class is static and we can't directly access NoteDao, we use the constructor
        private DeleteAllNotesAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }


}
