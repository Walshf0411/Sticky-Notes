package com.stickynotes.ui.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.stickynotes.Constants;
import com.stickynotes.FloatingViewService;
import com.stickynotes.LoginActivity;
import com.stickynotes.MainActivity;
import com.stickynotes.R;
import com.stickynotes.SharedPrefManager;

public class SettingsFragment extends Fragment {

    private SettingsViewModel settingsViewModel;
    private TextView logoutBtn;
    private View root;
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private Switch enableWidgetSwitch;
    private final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 9999;
    private SharedPrefManager sharedPrefManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel =
                ViewModelProviders.of(this).get(SettingsViewModel.class);
        root = inflater.inflate(R.layout.fragment_settings, container, false);

        initComponents();

        return root;
    }

    private void initComponents() {
        sharedPrefManager = new SharedPrefManager(this.getContext());
        logoutBtn = (TextView) root.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        firebaseAuth.signOut();
                        Constants.showToast(getContext(), "Logged out successfully");
                        startActivity(
                                Constants.getClearHistoryIntent(
                                        getActivity(), LoginActivity.class
                                )
                        );
                    }
                }
        );
        enableWidgetSwitch = (Switch) root.findViewById(R.id.enable_floating_button_switch);

        if (sharedPrefManager.isOverlayPrefered()) {
            enableWidgetSwitch.setChecked(true);
        } else {
            enableWidgetSwitch.setChecked(false);
        }

        enableWidgetSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        // SET SHARED PREFERENCES
                        sharedPrefManager.setOverlayPreferred(b);
                        if (b) {
                            // IF USER PREFERS SHOW ALERT DIALOG AND ASK FOR USER CONSENT
                            // ONCE THE USER IS OK SHOW THE FLOATING SERVICE
                            showAlertDialog("This feature will show will allow the application to show " +
                                    "a widget on your device.");
                        } else {
                            getContext().stopService(
                                    new Intent(getContext(), FloatingViewService.class)
                            );
                        }

                    }
                }
        );

    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage(message);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getContext().startService(
                                new Intent(getContext(), FloatingViewService.class)
                        );
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        enableWidgetSwitch.setChecked(false);
                    }
                }
        );
        builder.show();
    }

    private void showPermissionMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Alert");
        builder.setMessage("Enabling this feature requires enabling the application to draw over other apps." +
                " Kindly grant this permission to use this feature");
        builder.setPositiveButton("OK"
                , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

}