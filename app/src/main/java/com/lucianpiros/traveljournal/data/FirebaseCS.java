package com.lucianpiros.traveljournal.data;

import android.net.Uri;
import android.util.Log;

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
    private static final String TAG = FirebaseCS.class.getSimpleName();

    public interface FileUploaderListener {
        public void onPhotoUploaded(String downloadUri);
        public void onMovieUploaded(String downloadUri);
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
                    Log.d(TAG, "Error saving file on Firebase Cloud Storage " + e.getMessage());
                }

                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    fileUploaderListener.onPhotoUploaded(downloadUri.toString());
                } else {
                    Log.d(TAG, "File not uploaded on Firebase Cloud Stoarage");
                }
            }
        });
    }

    public void uploadMovie(String child, String fileName, Uri movieUri) {

        StorageReference storageRef = firebaseStorage.getReferenceFromUrl(FIREBASE_STORAGE_BUCKET);
        StorageReference photoRef = storageRef.child(child).child(fileName);

        UploadTask uploadTask = photoRef.putFile(movieUri);

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
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    fileUploaderListener.onMovieUploaded(downloadUri.toString());
                } else {
                    Log.d(TAG, "File not uploaded on Firebase Cloud Stoarage");
                }
            }
        });
    }
}
