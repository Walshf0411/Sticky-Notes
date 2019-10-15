package com.stickynotes.ui.add_note;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stickynotes.R;
import com.stickynotes.models.Note;

public class AddNoteFragment extends Fragment {

    private AddNoteViewModel addNoteViewModel;
    private TextInputLayout noteTitle, note, noteDatetime;
    private Button submitButton;
    private View root;
    private ProgressDialog progressDialog;
    // this is the firebase instance using which we have to read and write collections & documents
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_add_note, container, false);
        initComponents();
        return root;
    }

    private void initComponents() {
        addNoteViewModel = ViewModelProviders.of(this).get(AddNoteViewModel.class);
        // initialize all the components
        noteTitle = (TextInputLayout) root.findViewById(R.id.note_title_input);
        note = (TextInputLayout) root.findViewById(R.id.note_input);
        noteDatetime = (TextInputLayout) root.findViewById(R.id.note_datetime);
        submitButton = (Button) root.findViewById(R.id.add_note_submit_button);
        setDatetimePicker(noteDatetime);
        setFormSubmitListener();
    }

    private void setFormSubmitListener() {
        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (formIsValid()) {
                            log("Form valid - writing to firebase");
                            writeToFirebase(
                                    getString(noteTitle.getEditText()),
                                    getString(note.getEditText()),
                                    getString(noteDatetime.getEditText())
                            );
                        } else {
                            showToast("Kindly correct the errors");
                        }
                    }
                }
        );
    }

    private void setDatetimePicker(TextInputLayout layout) {
        // TODO: implement datetime picker dialog logic
        noteDatetime.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO: show date picker and time picker
                    }
                }
        );
    }

    private void showDatePicker() {
        // TODO: Show date picker
    }

    private void showTimePicker() {
        // TODO: Show time picker
    }

    private boolean formIsValid() {
        boolean valid = true;

        if (TextUtils.isEmpty(noteTitle.getEditText().getText())) {
            log("Note title is empty");
            noteTitle.setError("This field is required");
            valid = false;
        } else {
            noteTitle.setError("");
        }

        if (TextUtils.isEmpty(note.getEditText().getText())) {
            log("Note is empty");
            note.setError("This field is required");
            valid = false;
        } else {
            note.setError("");
        }

        return valid;
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void log(String message) {
        Log.i(getString(R.string.TAG), message);
    }

    private void writeToFirebase(String noteTitle, String note, String noteDatetime) {
        // Creating Note object for the current note
        Note noteToBeSaved = new Note();
        noteToBeSaved.setTitle(noteTitle);
        noteToBeSaved.setNote(note);
        noteToBeSaved.setDatetime(noteDatetime != null ? noteDatetime : "");

        // show progress Dialog
        showProgessDialog("Saving " + noteTitle);

        addNoteViewModel.getNotesRepository().addNote(
                noteToBeSaved,
                new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            // Task is successful
                            // TODO: navigate to appropriate screen
                            hideProgressDialog();
                            showToast("Note saved successfully");
                        } else {
                            // Task is not successful
                            hideProgressDialog();
                            showToast("Your note couldn't be saved");
                        }
                    }
                }
        );
    }

    private String getString(EditText editText) {
        String text = String.valueOf(editText.getText());
        return text.equals("") ? "default" : text;
    }

    private void showProgessDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

}