package com.lucianpiros.traveljournal.service;

import com.lucianpiros.traveljournal.data.FirebaseDB;
import com.lucianpiros.traveljournal.model.Adventure;

/**
 * Add adventure service implementation.
 * Handles all the logic of adding an adventure.
 * When an adventure is added, few steps have to be performed:
 * - add adventure to Firebase Realtime Database
 * - update adventure with adventure key
 *
 * <p>
 * This class is a singleton class.
 *
 * @author Lucian Piros
 * @version 1.0
 */
public class AddAdventureService implements FirebaseDB.OnDBCompleteListener {

    private final static String TAG = AddAdventureService.class.getSimpleName();

    // Static refference to AddAdventureService
    private static AddAdventureService addAdventureService = null;

    /**
     * Method of this interface are called when add operation is complete.
     *
     * @author Lucian Piros
     * @version 1.0
     */
    public interface AddAdventureServiceListener {
        void onComplete();
    }

    private AddAdventureServiceListener addAdventureServiceListener;
    private Adventure adventure;

    /**
     * Private class constructor
     */
    private AddAdventureService() {

    }

    /**
     * Static method returning singleton instance.
     *
     * @return singleton instance
     */
    public static AddAdventureService getInstance() {
        if (addAdventureService == null) {

            addAdventureService = new AddAdventureService();
        }

        FirebaseDB.getInstance().setOnDBCompleteListener(addAdventureService);
        return addAdventureService;
    }

    public void setAddAdventureServiceListener(AddAdventureServiceListener addAdventureServiceListener) {
        this.addAdventureServiceListener = addAdventureServiceListener;
    }

    /**
     * Add adventure method. Called to add a note to Firebase
     */
    public void addAdventure(Adventure adventure) {
        this.adventure = adventure;

        FirebaseDB.getInstance().insert(adventure);
    }


    @Override
    public void onInsertComplete(boolean success, String key) {
        if (success) {
            FirebaseDB.getInstance().update(key, adventure);
        }
    }

    @Override
    public void onUpdateComplete(boolean success) {
        addAdventureServiceListener.onComplete();
    }

    @Override
    public void onDeleteComplete(boolean success) {

    }
}
