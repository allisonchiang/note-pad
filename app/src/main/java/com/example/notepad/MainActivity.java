package com.example.notepad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener, Serializable {

    private static final String TAG = "MainActivity";

    private final List<Note> noteList = new ArrayList<>(); // Main content is here

    private RecyclerView recyclerView; // Layout's recyclerview

    private NotesAdapter nAdapter; // Data to recyclerview adapter

    private Note existingNote = null;

    private static final int CODE_FOR_EDIT_ACTIVITY = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);

        nAdapter = new NotesAdapter(noteList, this);

        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // load JSON file. If no file is found, app should start with no existing notes and no errors
        doRead();
        setTitle();

    }

    // JSON file saving should happen in the onPause method
    @Override
    protected void onPause() {
        doWrite();
        super.onPause();
    }

    public void doWrite() {

        JSONArray jsonArray = new JSONArray();

        for (Note n : noteList) {
            try {
                JSONObject noteJSON = new JSONObject();
                noteJSON.put("titleText", n.getTitle());
                noteJSON.put("contentText", n.getNoteText());
                noteJSON.put("time", n.getLastUpdateTime());
                jsonArray.put(noteJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String jsonText = jsonArray.toString();

        Log.d(TAG, "doWrite: " + jsonText);

        try {
            OutputStreamWriter outputStreamWriter =
                new OutputStreamWriter(
                        openFileOutput("data.json", Context.MODE_PRIVATE)
                );

            outputStreamWriter.write(jsonText);
            outputStreamWriter.close();
//            Toast.makeText(this, "File write success!", Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.d(TAG, "doWrite: File write failed: " + e.toString());
//            Toast.makeText(this, "File write failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void doRead() {

        noteList.clear();
        try {
            InputStream inputStream = openFileInput("data.json");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
//                Toast.makeText(this, "TIME: " + stringBuilder.toString(), Toast.LENGTH_LONG).show();

                String jsonText = stringBuilder.toString();

                try {
                    JSONArray jsonArray = new JSONArray(jsonText);
                    Log.d(TAG, "doRead: " + jsonArray.length());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String title = jsonObject.getString("titleText");
                        String content = jsonObject.getString("contentText");
                        String time = jsonObject.getString("time");
                        Note n = new Note(title, content, time);
                        noteList.add(n);
                    }

                    Log.d(TAG, "doRead: " + noteList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        catch (FileNotFoundException e) {
            Log.d(TAG, "doRead: File not found: \" + e.toString()");
        } catch (IOException e) {
            Log.d(TAG, "doRead: Can not read file: " + e.toString());
        }

    }

    // From OnClickListener
    // onClick, edits the clicked note
    @Override
    public void onClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        existingNote = noteList.get(pos);
        openEditActivity();
    }

    // From OnLongClickListener
    // long click to delete the note
    @Override
    public boolean onLongClick(View v) {
        int pos = recyclerView.getChildLayoutPosition(v);
        existingNote = noteList.get(pos);

        // delete dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Note?");
        builder.setPositiveButton("YES",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Code goes here
                    noteList.remove(existingNote);
                    nAdapter.notifyDataSetChanged();
                    existingNote = null;
                    setTitle();
                }
            });
        builder.setNegativeButton("NO",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAdd:
                openEditActivity();
                return true;
            case R.id.menuInfo:
                openAboutActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openEditActivity() {
        Intent intent = new Intent(this, EditActivity.class);
        if (existingNote != null) {
            intent.putExtra("ExistingNote", existingNote);
        }
        startActivityForResult(intent, CODE_FOR_EDIT_ACTIVITY);
    }

    public void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_FOR_EDIT_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                Note newNote = (Note) data.getSerializableExtra("newNote");
                noteList.add(0, newNote);   //add note to top of list
                noteList.remove(existingNote);
                nAdapter.notifyDataSetChanged();
                setTitle();

                Log.d(TAG, "onActivityResult: New note title: " + newNote.getTitle());
            } else {
                Log.d(TAG, "onActivityResult: result Code: " + resultCode);
            }
        } else {
        }
        existingNote = null;
        doWrite();
    }

    public void setTitle() {
        setTitle("NotePad (" + nAdapter.getItemCount() + ")");
    }

}