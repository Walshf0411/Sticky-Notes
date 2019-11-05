package com.stickynotes.ui.home;

import androidx.lifecycle.ViewModel;

import com.stickynotes.adapters.NoteAdapter;
import com.stickynotes.models.Note;
import com.stickynotes.repository.NotesRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private NotesRepository notesRepository;
    private List<Note> notes = new ArrayList<>();
    private NoteAdapter noteAdapter = new NoteAdapter();

    public NoteAdapter getNoteAdapter() {
        return noteAdapter;
    }

    public void setNoteAdapter(NoteAdapter noteAdapter) {
        this.noteAdapter = noteAdapter;
    }

    public HomeViewModel() {
        notesRepository = NotesRepository.getInstance();
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        noteAdapter.setNotes(this.notes);
    }

    public NotesRepository getNotesRepository() {
        return notesRepository;
    }

    public void setNotesRepository(NotesRepository notesRepository) {
        this.notesRepository = notesRepository;
    }

    public void deleteNote(int position) {
        // GET THE NOTE OBJECT
        Note note = notes.get(position);
        // DELETE THE NOTE FROM FIREBASE
        notesRepository.deleteNote(note);
        // REMOVE THE NOTE FROM THE RECYCLER VIEW ADAPTER
        notes.remove(position);
        noteAdapter.notifyItemRemoved(position);
    }

    public void editNote(int position, Note newNote) {
        Note oldNote = notes.get(position);
        Note mergedNote = oldNote;

        if (!newNote.getTitle().equals("")) {
            mergedNote.setTitle(newNote.getTitle());
        }

        if (!newNote.getNote().equals("")) {
            mergedNote.setNote(newNote.getNote());
        }

        if (!newNote.getDatetime().equals("")) {
            mergedNote.setDatetime(newNote.getDatetime());
        }

        // EDITING THE NOTE IN FIREBASE
        notesRepository.editNote(mergedNote, null);
        // EDITING AND REFLECTING THE CHANGES TO THE RECYCLER VIEW ADAPTER
        notes.set(position, mergedNote);
        // NOTIFY THAT AN ITEM HAS CHANGED
        noteAdapter.notifyItemChanged(position);
    }
}