package com.example.android.mvvm.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.Repository.NoteRepository;

import java.util.List;

//AndroidViewModel is a subclass of ViewModel
//ViewModel is an Android Architecture component that is Lifecycle aware
public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private long noteId;

    //Pass the context(Application thru to the repository to instantiate the database
    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }


    /*
    Activity(View) will get the data from the db through this class rather than the Repository class
    The ViewModel gets the data from the Repository class using
    rubber methods for the database operations methods from the Repository class
    */
    public long insert(Note note) {
        noteId = repository.insert(note);
        return noteId;
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public void deleteAllNotes() {
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }
}
