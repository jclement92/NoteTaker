package com.example.notetaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.notetaker.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import static com.example.notetaker.MainActivity.ITEM_POSITION;
import static com.example.notetaker.MainActivity.ITEM_TEXT;

public class NoteActivity extends AppCompatActivity {

    public static final String TAG = "AddActivity";

    int position;
    @Nullable String editableText;
    NoteAdapter adapter;
    boolean saved = false;
    boolean backPressed = false;
    boolean stopped = false;
    String updatedText;

    Toolbar toolbar;
    EditText editText;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.editText);
        floatingActionButton = findViewById(R.id.addText_fab);

        Log.i(TAG,"Created");

        toolbar.setBackgroundResource(R.color.colorPrimary);
        setSupportActionBar(toolbar);

        editableText = getIntent().getStringExtra(ITEM_TEXT);
        updatedText = editableText;

        editText.setText(editableText);
        editText.setSelection(editText.getText().length());

        position = getIntent().getIntExtra(ITEM_POSITION,-1);
        adapter = new NoteAdapter(this);

        if(editableText == null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Create Item");
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Edit Item");
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();

                Log.i(TAG, text);
                if (text.isEmpty()) return;
                editText.setText("");

                goToMainActivity(text);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public void onBackPressed() {
        backPressed = true;

        if(stopped && !Objects.equals(updatedText, editableText)) {
            editableText = editText.getText().toString();
            goToMainActivity(editableText);
        } else {
            updatedText = editableText;
            editableText = editText.getText().toString();

            if(!Objects.equals(updatedText, editableText)) {
                goToMainActivity(editableText);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"Started");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"Paused");
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopped = true;
        Log.i(TAG,"Stopped");

        updatedText = editableText;
        editableText = editText.getText().toString();

        Log.i(TAG, "Previous text: " + updatedText);
        Log.i(TAG, "Current text: " + editableText);

        if(!Objects.equals(updatedText, editableText) && !backPressed) {
            if (editableText != null) {
                adapter.updateList(editableText, position);
                saved = true;
                Log.i(TAG, "Updated.");
            } else {
                Log.i(TAG, "Error: Not updated.");
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"Restarted");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"Resumed");
        Log.i(TAG,"Position: " + position);

        if((Objects.equals(Objects.requireNonNull(getSupportActionBar()).getTitle(), "Create Item")) && saved) {
            position = 0;
            Log.i(TAG,"New position: " + position);
            Log.i(TAG, "Note saved: " + NoteAdapter.notes.get(position));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"Destroyed");
    }

    private void goToMainActivity(String text) {
        Intent intent = new Intent();

        intent.putExtra(ITEM_TEXT, text);
        intent.putExtra(ITEM_POSITION, position);

        setResult(RESULT_OK, intent);
        finish();
    }
}
