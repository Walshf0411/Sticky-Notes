package com.stickynotes.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stickynotes.R;
import com.stickynotes.adapters.NoteAdapter;
import com.stickynotes.models.Note;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    private RecyclerView notesListRecyclerView;
    private NoteAdapter noteAdapter = new NoteAdapter();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);

        initRecyclerView();

        homeViewModel.getNotes().observe(this,
                new Observer<List<Note>>() {
                    @Override
                    public void onChanged(List<Note> notes) {
                        noteAdapter.setNotes(notes);
                    }
                });

        return root;
    }
    private void initRecyclerView () {
        notesListRecyclerView = root.findViewById(R.id.notes_list);
        notesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notesListRecyclerView.setHasFixedSize(true);
        notesListRecyclerView.setAdapter(noteAdapter);
        populateRecyclerView();
    }

    private void populateRecyclerView () {
        fetchData();
//        List<Note> notes = new ArrayList<>();
//
//        Note note = new Note();
//        note.setTitle("First Note");
//        note.setNote("This is the first note");
//        notes.add(note);
//
//        Note note1 = new Note();
//        note1.setTitle("Second Note");
//        note1.setNote("This is the second note");
//        notes.add(note1);
//
//        Note note2 = new Note();
//        note2.setTitle("Third Note");
//        note2.setNote("This is the third note");
//        notes.add(note2);
//        MutableLiveData<List<Note>> notesLiveDate = new MutableLiveData<>();
//        notesLiveDate.setValue(notes);
//        homeViewModel.setNotes(notesLiveDate);
    }

    private void fetchData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String documentPath = "users/" + user.getUid() + "/notes/";

        db.collection(documentPath)
                .get()
                .addOnSuccessListener(
                        new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Note> notes = queryDocumentSnapshots.toObjects(Note.class);
                                MutableLiveData<List<Note>> liveData = new MutableLiveData<>();
                                liveData.setValue(notes);
                                homeViewModel.setNotes(liveData);
                            }
                        }
                );
    }
}