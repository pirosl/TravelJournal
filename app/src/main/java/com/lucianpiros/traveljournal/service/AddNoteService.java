package com.lucianpiros.traveljournal.service;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class AddNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileUploaderListener  {
    private final static String TAG = AddNoteService.class.getSimpleName();

    public interface AddNoteServiceListener {
        void onComplete();
    }

    private static AddNoteService addNoteService = null;

    private String noteTitle;
    private String noteContent;
    private Date noteCreationDate;
    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;
    private ContentResolver contentResolver;

    private String childKey;

    private Note note;
    private int updatesPerformed;

    private AddNoteServiceListener addNoteServiceListener;

    private AddNoteService() {

    }

    public static AddNoteService getInstance() {
        if(addNoteService == null) {
            addNoteService = new AddNoteService();
        }

        FirebaseDB.getInstance().setOnDBCompleteListener(addNoteService);
        FirebaseCS.getInstance().setFileUploaderListener(addNoteService);
        return addNoteService;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public void setNoteCreationDate(Date noteCreationDate) {
        this.noteCreationDate = noteCreationDate;
    }

    public void setSelectedPhotoUri(Uri selectedPhotoUri) {
        this.selectedPhotoUri = selectedPhotoUri;
    }

    public void setSelectedVideoUri(Uri selectedVideoUri) {
        this.selectedVideoUri = selectedVideoUri;
    }

    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void setAddNoteServiceListener(AddNoteServiceListener addNoteServiceListener) {
        this.addNoteServiceListener = addNoteServiceListener;
    }

    public void addNote() {
        note = new Note();
        note.setNoteTitle(noteTitle);
        note.setNoteContent(noteContent);
        note.setNoteCreationDate(noteCreationDate);

        note.setLatitude(LocationService.getInstance().getLatitude());
        note.setLongitude(LocationService.getInstance().getLongitude());

        FirebaseDB.getInstance().insert(note);
    }

    @Override
    public void onInsertComplete(boolean success, String childKey) {
        updatesPerformed = 0;
        if(selectedPhotoUri != null) {
            this.childKey = childKey;
            String name = getName(selectedPhotoUri);

            try {
                synchronized (this) {
                    updatesPerformed++;
                }
                InputStream imageStream = contentResolver.openInputStream(selectedPhotoUri);
                FirebaseCS.getInstance().uploadPhoto(childKey, name, imageStream);
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        else {
            if(selectedVideoUri != null) {
                synchronized (this) {
                    updatesPerformed++;
                }
                this.childKey = childKey;
                String name = getName(selectedVideoUri);

                FirebaseCS.getInstance().uploadMovie(childKey, name, selectedVideoUri);
            }
            else {
                complete();
            }
        }
    }

    private String getName(Uri uri) {
        File file= new File(uri.getPath());
        String name = file.getName();
        return name;
    }

    @Override
    public void onUpdateComplete(boolean success) {
        synchronized (this) {
            updatesPerformed--;
        }
        if(updatesPerformed == 0)
            complete();
    }

    @Override
    public void onPhotoUploaded(String downloadUri) {
        note.setPhotoDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(childKey, note);

        if(selectedVideoUri != null) {
            synchronized (this) {
                updatesPerformed++;
            }
            String name = getName(selectedVideoUri);

            FirebaseCS.getInstance().uploadMovie(childKey, name, selectedVideoUri);
        }
        else {
            complete();
        }
    }

    @Override
    public void onMovieUploaded(String downloadUri) {
        note.setMovieDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(childKey, note);
    }

    private void complete() {
        noteTitle = null;
        noteContent = null;
        noteCreationDate = null;
        selectedPhotoUri = null;
        selectedVideoUri = null;

        addNoteServiceListener.onComplete();
    }
}
