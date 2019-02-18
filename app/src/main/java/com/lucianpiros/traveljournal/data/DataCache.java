package com.lucianpiros.traveljournal.data;

import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.model.Note;

import java.util.List;
import java.util.Map;

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
    private Map<String, Note> notesMap;

    private List<Adventure> adventuresList;
    private Map<String, Adventure> adventuresMap;

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

    public Map<String, Note> getNotesMap() {
        return notesMap;
    }

    public void setNotesMap(Map<String, Note> notesMap) {
        this.notesMap = notesMap;
    }

    public Note getNote(int noteIdx) {
        return notesList.get(noteIdx);
    }

    public Note getNote(String key) {
        if(notesMap != null && notesMap.containsKey(key))
            return notesMap.get(key);
        return null;
    }

    public List<Adventure> getAdventuresList() {
        return adventuresList;
    }

    public void setAdventuresList(List<Adventure> adventuresList) {
        this.adventuresList = adventuresList;
    }

    public void setAdventuresMap(Map<String, Adventure> adventuresMap) {
        this.adventuresMap = adventuresMap;
    }

    public Adventure getAdventure(String adventureKey) {
        return adventuresMap.get(adventureKey);
    }

}
