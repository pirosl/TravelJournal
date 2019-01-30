package com.lucianpiros.traveljournal.data;

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
import java.util.List;

import androidx.annotation.NonNull;

public class FirebaseDB {

    public interface NoteDBEventsListener {
        public void OnNotesListChanged(List<Note> notesList);
    }

    public interface OnDBCompleteListener {
        public void onCoplete(boolean success);
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

    public Boolean save(@NonNull Note note) {
        Boolean saved = false;

        try {
            databaseReference.child("notes").push().setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    onDBCompleteListener.onCoplete(task.isSuccessful());
                }
            });
            saved = true;
        }catch (DatabaseException e) {
            e.printStackTrace();
        }

        return saved;
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
