package com.lucianpiros.traveljournal.service;

import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Note;

public class DeleteNoteService implements FirebaseDB.OnDBCompleteListener, FirebaseCS.FileDeleteListener {
    private final static String TAG = DeleteNoteService.class.getSimpleName();

    public interface DeleteNoteServiceListener {
        void onComplete();
    }

    private static DeleteNoteService deleteNoteService = null;

    private Note note;
    private DeleteNoteServiceListener deleteNoteServiceListener;

    private DeleteNoteService() {

    }

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
