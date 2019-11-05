package com.stickynotes.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.stickynotes.R;
import com.stickynotes.models.Note;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View root;
    private RecyclerView notesListRecyclerView;
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

    ItemTouchHelper.SimpleCallback recyclerViewCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // THIS IS WHERE THE LOGIC FOR SWIPING THE ITEM SHOULD BE HANDLED.

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    // ITEM LEFT SWIPED, DELETE NOTE
                    deleteNote(position);
                    Toast.makeText(getActivity(), "Note deleted", Toast.LENGTH_SHORT).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    // ITEM RIGHT SWIPED, EDIT NOTE
                    editNote(position);
                    Toast.makeText(getActivity(), "Note edited", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(getContext(), c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark))
                    .addSwipeRightActionIcon(R.drawable.ic_edit)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void editNote(int position) {
        // SHOW ALERT DIALOG FOR THE USER TO EDIT THE NOTE.
        buildAlertDialog(position);
    }

    private void buildAlertDialog(final int position) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_note, null, false);

        TextView textView = view.findViewById(R.id.add_note_header);
        textView.setText("Edit Note");
        final TextInputLayout title = view.findViewById(R.id.note_title_input);
        final TextInputLayout note = view.findViewById(R.id.note_input);
        final TextInputLayout datetime = view.findViewById(R.id.note_datetime);
        Button button = view.findViewById(R.id.add_note_submit_button);
        button.setVisibility(View.GONE);

        //Building dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //save info where you want it
                homeViewModel.editNote(position, new Note(
                        String.valueOf(title.getEditText().getText()),
                        String.valueOf(note.getEditText().getText()),
                        String.valueOf(datetime.getEditText().getText())));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                homeViewModel.getNoteAdapter().notifyItemChanged(position);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteNote(int position) {
        // REMOVE THE NOTE FROM THE ADAPTER's LIT NOTES AND ALSO FROM FIREBASE FIRESTORE
        homeViewModel.deleteNote(position);
    }

    private void initComponents() {
        noNotesOverlay = (LinearLayout) root.findViewById(R.id.no_notes_overlay);
        noNotesOverlay.setVisibility(View.GONE);
    }

    private void initRecyclerView() {
        notesListRecyclerView = root.findViewById(R.id.notes_list);
        notesListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        notesListRecyclerView.setHasFixedSize(true);
        notesListRecyclerView.setAdapter(homeViewModel.getNoteAdapter());

        // initialize callback
        ItemTouchHelper touchHelper = new ItemTouchHelper(recyclerViewCallback);
        touchHelper.attachToRecyclerView(notesListRecyclerView);

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
                                List<Note> notes = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // LOOP THROUGH ALL THE DOCUMENT SNAPSHOTS AND CREATE NOTE OBJECTS
                                    Note note = (Note) document.toObject(Note.class);
                                    note.setId(document.getId());
                                    notes.add(note);
                                }
                                homeViewModel.setNotes(notes);
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