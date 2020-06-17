package com.example.android.mvvm.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.android.mvvm.Model.Note;

import java.util.List;

//Dat Access Object Class
//Interface = no method body
//Good to have a Dao for each entity
@Dao
public interface NoteDao {
    //Methods for all the operations in the Note Table
    @Insert
    Long insert(Note note);
    @Update
    void update(Note note);
    @Delete
    void delete(Note note);
    @Query("DELETE FROM note_table")
    void deleteAllNotes();
    //Room updates LiveData object whenever there are changes to the table
    @Query("SELECT * FROM note_table")
    LiveData<List<Note>> getAllNotes();
}
