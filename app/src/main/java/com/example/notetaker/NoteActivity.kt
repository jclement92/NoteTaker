package com.example.notetaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.notetaker.adapter.NoteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class NoteActivity : AppCompatActivity() {
    val TAG = "AddActivity"

    var position = 0
    var editableText: String? = null
    var adapter: NoteAdapter? = null
    var saved = false
    var backPressed = false
    var stopped = false
    var updatedText: String? = null

    var toolbar: Toolbar? = null
    var editText: EditText? = null
    var floatingActionButton: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        toolbar = findViewById(R.id.toolbar)
        editText = findViewById(R.id.editText)
        floatingActionButton = findViewById(R.id.addText_fab)

        Log.i(TAG, "Created")

        toolbar!!.setBackgroundResource(R.color.colorPrimary)
        setSupportActionBar(toolbar)

        editableText = intent.getStringExtra("itemText")
        updatedText = editableText

        editText!!.setText(editableText)
        editText!!.setSelection(editText!!.text.length)

        position = intent.getIntExtra("itemPosition", -1)
        adapter = NoteAdapter(this)

        if (editableText == null) {
            Objects.requireNonNull(supportActionBar!!).title = "Create Item"
        } else {
            Objects.requireNonNull(supportActionBar!!).title = "Edit Item"
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        floatingActionButton!!.setOnClickListener(View.OnClickListener {
            val text = editText!!.text.toString()
            Log.i(TAG, text)
            if (text.isEmpty()) return@OnClickListener
            editText!!.setText("")
            goToMainActivity(text)
        })
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onBackPressed() {
        backPressed = true
        if (stopped && updatedText != editableText) {
            editableText = editText!!.text.toString()
            goToMainActivity(editableText!!)
        } else {
            updatedText = editableText
            editableText = editText!!.text.toString()
            if (updatedText != editableText) {
                goToMainActivity(editableText!!)
            } else {
                finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i(TAG, "Started")
    }

    override fun onPause() {
        super.onPause()
        Log.i(TAG, "Paused")
    }

    override fun onStop() {
        super.onStop()
        stopped = true
        Log.i(TAG, "Stopped")
        updatedText = editableText
        editableText = editText!!.text.toString()
        Log.i(TAG, "Previous text: $updatedText")
        Log.i(TAG, "Current text: $editableText")
        if (updatedText != editableText && !backPressed) {
            if (editableText != null) {
                adapter!!.updateList(editableText!!, position)
                saved = true
                Log.i(TAG, "Updated.")
            } else {
                Log.i(TAG, "Error: Not updated.")
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "Restarted")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "Resumed")
        Log.i(TAG, "Position: $position")
        if (Objects.requireNonNull(supportActionBar!!).title == "Create Item" && saved) {
            position = 0
            Log.i(TAG, "New position: $position")
            Log.i(TAG, "Note saved: " + NoteAdapter.notes!![position])
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "Destroyed")
    }

    private fun goToMainActivity(text: String) {
        val intent = Intent()
        intent.putExtra("itemText", text)
        intent.putExtra("itemPosition", position)
        setResult(RESULT_OK, intent)
        finish()
    }

}