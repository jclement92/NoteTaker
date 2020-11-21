package com.example.notetaker.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notetaker.NoteActivity
import com.example.notetaker.R
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

class NoteAdapter(private val context: Context) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var currentString: String? = null
    private var pos = 0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.todo_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_row, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = notes!![position]
        holder.tvName.setOnClickListener {
            if (notes!!.size > 0) {
                currentString = notes!![position]
                val intent = Intent(context, NoteActivity::class.java)
                intent.putExtra("itemText", currentString)
                intent.putExtra("itemPosition", position)
                (context as Activity).startActivityForResult(intent, 42)
            }
        }
        holder.tvName.setOnLongClickListener {
            var isDeleted = false
            try {
                Log.i(TAG, "File to delete: " + files!![files!!.size - 1 - position].canonicalFile)
                isDeleted = files!![files!!.size - 1 - position].canonicalFile.delete()
                if (isDeleted) {
                    files!!.removeAt(files!!.size - 1 - position)
                    notes!!.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                    //writeItems();
                    Toast.makeText(holder.tvName.context,
                            "Removed",
                            Toast.LENGTH_SHORT)
                            .show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            Log.i(TAG, "Deleted? $isDeleted")
            isDeleted
        }
    }

    override fun getItemCount(): Int {
        return notes!!.size
    }

    fun updateList(item: String, position: Int) {
        Log.i(TAG, "String extra: $item")
        Log.i(TAG, "Position: $position")
        pos = position
        if (item.isNotEmpty()) {
            if (position == -1) {
                notes!!.add(0, item)
                notifyItemInserted(0)
                notifyItemRangeChanged(0, itemCount)
            } else {
                notes!![position] = item
                notifyItemChanged(position)
            }
            writeItems()
            for (string in notes!!) {
                Log.i(TAG, string)
            }
            Log.i(TAG, "Saved")
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
        }
        //        else {
//            notes.remove(position);
//            notifyDataSetChanged();
//            writeItems();
//        }
    }

    fun readFiles() {
        files = FileUtils.listFiles(context.filesDir, arrayOf("txt"), false) as MutableList<File>
        //List<String> arrayList = new ArrayList<>();
        try {
            notes = ArrayList()
            for (file in files!!) {
                notes!!.add(0, FileUtils.readFileToString(file, Charset.defaultCharset()))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            notes = ArrayList()
        }
    }

    // Returns the file in which the data is stored
    private val dataFile: File
        get() = File(context.filesDir, FilenameUtils.getName(files!![pos].toString()))

    // read the items from the file system
    fun readItems() {
        notes = try {
            // create the array using the content in the file
            ArrayList(FileUtils.readLines(dataFile, Charset.defaultCharset()))
        } catch (e: IOException) {
            // print the error to the console
            e.printStackTrace()
            // just load an empty list
            ArrayList()
        }
    }

    // write the items to the file system
    fun writeItems() {
        try {
            // save the item list as a line-delimited text file
            if (pos == -1) {
                pos = notes!!.size - 1
                val file = File(context.filesDir, "todo" + notes!!.size + ".txt")
                files!!.add(file)
                FileUtils.writeStringToFile(dataFile, notes!![0], Charset.defaultCharset())
            } else {
                FileUtils.writeStringToFile(files!![notes!!.size - 1 - pos], notes!![pos], Charset.defaultCharset())
            }
        } catch (e: IOException) {
            // print the error to the console
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "NoteAdapter"
        var notes: ArrayList<String>? = null
        private var files: MutableList<File>? = null
    }
}