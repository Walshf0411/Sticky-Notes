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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stickynotes.Constants;
import com.stickynotes.R;

import java.util.HashMap;
import java.util.Map;

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
                            showToast("Sending to db");
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
        Map<String, String> data = new HashMap<>();
        data.put(Constants.FIRESTORE_NOTE_TITLE_KEY, noteTitle);
        data.put(Constants.FIRESTORE_NOTE_KEY, note);
        data.put(Constants.FIRESTORE_NOTE_DATETIME_KEY, noteDatetime);

        String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        showToast(UID);
        String documentPath = "/users/" + UID + "/notes";
        showProgesssDialog("Saving note...");
        db.collection(documentPath)
                .add(data)
                .addOnCompleteListener(
                        new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    // TODO: Navigate to appropiate screen
                                    hideProgressDialog();
                                    showToast("Written successfully to document");
                                } else {
                                    // TODO: Show error
                                    hideProgressDialog();
                                    showToast("Some error occurred");
                                    log(task.getException().toString());
                                }
                            }
                        }
                );
    }

    private String getString(EditText editText) {
        String text = String.valueOf(editText.getText());
        return text.equals("") ? "default" : text;
    }

    private void showProgesssDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void hideProgressDialog() {
        progressDialog.dismiss();
    }

}