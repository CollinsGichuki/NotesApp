package com.example.android.mvvm.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.Repository.NoteRepository;

import java.util.List;

import static com.example.android.mvvm.Repository.NoteRepository.noteId;

//AndroidViewModel is a subclass of ViewModel
//ViewModel is an Android Architecture component that is Lifecycle aware
public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;

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
    public void insert(Note note) {
         repository.insert(note);

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

    public LiveData<Note> getNoteWithId(int id){
        return repository.getNoteWithId(id);
    }
}
