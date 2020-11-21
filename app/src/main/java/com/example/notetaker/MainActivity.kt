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
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.notetaker.adapter.NoteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivity"
    val ITEM_TEXT = "itemText"
    val ITEM_POSITION = "itemPosition"

    val REQUEST_CODE = 42

    private var adapter: NoteAdapter? = null

    private var stringExtra: String? = null

    private var recyclerView: RecyclerView? = null
    private var mDrawer: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private var floatingActionButton: FloatingActionButton? = null
    private var navigationView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.list_recycler_view)
        mDrawer = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        floatingActionButton = findViewById(R.id.add_fab)
        navigationView = findViewById(R.id.nvView)

        toolbar!!.setBackgroundResource(R.color.colorPrimary)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        toggle.isDrawerSlideAnimationEnabled = false
        mDrawer!!.addDrawerListener(toggle)
        toggle.syncState()

        setupDrawerContent(navigationView!!)

        adapter = NoteAdapter(this)
        adapter!!.readFiles()


        recyclerView!!.hasFixedSize()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        (Objects.requireNonNull(recyclerView!!.itemAnimator) as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerView!!.adapter = adapter

        floatingActionButton!!.setOnClickListener { goToNoteActivity() }
        TooltipCompat.setTooltipText(floatingActionButton!!, "New note")
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
        mDrawer!!.closeDrawers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            mDrawer!!.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(tag, "" + requestCode)
        Log.i(tag, "" + resultCode)
        if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == REQUEST_CODE && data != null) {
            stringExtra = data.getStringExtra(ITEM_TEXT)
            val position = data.getIntExtra(ITEM_POSITION, -1)
            Log.i(tag, "Position: $position")
            if (stringExtra != null) {
                Log.i(tag, "String extra: $stringExtra")
                if (!stringExtra.equals("", ignoreCase = true)) {
                    adapter!!.updateList(stringExtra!!, position)
                    recyclerView!!.scrollToPosition(adapter!!.itemCount - 1)
                }
            } else {
                Log.e(tag, "No extra found.")
            }
        }
    }

    private fun goToNoteActivity() {
        val intent = Intent(this@MainActivity, NoteActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)
    }
}