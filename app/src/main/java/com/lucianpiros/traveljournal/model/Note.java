package com.lucianpiros.traveljournal.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Note {
    private String noteTitle;
    private String noteContent;


    public Note() {
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }
}
