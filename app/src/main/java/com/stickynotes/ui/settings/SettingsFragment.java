package com.stickynotes.ui.settings;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.stickynotes.Constants;
import com.stickynotes.LoginActivity;
import com.stickynotes.R;
import com.stickynotes.SharedPrefManager;

import java.security.Permissions;

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

        if (true) {
            enableWidgetSwitch.setChecked(true);
        } else {
            enableWidgetSwitch.setChecked(false);
        }

        enableWidgetSwitch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if (isChecked) {
                            // TODO:CHECK FOR THE PERMISSION AND IF NOT PERMITTED, ASK FOR PERMISSION
                            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                if (Settings.canDrawOverlays(getActivity())) {
                                    // PERMISSION IS ALREADY GRANTED, SET BOOLEAN IN SHARED PREFERENCES
                                    sharedPrefManager.setOverlayPreferred(isChecked);
                                } else {
                                    showPermissionMessage();
                                    getPermission();
                                }
                            }
                        } else {
                            // TODO: REMOVE FROM SHARED PREFERENCES
                            sharedPrefManager.setOverlayPreferred(false);
                        }
                    }
                }
        );
    }

    private void directUserToOverlaySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Show alert dialog to the user saying a separate permission is needed
            // Launch the settings activity if the user prefers
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
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
                        directUserToOverlaySettings();
                    }
        });
        builder.show();
    }

    public void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this.getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + this.getActivity().getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this.getContext())) {
                // You don't have permission
                getPermission();
            } else {
                // Do as per your logic

            }

        }

    }
}