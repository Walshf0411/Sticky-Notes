package com.stickynotes.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.stickynotes.Constants;
import com.stickynotes.models.Note;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotesRepository {

    private static NotesRepository instance;
    private FirebaseFirestore db;
    private String UID;

    private NotesRepository() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static NotesRepository getInstance() {
        if (instance == null) {
            instance = new NotesRepository();
        }
        return instance;
    }

    // TODO: implement methods to do basic operations on the notes

    public void addNote(Note note, OnCompleteListener<DocumentReference> onCompleteListener) {
        // Create a Map object to store the data to be sent
        Map<String, String> data = note.toMap();

        // build the document path
        String documentPath = "/users/" + UID + "/notes";

        // add the data to the collection
        db.collection(documentPath)
                .add(data)
                .addOnCompleteListener(onCompleteListener);
    }

    public void fetchAllNotes(OnCompleteListener<QuerySnapshot> onCompleteListener) {
        // build the document path
        String documentPath = "/users/" + UID + "/notes";
        db.collection(documentPath)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void editNote(Note editedNote, OnCompleteListener onCompleteListener) {
        // FETCH THE DOCUMENT THAT HAS TO BE EDITED
        String documentPath = "/users/" + UID + "/notes/";
        db.collection(documentPath).document(editedNote.getId())
                .set(editedNote.toMap(), SetOptions.merge());
    }

    public void deleteNote(Note note) {
        String documentPath = "/users/" + UID + "/notes/";
        db.collection(documentPath).document(note.getId())
                .delete();
    }


}
