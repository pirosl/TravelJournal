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

public class UpdateNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileUploaderListener  {
    private final static String TAG = UpdateNoteService.class.getSimpleName();

    public interface UpdateNoteServiceListener {
        void onComplete();
    }

    private static UpdateNoteService updateNoteService = null;

    private Note note;
    private Uri selectedPhotoUri;
    private Uri selectedVideoUri;
    private ContentResolver contentResolver;

    private int updatesPerformed;

    private UpdateNoteServiceListener updateNoteServiceListener;

    private UpdateNoteService() {

    }

    public static UpdateNoteService getInstance() {
        if(updateNoteService == null) {
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

    public void updateNote(@NotNull Note note) {
        this.note = note;

        updatesPerformed = 0;
        if(selectedPhotoUri != null) {
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
        }
        else {
            if(selectedVideoUri != null) {
                synchronized (this) {
                    updatesPerformed++;
                }
                String name = getName(selectedVideoUri);
                note.setMovieFileName(name);
                FirebaseCS.getInstance().uploadMovie(note.getNoteKey(), name, selectedVideoUri);
            }
            else {
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
    public void onDeleteComplete(boolean success) {
        // nothing to do here
    }

    @Override
    public void onPhotoUploaded(String downloadUri) {
        note.setPhotoDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(note.getNoteKey(), note);

        if(selectedVideoUri != null) {
            synchronized (this) {
                updatesPerformed++;
            }
            String name = getName(selectedVideoUri);
            note.setMovieFileName(name);
            FirebaseCS.getInstance().uploadMovie(note.getNoteKey(), name, selectedVideoUri);
        }
        else {
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
