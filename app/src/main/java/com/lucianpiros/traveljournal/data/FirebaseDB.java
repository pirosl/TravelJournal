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

    private static final String TAG = FirebaseDB.class.getSimpleName();

    public interface NoteDBEventsListener {
        public void OnNotesListChanged(List<Note> notesList);
    }

    public interface OnDBCompleteListener {
        public void onInsertComplete(boolean success, String key);
        public void onUpdateComplete(boolean success);
    }

    private static FirebaseDB firebaseDB = null;
    private DatabaseReference databaseReference;

    private NoteDBEventsListener noteDBEventsListener;
    private OnDBCompleteListener onDBCompleteListener;

    private ArrayList<Note> notes;

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
            DatabaseReference childRefference = databaseReference.child("notes").push();
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

    public void update(String path, @NotNull Note note) {
        DatabaseReference notesRef = databaseReference.child("notes");
        Map<String, Object> noteUpdate = new HashMap<>();
        noteUpdate.put(path, note);

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

    private void fetchData(DataSnapshot dataSnapshot) {
        notes.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Note name = ds.getValue(Note.class);
            notes.add(name);
        }
    }
}
