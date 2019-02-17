package com.lucianpiros.traveljournal.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Adventure class. Represent an adventure. An adventure is a collection of notes
 * Stores adventure title, description and a list of key notes.
 * Also stores key created on Firebase Realtime Database. This information is stored
 * to easily update adventure on Firebase
 *
 * @author Lucian Piros
 * @version 1.0
 */
@IgnoreExtraProperties
public class Adventure {
    private String adventureKey;
    private String adventureTitle;
    private String adventureContent;
    List<String> noteKeysList;

    public Adventure() {
        adventureKey = null;
        adventureTitle = null;
        adventureContent = null;
        noteKeysList = null;
    }

    public String getAdventureKey() {
        return adventureKey;
    }

    public void setAdventureKey(String adventureKey) {
        this.adventureKey = adventureKey;
    }

    public String getAdventureTitle() {
        return adventureTitle;
    }

    public void setAdventureTitle(String adventureTitle) {
        this.adventureTitle = adventureTitle;
    }

    public String getAdventureContent() {
        return adventureContent;
    }

    public void setAdventureContent(String adventureContent) {
        this.adventureContent = adventureContent;
    }

    public List<String> getNoteKeysList() {
        return noteKeysList;
    }

    public void setNoteKeysList(List<String> noteKeysList) {
        this.noteKeysList = noteKeysList;
    }
}
