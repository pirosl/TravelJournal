package com.lucianpiros.traveljournal.data;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lucianpiros.traveljournal.model.Note;

import java.util.ArrayList;

import androidx.annotation.NonNull;

public class FirebaseDB {

    private static FirebaseDB firebaseDB = null;
    private DatabaseReference databaseReference;

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

    public Boolean save(@NonNull Note note) {
        Boolean saved = false;
        try {
            databaseReference.child("notes").push().setValue(note);
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
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot);

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
            Note name=ds.getValue(Note.class);
            notes.add(name);
        }
    }
}
