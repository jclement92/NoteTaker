package com.example.notetaker.model

class Note {
    private var note: String? = null

    fun Note(note: String?) {
        this.note = note
    }

    fun getNote(): String? {
        return note
    }

    fun setNote(note: String?) {
        this.note = note
    }
}