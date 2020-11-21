package com.example.notetaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.notetaker.adapter.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";

    public static final int REQUEST_CODE = 42;

    NoteAdapter adapter;

    @Nullable
    String stringExtra;

    RecyclerView recyclerView;
    DrawerLayout mDrawer;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list_recycler_view);
        mDrawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.add_fab);
        navigationView = findViewById(R.id.nvView);

        toolbar.setBackgroundResource(R.color.colorPrimary);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        toggle.setDrawerSlideAnimationEnabled(false);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerContent(navigationView);

        adapter = new NoteAdapter(this);
        adapter.readFiles();

        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) Objects.requireNonNull(recyclerView.getItemAnimator())).setSupportsChangeAnimations(false);
        recyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNoteActivity();
            }
        });
        TooltipCompat.setTooltipText(floatingActionButton, "New note");
    }

    private void setupDrawerContent(NavigationView navigationView) {
        View header = navigationView.getHeaderView(0);
        LinearLayout sideNavLayout = header.findViewById(R.id.sideNavLayout);
        sideNavLayout.setBackgroundResource(R.color.colorPrimary);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        Toast.makeText(this, menuItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
        mDrawer.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawer.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "" + requestCode);
        Log.i(TAG, "" + resultCode);

        if ((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == REQUEST_CODE && data != null) {
            stringExtra = data.getStringExtra(ITEM_TEXT);
            int position = data.getIntExtra(ITEM_POSITION, -1);

            Log.i(TAG, "Position: " + position);
            if (stringExtra != null) {
                Log.i(TAG, "String extra: " + stringExtra);
                if (!stringExtra.equalsIgnoreCase("")) {
                    adapter.updateList(stringExtra, position);

                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            } else {
                Log.e(TAG, "No extra found.");
            }
        }
    }

    private void goToNoteActivity() {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
