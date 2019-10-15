package com.stickynotes.ui.home;

import androidx.lifecycle.ViewModel;

import com.stickynotes.repository.NotesRepository;

public class HomeViewModel extends ViewModel {

    private NotesRepository notesRepository;

    public HomeViewModel() {
        notesRepository = NotesRepository.getInstance();
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }

    public void setNotesRepository(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }
}