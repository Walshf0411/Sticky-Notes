package com.stickynotes.ui.add_note;

import androidx.lifecycle.ViewModel;

import com.stickynotes.repository.NotesRepository;

public class AddNoteViewModel extends ViewModel {

    public NotesRepository notesRepository;

    public AddNoteViewModel() {
        notesRepository = NotesRepository.getInstance();
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }

    public void setNotesRepository(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }
}