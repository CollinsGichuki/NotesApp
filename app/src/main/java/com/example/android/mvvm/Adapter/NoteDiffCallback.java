package com.example.android.mvvm.Adapter;

import androidx.recyclerview.widget.DiffUtil;

import com.example.android.mvvm.Model.Note;

import java.util.List;

public class NoteDiffCallback extends DiffUtil.Callback {

    private final List<Note> oldNotesList;
    private final List<Note> newNotesList;

    public NoteDiffCallback(List<Note> oldNotesList, List<Note> newNotesList) {
        this.oldNotesList = oldNotesList;
        this.newNotesList = newNotesList;

    }

    @Override
    public int getOldListSize() {
        return oldNotesList.size();
    }

    @Override
    public int getNewListSize() {
        return newNotesList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldNotesList.get(oldItemPosition).getId() == newNotesList.get(newItemPosition).getId()
                && oldNotesList.get(oldItemPosition) == newNotesList.get(newItemPosition);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Note oldNote = oldNotesList.get(oldItemPosition);
        Note newNote = newNotesList.get(newItemPosition);

        return oldNote.getCategory().equals(newNote.getCategory()) && oldNote.getTitle().equals(newNote.getTitle()) &&
                oldNote.getDescription().equals(newNote.getDescription()) && oldNote.isReminderBoolean() == newNote.isReminderBoolean();
    }
}
