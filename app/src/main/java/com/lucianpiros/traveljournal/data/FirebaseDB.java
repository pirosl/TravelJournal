package com.lucianpiros.traveljournal.data;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.model.Note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Firebase Realtime Database management clas. Handles all CRUD database operations
 *
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class FirebaseDB {

    private interface DatabaseStructure {
        String NotesTable = "notes";
        String AdventuresTable = "adventures";
    }

    private static final String TAG = FirebaseDB.class.getSimpleName();

    public interface NoteDBEventsListener {
        void OnNotesListChanged();
    }

    public interface AdventuresDBEventsListener {
        void OnAdventuresListChanged();
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
    private AdventuresDBEventsListener adventuresDBEventsListener;

    /**
     * Private constructor as this is a singleton
     */
    private FirebaseDB() {
    }

    public static FirebaseDB getInstance() {
        if (firebaseDB == null) {
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

    public void setAdventuresDBEventsListener(@NotNull AdventuresDBEventsListener adventuresDBEventsListener) {
        this.adventuresDBEventsListener = adventuresDBEventsListener;

        retrieveAdventures();
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
                    if (task.isSuccessful()) {
                        childKey = childRefference.getKey();
                    }
                    onDBCompleteListener.onInsertComplete(task.isSuccessful(), childKey);
                }
            });
        } catch (DatabaseException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void insert(@NonNull Adventure adventure) {
        try {
            DatabaseReference childRefference = databaseReference.child(DatabaseStructure.AdventuresTable).push();
            Task<Void> insertTask = childRefference.setValue(adventure);
            insertTask.addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.d(TAG, "Note insertion result " + task.isSuccessful());
                    String childKey = null;
                    if (task.isSuccessful()) {
                        childKey = childRefference.getKey();
                    }
                    onDBCompleteListener.onInsertComplete(task.isSuccessful(), childKey);
                }
            });
        } catch (DatabaseException e) {
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

    public void update(String noteKey, @NotNull Adventure adventure) {
        adventure.setAdventureKey(noteKey);
        DatabaseReference notesRef = databaseReference.child(DatabaseStructure.AdventuresTable);
        Map<String, Object> noteUpdate = new HashMap<>();
        noteUpdate.put(noteKey, adventure);

        notesRef.updateChildren(noteUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onDBCompleteListener.onUpdateComplete(task.isSuccessful());
            }
        });
    }

    public void retrieveNotes() {
        databaseReference.child(DatabaseStructure.NotesTable).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchNotes(dataSnapshot);

                noteDBEventsListener.OnNotesListChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retrieveAdventures() {
        databaseReference.child(DatabaseStructure.AdventuresTable).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchAdventures(dataSnapshot);

                adventuresDBEventsListener.OnAdventuresListChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void fetchNotes(DataSnapshot dataSnapshot) {
        List<Note> notes = new ArrayList<>();
        Map<String, Note> notesMap = new HashMap<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Note note = ds.getValue(Note.class);
            notes.add(note);
            notesMap.put(ds.getKey(), note);
        }

        DataCache.getInstance().setNotesList(notes);
        DataCache.getInstance().setNotesMap(notesMap);
    }

    private void fetchAdventures(DataSnapshot dataSnapshot) {
        List<Adventure> adventures = new ArrayList<>();
        Map<String, Adventure> adventuresMap = new HashMap<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            Adventure adventure = ds.getValue(Adventure.class);
            adventures.add(adventure);
            adventuresMap.put(ds.getKey(), adventure);
        }

        DataCache.getInstance().setAdventuresList(adventures);
        DataCache.getInstance().setAdventuresMap(adventuresMap);
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

    public void deleteAdventure(@NotNull Adventure adventure) {
        DatabaseReference adventureRef = databaseReference.child(DatabaseStructure.AdventuresTable).child(adventure.getAdventureKey());
        adventureRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                onDBCompleteListener.onDeleteComplete(task.isSuccessful());
            }
        });
    }
}
