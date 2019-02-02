package com.lucianpiros.traveljournal.service;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

public class AddNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileUploaderListener  {
    private final static String TAG = AddNoteService.class.getSimpleName();

    public interface AddNoteServiceListener {
        public void onComplete();
    }

    private static AddNoteService addNoteService = null;

    private String noteTitle;
    private String noteContent;
    private Date noteCreationDate;
    private Uri selectedPhotoUri;
    private ContentResolver contentResolver;

    private String childKey;

    private Note note;

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

        FirebaseDB.getInstance().insert(note);
    }

    @Override
    public void onInsertComplete(boolean success, String childKey) {
        if(selectedPhotoUri != null) {
            this.childKey = childKey;
            Cursor returnCursor =
                    contentResolver.query(selectedPhotoUri, null, null, null, null);
            assert returnCursor != null;
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            String name = returnCursor.getString(nameIndex);
            returnCursor.close();

            try {
                InputStream imageStream = contentResolver.openInputStream(selectedPhotoUri);
                FirebaseCS.getInstance().uploadPhoto(childKey, name, imageStream);
            } catch (FileNotFoundException e) {
                Log.d(TAG, e.getMessage());
            }
        }
        else {
            addNoteServiceListener.onComplete();
        }
    }

    @Override
    public void onUpdateComplete(boolean success) {
        addNoteServiceListener.onComplete();
    }

    @Override
    public void onPhotoUploaded(String downloadUri) {
        note.setPhotoDownloadURL(downloadUri);
        FirebaseDB.getInstance().update(childKey, note);
    }
}
