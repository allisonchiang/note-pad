package com.example.notepad;

import androidx.annotation.NonNull;
import java.io.Serializable;

public class Note implements  Serializable{
    private String title;
    private String noteText;
    private String lastUpdateTime;

    Note(String title, String noteText, String lastUpdateTime) {
        this.title = title;
        this.noteText = noteText;
        this.lastUpdateTime = lastUpdateTime;
    }


    public String getTitle() {
        return title;
    }

    public String getNoteText() {
        return noteText;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    @NonNull
    @Override
    public String toString() {
        return title + " (" + noteText+ "), " + lastUpdateTime;
    }
}
