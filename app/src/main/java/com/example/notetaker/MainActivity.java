package com.example.notetaker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String ITEM_TEXT = "itemText";
    public static final String ITEM_POSITION = "itemPosition";

    public static final int REQUEST_CODE = 42;

    NoteAdapter adapter;

    @Nullable String stringExtra;

    @BindView(R.id.list_recycler_view) RecyclerView recyclerView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.add_fab) FloatingActionButton floatingActionButton;
    @BindView(R.id.nvView) NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        setupDrawerContent(navigationView);

        adapter = new NoteAdapter(this);
        adapter.readItems();

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
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
            case R.id.nav_second_fragment:
            case R.id.nav_third_fragment:
            case R.id.nav_fourth_fragment:
            default:
                break;
        }

        menuItem.setChecked(true);
        Toast.makeText(this, menuItem.getTitle() + " selected", Toast.LENGTH_SHORT).show();
        mDrawer.closeDrawers();
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

        if((resultCode == RESULT_OK || resultCode == RESULT_CANCELED) && requestCode == REQUEST_CODE && data != null) {
            stringExtra = data.getStringExtra(ITEM_TEXT);
            int position = data.getIntExtra(ITEM_POSITION,-1);

            Log.i(TAG,"Position: " + position);
            if(stringExtra != null) {
                Log.i(TAG, "String extra: " + stringExtra);
                if(!stringExtra.equalsIgnoreCase("")) {
                    adapter.updateList(stringExtra, position);

                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
                }
            } else {
                Log.e(TAG, "No extra found.");
            }

            // TODO #1: Handle empty output (Done)
            // TODO #2: Handle Back button clicks
        }
    }

    private void goToNoteActivity() {
        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

}
