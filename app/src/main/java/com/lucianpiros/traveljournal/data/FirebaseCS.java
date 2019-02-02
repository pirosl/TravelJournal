package com.lucianpiros.traveljournal.data;

import android.net.Uri;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;

public class FirebaseCS {

    public interface FileUploaderListener {
        public void onPhotoUploaded (String downloadUri);
    }

    private static final String FIREBASE_STORAGE_BUCKET = "gs://traveljournal-bedf8.appspot.com";

    private static FirebaseCS firebaseCS = null;

    private FirebaseStorage firebaseStorage;

    private FileUploaderListener fileUploaderListener;


    /**
     * Private constructor as this is a singleton
     */
    private FirebaseCS() {

    }

    public static FirebaseCS getInstance() {
        if(firebaseCS == null) {
            firebaseCS = new FirebaseCS();
            firebaseCS.firebaseStorage = FirebaseStorage.getInstance();
        }

        return firebaseCS;
    }

    public void setFileUploaderListener(FileUploaderListener fileUploaderListener) {
        this.fileUploaderListener = fileUploaderListener;
    }

    public void uploadPhoto(String child, String fileName, InputStream photoStream) {

        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(FIREBASE_STORAGE_BUCKET);
        StorageReference photoRef = storageRef.child(child).child(fileName);

        UploadTask uploadTask = photoRef.putStream(photoStream);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return photoRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                try {
                    photoStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    //Picasso.get().load(downloadUri.toString()).into(image);
                    fileUploaderListener.onPhotoUploaded(downloadUri.toString());
                } else {
                    // Handle failures
                }


            }
        });
    }
}
