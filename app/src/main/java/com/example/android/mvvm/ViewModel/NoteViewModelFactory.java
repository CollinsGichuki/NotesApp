package com.example.android.mvvm.ViewModel;


import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class NoteViewModelFactory implements ViewModelProvider.Factory {
    private Application fApplication;

    public NoteViewModelFactory(Application application) {
        fApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NoteViewModel(fApplication);
    }
}
