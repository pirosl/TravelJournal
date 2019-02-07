package com.lucianpiros.traveljournal.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class FirebaseDB {

    private interface DatabaseStructure {
        String NotesTable = "notes";
    }

    private static final String TAG = FirebaseDB.class.getSimpleName();

    public interface NoteDBEventsListener {
        void OnNotesListChanged(List<Note> notesList);
    }

    public interface OnDBCompleteListener {
        void onInsertComplete(boolean success, String key);
        void onUpdateComplete(boolean success);
        void onDeleteComplete(boolean success);
    }

    private static FirebaseDB firebaseDB = null;
    private DatabaseReference databaseReference;

    private NoteDBEventsListener noteDBEventsListener;
    private OnDBCompleteListener onDBCompleteListener;

    private ArrayList<Note> notes;
    private Map<String, Note> notesMap;

    /**
     * Private constructor as this is a singleton
     */
    private FirebaseDB() {
    }

    public static FirebaseDB getInstance() {
        if(firebaseDB == null) {
            firebaseDB = new FirebaseDB();
            firebaseDB.databaseReference = FirebaseDatabase.getInstance().getReference();
            firebaseDB.notes = new ArrayList<>();
            firebaseDB.notesMap = new HashMap<>();
        }

        return firebaseDB;
    }

    public void setNoteDBEventsListener(@NotNull NoteDBEventsListener noteDBEventsListener) {
        this.noteDBEventsListener = noteDBEventsListener;

        retrieveNotes();
    }

    public void setOnDBCompleteListener(OnDBCompleteListener onDBCompleteListener) {
        this.onDBCompleteListener = onDBCompleteListener;
    }

    public void insert(@NonNull Note note) {
        try {
            DatabaseReference childRefference = databaseReference.child(DatabaseStructure.NotesTable).push();
            childRefference.setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    String childKey = null;
                    if(task.isSuccessful()) {
                        childKey = childRefference.getKey();
                    }
                    onDBCompleteListener.onInsertComplete(task.isSuccessful(), childKey);
                }
            });
        }catch (DatabaseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void update(String noteKey, @NotNull Note note) {
        note.setNoteKey(noteKey);
        DatabaseReference notesRef = databaseReference.child(DatabaseStructure.NotesTable);
        Map<String, Object> noteUpdate = new HashMap<>();
        noteUpdate.put(noteKey, note);

        notesRef.updateChildren(noteUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onDBCompleteListener.onUpdateComplete(task.isSuccessful());
            }
        });;
    }

    public ArrayList<Note> retrieveNotes() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged(notes);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged(notes);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged(notes);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return notes;
    }

    public Note getNote(String key) {
        return notesMap.get(key);
    }

    public Note getNote(int noteIdx) {
        return notes.get(noteIdx);
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        notes.clear();
        notesMap.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Note name = ds.getValue(Note.class);
            notes.add(name);
            notesMap.put(ds.getKey(), name);
        }
    }

    public void deleteNote(@NotNull Note note) {
        DatabaseReference notesRef = databaseReference.child(DatabaseStructure.NotesTable).child(note.getNoteKey());
        notesRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onDBCompleteListener.onDeleteComplete(task.isSuccessful());
            }
        });
    }
}
