package com.lucianpiros.traveljournal.service;

import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * Add note service implementation.
 * Handles all the logic of adding a note.
 * When a note is added, few steps have to be performed:
 * - add note to Firebase Realtime Database
 * - if note has a photo or movie attached, upload them on Firebase Cloud Storage
 * using the key from Firebase Database as refference
 * - update note with downloadable URL provided by Firebase Cloud Storage
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AddNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileUploaderListener {
    private final static String TAG = AddNoteService.class.getSimpleName();

    /**
     * Method of this interface are called when add operation is complete.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface AddNoteServiceListener {
        void onComplete();
    }

    // Static refference to AddNoteService
    private static AddNoteService addNoteService = null;

    private String noteTitle;
    private String noteContent;
    private Date noteCreationDate;
    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;
    private ContentResolver contentResolver;

    private String noteKey;

    // Note object stored across FirebaseCS and FirebaseBD operations
    private Note note;

    private int updatesPerformed;

    private AddNoteServiceListener addNoteServiceListener;

    /**
     * Private class constructor
     */
    private AddNoteService() {

    }

    /**
     * Static method returning singleton instance.
     *
     * @return singleton instance
     */
    public static AddNoteService getInstance() {
        if (addNoteService == null) {

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

    public Uri getSelectedPhotoUri() {
        return this.selectedPhotoUri;
    }

    public void setSelectedVideoUri(Uri selectedVideoUri) {
        this.selectedVideoUri = selectedVideoUri;
    }

    public Uri getSelectedVideoUri() {
        return this.selectedVideoUri;
    }

    public void setContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void setAddNoteServiceListener(AddNoteServiceListener addNoteServiceListener) {
        this.addNoteServiceListener = addNoteServiceListener;
    }

    /**
     * Add note method. Called to add a note to Firebase
     */
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
    public void onInsertComplete(boolean success, String noteKey) {
        updatesPerformed = 0;
        if (selectedPhotoUri != null) {
            this.noteKey = noteKey;
            String name = getName(selectedPhotoUri);

            try {
                synchronized (this) {
                    updatesPerformed++;
                }
                InputStream imageStream = contentResolver.openInputStream(selectedPhotoUri);
                note.setPhotoFileName(name);
                FirebaseCS.getInstance().uploadPhoto(noteKey, name, imageStream);
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            if (selectedVideoUri != null) {
                synchronized (this) {
                    updatesPerformed++;
                }
                this.noteKey = noteKey;
                String name = getName(selectedVideoUri);
                note.setMovieFileName(name);
                FirebaseCS.getInstance().uploadMovie(noteKey, name, selectedVideoUri);
            } else {
                synchronized (this) {
                    updatesPerformed++;
                }
                this.noteKey = noteKey;
                FirebaseDB.getInstance().update(this.noteKey, note);
            }
        }
    }

    private String getName(@NotNull Uri uri) {
        String path = uri.getPath();
        if(path != null) {
            File file = new File(uri.getPath());
            return file.getName();
        }

        return null;
    }

    @Override
    public void onUpdateComplete(boolean success) {
        synchronized (this) {
            updatesPerformed--;
        }
        if (updatesPerformed == 0)
            complete();
    }

    @Override
    public void onDeleteComplete(boolean success) {
        // nothing to do here
    }

    @Override
    public void onPhotoUploaded(String downloadUri) {
        note.setPhotoDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(noteKey, note);

        if (selectedVideoUri != null) {
            synchronized (this) {
                updatesPerformed++;
            }
            String name = getName(selectedVideoUri);
            note.setMovieFileName(name);
            FirebaseCS.getInstance().uploadMovie(noteKey, name, selectedVideoUri);
        } else {
            complete();
        }
    }

    @Override
    public void onMovieUploaded(String downloadUri) {
        note.setMovieDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(noteKey, note);
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
