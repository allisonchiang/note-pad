package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    EditText noteTitle;
    EditText noteText;

    Note existingNote = null; // the note to be edited
    String existingTitle;
    String existingNoteText;

    // editingExistingNote == true: we are editing an existing note
    // editingExistingNote == false: we are editing/adding a new note
    boolean editingExistingNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        noteTitle = findViewById(R.id.editNoteTitle);
        noteText = findViewById(R.id.editNoteText);

        Intent intent = getIntent();
        if (intent.hasExtra("ExistingNote")) {
            existingNote = (Note)intent.getSerializableExtra("ExistingNote");
            existingTitle = existingNote.getTitle();
            existingNoteText = existingNote.getNoteText();
            noteTitle.setText(existingTitle);
            noteText.setText(existingNoteText);

            editingExistingNote = true;
        } else {
            noteTitle.setText("");
            noteText.setText("");
            editingExistingNote = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSave:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveNote() {
        Intent data = new Intent(); // Used to hold results data to be returned to original activity
        String noteTitleString = noteTitle.getText().toString();

        if (noteTitleString.isEmpty()) { //simply exit if note without title is saved
            Toast.makeText(this, "The un-titled note was not saved.", Toast.LENGTH_LONG).show();
        } else { // if note has title, save the note
            String noteTextString = noteText.getText().toString();
            long lastUpdateTime = System.currentTimeMillis();

            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, hh:mm aa", Locale.getDefault());
            Date resultdate = new Date(lastUpdateTime);
            String lastUpdateTimeString = sdf.format(resultdate);

            Note newNote = new Note(noteTitleString, noteTextString, lastUpdateTimeString);
            data.putExtra("newNote", newNote);
            setResult(RESULT_OK, data);
//            Toast.makeText(this, "Note saved.", Toast.LENGTH_LONG).show();
        }
        finish(); // This closes the current activity, returning us to the original activity
    }

    // returns true if changes have been made to the note.
    // changes include changes to the title or note text
    public boolean noteChanged() {
        existingTitle = existingNote.getTitle();
        existingNoteText = existingNote.getNoteText();
        boolean titleChanged = !existingTitle.equals(noteTitle.getText().toString());
        boolean noteTextChanged = !existingNoteText.equals(noteText.getText().toString());

        if (titleChanged || noteTextChanged) return true;
            else return false;
    }

    @Override
    public void onBackPressed() {
        if (editingExistingNote) {
            if (noteChanged()) {
                // confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Your note is not saved!\nSave note '" + noteTitle.getText().toString() + "'?");

                builder.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                saveNote();
                            }
                        });

                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // if No selected, do nothing
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                //        super.onBackPressed();
            } else {
                super.onBackPressed();
            }
        } else { // new note
            // confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your note is not saved!\nSave note '" + noteTitle.getText().toString() + "'?");

            builder.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            saveNote();
                        }
                    });

            builder.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // if No selected, do nothing
                            finish();
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
            //        super.onBackPressed();
        }
    }
}