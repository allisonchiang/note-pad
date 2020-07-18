package com.example.notepad;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "NotesAdapter";
    private List<Note> noteList;
    private MainActivity mainAct;

    NotesAdapter(List<Note> noteList, MainActivity mainAct) {
        this.noteList = noteList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Making new MyViewHolder");

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_row, parent, false);

        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Filling viewHolder employee " + position);

        Note note = noteList.get(position);
        String noteText = note.getNoteText();
        if (noteText.length() > 80) {
            noteText = noteText.substring(0, 79) + "...";
        }

        holder.title.setText(note.getTitle());
        holder.noteText.setText(noteText);
        holder.date.setText(note.getLastUpdateTime());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
}
