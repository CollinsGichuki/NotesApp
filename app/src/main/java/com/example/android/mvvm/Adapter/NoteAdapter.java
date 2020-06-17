package com.example.android.mvvm.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.mvvm.Model.Note;
import com.example.android.mvvm.R;


//We extend ListAdapter.RecyclerView which we will use to compare the lists and get a position(for animations)
//DiffUtil is used for the inbuilt recyclerView animations
public class NoteAdapter extends ListAdapter<Note, NoteAdapter.NoteHolder> {
    private OnCardClickListener fListener;

    public NoteAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Note> DIFF_CALLBACK = new DiffUtil.ItemCallback<Note>() {
        @Override
        public boolean areItemsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            //Know if the items are the same
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Note oldItem, @NonNull Note newItem) {
            //Return true is none of the items have changed(same title,description and priority)
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getDescription().equals(newItem.getDescription());
        }
    };

    @NonNull
    @Override
    //Create the view to populate the recycler view
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_card, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    //Binds the data from the Note object to the Views
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNotes = getItem(position);
        holder.textViewTitle.setText(currentNotes.getTitle());
        holder.textViewDescription.setText(currentNotes.getDescription());
    }


    //Returns the note at a certain position
    public Note getNoteAt(int position) {
        return getItem(position);
    }

    class NoteHolder extends RecyclerView.ViewHolder {
        //Our Views
        private TextView textViewTitle;
        private TextView textViewDescription;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Get the note at a particular position
                    int position = getAdapterPosition();
                    //Check if the listener is called and we have a valid position of the itemView(Card)
                    if (fListener != null && position != RecyclerView.NO_POSITION) {
                        fListener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    //Interface for the card click listener
    public interface OnCardClickListener {
        //Declare a method
        void onItemClick(Note note);
    }

    //Method that contains our OnCardClickListener
    public void setItemClickListener(OnCardClickListener listener) {
        this.fListener = listener;
    }

}
