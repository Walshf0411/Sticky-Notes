package com.stickynotes.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;
import com.stickynotes.Constants;
import com.stickynotes.models.Note;

import java.util.HashMap;
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
        Map<String, String> data = new HashMap<>();
        data.put(Constants.FIRESTORE_NOTE_TITLE_KEY, note.getTitle());
        data.put(Constants.FIRESTORE_NOTE_KEY, note.getNote());
        data.put(
                Constants.FIRESTORE_NOTE_DATETIME_KEY,
                note.getDatetime() != null ? note.getDatetime() : ""
        );

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
}
