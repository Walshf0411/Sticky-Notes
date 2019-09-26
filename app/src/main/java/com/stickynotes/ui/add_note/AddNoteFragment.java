package com.stickynotes.ui.add_note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.stickynotes.R;

public class AddNoteFragment extends Fragment {

    private AddNoteViewModel addNoteViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        addNoteViewModel =
                ViewModelProviders.of(this).get(AddNoteViewModel.class);
        View root = inflater.inflate(R.layout.fragment_add_note, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        addNoteViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}