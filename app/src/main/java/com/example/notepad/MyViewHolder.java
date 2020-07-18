package com.example.notepad;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView noteText;
    TextView date;

    MyViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.title);
        noteText = view.findViewById(R.id.noteText);
        date = view.findViewById(R.id.date);
    }
}
