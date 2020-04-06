package com.example.notetaker.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notetaker.NoteActivity;
import com.example.notetaker.MainActivity;
import com.example.notetaker.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = "Adapter";
    private ArrayList<String> notes;
    private Context context;
    private String currentString;

    public NoteAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.tvName.setText(notes.get(position));
        holder.tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentString = notes.get(position);
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(MainActivity.ITEM_TEXT, currentString);
                intent.putExtra(MainActivity.ITEM_POSITION, position);

                ((Activity)context).startActivityForResult(intent, MainActivity.REQUEST_CODE);
            }
        });

        holder.tvName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                notes.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                writeItems();
                Toast.makeText(holder.tvName.getContext(),
                        "Removed",
                        Toast.LENGTH_SHORT)
                        .show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void updateList(@NonNull String item, int position) {
        Log.i(TAG, "String extra: " + item);
        Log.i(TAG, "Position: " + position);

        if(!item.isEmpty()) {

            if(position == -1) {
                notes.add(0, item);
                notifyItemInserted(0);
                notifyItemRangeChanged(0, getItemCount());
            } else {
                notes.set(position, item);
                notifyItemChanged(position);
            }

            writeItems();

            Log.i(TAG,"Saved");
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        }
//        else {
//            notes.remove(position);
//            notifyDataSetChanged();
//            writeItems();
//        }
    }

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), "todo.txt");
    }

    // read the items from the file system
    public void readItems() {
        try {
            // create the array using the content in the file
            notes = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
            // just load an empty list
            notes = new ArrayList<>();
        }
    }

    // write the items to the file system
    private void writeItems() {
        try {
            // save the item list as a line-delimited text file
            FileUtils.writeLines(getDataFile(), notes);
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.todo_item) TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
