package com.stickynotes.repository;

import java.util.List;
import java.util.ArrayList;

import com.google.firebase.firestore.FirebaseFirestore;
import com.stickynotes.models.Note;

public class NotesRepository {

    private static NotesRepository instance;
    private List<Note> notes = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static NotesRepository getInstance() {
        if (instance == null) {
            instance = new NotesRepository();
        }
        return instance;
    }
}
