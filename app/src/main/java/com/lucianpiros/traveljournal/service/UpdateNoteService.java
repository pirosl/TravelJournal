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

/**
 * Update note service implementation.
 * Handles all the logic of updating a note.
 * When a note is updated, few steps have to be performed:
 * - if note has a photo or movie attached, upload them on Firebase Cloud Storage
 * using the key from Firebase Database as refference
 * - update note with downloadable URL provided by Firebase Cloud Storage
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class UpdateNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileUploaderListener {
    private final static String TAG = UpdateNoteService.class.getSimpleName();

    /**
     * Method of this interface are called when update operation is complete.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface UpdateNoteServiceListener {
        void onComplete();
    }

    // Static refference to UpdateNoteService instance
    private static UpdateNoteService updateNoteService = null;

    // Global Note used through all update steps
    private Note note;
    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;
    private ContentResolver contentResolver;

    private int updatesPerformed;

    private UpdateNoteServiceListener updateNoteServiceListener;

    /**
     * Private class constructor
     */
    private UpdateNoteService() {

    }

    /**
     * Return class singleton instance.
     * Register this class as FirebaseCS and FirebaseDB listeners.
     *
     * @return singleton instance
     */
    public static UpdateNoteService getInstance() {
        if (updateNoteService == null) {
            updateNoteService = new UpdateNoteService();
        }

        FirebaseDB.getInstance().setOnDBCompleteListener(updateNoteService);
        FirebaseCS.getInstance().setFileUploaderListener(updateNoteService);
        return updateNoteService;
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

    public void setUpdateNoteServiceListener(UpdateNoteServiceListener updateNoteServiceListener) {
        this.updateNoteServiceListener = updateNoteServiceListener;
    }

    /**
     * Update method. Updates Note provided as parameter.
     *
     * @param note note to be updated.
     */
    public void updateNote(@NotNull Note note) {
        this.note = note;

        updatesPerformed = 0;
        if (selectedPhotoUri != null) {
            String name = getName(selectedPhotoUri);

            try {
                synchronized (this) {
                    updatesPerformed++;
                }
                InputStream imageStream = contentResolver.openInputStream(selectedPhotoUri);
                note.setPhotoFileName(name);
                FirebaseCS.getInstance().uploadPhoto(note.getNoteKey(), name, imageStream);
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            if (selectedVideoUri != null) {
                synchronized (this) {
                    updatesPerformed++;
                }
                String name = getName(selectedVideoUri);
                note.setMovieFileName(name);
                FirebaseCS.getInstance().uploadMovie(note.getNoteKey(), name, selectedVideoUri);
            } else {
                synchronized (this) {
                    updatesPerformed++;
                }
                FirebaseDB.getInstance().update(note.getNoteKey(), note);
            }
        }
    }

    @Override
    public void onInsertComplete(boolean success, String noteKey) {
        // nothing to do here
    }

    private String getName(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
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
        FirebaseDB.getInstance().update(note.getNoteKey(), note);

        if (selectedVideoUri != null) {
            synchronized (this) {
                updatesPerformed++;
            }
            String name = getName(selectedVideoUri);
            note.setMovieFileName(name);
            FirebaseCS.getInstance().uploadMovie(note.getNoteKey(), name, selectedVideoUri);
        } else {
            complete();
        }
    }

    @Override
    public void onMovieUploaded(String downloadUri) {
        note.setMovieDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(note.getNoteKey(), note);
    }

    private void complete() {
        note = null;
        selectedPhotoUri = null;
        selectedVideoUri = null;

        updateNoteServiceListener.onComplete();
    }
}
