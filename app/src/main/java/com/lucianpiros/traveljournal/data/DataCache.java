package com.lucianpiros.traveljournal.data;

import com.lucianpiros.traveljournal.model.Note;

import java.util.List;

/**
 * Data cache class. Cache data from Fierebase and provides it to Adapter classes
 * Singleton class
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class DataCache {

    private static DataCache dataCache = null;

    private List<Note> notesList;

    private DataCache() {

    }

    public static DataCache getInstance() {
        if (dataCache == null) {
            dataCache = new DataCache();
        }

        return dataCache;
    }

    public List<Note> getNotesList() {
        return notesList;
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
    }

    public Note getNote(int noteIdx) {
        return notesList.get(noteIdx);
    }
}
