package com.lucianpiros.traveljournal.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

@IgnoreExtraProperties
public class Note {
    private String noteKey;
    private String noteTitle;
    private String noteContent;
    private Date noteCreationDate;
    private String photoDownloadURL;
    private String movieDownloadURL;
    private double latitude;
    private double longitude;

    public Note() {
        noteTitle = null;
        noteContent = null;
        noteCreationDate = null;
        photoDownloadURL = null;
        movieDownloadURL = null;
    }

    public String getNoteKey() {
        return noteKey;
    }

    public void setNoteKey(String noteKey) {
        this.noteKey = noteKey;
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

    public Date getNoteCreationDate() {
        return noteCreationDate;
    }

    public void setNoteCreationDate(Date noteCreationDate) {
        this.noteCreationDate = noteCreationDate;
    }

    public String getPhotoDownloadURL() {
        return photoDownloadURL;
    }

    public void setPhotoDownloadURL(String photoDownloadURL) {
        this.photoDownloadURL = photoDownloadURL;
    }

    public void setMovieDownloadURL(String movieDownloadURL) {
        this.movieDownloadURL = movieDownloadURL;
    }

    public String getMovieDownloadURL() {
        return movieDownloadURL;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
