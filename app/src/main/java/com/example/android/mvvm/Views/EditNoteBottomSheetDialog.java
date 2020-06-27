package com.example.android.mvvm.Views;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.mvvm.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditNoteBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener fListener;

    //Override onCreate with our bottom sheet dialog view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.note_bottom_sheet_layout, container, false);

        Button cancelBtn = v.findViewById(R.id.cancel_edit_btn);
        Button saveBtn = v.findViewById(R.id.save_edit_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tell AddEditNoteActivity which btn is clicked
                fListener.onButtonClicked("Cancel");
                dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tell AddEditNoteActivity which btn is clicked
                fListener.onButtonClicked("Save");
                dismiss();
            }
        });

        return v;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //We will use this to assign the listener to the activity where we want to(context)
        try {
            fListener = (BottomSheetListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
            + " must implement BottomSheetListener");
        }
    }
}
