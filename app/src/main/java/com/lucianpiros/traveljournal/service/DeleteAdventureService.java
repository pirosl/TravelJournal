package com.lucianpiros.traveljournal.service;

import com.lucianpiros.traveljournal.data.FirebaseCS;
import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Adventure;
import com.lucianpiros.traveljournal.model.Note;

/**
 * Delete adventure service implementation.
 * Handles all the logic of deleting an adventure.
 *
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class DeleteAdventureService implements FirebaseDB.OnDBCompleteListener {
    private final static String TAG = DeleteNoteService.class.getSimpleName();

    /**
     * Method of this interface are called when delete operation is complete.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface DeleteAdventureServiceListener {
        void onComplete();
    }

    private static DeleteAdventureService deleteAdventureService = null;

    // DeleteAdventureServiceListener instanec
    private DeleteAdventureServiceListener deleteAdventureServiceListener;

    /**
     * Private class constructor
     */
    private DeleteAdventureService() {

    }

    /**
     * Static method returning an instance of this class.
     * Instance is registered as a FirebaseBD
     *
     * @return singleton instance
     */
    public static DeleteAdventureService getInstance() {
        if (deleteAdventureService == null) {
            deleteAdventureService = new DeleteAdventureService();
        }

        FirebaseDB.getInstance().setOnDBCompleteListener(deleteAdventureService);

        return deleteAdventureService;
    }

    public void setDeleteAdventureServiceListener(DeleteAdventureServiceListener deleteAdventureServiceListener) {
        this.deleteAdventureServiceListener = deleteAdventureServiceListener;
    }

    /**
     * Delete adventure method. Called to delete a note from FirebaseBD.
     *
     * @param adventure adventure to be deleted
     */
    public void deleteAdventure(Adventure adventure) {
        FirebaseDB.getInstance().deleteAdventure(adventure);
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
        deleteAdventureServiceListener.onComplete();
    }
}
