package com.example.notetaker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.notetaker.adapter.NoteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"

        const val ITEM_TEXT = "itemText"
        const val ITEM_POSITION = "itemPosition"
        const val REQUEST_CODE = 42
    }

    private lateinit var noteAdapter: NoteAdapter

    private lateinit var stringExtra: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var mDrawer: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var floatingActionButton: FloatingActionButton
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.list_recycler_view)
        mDrawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        floatingActionButton = findViewById(R.id.add_fab)
        navigationView = findViewById(R.id.nvView)

        toolbar.setBackgroundResource(R.color.colorPrimary)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        toggle.apply {
            isDrawerSlideAnimationEnabled = false
            mDrawer.addDrawerListener(toggle)
            syncState()
        }
//        toggle.isDrawerSlideAnimationEnabled = false
//        mDrawer.addDrawerListener(toggle)
//        toggle.syncState()

        setupDrawerContent(navigationView)

        noteAdapter = NoteAdapter(this)
        noteAdapter.readFiles()

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        floatingActionButton.setOnClickListener { goToNoteActivity() }

    }

    private fun setupDrawerContent(navigationView: NavigationView) {
        val header = navigationView.getHeaderView(0)
        val sideNavLayout = header.findViewById<LinearLayout>(R.id.sideNavLayout)
        sideNavLayout.setBackgroundResource(R.color.colorPrimary)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            selectDrawerItem(menuItem)
            true
        }
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        menuItem.isChecked = true
        Toast.makeText(this, menuItem.title.toString() + " selected", Toast.LENGTH_SHORT).show()
        mDrawer.closeDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "" + requestCode)
        Log.i(TAG, "" + resultCode)

        if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == requestCode && data != null) {
            stringExtra = data.getStringExtra(ITEM_TEXT).toString()
            val position = data.getIntExtra(ITEM_POSITION, -1)

            Log.i(TAG, "Position: $position")
            Log.i(TAG, "String extra: $stringExtra")

            if (!stringExtra.equals("", ignoreCase = true)) {
                noteAdapter.updateList(stringExtra, position)
                recyclerView.scrollToPosition(noteAdapter.itemCount - 1)
            }
        }
    }

    private fun goToNoteActivity() {
        val intent = Intent(this@MainActivity, NoteActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }
}