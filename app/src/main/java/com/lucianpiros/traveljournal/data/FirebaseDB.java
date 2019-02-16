package com.lucianpiros.traveljournal.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
        void OnNotesListChanged();
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

    /**
     * Private constructor as this is a singleton
     */
    private FirebaseDB() {
    }

    public static FirebaseDB getInstance() {
        if(firebaseDB == null) {
            firebaseDB = new FirebaseDB();
            firebaseDB.databaseReference = FirebaseDatabase.getInstance().getReference();
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
            Task<Void> insertTask = childRefference.setValue(note);
            insertTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Note insertion result " + task.isSuccessful());
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
        });
    }

    public void retrieveNotes() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                fetchData(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchData(DataSnapshot dataSnapshot) {
        List<Note> notes = new ArrayList<>();
        Map<String, Note> notesMap = new HashMap<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Note name = ds.getValue(Note.class);
            notes.add(name);
            notesMap.put(ds.getKey(), name);
        }

        DataCache.getInstance().setNotesList(notes);
        DataCache.getInstance().setNotesMap(notesMap);
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
