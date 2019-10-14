package com.stickynotes.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stickynotes.models.Note;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Note>> notes;

    public HomeViewModel() {

    }

    public MutableLiveData<List<Note>> getNotes() {
        return notes;
    }

    public void setNotes(MutableLiveData<List<Note>> notes) {
        this.notes = notes;
    }
}