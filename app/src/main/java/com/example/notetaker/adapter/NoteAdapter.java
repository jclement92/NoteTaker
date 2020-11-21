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

import com.example.notetaker.MainActivity;
import com.example.notetaker.NoteActivity;
import com.example.notetaker.R;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static final String TAG = "NoteAdapter";
    public static ArrayList<String> notes;
    private final Context context;
    private String currentString;
    private int pos;
    private static List<File> files;

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.todo_item);
        }
    }

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
                if (notes.size() > 0) {
                    currentString = notes.get(position);
                    Intent intent = new Intent(context, NoteActivity.class);
                    intent.putExtra(MainActivity.ITEM_TEXT, currentString);
                    intent.putExtra(MainActivity.ITEM_POSITION, position);

                    ((Activity) context).startActivityForResult(intent, MainActivity.REQUEST_CODE);
                }
            }
        });

        holder.tvName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                boolean isDeleted = false;
                try {
                    Log.i(TAG, "File to delete: " + files.get(files.size() - 1 - position).getCanonicalFile());
                    isDeleted = files.get(files.size() - 1 - position).getCanonicalFile().delete();
                    if (isDeleted) {
                        files.remove(files.size() - 1 - position);
                        notes.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        //writeItems();
                        Toast.makeText(holder.tvName.getContext(),
                                "Removed",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "Deleted? " + isDeleted);
                return isDeleted;
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
        pos = position;

        if (!item.isEmpty()) {
            if (position == -1) {
                notes.add(0, item);
                notifyItemInserted(0);
                notifyItemRangeChanged(0, getItemCount());
            } else {
                notes.set(position, item);
                notifyItemChanged(position);
            }

            writeItems();

            for (String string : notes) {
                Log.i(TAG, string);
            }

            Log.i(TAG, "Saved");
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        }
//        else {
//            notes.remove(position);
//            notifyDataSetChanged();
//            writeItems();
//        }
    }

    public void readFiles() {
        files = (List<File>) FileUtils.listFiles(context.getFilesDir(), new String[]{"txt"}, false);
        //List<String> arrayList = new ArrayList<>();

        try {
            notes = new ArrayList<>();
            for (File file : files) {
                notes.add(0, FileUtils.readFileToString(file, Charset.defaultCharset()));
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
            notes = new ArrayList<>();
        }
    }

    // Returns the file in which the data is stored
    private File getDataFile() {
        return new File(context.getFilesDir(), FilenameUtils.getName(files.get(pos).toString()));
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
    public void writeItems() {
        try {
            // save the item list as a line-delimited text file
            if (pos == -1) {
                pos = notes.size() - 1;
                File file = new File(context.getFilesDir(), "todo" + notes.size() + ".txt");
                files.add(file);
                FileUtils.writeStringToFile(getDataFile(), notes.get(0), Charset.defaultCharset());
            } else {
                FileUtils.writeStringToFile(files.get(notes.size() - 1 - pos), notes.get(pos), Charset.defaultCharset());
            }
        } catch (IOException e) {
            // print the error to the console
            e.printStackTrace();
        }
    }

}
