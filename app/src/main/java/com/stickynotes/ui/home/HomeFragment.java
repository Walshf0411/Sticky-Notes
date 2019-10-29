package com.stickynotes.ui.home;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.admin.v1beta1.Progress;
import com.stickynotes.R;
import com.stickynotes.adapters.NoteAdapter;
import com.stickynotes.models.Note;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    private RecyclerView notesListRecyclerView;
    private NoteAdapter noteAdapter = new NoteAdapter();
    private ProgressDialog progressDialog;
    private LinearLayout noNotesOverlay;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        // initialize view model
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);

        initComponents();
        initRecyclerView();

        return root;
    }

    private void initComponents() {
        noNotesOverlay = (LinearLayout) root.findViewById(R.id.no_notes_overlay);
        noNotesOverlay.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        notesListRecyclerView = root.findViewById(R.id.notes_list);
        notesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notesListRecyclerView.setHasFixedSize(true);
        notesListRecyclerView.setAdapter(noteAdapter);
        fetchData();
    }

    private void fetchData() {
        // TODO: load shimmer here and remove progressDialog
        showProgressDialog("Fetching your notes");
        homeViewModel.getNotesRepository().fetchAllNotes(
                new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Task is successful
                            if (task.getResult() != null) {
                                List<Note> notes = task.getResult().toObjects(Note.class);
                                noteAdapter.setNotes(notes);
                                if (notes.isEmpty()) {
                                    noNotesOverlay.setVisibility(View.VISIBLE);
                                    notesListRecyclerView.setVisibility(View.GONE);
                                }
                            }
                            hideProgressDialog();
                        } else {
                            hideProgressDialog();
                        }
                    }
                }
        );

    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
        }
    }

    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void log(String message) {
        Log.i(getString(R.string.TAG), message);
    }

}