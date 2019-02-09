package com.lucianpiros.traveljournal.service;

import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

/**
 * Delete note service implementation.
 * Handles all the logic of deleting a note.
 * When a note is deleted, few steps have to be performed:
 * - delete note to Firebase Realtime Database
 * - delete any photo or movie stored on Firebase cloud storage
 *
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class DeleteNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileDeleteListener {
    private final static String TAG = DeleteNoteService.class.getSimpleName();

    /**
     * Method of this interface are called when delete operation is complete.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface DeleteNoteServiceListener {
        void onComplete();
    }

    private static DeleteNoteService deleteNoteService = null;

    // note stored across delete operation
    private Note note;

    // DeleteNoteServiceListener instanec
    private DeleteNoteServiceListener deleteNoteServiceListener;

    /**
     * Private class constructor
     */
    private DeleteNoteService() {

    }

    /**
     * Static method returning an instance of this class.
     * Instance is registered as a FirebaseBD and FirebaseCS listener
     *
     * @return singleton instance
     */
    public static DeleteNoteService getInstance() {
        if(deleteNoteService == null) {
            deleteNoteService = new DeleteNoteService();
        }

        FirebaseCS.getInstance().setFileDeleteListener(deleteNoteService);
        FirebaseDB.getInstance().setOnDBCompleteListener(deleteNoteService);

        return deleteNoteService;
    }

    public void setDeleteNoteServiceListener(DeleteNoteServiceListener deleteNoteServiceListener) {
        this.deleteNoteServiceListener = deleteNoteServiceListener;
    }

    /**
     * Delete note method. Called to delete a note from FirebaseBD.
     *
     * @param note note to be deleted
     */
    public void deleteNote(Note note) {
        this.note = note;

        String photoDownloadURL = note.getPhotoDownloadURL();
        if(photoDownloadURL != null && photoDownloadURL.length() > 0) {
            FirebaseCS.getInstance().deleteFile(note.getNoteKey(), note.getPhotoFileName(), FirebaseCS.FileDeleteListener.PHOTO);
        }
        else {
            String movieDownloadURL = note.getMovieDownloadURL();
            if(movieDownloadURL != null && movieDownloadURL.length() > 0) {
                FirebaseCS.getInstance().deleteFile(note.getNoteKey(), note.getMovieFileName(), FirebaseCS.FileDeleteListener.MOVIE);
            }
            else {
                FirebaseDB.getInstance().deleteNote(note);
            }
        }
    }

    @Override
    public void onFileDelete(int fileType, boolean success) {
        if(fileType == FirebaseCS.FileDeleteListener.PHOTO) {
            String movieDownloadURL = note.getMovieDownloadURL();
            if(movieDownloadURL != null && movieDownloadURL.length() > 0) {
                FirebaseCS.getInstance().deleteFile(note.getNoteKey(), note.getMovieFileName(), FirebaseCS.FileDeleteListener.MOVIE);
            }
            else {
                FirebaseDB.getInstance().deleteNote(note);
            }
        }
        if(fileType == FirebaseCS.FileDeleteListener.MOVIE) {
            FirebaseDB.getInstance().deleteNote(note);
        }
    }


    @Override
    public void onInsertComplete(boolean success, String key) {
        // nothing to do here
    }

    @Override
    public void onUpdateComplete(boolean success) {
        // nothing to do here
    }

    @Override
    public void onDeleteComplete(boolean success) {
        deleteNoteServiceListener.onComplete();
    }
}
